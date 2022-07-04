package com.zinkworks.atmservices;

import com.zinkworks.atmservices.common.CommonConstants;
import com.zinkworks.atmservices.exception.SessionExpiredException;
import com.zinkworks.atmservices.persistence.entity.CustomerAccount;
import com.zinkworks.atmservices.persistence.repositories.CustomerAccountRepository;
import com.zinkworks.atmservices.service.ATMAuthService;
import com.zinkworks.atmservices.service.ATMSessionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ATMAuthServiceTest {

    @Mock
    CustomerAccountRepository accountRepository;

    @InjectMocks
    ATMAuthService authService;

    @Spy
    ATMSessionService atmSessionService = new ATMSessionService();

    @Test
    public void testValidateSuccessfulAccountNumber()  {
        String accountNumber = "123456789";
        mockCustomerAccount(accountNumber, "1234");
        Assertions.assertNotNull(authService.validateAccountNumber(accountNumber));
    }

    private void mockCustomerAccount(String accountNumber, String pin) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CustomerAccount account = new CustomerAccount(accountNumber, encoder.encode(pin),
                800, 200);
        Mockito.when(accountRepository.findById(accountNumber)).thenReturn(of(account));
    }

    @Test
    public void testValidateFailedAccountNumber()  {
        Mockito.when(accountRepository.findById("123456788")).thenReturn(Optional.empty());
        Assertions.assertNull(authService.validateAccountNumber("123456788"));
    }

    @Test
    public void testSuccessfulLoginWithAccountNumberAndPin() throws SessionExpiredException {
        String accountNumber = "123456789";
        String pin = "1234";
        mockCustomerAccount(accountNumber, pin);
        String sessionId = authService.validateAccountNumber(accountNumber);
        Assertions.assertTrue(authService.loginWithAccountNumberAndPin(sessionId, pin));
    }

    @Test
    public void testFailedLoginWithAccountNumberAndPin() throws SessionExpiredException {
        String accountNumber = "123456789";
        String pin = "1234";
        mockCustomerAccount(accountNumber, pin);
        String sessionId = authService.validateAccountNumber(accountNumber);
        Assertions.assertFalse(authService.loginWithAccountNumberAndPin(sessionId, "1235"));
    }

    @Test
    public void testSessionExpiredLoginWithAccountNumberAndPin() {
        Exception exception = Assertions.assertThrows(SessionExpiredException.class, () -> {
            authService.loginWithAccountNumberAndPin("3232", "1235");
        });
        Assertions.assertEquals(CommonConstants.SESSION_EXPIRED_MSG, exception.getMessage());
    }

    @Test
    public void testLogout() throws SessionExpiredException {
        String accountNumber = "123456789";
        mockCustomerAccount(accountNumber, "1234");
        String sessionId = authService.validateAccountNumber(accountNumber);
        authService.loginWithAccountNumberAndPin(sessionId, "1234");
        Assertions.assertTrue(authService.logout(sessionId));
        Exception exception = Assertions.assertThrows(SessionExpiredException.class, () -> {
            authService.logout(sessionId);
        });
        Assertions.assertEquals(CommonConstants.SESSION_EXPIRED_MSG, exception.getMessage());
    }
}
