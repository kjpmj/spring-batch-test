package com.example.tformattertest.job;

import com.example.tformattertest.domain.Member;
import com.example.tformattertest.domain.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FlatFileFixedLengthWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;

    private static final String JOB_NAME = "flatFileFixedLengthWriterJob";
    private static final String BEAN_PREFIX = JOB_NAME + "_";
    private static final int CHUNK_SIZE = 1000;

    @Bean(JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    public Step step() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Member, Member> chunk(CHUNK_SIZE)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean(BEAN_PREFIX + "reader")
    public JpaPagingItemReader<Member> reader() {
        return new JpaPagingItemReaderBuilder<Member>()
                .name(BEAN_PREFIX + "reader")
                .pageSize(CHUNK_SIZE)
                .entityManagerFactory(emf)
                .queryString("SELECT m FROM Member m")
                .build();
    }

    @Bean(BEAN_PREFIX + "writer")
    public FlatFileItemWriter<Member> writer() {
        BeanWrapperFieldExtractor<Member> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"id", "name"});
        fieldExtractor.afterPropertiesSet();

        FormatterLineAggregator<Member> lineAggregator = new FormatterLineAggregator<>();
        lineAggregator.setFormat("%-10s%-10s");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<Member>()
                .name(BEAN_PREFIX + "writer")
                .resource(new FileSystemResource("output/member.txt"))
                .lineAggregator(lineAggregator)
                .build();
    }

}
