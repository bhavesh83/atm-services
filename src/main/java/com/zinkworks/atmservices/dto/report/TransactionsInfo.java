package com.zinkworks.atmservices.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
public class TransactionsInfo {

    int transactionId;
    String transactionType;
    int amount;
    Map<Integer,Integer> denominationPair;
}
