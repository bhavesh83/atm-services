package com.zinkworks.atmservices.common;

public enum ErrorCodes {

    ATM_101("Incorrect Account Number"),
    ATM_103("Account doesn't have enough balance"),
    ATM_104("Internal Error, please contact customer support team"),
    ATM_105("Internal Error, please contact technical support team"),
    ATM_106("Session Expired"),
    ATM_107("Invalid Session Id"),
    ATM_108("ATM doesn't have enough cash available"),
    ATM_109("Withdrawal amount should be in multiples of available denomination ");

    private String errorMessage;

    ErrorCodes(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
