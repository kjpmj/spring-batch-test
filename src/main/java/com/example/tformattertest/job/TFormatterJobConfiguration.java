package com.example.tformattertest.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TFormatterJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final String JOB_NAME = "tFormatterJob";
    private static final String BEAN_PREFIX = JOB_NAME + "_";
    private static final int CHUNK_SIZE = 1000;

    @Bean(JOB_NAME)
    public Job job () {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build();
    }

    @Bean(JOB_NAME + "step")
    public Step step() {
        return stepBuilderFactory.get(JOB_NAME + "step")
                .<String, String> chunk(CHUNK_SIZE)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean(BEAN_PREFIX + "reader")
    public ListItemReader<String> reader() {
        List<String> list = new ArrayList<>();
        for(int i=1; i<=10000; i++ ) {
            list.add("item" + i);
        }

        ListItemReader<String> reader = new ListItemReader<>(list);
        return reader;
    }

    private ItemWriter<String> writer() {
        return list -> {
            System.out.println("=====================");
            for (String msg: list) {
                log.info(">>>>>>>> ITEM={}", msg);
            }
        };
    }
}
