package com.zinkworks.atmservices.service;

import com.zinkworks.atmservices.dto.admin.ATMMaintenanceRequest;
import com.zinkworks.atmservices.dto.admin.AccountDetails;
import com.zinkworks.atmservices.dto.admin.AddAccountRequest;
import com.zinkworks.atmservices.dto.admin.LoadingMoneyRequest;
import com.zinkworks.atmservices.exception.ATMAdminException;
import com.zinkworks.atmservices.persistence.entity.CashInventory;
import com.zinkworks.atmservices.persistence.entity.CustomerAccount;
import com.zinkworks.atmservices.persistence.repositories.CashInventoryRepository;
import com.zinkworks.atmservices.persistence.repositories.CustomerAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ATMAdminService {

    @Autowired
    CustomerAccountRepository accountRepository;

    @Autowired
    CashInventoryRepository cashInventoryRepository;

    public void addAccounts(AddAccountRequest request) throws ATMAdminException {
        if (request.getAccountDetails() != null && !request.getAccountDetails().isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<CustomerAccount> customerAccounts = new ArrayList<>();
            for (AccountDetails accountDetails : request.getAccountDetails()) {
                validateAccountBalance(accountDetails);
                CustomerAccount account = new CustomerAccount(accountDetails.getAccountNumber(),
                        encoder.encode(accountDetails.getAccountPin()), accountDetails.getOpeningBal(),
                        accountDetails.getOverdraftLimit());
                customerAccounts.add(account);
            }
            accountRepository.saveAll(customerAccounts);
        }
    }

    public void loadMoney(LoadingMoneyRequest request) throws ATMAdminException {
        if (request.getDenominationMap() == null || request.getDenominationMap().isEmpty()) {
            throw new ATMAdminException("Invalid Request");
        }
        List<CashInventory> cashInventories = new ArrayList<>();
        for (int denomination : request.getDenominationMap().keySet()) {
            validateDenomination(denomination, request.getDenominationMap().get(denomination));
            CashInventory cashInventory = new CashInventory(denomination, request.getDenominationMap()
                    .get(denomination));
            cashInventories.add(cashInventory);
        }
        cashInventoryRepository.saveAll(cashInventories);
    }

    private void validateDenomination(int denomination, int numberOfNotes) throws ATMAdminException {
        if (denomination < 0 || numberOfNotes < 0) {
            throw new ATMAdminException("Denomination or number of notes can not be negative");
        }
    }

    private void validateAccountBalance(AccountDetails details) throws ATMAdminException {
        if (details.getOpeningBal() < 0 || details.getOverdraftLimit() < 0) {
            throw new ATMAdminException("Account Balance or Over draft limit can not be negative");
        }
    }
}
