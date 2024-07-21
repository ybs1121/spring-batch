package com.study.springbatch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class StepBatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    public Job job(ItemReader<Person> itemReader,
                   ItemProcessor<Person, Person> itemProcessor,
                   ItemWriter<Person> itemWriter) {
        return new JobBuilder("personJob", jobRepository)
                .start(step(itemReader, itemProcessor, itemWriter))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step(ItemReader<Person> itemReader,
                     ItemProcessor<Person, Person> itemProcessor,
                     ItemWriter<Person> itemWriter) {
        return new StepBuilder("personStep", jobRepository)
                .<Person, Person>chunk(2, transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .allowStartIfComplete(true)  // Step이 완료되었더라도 재실행 가능하도록 설정
                .build();
    }

    @Bean
    public ItemReader<Person> itemReader() {
        return new CustomItemReader(); // 데이터 소스에서 데이터 읽어오는 로직 구현
    }

    @Bean
    public ItemProcessor<Person, Person> itemProcessor() {
        return person -> { // 함수형 인터페이스라서 이렇게 표현이 가능
            person.setName(person.getName().toUpperCase());
            return person;
        };
    }

    @Bean
    public ItemWriter<Person> itemWriter() {
        return items -> { // 함수형 인터페이스라서 이렇게 표현이 가능
            for (Person person : items) {
                log.info("Processed person: {}", person);
            }
        };
    }


    private static class CustomItemReader<Person> implements ItemReader<Person> {
        private List<com.study.springbatch.batch.Person> data;
        private int currentIndex = 0;

        public CustomItemReader() {
            // 데이터 소스에서 데이터를 읽어와 data 리스트에 저장
            data = new ArrayList<>();
            data.add(new com.study.springbatch.batch.Person(1, "John"));
            data.add(new com.study.springbatch.batch.Person(2, "Jane"));
            data.add(new com.study.springbatch.batch.Person(3, "Bob"));
        }

        @Override
        public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
            if (currentIndex < data.size()) {
                return (Person) data.get(currentIndex++);
            } else {
                return null; // 더 이상 읽을 데이터가 없음을 나타냅니다.
            }
        }
    }
}