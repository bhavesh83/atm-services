package com.zinkworks.atmservices.controller;

import com.zinkworks.atmservices.dto.admin.AddAccountRequest;
import com.zinkworks.atmservices.dto.admin.LoadingMoneyRequest;
import com.zinkworks.atmservices.exception.ATMAdminException;
import com.zinkworks.atmservices.service.ATMAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm/admin")
public class ATMAdminController {

    Logger logger = LoggerFactory.getLogger(ATMAdminController.class);

    public static final String ADD_ACCOUNT_SUCCESS_MSG = "Account details were added successfully";
    public static final String LOAD_CASH_MONEY_MSG = "Cash is loaded into ATM successfully";

    @Autowired
    ATMAdminService adminService;

    @PostMapping("/add/account")
    @ResponseStatus(HttpStatus.OK)
    public String addAccounts(@RequestBody AddAccountRequest addAccountRequest) throws ATMAdminException {
        adminService.addAccounts(addAccountRequest);
        logger.info("Successfully added requested accounts ");
        return ADD_ACCOUNT_SUCCESS_MSG;
    }

    @PostMapping("/load/cash")
    @ResponseStatus(HttpStatus.OK)
    public String loadCash(@RequestBody LoadingMoneyRequest request) throws ATMAdminException{
        adminService.loadMoney(request);
        logger.info("Successfully added cash detailed ");
        return LOAD_CASH_MONEY_MSG;
    }
}
