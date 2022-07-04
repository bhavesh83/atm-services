package com.zinkworks.atmservices.dto.auth;

import com.zinkworks.atmservices.dto.error.Error;
import lombok.Data;

@Data
public class AccountLogoutResponse {
    boolean isSuccessful;
    Error error;
}
