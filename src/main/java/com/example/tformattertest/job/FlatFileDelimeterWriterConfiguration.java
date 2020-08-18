package com.example.tformattertest.job;

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
                .<Pay, Pay> chunk(CHUNK_SIZE)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Pay> reader() {
        return new JpaPagingItemReaderBuilder<Pay>()
                .name(BEAN_PREFIX + "reader")
                .pageSize(CHUNK_SIZE)
                .entityManagerFactory(emf)
                .queryString("SELECT p FROM Pay p")
                .build();
    }

    @Bean
    public FlatFileItemWriter<Pay> writer() {
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

        return new FlatFileItemWriterBuilder<Pay>()
                .name(BEAN_PREFIX + "writer")
                .resource(new FileSystemResource("target/test-outputs/output.csv"))
                .lineAggregator(new DelimitedLineAggregator<>())
                .delimited()
                .names("id", "amount", "txName", "txDateTime")
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
