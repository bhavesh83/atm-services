package com.zinkworks.atmservices.dto.auth;

import lombok.Data;

@Data
public class AccountLogoutRequest {
    String sessionId;
}
