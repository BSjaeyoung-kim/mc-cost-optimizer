package com.mcmp.cost.azure.collector.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "azure_api_credentials")
public class AzureApiCredential extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /**
     * 클라이언트 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    @Column(name = "client_id", nullable = false)
    private String clientId;

    /**
     * client secret. ex) aaaaaaaaaaaaaaaaaaaaaaaaaaaaa
     */
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    /**
     * subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    @Column(name = "subscription_id", nullable = false)
    private String subscriptionId;
}
