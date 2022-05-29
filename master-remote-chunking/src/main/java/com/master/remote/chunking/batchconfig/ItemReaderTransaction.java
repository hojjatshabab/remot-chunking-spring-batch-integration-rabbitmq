package com.master.remote.chunking.batchconfig;

import com.master.remote.chunking.model.Transaction;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class ItemReaderTransaction {

    public static String[] names = new String[] {"account","amount","timestamp"};

    @Bean
    public ItemReader<Transaction> itemReaderTransactionBean(){

        FlatFileItemReader<Transaction> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setName("CSV-Reader");
        itemReader.setResource(new FileSystemResource("src/main/resources/csv-file/transaction.csv"));
        itemReader.setLineMapper(lineMapper());
        itemReader.setSaveState(false);
        return itemReader;
    }

    public DefaultLineMapper<Transaction> lineMapper(){
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(names);

        DefaultLineMapper<Transaction> lineMapper= new DefaultLineMapper<>();

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet ->{
            Transaction transaction = new Transaction();

            transaction.setAccount(fieldSet.readString("account"));
            transaction.setAmount(fieldSet.readString("amount"));
            transaction.setTimestamp(fieldSet.readString("timestamp"));

            return transaction;
        });
        return lineMapper;
    }

}