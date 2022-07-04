package com.zinkworks.atmservices.dto.report;

import lombok.Data;

import java.util.List;

@Data
public class TransactionsReport {
    List<TransactionsInfo> transactions;
}
