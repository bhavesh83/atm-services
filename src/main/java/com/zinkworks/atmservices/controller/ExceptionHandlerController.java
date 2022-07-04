package com.zinkworks.atmservices.controller;

import com.zinkworks.atmservices.common.ErrorCodes;
import com.zinkworks.atmservices.dto.error.Error;
import com.zinkworks.atmservices.exception.ATMAdminException;
import com.zinkworks.atmservices.exception.InvalidSessionException;
import com.zinkworks.atmservices.exception.SessionExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Error> handleError
            (Exception ex)
    {
        Error error = new Error(ErrorCodes.ATM_104.toString(), ErrorCodes.ATM_104.getErrorMessage());
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(ATMAdminException.class)
    public final ResponseEntity<Error> handleError
            (ATMAdminException ex)
    {
        Error error = new Error(ErrorCodes.ATM_105.toString(), ErrorCodes.ATM_105.getErrorMessage());
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(SessionExpiredException.class)
    public final ResponseEntity<Error> handleError
            (SessionExpiredException ex)
    {
        Error error = new Error(ErrorCodes.ATM_106.toString(), ErrorCodes.ATM_106.getErrorMessage());
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidSessionException.class)
    public final ResponseEntity<Error> handleError
            (InvalidSessionException ex)
    {
        Error error = new Error(ErrorCodes.ATM_107.toString(), ErrorCodes.ATM_107.getErrorMessage());
        return new ResponseEntity<>(error, HttpStatus.OK);
    }
}
