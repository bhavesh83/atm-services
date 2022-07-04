package com.zinkworks.atmservices.dto.transaction;

import com.zinkworks.atmservices.dto.error.Error;
import lombok.Data;

@Data
public class BalanceInquiryResponse {
    int accountBal;
    int withdrawableBal;
    Error error;
}
