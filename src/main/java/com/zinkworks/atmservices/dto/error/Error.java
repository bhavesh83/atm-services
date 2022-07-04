package com.zinkworks.atmservices.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
    String errorCode;
    String errorMessage;
}
