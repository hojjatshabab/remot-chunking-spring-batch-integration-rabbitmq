package com.worker.remote.chunking.config;

import com.worker.remote.chunking.model.Transaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemProcessTransaction implements ItemProcessor<Transaction,Transaction>{
    @Override
    public Transaction process(Transaction transaction) throws Exception {

        System.out.println("processing transaction " + transaction);
        return transaction;
    }
}
