package com.example.tformattertest.job;

import com.example.tformattertest.domain.Member;
import com.example.tformattertest.domain.Pay;
import com.example.tformattertest.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FlatFileDelimeterWriterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;

    private static final String JOB_NAME = "flatFileDelimiterWriterJob";
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
//        BeanWrapperFieldExtractor<Pay> fieldExtractor = new BeanWrapperFieldExtractor<>();
//        fieldExtractor.setNames(new String[] {"id", "amount", "txName", "txDateTime"});
//
//        DelimitedLineAggregator<Pay> lineAggregator = new DelimitedLineAggregator<>();
//        lineAggregator.setDelimiter(",");
//        lineAggregator.setFieldExtractor(fieldExtractor);
//
//        return new FlatFileItemWriterBuilder<Pay>()
//                .name(BEAN_PREFIX + "writer")
//                .resource(new FileSystemResource("target/test-outputs/output.csv"))
//                .lineAggregator(lineAggregator)
//                .build();

        return new FlatFileItemWriterBuilder<Member>()
                .name(BEAN_PREFIX + "writer")
                .resource(new FileSystemResource("output/pay.csv"))
                .lineAggregator(new DelimitedLineAggregator<>())
                .delimited()
                .names("mbrNo", "id", "name")
                .build();
    }

//    @Bean
//    public ItemWriter<Pay> writer() {
//        return items -> {
//            for(Pay pay: items) {
//                log.info(">>>>>> Pay={}", pay);
//            }
//        };
//    }

}
