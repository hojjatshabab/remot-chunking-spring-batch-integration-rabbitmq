package com.worker.remote.chunking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FactorsCount {
    private long number;
    private int count;
}
