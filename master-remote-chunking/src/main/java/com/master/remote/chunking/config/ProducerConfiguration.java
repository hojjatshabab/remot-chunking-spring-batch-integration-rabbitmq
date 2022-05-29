package com.master.remote.chunking.config;

import com.master.remote.chunking.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkingMasterStepBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

import java.util.Objects;

import static org.springframework.batch.item.file.transform.DelimitedLineTokenizer.DELIMITER_TAB;

@Configuration
@RequiredArgsConstructor
public class ProducerConfiguration {


    private final JobBuilderFactory jobBuilderFactory;
    private final RemoteChunkingMasterStepBuilderFactory remoteChunkingMaster;
    private final ItemReader<Transaction> itemReader;

    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows
                .from(requests())
                .handle(Amqp.outboundAdapter(amqpTemplate)
                        .routingKey("requests"))
                .get();
    }

    @Bean
    public QueueChannel replies() {
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Amqp.inboundAdapter(connectionFactory, "replies"))
                .channel(replies())
                .get();
    }

    @Bean
    public TaskletStep masterStep() {
        return this.remoteChunkingMaster.get("masterStep")
                .<Transaction, Transaction>chunk(100)
                .reader(itemReader)
                .outputChannel(requests())
                .inputChannel(replies())
                .build();
    }

    @Bean
    public Job remoteChunkingJob() {
        return this.jobBuilderFactory.get("remoteChunkingJob")
                .start(masterStep())
                .build();
    }

}
