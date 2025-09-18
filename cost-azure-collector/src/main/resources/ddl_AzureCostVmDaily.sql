CREATE TABLE azure_cost_vm_daily
(
    id                  BIGINT AUTO_INCREMENT NOT NULL comment '아이디',
    created             datetime              NULL COMMENT 'row insert 시간',
    updated             datetime              NULL COMMENT 'row update 시간',
    tenant_id           VARCHAR(255)          NOT NULL COMMENT '테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000',
    subscription_id     VARCHAR(255)          NOT NULL COMMENT 'subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000',
    pre_tax_cost        DOUBLE                NOT NULL COMMENT '비용. ex) 16345.824',
    usage_date          VARCHAR(255)          NOT NULL COMMENT '날짜. ex) 20250903',
    resource_group_name VARCHAR(255)          NOT NULL COMMENT '리소스 그룹. ex) rg-dongwoo-1',
    resource_id         VARCHAR(255)          NOT NULL COMMENT '리소스 아이디.(AWS의 ARN과 비슷) ex) "/subscriptions/00000000-0000-0000-0000-00000000000/resourcegroups/rg-dongwoo-1/providers/microsoft.compute/virtualmachines/vm-1"',
    vm_id               VARCHAR(255)          NOT NULL COMMENT 'vm 아이디 (resourceId의 맨마지막 / 뒤 값, ex) vm-1',
    resource_guid       VARCHAR(255)          NOT NULL COMMENT '리소스 고유 아이디. ex) 00000000-0000-0000-0000-00000000000',
    currency            VARCHAR(255)          NOT NULL COMMENT '통화 단위. ex) KRW',
    CONSTRAINT pk_azure_cost_vm_daily PRIMARY KEY (id) COMMENT 'Azure Virtual Machines 별 요금 목록'
);