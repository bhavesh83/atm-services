package com.zinkworks.atmservices.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zinkworks.atmservices.persistence.entity.CustomerAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ATMSessionService {
    Cache<String, SessionData> accountSessionMap = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS).build();

    public boolean isSessionIdPresent(String sessionId) {
        return accountSessionMap.asMap().containsKey(sessionId);
    }

    public boolean isSessionIdActive(String sessionId) {
        SessionData sessionData = accountSessionMap.getIfPresent(sessionId);
        if (sessionData != null) {
            return sessionData.getSessionStatus() == SessionStatus.ACTIVE;
        }
        return false;
    }

    public CustomerAccount getCustomerAccount(String sessionId) {
        SessionData sessionData = accountSessionMap.getIfPresent(sessionId);
        return sessionData != null ? sessionData.getAccount() : null;
    }

    public void addSessionData(String sessionId, CustomerAccount account) {
        SessionData sessionData = new SessionData(account, SessionStatus.CREATED);
        accountSessionMap.put(sessionId, sessionData);
    }

    public void removeSessionData(String sessionId) {
        accountSessionMap.asMap().remove(sessionId);
    }

    public void makeSessionDataActive(String sessionId) {
        SessionData sessionData = accountSessionMap.getIfPresent(sessionId);
        if (sessionData != null) {
            sessionData.setSessionStatus(SessionStatus.ACTIVE);
        }
    }
}

@Data
@AllArgsConstructor
class SessionData {
    CustomerAccount account;
    SessionStatus sessionStatus;
}

enum SessionStatus {
    ACTIVE,
    CREATED;
}