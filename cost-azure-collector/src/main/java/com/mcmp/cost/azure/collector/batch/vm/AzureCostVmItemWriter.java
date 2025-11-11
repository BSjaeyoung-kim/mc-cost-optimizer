package com.mcmp.cost.azure.collector.batch.vm;

import com.mcmp.cost.azure.collector.client.AlarmServiceClient;
import com.mcmp.cost.azure.collector.dto.AlarmHistoryDto;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.mapper.AlarmHistoryMapper;
import com.mcmp.cost.azure.collector.properties.AzureCredentialProperties;
import com.mcmp.cost.azure.collector.repository.AzureCostVmDailyRepository;
import com.mcmp.cost.azure.collector.service.BudgetMonthlyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AzureCostVmItemWriter implements ItemWriter<List<AzureCostVmDaily>> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AzureCostVmDailyRepository azureCostVmDailyRepository;
    private final AlarmHistoryMapper alarmHistoryMapper;
    private final AlarmServiceClient alarmServiceClient;
    private final BudgetMonthlyService budgetMonthlyService;

    @Override
    public void write(Chunk<? extends List<AzureCostVmDaily>> chunk) throws Exception {
        // 당일 VM 비용 저장
        for (List<AzureCostVmDaily> azureCostVmDailyList : chunk) {
            if (azureCostVmDailyList != null && !azureCostVmDailyList.isEmpty()) {
                azureCostVmDailyRepository.saveAll(azureCostVmDailyList);
                azureCostVmDailyRepository.flush();
                log.info("Saved {} Azure cost vm records to database", azureCostVmDailyList.size());
            }
        }
    }
}
