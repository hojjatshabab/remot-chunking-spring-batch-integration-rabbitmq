package com.worker.remote.chunking.batchconfig;

import com.master.remote.chunking.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@RequiredArgsConstructor
public class WorkerConfiguration {

    private final RemoteChunkingWorkerBuilder<Transaction, Transaction> workerBuilder;
    private final ItemWriter<Transaction> itemWriter;
    private final ItemProcessor<Transaction, Transaction> itemProcessor;

    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlows
                .from(replies())
                .handle(Amqp.outboundAdapter(amqpTemplate)
                        .routingKey("replies"))
                .get();
    }

    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Amqp.inboundAdapter(connectionFactory, "requests"))
                .channel(requests())
                .get();
    }

    @Bean
    public IntegrationFlow integrationFlow() {
        return this.workerBuilder
                .itemProcessor(itemProcessor)
                .itemWriter(itemWriter)
                .inputChannel(requests())
                .outputChannel(replies())
                .build();
    }

}
