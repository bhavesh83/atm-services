package com.zinkworks.atmservices.controller;

import com.zinkworks.atmservices.dto.report.CashInventoryResponse;
import com.zinkworks.atmservices.dto.report.TransactionsReport;
import com.zinkworks.atmservices.service.ATMReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atm/report")
public class ATMReportController {

    @Autowired
    ATMReportService atmReportService;

    @GetMapping("/cash/inventory")
    public CashInventoryResponse fetchCashInventory() {
        return atmReportService.fetchCurrentCashInventory();
    }

    @GetMapping("transactions")
    public TransactionsReport fetchTransactions() {
        return atmReportService.fetchAllTransactions();
    }

}
