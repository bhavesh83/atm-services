package com.zinkworks.atmservices.dto.auth;

import lombok.Data;

@Data
public class AccountLoginRequest {
    String sessionId;
    String accountPin;
}
