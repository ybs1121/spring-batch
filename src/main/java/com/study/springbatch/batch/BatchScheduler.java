package com.study.springbatch.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class BatchScheduler {

    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;


    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor jobProcessor = new JobRegistryBeanPostProcessor();
        jobProcessor.setJobRegistry(jobRegistry);
        return jobProcessor;
    }

    @Scheduled(cron = "0/60 * * * * *") // 10초마다 실행
    public void runJob() {
        String time = LocalDateTime.now().toString();
        try {
            Job job = jobRegistry.getJob("testJob"); // job 이름
            JobParametersBuilder jobParam = new JobParametersBuilder().addString("time", time);
            jobLauncher.run(job, jobParam.toJobParameters());
        } catch (NoSuchJobException e) {
            throw new RuntimeException(e);
        } catch (JobInstanceAlreadyCompleteException |
                 JobExecutionAlreadyRunningException |
                 JobParametersInvalidException |
                 JobRestartException e
        ) {
            throw new RuntimeException(e);
        }
    }


    @Scheduled(cron = "0/5 * * * * *") // 5초마다 실행
    public void runJob2() {
        try {
            Job job = jobRegistry.getJob("personJob");
            jobLauncher.run(job, new JobParameters());
            /*
            * JobParameters가 필요하지만, 값이 없어도 되는 경우 빈 JobParameters를 전달할 수 있습니다.
            *  빈 JobParameters를 전달하면 Job은 실행되지만, RunIdIncrementer와 같은 설정이 없으면 동일한 파라미터로
            *  다시 실행되지 않습니다.
            * */
        } catch (NoSuchJobException e) {
            throw new RuntimeException(e);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        }
    }

}
