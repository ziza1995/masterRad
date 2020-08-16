package com.tools.edutool.exceptions;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;


@ControllerAdvice
public class AppExHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class, AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<Object> handleAnyEx(Exception ex, WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage("");
        boolean isLocked = false;
        if(ex instanceof LockedException){
            errorMessage = new ErrorMessage("locked");
            isLocked= true;
        }

        return new ResponseEntity<>(isLocked ? errorMessage : ex, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}