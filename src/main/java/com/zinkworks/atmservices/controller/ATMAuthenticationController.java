package com.zinkworks.atmservices.controller;

import com.zinkworks.atmservices.dto.auth.*;
import com.zinkworks.atmservices.dto.error.Error;
import com.zinkworks.atmservices.exception.SessionExpiredException;
import com.zinkworks.atmservices.service.ATMAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zinkworks.atmservices.common.ErrorCodes.ATM_101;

@RestController
@RequestMapping("/atm/auth")
public class ATMAuthenticationController {

    Logger logger = LoggerFactory.getLogger(ATMAuthenticationController.class);

    @Autowired
    ATMAuthService atmAuthService;

    @PostMapping("/validate/accNumber")
    public AccountNumberResponse validateAccountNumber(@RequestBody AccountNumberRequest accountNumberRequest) {
        logger.debug("Received request for Account number Validation");
        String sessionId = atmAuthService.validateAccountNumber(accountNumberRequest.getAccNumber());
        AccountNumberResponse response = new AccountNumberResponse();
        if (sessionId == null || sessionId.isEmpty()) {
            logger.debug("Returning Error response for account validation : " + ATM_101);
            response.setError(new Error(ATM_101.toString(), ATM_101.getErrorMessage()));
        } else {
            response.setSessionId(sessionId);
        }
        logger.debug("Successfully validated account");
        return response;
    }

    @PostMapping("/login")
    public AccountLoginResponse loginWithAccountNumberAndPin(@RequestBody AccountLoginRequest accountLoginRequest)
            throws SessionExpiredException {
        logger.debug("Received login request to Account");
        boolean isAuthSuccessful = atmAuthService.loginWithAccountNumberAndPin(accountLoginRequest.getSessionId(),
                accountLoginRequest.getAccountPin());
        AccountLoginResponse response = new AccountLoginResponse();
        response.setAuthenticated(isAuthSuccessful);
        return response;
    }

    @PostMapping("/logout")
    public AccountLogoutResponse logout(@RequestBody AccountLogoutRequest request) throws SessionExpiredException {
        logger.debug("Received logout request to Account");
        boolean isSuccessful = atmAuthService.logout(request.getSessionId());
        AccountLogoutResponse response = new AccountLogoutResponse();
        response.setSuccessful(isSuccessful);
        return response;
    }

}
