package com.zinkworks.atmservices.dto.admin;

import lombok.Data;

@Data
public class AccountDetails {
    String accountNumber;
    String accountPin;
    int openingBal;
    int overdraftLimit;
}
