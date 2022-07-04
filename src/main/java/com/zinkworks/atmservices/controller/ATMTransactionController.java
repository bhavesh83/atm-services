package com.zinkworks.atmservices.controller;

import com.zinkworks.atmservices.dto.transaction.BalanceInquiryRequest;
import com.zinkworks.atmservices.dto.transaction.BalanceInquiryResponse;
import com.zinkworks.atmservices.dto.transaction.WithdrawalRequest;
import com.zinkworks.atmservices.dto.transaction.WithdrawalResponse;
import com.zinkworks.atmservices.service.ATMTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atm/transaction")
public class ATMTransactionController {

    Logger logger = LoggerFactory.getLogger(ATMTransactionController.class);

    @Autowired
    ATMTransactionService atmTransactionService;

    @PostMapping("/cash/withdrawal")
    public WithdrawalResponse withdrawalMoney(@RequestBody WithdrawalRequest request)  {
        logger.info("Received withdrawal request " + request);
        return atmTransactionService.processCashWithdrawal(request);
    }

    @PostMapping("/balance/inquiry")
    public BalanceInquiryResponse balanceInquiry(@RequestBody BalanceInquiryRequest request) {
        logger.info("Received Balance Enquiry request " + request);
        return atmTransactionService.processBalanceInquiry(request);
    }
}
