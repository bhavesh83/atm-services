package com.zinkworks.atmservices;

import com.zinkworks.atmservices.common.ErrorCodes;
import com.zinkworks.atmservices.dto.transaction.BalanceInquiryRequest;
import com.zinkworks.atmservices.dto.transaction.BalanceInquiryResponse;
import com.zinkworks.atmservices.dto.transaction.WithdrawalRequest;
import com.zinkworks.atmservices.dto.transaction.WithdrawalResponse;
import com.zinkworks.atmservices.persistence.entity.CashInventory;
import com.zinkworks.atmservices.persistence.entity.CustomerAccount;
import com.zinkworks.atmservices.persistence.entity.Transactions;
import com.zinkworks.atmservices.persistence.entity.TransactionsDetails;
import com.zinkworks.atmservices.persistence.repositories.CashInventoryRepository;
import com.zinkworks.atmservices.persistence.repositories.CustomerAccountRepository;
import com.zinkworks.atmservices.persistence.repositories.TransactionsRepository;
import com.zinkworks.atmservices.service.ATMSessionService;
import com.zinkworks.atmservices.service.ATMTransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zinkworks.atmservices.persistence.entity.TransactionType.WITHDRAWAL;
import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
public class ATMTransactionServiceTest {

    @Mock
    CashInventoryRepository cashInventoryRepository;

    @Mock
    TransactionsRepository transactionsRepository;

    @Mock
    ATMSessionService atmSessionService;

    @Mock
    CustomerAccountRepository accountRepository;

    @InjectMocks
    ATMTransactionService atmTransactionService;

    @Test
    public void testSuccessfulProcessBalanceInquiry() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CustomerAccount account = new CustomerAccount("123456789", encoder.encode("1234"),
                800, 200);
        Mockito.when(atmSessionService.isSessionIdPresent("232-433-4444-3443")).thenReturn(true);
        Mockito.when(atmSessionService.isSessionIdActive("232-433-4444-3443")).thenReturn(true);
        Mockito.when(atmSessionService.getCustomerAccount("232-433-4444-3443")).thenReturn(account);
        Mockito.when(accountRepository.findById("123456789")).thenReturn(of(account));

        BalanceInquiryRequest request = new BalanceInquiryRequest();
        request.setSessionId("232-433-4444-3443");
        BalanceInquiryResponse response = atmTransactionService.processBalanceInquiry(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(800, response.getAccountBal());
        Assertions.assertEquals(1000, response.getWithdrawableBal());
    }

    @Test
    public void testSessionExpiredProcessBalanceInquiry() {
        Mockito.when(atmSessionService.isSessionIdPresent("232-433-4444-3443")).thenReturn(true);
        Mockito.when(atmSessionService.isSessionIdActive("232-433-4444-3443")).thenReturn(false);

        BalanceInquiryRequest request = new BalanceInquiryRequest();
        request.setSessionId("232-433-4444-3443");
        BalanceInquiryResponse response = atmTransactionService.processBalanceInquiry(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getError());
        Assertions.assertEquals("ATM_106", response.getError().getErrorCode());
        Assertions.assertEquals("Session Expired", response.getError().getErrorMessage());
    }

    @Test
    public void testSuccessWithdrawalRequest() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CustomerAccount account = new CustomerAccount("123456789", encoder.encode("1234"),
                800, 200);
        Mockito.when(atmSessionService.isSessionIdPresent("232-433-4444-3443")).thenReturn(true);
        Mockito.when(atmSessionService.isSessionIdActive("232-433-4444-3443")).thenReturn(true);
        Mockito.when(atmSessionService.getCustomerAccount("232-433-4444-3443")).thenReturn(account);
        Mockito.when(accountRepository.findById("123456789")).thenReturn(of(account));
        mockCashInventory();
        List<CashInventory> cashInventories = new ArrayList<>();
        cashInventories.add(new CashInventory(50,0));
        cashInventories.add(new CashInventory(20,10));
        Mockito.when(cashInventoryRepository.saveAll(cashInventories)).thenReturn(cashInventories);
        CustomerAccount updatedAccount = new CustomerAccount("123456789", encoder.encode("1234"),
                0, 100);
        Mockito.when(accountRepository.save(account)).thenReturn(updatedAccount);
        Map<Integer,Integer> denominationMap = new HashMap<>();
        denominationMap.put(50,10);
        denominationMap.put(20,20);
        Mockito.when(transactionsRepository.save(addTransaction(900, "123456789", denominationMap, true)))
                .thenReturn(addTransaction(900, "123456789", denominationMap, 432));

        WithdrawalRequest request = new WithdrawalRequest();
        request.setSessionId("232-433-4444-3443");
        request.setAmount(900);
        WithdrawalResponse response = atmTransactionService.processCashWithdrawal(request);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getError());
        Assertions.assertNotNull(response.getTransactionId());
        Assertions.assertEquals("432", response.getTransactionId());
        Assertions.assertEquals(response.getDenominationMap(), denominationMap);
    }

    public Transactions addTransaction(int amount, String accountNumber, Map<Integer,Integer> denominationMap, int transactionId) {
        Transactions transactions = addTransaction(amount,accountNumber,denominationMap, false);
        transactions.setTransactionId(transactionId);
        return transactions;
    }

    public Transactions addTransaction(int amount, String accountNumber, Map<Integer,Integer> denominationMap, boolean beforeInsert) {
        List<TransactionsDetails> transactionsDetailsList = new ArrayList<>();
        Transactions transactions = new Transactions();
        AtomicInteger i = new AtomicInteger(0);
        denominationMap.entrySet().stream().forEach(entry -> {
            TransactionsDetails details = new TransactionsDetails(i.get(), entry.getKey(), entry.getValue(),
                    transactions);
            if (!beforeInsert) {
                i.getAndIncrement();
            }
            transactionsDetailsList.add(details);
        } );
        transactions.setTransactionType(WITHDRAWAL);
        transactions.setAmount(amount);
        transactions.setAccountNumber(accountNumber);
        transactions.setTransactionsDetails(transactionsDetailsList);
        return transactions;
    }

    private void mockCashInventory() {
        List<CashInventory> cashInventories = new ArrayList<>();
        cashInventories.add(new CashInventory(50,10));
        cashInventories.add(new CashInventory(20,30));
        cashInventories.add(new CashInventory(10,30));
        cashInventories.add(new CashInventory(5,20));
        Mockito.when(cashInventoryRepository.findAll()).thenReturn(cashInventories);
    }

    @Test
    public void testInsufficientBalWithdrawalRequest() {
        setupAccount();

        WithdrawalRequest request = new WithdrawalRequest();
        request.setSessionId("232-433-4444-3443");
        request.setAmount(1100);
        WithdrawalResponse response = atmTransactionService.processCashWithdrawal(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getError());
        Assertions.assertNotNull(response.getError().getErrorCode(), ErrorCodes.ATM_103.toString());
        Assertions.assertNotNull(response.getError().getErrorMessage(), ErrorCodes.ATM_103.getErrorMessage());
    }

    private void setupAccount() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        CustomerAccount account = new CustomerAccount("123456789", encoder.encode("1234"),
                800, 200);
        Mockito.when(atmSessionService.isSessionIdPresent("232-433-4444-3443")).thenReturn(true);
        Mockito.when(atmSessionService.isSessionIdActive("232-433-4444-3443")).thenReturn(true);
        Mockito.when(atmSessionService.getCustomerAccount("232-433-4444-3443")).thenReturn(account);
        Mockito.when(accountRepository.findById("123456789")).thenReturn(of(account));
    }

    @Test
    public void testNotMultiplesOfDenominationWithdrawalRequest() {
        setupAccount();

        WithdrawalRequest request = new WithdrawalRequest();
        request.setSessionId("232-433-4444-3443");
        request.setAmount(643);
        WithdrawalResponse response = atmTransactionService.processCashWithdrawal(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getError());
        Assertions.assertNotNull(response.getError().getErrorCode(), ErrorCodes.ATM_109.toString());
        Assertions.assertNotNull(response.getError().getErrorMessage(), ErrorCodes.ATM_109.getErrorMessage());
    }

    @Test
    public void testNotEnoughCashInATM() {
        setupAccount();
        List<CashInventory> cashInventories = new ArrayList<>();
        cashInventories.add(new CashInventory(10,30));
        cashInventories.add(new CashInventory(5,20));
        Mockito.when(cashInventoryRepository.findAll()).thenReturn(cashInventories);

        WithdrawalRequest request = new WithdrawalRequest();
        request.setSessionId("232-433-4444-3443");
        request.setAmount(500);
        WithdrawalResponse response = atmTransactionService.processCashWithdrawal(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getError());
        Assertions.assertNotNull(response.getError().getErrorCode(), ErrorCodes.ATM_108.toString());
        Assertions.assertNotNull(response.getError().getErrorMessage(), ErrorCodes.ATM_108.getErrorMessage());
    }
}
