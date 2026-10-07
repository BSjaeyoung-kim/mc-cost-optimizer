package com.mcmp.costbe.tumblebugMeta.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tumblebug 자원 정보를 각 CSP 과금 데이터의 자원 식별자(servicegroup_meta.csp_instanceid)로 정규화하는 순수 함수 모음.
 *
 * <p>근거(cb-spider IId.SystemId → Tumblebug cspResourceId):
 * AWS EKS = 클러스터 이름(CUR은 ARN) / Azure AKS = ARM 전체 ID / GCP GKE = 클러스터 이름 / NCP NKS = UUID.
 * Object Storage는 전 CSP 공통으로 CSP측 버킷명.
 */
public final class TbbResourceIdResolver {

    private static final Pattern AWS_EKS_ARN_ACCOUNT = Pattern.compile("^arn:aws:eks:[^:]*:(\\d{12}):");
    private static final Pattern AZURE_SUBSCRIPTION = Pattern.compile("/subscriptions/([a-f0-9-]{36})", Pattern.CASE_INSENSITIVE);
    private static final Pattern GCP_PROJECT_IN_LINK = Pattern.compile("/projects/([\\w-]+)/");

    private TbbResourceIdResolver() {}

    /**
     * K8s 클러스터의 과금 매칭 키.
     * AWS만 keyValueList의 "Arn"으로 치환(CUR lineitem_resourceid가 ARN), 그 외 CSP는 cspResourceId 그대로.
     */
    public static String k8sInstanceId(String cspType, String cspResourceId, List<Map<String, String>> keyValueList) {
        if ("AWS".equalsIgnoreCase(cspType)) {
            String arn = kvValue(keyValueList, "Arn");
            if (arn != null && arn.startsWith("arn:aws:eks:")) {
                return arn;
            }
        }
        return cspResourceId;
    }

    /**
     * K8s 클러스터의 CSP 계정 식별자.
     * AWS = ARN의 account id, AZURE = subscription id, GCP = SelfLink의 project id, 그 외/실패 = connectionName.
     */
    public static String k8sAccount(String cspType, String instanceId, List<Map<String, String>> keyValueList, String connectionName) {
        String fallback = isBlank(connectionName) ? "mcmpcostopti" : connectionName;
        if (cspType == null) {
            return fallback;
        }
        switch (cspType.toUpperCase()) {
            case "AWS": {
                Matcher m = AWS_EKS_ARN_ACCOUNT.matcher(nz(instanceId));
                if (m.find()) return m.group(1);
                break;
            }
            case "AZURE": {
                Matcher m = AZURE_SUBSCRIPTION.matcher(nz(instanceId));
                if (m.find()) return m.group(1);
                break;
            }
            case "GCP": {
                Matcher m = GCP_PROJECT_IN_LINK.matcher(nz(kvValue(keyValueList, "SelfLink")));
                if (m.find()) return m.group(1);
                break;
            }
            default:
                break;
        }
        return fallback;
    }

    /** Object Storage의 과금 매칭 키 = CSP측 버킷명. cspResourceId → cspResourceName → name 순으로 폴백. */
    public static String objectStorageInstanceId(String cspResourceId, String cspResourceName, String name) {
        if (!isBlank(cspResourceId)) return cspResourceId;
        if (!isBlank(cspResourceName)) return cspResourceName;
        return isBlank(name) ? null : name;
    }

    /** keyValueList([{key, value}])에서 key(대소문자 무시)에 해당하는 value. 없으면 null. */
    static String kvValue(List<Map<String, String>> keyValueList, String key) {
        if (keyValueList == null || key == null) return null;
        return keyValueList.stream()
                .filter(Objects::nonNull)
                .filter(m -> key.equalsIgnoreCase(m.get("key")))
                .map(m -> m.get("value"))
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
}
