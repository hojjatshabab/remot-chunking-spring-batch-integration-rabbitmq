package com.worker.remote.chunking.config;

import com.worker.remote.chunking.domain.PrimeFactors;
import com.worker.remote.chunking.dto.FactorsCount;
import com.worker.remote.chunking.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.Assert;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ConsumerConfiguration {

    private final RemoteChunkingWorkerBuilder<Transaction, Transaction> workerBuilder;
    private final ItemWriter<Transaction> itemWriter;
    private final ItemProcessor<Transaction, Transaction> itemProcessor;

    @Bean
    public QueueChannel requests() {
        return new QueueChannel();
    }

    /**
     * Reads messages from "requests" queue.
     *
     * @param connectionFactory Rabbit connection factory.
     * @return Inbound Integration Flow.
     */
    @Bean
    public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlows
                .from(Amqp.inboundAdapter(connectionFactory, "requests"))
                .channel(requests())
                .get();
    }

    /**
     * Outbound Channel.
     *
     * @return DirectChannel.
     */
    @Bean
    public DirectChannel replies() {
        return new DirectChannel();
    }

    /**
     * Send messages to "replies" queue.
     *
     * @param template AmqpTemplate.
     * @return Inbound Integration Flow.
     */
    @Bean
    public IntegrationFlow outboundFlow(AmqpTemplate template) {
        return IntegrationFlows.from(replies())
                .handle(Amqp.outboundAdapter(template).routingKey("replies"))
                .get();
    }

    /**
     * {@link PollerMetadata} with polling interval set to 0.1 sec.
     *
     * @return PollerMetadata.
     */
    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(100));
        return pollerMetadata;
    }

    /**
     * Service activation point. Configure ChunkProcessorChunkHandler in order to process
     * numbers and write results to DB with {@link JdbcBatchItemWriter}.
     *
     * @return ChunkProcessorChunkHandler.
     */
    @Bean
    @ServiceActivator(inputChannel = "requests", outputChannel = "replies", sendTimeout = "3000")
    public ChunkProcessorChunkHandler<Transaction> chunkProcessorChunkHandler() {
        ChunkProcessorChunkHandler<Transaction> chunkProcessorChunkHandler = new ChunkProcessorChunkHandler<>();
        PrimeFactors primeFactors = new PrimeFactors();
        Transaction transaction = new Transaction();
        chunkProcessorChunkHandler.setChunkProcessor(
                new SimpleChunkProcessor<Transaction, Transaction>((number) -> {
                    System.out.println("skd;lkasl;dkdk;laskd");
                    return null;
                }, itemWriter));

        return chunkProcessorChunkHandler;
    }






}
