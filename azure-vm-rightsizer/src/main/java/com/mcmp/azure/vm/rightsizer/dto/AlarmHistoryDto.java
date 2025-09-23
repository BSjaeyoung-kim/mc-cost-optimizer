package com.mcmp.azure.vm.rightsizer.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class AlarmHistoryDto {

    private String eventType;
    private String resourceId;
    private String resourceType;
    private Timestamp occureDt;
    private String accountId;
    private String urgency;
    private String plan;
    private String note;
    private Timestamp occureDate;
    private String cspType;
    private String alarmImpl;
    private String projectCd;

    @Builder
    public AlarmHistoryDto(String eventType, String resourceId, String resourceType, Timestamp occureDt, String accountId, String urgency, String plan, String note, Timestamp occureDate, String cspType, String alarmImpl, String projectCd) {
        this.eventType = eventType;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.occureDt = occureDt;
        this.accountId = accountId;
        this.urgency = urgency;
        this.plan = plan;
        this.note = note;
        this.occureDate = occureDate;
        this.cspType = cspType;
        this.alarmImpl = alarmImpl;
        this.projectCd = projectCd;
    }
}
