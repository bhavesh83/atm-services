package com.zinkworks.atmservices.service;

import com.zinkworks.atmservices.common.CommonConstants;
import com.zinkworks.atmservices.exception.SessionExpiredException;
import com.zinkworks.atmservices.persistence.entity.CustomerAccount;
import com.zinkworks.atmservices.persistence.repositories.CustomerAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ATMAuthService {

    @Autowired
    CustomerAccountRepository accountRepository;

    @Autowired
    ATMSessionService atmSessionService;

    public String validateAccountNumber(String accNumber)  {
        Optional<CustomerAccount> customerAccountOptional = accountRepository.findById(accNumber);
        String sessionId = null;
        if (customerAccountOptional.isPresent()) {
            sessionId = UUID.randomUUID().toString();
            atmSessionService.addSessionData(sessionId, customerAccountOptional.get());
        }
        return sessionId;
    }

    public boolean loginWithAccountNumberAndPin(String sessionId, String accountPin) throws SessionExpiredException {
        CustomerAccount account = atmSessionService.getCustomerAccount(sessionId);
        if (account != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isAuthenticated = encoder.matches(accountPin, account.getHashedPin());
            if (isAuthenticated) {
                atmSessionService.makeSessionDataActive(sessionId);
            } else {
                atmSessionService.removeSessionData(sessionId);
            }
            return isAuthenticated;
        }
        throw new SessionExpiredException(CommonConstants.SESSION_EXPIRED_MSG);
    }

    public boolean logout(String sessionId) throws SessionExpiredException {
        if (atmSessionService.isSessionIdActive(sessionId)) {
            atmSessionService.removeSessionData(sessionId);
            return true;
        }
        throw new SessionExpiredException(CommonConstants.SESSION_EXPIRED_MSG);
    }
}

