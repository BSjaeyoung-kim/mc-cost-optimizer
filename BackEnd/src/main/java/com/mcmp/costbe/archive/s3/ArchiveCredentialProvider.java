package com.mcmp.costbe.archive.s3;

import com.mcmp.costbe.archive.config.ArchiveS3Properties;
import com.mcmp.costbe.archive.credential.CredentialResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.Credentials;

import java.time.Instant;

/**
 * 아카이브 S3 쓰기용 자격증명 제공자. CUR 읽기와 동일 경로:
 *   base 키(env) → temp 테이블 role_arn 로 STS AssumeRole → 세션 크레덴셜.
 *
 * ★ 비용 방어: STS AssumeRole 은 클라우드 API 호출이므로 세션 크레덴셜을 만료 전까지 캐시·재사용한다.
 *   → 아카이브를 아무리 많이 눌러도 STS 호출은 (세션당 1회 = 대략 시간당 1회)로 제한.
 * ※ costCollector.AssumeRole 의 독립 사본(costCollector 무수정). base 키는 v1은 env.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArchiveCredentialProvider {

    private static final long EXPIRY_BUFFER_SEC = 300; // 만료 5분 전 갱신

    private final ArchiveS3Properties props;
    private final CredentialResolver credentialResolver;   // OpenBao/env (콜렉터와 동일)

    // 캐시 (동기화로 보호). 같은 role 이고 만료 전이면 재사용 → STS 호출 안 함.
    private StaticCredentialsProvider cached;
    private Instant cachedExpiry;
    private String  cachedRoleArn;

    /** roleArn 으로 세션 크레덴셜 제공자 반환(캐시 우선). 실패 시 null. */
    public synchronized StaticCredentialsProvider assumeRole(String roleArn) {
        // base 키: openbao.enabled면 OpenBao(secret/data/csp/aws), 아니면 env (콜렉터와 동일)
        String baseAccessKey = credentialResolver.resolve("aws", "AWS_ACCESS_KEY_ID", System.getenv("AWS_ACCESS_KEY_ID"));
        String baseSecretKey = credentialResolver.resolve("aws", "AWS_SECRET_ACCESS_KEY", System.getenv("AWS_SECRET_ACCESS_KEY"));
        if (baseAccessKey == null || baseAccessKey.isBlank() || baseSecretKey == null || baseSecretKey.isBlank()) {
            log.error("[archive] AWS base 자격증명 없음 (OpenBao secret/data/csp/aws 또는 env 확인)");
            return null;
        }
        if (roleArn == null || roleArn.isBlank()) {
            log.error("[archive] role_arn 없음 (temp_cmp_user_role_arn 조회 실패)");
            return null;
        }

        // 캐시 유효하면 재사용 (STS 호출 회피 = 과금/호출 방어)
        if (cached != null
                && roleArn.equals(cachedRoleArn)
                && cachedExpiry != null
                && Instant.now().isBefore(cachedExpiry.minusSeconds(EXPIRY_BUFFER_SEC))) {
            return cached;
        }

        try (StsClient sts = StsClient.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(baseAccessKey, baseSecretKey)))
                .build()) {

            AssumeRoleRequest req = AssumeRoleRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName("invoice-archive-" + Instant.now().toEpochMilli())
                    .build();

            Credentials c = sts.assumeRole(req).credentials();
            StaticCredentialsProvider provider = StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(c.accessKeyId(), c.secretAccessKey(), c.sessionToken()));

            // 캐시 갱신
            cached = provider;
            cachedRoleArn = roleArn;
            cachedExpiry = c.expiration();
            log.info("[archive] AssumeRole 갱신 (만료: {})", cachedExpiry);
            return provider;
        } catch (Exception e) {
            log.error("[archive] AssumeRole 실패 - role: {}, cause: {}", roleArn, e.getMessage());
            return null;
        }
    }
}
