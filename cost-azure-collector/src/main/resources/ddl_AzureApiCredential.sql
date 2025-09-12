CREATE TABLE azure_api_credentials
(
    id              BIGINT AUTO_INCREMENT NOT NULL comment      '아이디',
    created         datetime              NULL COMMENT          'row insert 시간',
    updated         datetime              NULL COMMENT          'row update 시간',
    tenant_id       VARCHAR(255)          NOT NULL COMMENT      '테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000',
    client_id       VARCHAR(255)          NOT NULL COMMENT      '클라이언트 아이디. ex) 00000000-0000-0000-0000-00000000000',
    client_secret   VARCHAR(255)          NOT NULL COMMENT      'client secret. ex) aaaaaaaaaaaaaaaaaaaaaaaaaaaaa',
    subscription_id VARCHAR(255)          NOT NULL COMMENT      'subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000',
    CONSTRAINT pk_azure_api_credentials PRIMARY KEY (id) COMMENT 'Azure Credential'
);