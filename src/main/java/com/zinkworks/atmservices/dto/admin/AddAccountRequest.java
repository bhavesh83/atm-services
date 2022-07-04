package com.zinkworks.atmservices.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class AddAccountRequest {
    List<AccountDetails> accountDetails;
}
