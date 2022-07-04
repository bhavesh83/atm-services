package com.zinkworks.atmservices.dto.transaction;

import lombok.Data;

@Data
public class WithdrawalRequest {
    String sessionId;
    int amount;
}
