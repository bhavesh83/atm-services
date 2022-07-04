package com.zinkworks.atmservices.dto.report;

import lombok.Data;

import java.util.Map;

@Data
public class CashInventoryResponse {
    private int totalCash;
    Map<Integer,Integer> denomination;
}
