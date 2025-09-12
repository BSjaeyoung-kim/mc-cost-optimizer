CREATE TABLE ncp_api_credentials
(
    id             BIGINT AUTO_INCREMENT NOT NULL comment   '아이디',
    created        datetime              NULL COMMENT       'row insert 시간',
    updated        datetime              NULL COMMENT       'row update 시간',
    iam_access_key VARCHAR(255)          NOT NULL COMMENT   'Access Key',
    iam_secret_key VARCHAR(255)          NOT NULL COMMENT   'Secret Key',
    CONSTRAINT pk_ncp_api_credentials PRIMARY KEY (id) COMMENT 'NCP Credential'
);