package com.worker.remote.chunking.batchconfig;

import com.master.remote.chunking.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ItemWriterTransaction {

    public DataSource dataSource(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/remote-chunking");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("postgres");
        return dataSourceBuilder.build();
    }

    public static String INSERT_TRANSACTION_SQL =
            "INSERT INTO TRANSACTION (ACCOUNT,AMOUNT,TIMESTAMP) VALUES (:account,:amount,:timestamp)";

    @Bean
    public ItemWriter<Transaction> itemWriterTransactionBean() {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource())
                .sql(INSERT_TRANSACTION_SQL)
                .beanMapped()
                .build();
    }

}
