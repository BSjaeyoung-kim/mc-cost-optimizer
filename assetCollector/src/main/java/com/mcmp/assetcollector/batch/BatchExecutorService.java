package com.mcmp.assetcollector.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class BatchExecutorService {

    private final JobLauncher jobLauncher;
    private final Map<String, Job> jobMap;

    public void executeBatch(String jobName) {
        Job job = getJobByName(jobName);
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("createTime", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info("{} Batch Job started with execution id: {}",
                    jobName, jobExecution.getId());

        } catch (Exception e) {
            log.error("Error running {} Batch Job", jobName, e);
            throw new RuntimeException("Batch execution failed: " + jobName, e);
        }
    }

    public Job getJobByName(String jobName) {
        Job job = jobMap.get(jobName);
        if (job == null) {
            throw new IllegalArgumentException("Job not found for type: " + jobName);
        }
        return job;
    }
}
