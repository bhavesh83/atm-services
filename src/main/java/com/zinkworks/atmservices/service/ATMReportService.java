package com.zinkworks.atmservices.service;

import com.zinkworks.atmservices.dto.report.CashInventoryResponse;
import com.zinkworks.atmservices.dto.report.TransactionsInfo;
import com.zinkworks.atmservices.dto.report.TransactionsReport;
import com.zinkworks.atmservices.persistence.entity.CashInventory;
import com.zinkworks.atmservices.persistence.entity.Transactions;
import com.zinkworks.atmservices.persistence.entity.TransactionsDetails;
import com.zinkworks.atmservices.persistence.repositories.CashInventoryRepository;
import com.zinkworks.atmservices.persistence.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ATMReportService {

    @Autowired
    CashInventoryRepository cashInventoryRepository;

    @Autowired
    TransactionsRepository transactionsRepository;

    public CashInventoryResponse fetchCurrentCashInventory() {
        CashInventoryResponse response = new CashInventoryResponse();
        List<CashInventory> cashInventoryList = cashInventoryRepository.findAll();
        Map<Integer,Integer> denominationMap = new HashMap<>();
        int totalCash = 0;
        for (CashInventory cashInventory : cashInventoryList) {
            denominationMap.put(cashInventory.getDenomination(), cashInventory.getNumberOfNotes());
            totalCash += cashInventory.getDenomination() * cashInventory.getNumberOfNotes();
        }
        response.setDenomination(denominationMap);
        response.setTotalCash(totalCash);
        return response;
    }

    public TransactionsReport fetchAllTransactions() {
        TransactionsReport report = new TransactionsReport();
        List<TransactionsInfo> responseTransactions = new ArrayList<>();
        List<Transactions> transactionsDB =  transactionsRepository.findAll();
        for (Transactions transactions : transactionsDB) {
            TransactionsInfo transactionsInfo = new TransactionsInfo();
            transactionsInfo.setTransactionId(transactions.getTransactionId());
            transactionsInfo.setTransactionType(transactions.getTransactionType().toString());
            transactionsInfo.setAmount(transactions.getAmount());
            transactionsInfo.setDenominationPair(denominationMap(transactions.getTransactionsDetails()));
            responseTransactions.add(transactionsInfo);
        }
        report.setTransactions(responseTransactions);
        return report;
    }

    private Map<Integer,Integer> denominationMap(List<TransactionsDetails> transactionsDetailsList) {
        Map<Integer,Integer> denominationMap = new HashMap<>();
        transactionsDetailsList.forEach(transactionsDetails
                -> denominationMap.put(transactionsDetails.getDenomination(), transactionsDetails.getNumberOfNotes()));
        return denominationMap;
    }



}
