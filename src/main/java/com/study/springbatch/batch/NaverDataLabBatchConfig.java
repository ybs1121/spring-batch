package com.study.springbatch.batch;

import com.study.springbatch.NaverDataLab;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class NaverDataLabBatchConfig extends DefaultBatchConfiguration {

    private final WebClient webClient;

    @Bean
    public Job naverDataLabJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws DuplicateJobException {
        Job job = new JobBuilder("naverDataLabJob",jobRepository)
                .start(testStep(jobRepository,transactionManager))
                .build();
        return job;
    }

    public Step testStep(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        Step step = new StepBuilder("naverDataLabStep",jobRepository)
                .tasklet(testTasklet(),transactionManager)
                .build();
        return step;
    }

    public Tasklet testTasklet(){
        return ((contribution, chunkContext) -> {
            System.out.println("***** naverDataLabStep batch start! *****");
            // 원하는 비지니스 로직 작성

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("startDate", "2023-07-22");
            requestBody.put("endDate", "2024-07-22");
            requestBody.put("timeUnit", "month");

            List<Map<String,Object>> keywordGroups = new ArrayList<>();
            Map<String,Object> keywordGroup = new HashMap<>();
            keywordGroup.put("groupName", "의류");
            keywordGroup.put("keywords", List.of("맨투맨","반팔"));
            keywordGroups.add(keywordGroup);
            requestBody.put("keywordGroups", keywordGroups);

            Mono<String> naverDataLabMono = webClient.post()
                    .uri("/v1/datalab/search")
                    .header("Content-Type", "application/json")
                    .header("X-Naver-Client-Id", "네이버에서확인")
                    .header("X-Naver-Client-Secret", "네이버에서확인")
                    .body(Mono.just(requestBody), Map.class)
                    .retrieve()
                    .bodyToMono(String.class);

            String naverDataLab = naverDataLabMono.block();


            log.info(naverDataLab.toString());

            return RepeatStatus.FINISHED;
        });
    }
}
