package com.zinkworks.atmservices.dto.transaction;

import com.zinkworks.atmservices.dto.error.Error;
import lombok.Data;

import java.util.Map;

@Data
public class WithdrawalResponse {
    String transactionId;
    Error error;
    Map<Integer,Integer> denominationMap;
}
