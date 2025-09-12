package com.mcmp.cost.ncp.collector.entity;

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
@Table(name = "ncp_api_credentials")
public class NcpApiCredential extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * NCP AccessKey. ex) ncp_iam_abcedfghijklmopq12341234
     */
    @Column(name = "iam_access_key", nullable = false)
    private String iamAccessKey;

    /**
     * NCP SecretKey. ex) ncp_iam_abcedfghijklmopq1234123430430403403
     */
    @Column(name = "iam_secret_key", nullable = false)
    private String iamSecretKey;
}
