package com.master.remote.chunking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {

    private String account;
    private String amount;
    private String timestamp;

}
