package com.example.tformattertest.job;

import com.example.tformattertest.domain.Player;
import com.example.tformattertest.domain.PlayerFieldSetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FlatFileDelimierReaderConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final String JOB_NAME = "flatFileDelimiterReaderJob";
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
                .<Player, Player> chunk(CHUNK_SIZE)
                .reader(reader())
                .writer(writer())
                .build();
    }


    @Bean(BEAN_PREFIX + "reader")
    public FlatFileItemReader<Player> reader() {
//        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
//        tokenizer.setNames("ID", "lastName","firstName","position","birthYear","debutYear");
//        return new FlatFileItemReaderBuilder<Player>()
//                .name(JOB_NAME + "reader")
//                .resource(new ClassPathResource("player.csv"))
//                .lineTokenizer(tokenizer)
//                .targetType(Player.class)
//                .linesToSkip(1)
//                .build();

        // 요렇게 하면 FieldSetMapper를 따로 만들지 않아도 된다.
        return new FlatFileItemReaderBuilder<Player>()
                .name(JOB_NAME + "reader")
                .resource(new ClassPathResource("player.csv"))
                .delimited()
                .names("ID","lastName","firstName","position","birthYear","debutYear")
                .targetType(Player.class)
                .linesToSkip(1)
                .build();

        // 이렇게 하면 FieldSetMapper를 따로 만들어야하지만 위 처럼 field를 따로 명시하지 않아도 된다.
//        FlatFileItemReader<Player> itemReader = new FlatFileItemReader<>();
//        itemReader.setResource(new ClassPathResource("player.csv"));
//        DefaultLineMapper<Player> lineMapper = new DefaultLineMapper<>();
//        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
//        lineMapper.setFieldSetMapper(new PlayerFieldSetMapper());
//        itemReader.setLineMapper(lineMapper);
//        itemReader.setLinesToSkip(1);
//        itemReader.open(new ExecutionContext());
//
//        return itemReader;
    }

    private ItemWriter<Player> writer() {
        return list -> {
            for (Player player : list) {
                log.info(">>>>>>> Player={}", player);
            }
        };
    }

}
