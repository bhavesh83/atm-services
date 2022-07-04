package com.zinkworks.atmservices.service;

import com.zinkworks.atmservices.controller.ATMAdminController;
import com.zinkworks.atmservices.dto.error.Error;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static com.zinkworks.atmservices.common.ErrorCodes.*;
import static com.zinkworks.atmservices.persistence.entity.TransactionType.WITHDRAWAL;
import static java.util.Comparator.comparingInt;

@Service
public class ATMTransactionService {

    Logger logger = LoggerFactory.getLogger(ATMTransactionService.class);

    @Autowired
    CustomerAccountRepository accountRepository;

    @Autowired
    CashInventoryRepository cashInventoryRepository;

    @Autowired
    TransactionsRepository transactionsRepository;

    @Autowired
    ATMSessionService sessionService;

    public BalanceInquiryResponse processBalanceInquiry(BalanceInquiryRequest request) {
        BalanceInquiryResponse response = new BalanceInquiryResponse();
        if (isError(isValidSession(request.getSessionId()), response)) {
            logger.debug("Session Id not valid for balance inquiry");
            return response;
        }
        if (sessionService.isSessionIdActive(request.getSessionId())) {
            CustomerAccount accountFromCache = sessionService.getCustomerAccount(request.getSessionId());
            CustomerAccount accountFromDB = accountRepository.findById(accountFromCache.getAccountNumber()).get();
            response.setAccountBal(accountFromDB.getBalance());
            response.setWithdrawableBal(accountFromDB.getBalance() + accountFromDB.getOverDraftLimit());
            logger.debug("Session Id is valid and balance was fetched successfully");
        } else {
            response.setError(new Error(ATM_106.toString(), ATM_106.getErrorMessage()));
        }
        return response;
    }

    public WithdrawalResponse processCashWithdrawal(WithdrawalRequest request)  {
        WithdrawalResponse response = new WithdrawalResponse();
        if (isError(isValidSession(request.getSessionId()), response)) {
            logger.debug("Session Id not valid for cash withdrawal ");
            return response;
        }
        if(sessionService.isSessionIdActive(request.getSessionId())) {
            CustomerAccount accountFromCache = sessionService.getCustomerAccount(request.getSessionId());
            CustomerAccount accountFromDB = accountRepository.findById(accountFromCache.getAccountNumber()).get();
            int withdrawalReqAmount = request.getAmount();
            if (isError(checkForEnoughAccountBalance(accountFromDB, withdrawalReqAmount), response)) {
                return response;
            }
            List<CashInventory> cashInventories = cashInventoryRepository.findAll();
            if (isError(isCashInventoryEmpty(cashInventories),response)) {
                return response;
            }
            Collections.sort(cashInventories, comparingInt(CashInventory::getDenomination).reversed());
            if (isError(isWithdrawalAmountInMultiplesOfDenomination(withdrawalReqAmount, cashInventories), response)) {
                return response;
            }
            Map<Integer,Integer> withdrawalMoneyMap = new HashMap<>();
            boolean isWithdrawalPossible = calculateDenominationForWithdrawal(withdrawalReqAmount, cashInventories,
                    withdrawalMoneyMap);
            if (!isWithdrawalPossible) {
                response.setError(new Error(ATM_108.toString(), ATM_108.getErrorMessage()));
                return response;
            }
            int transactionId = updateBankAccountAndATMInventory(accountFromDB.getAccountNumber(), withdrawalReqAmount,
                    withdrawalMoneyMap, cashInventories);
            response.setTransactionId(String.valueOf(transactionId));
            response.setDenominationMap(withdrawalMoneyMap);
            logger.debug("Cash withdrawal request was successful");
        } else {
            response.setError(new Error(ATM_106.toString(), ATM_106.getErrorMessage()));
        }
        return response;
    }

    @Transactional
    public int updateBankAccountAndATMInventory(String accountNumber, int withdrawalAmount,
                                                 Map<Integer,Integer> withdrawalMoneyMap,
                                                 List<CashInventory> cashInventories) {
        updateBankAccount(accountNumber, withdrawalAmount);
        updateATMInventory(withdrawalMoneyMap, cashInventories);
        return addTransaction(withdrawalAmount, accountNumber, withdrawalMoneyMap);
    }

    public int addTransaction(int amount, String accountNumber, Map<Integer,Integer> denominationMap) {
        List<TransactionsDetails> transactionsDetailsList = new ArrayList<>();
        Transactions transactions = new Transactions();
        denominationMap.entrySet().stream().forEach(entry -> {
            TransactionsDetails details = new TransactionsDetails();
            details.setDenomination(entry.getKey());
            details.setNumberOfNotes(entry.getValue());
            details.setTransactions(transactions);
            transactionsDetailsList.add(details);
        } );
        transactions.setTransactionType(WITHDRAWAL);
        transactions.setAmount(amount);
        transactions.setAccountNumber(accountNumber);
        transactions.setTransactionsDetails(transactionsDetailsList);
        return transactionsRepository.save(transactions).getTransactionId();
    }

    private void updateATMInventory(Map<Integer,Integer> withdrawalMoneyMap, List<CashInventory> cashInventories) {
        List<CashInventory> updatedCashInventories = new ArrayList<>();
        for (CashInventory cashInventory : cashInventories) {
            if (withdrawalMoneyMap.containsKey(cashInventory.getDenomination())) {
                cashInventory.setNumberOfNotes(
                        cashInventory.getNumberOfNotes() - withdrawalMoneyMap.get(cashInventory.getDenomination()));
                updatedCashInventories.add(cashInventory);
            }
        }
        cashInventoryRepository.saveAll(updatedCashInventories);
    }

    private void updateBankAccount(String accountNumber, int withdrawalAmount) {
        CustomerAccount account = accountRepository.findById(accountNumber).get();
        if (withdrawalAmount > account.getBalance()) {
            int amountWithdrawalfromOD = withdrawalAmount-account.getBalance();
            account.setOverDraftLimit(account.getOverDraftLimit()-amountWithdrawalfromOD);
            withdrawalAmount -= amountWithdrawalfromOD;
        }
        account.setBalance(account.getBalance()-withdrawalAmount);
        accountRepository.save(account);
    }

    private boolean calculateDenominationForWithdrawal(int withdrawalReqAmount, List<CashInventory> cashInventories,
                                                       Map<Integer, Integer> withdrawalMoneyMap) {
        for (CashInventory cashInventory : cashInventories) {
            if (cashInventory.getNumberOfNotes() > 0 && withdrawalReqAmount >= cashInventory.getDenomination()) {
                int numberOfRequiredNotes = withdrawalReqAmount / cashInventory.getDenomination();
                int actualNumberOfNotes = Math.min(numberOfRequiredNotes, cashInventory.getNumberOfNotes());
                withdrawalMoneyMap.put(cashInventory.getDenomination(), actualNumberOfNotes);
                withdrawalReqAmount -= cashInventory.getDenomination() * actualNumberOfNotes;
                if (withdrawalReqAmount == 0) {
                    break;
                }
            }
        }
        return withdrawalReqAmount == 0;
    }

    private Error checkForEnoughAccountBalance(CustomerAccount account, int withdrawalReqAmount) {
        Error error = null;
        if (withdrawalReqAmount > account.getBalance() + account.getOverDraftLimit()) {
            error = new Error(ATM_103.toString(), ATM_103.getErrorMessage());
        }
        return error;
    }

    private Error isValidSession(String sessionId) {
        Error error = null;
        if (sessionId == null || !sessionService.isSessionIdPresent(sessionId)) {
            error = new Error(ATM_107.toString(), ATM_107.getErrorMessage());
        }
        return error;
    }

    private boolean isError(Error error, WithdrawalResponse response) {
        if (error != null) {
            response.setError(error);
            return true;
        }
        return false;
    }

    private boolean isError(Error error, BalanceInquiryResponse response) {
        if (error != null) {
            response.setError(error);
            return true;
        }
        return false;
    }

    private Error isCashInventoryEmpty(List<CashInventory> cashInventories) {
        Error error = null;
        if (cashInventories == null || cashInventories.isEmpty()) {
            error = new Error(ATM_108.toString(), ATM_108.getErrorMessage());
        }
        return error;
    }

    private Error isWithdrawalAmountInMultiplesOfDenomination(int withdrawalAmount, List<CashInventory> cashInventories) {
        Error error = null;
        if (withdrawalAmount % cashInventories.get(cashInventories.size()-1).getDenomination() != 0) {
            error = new Error(ATM_109.toString(), ATM_109.getErrorMessage());
        }
        return error;
    }


}
