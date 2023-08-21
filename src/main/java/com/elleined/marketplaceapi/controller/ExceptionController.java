package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.APIResponse;
import com.elleined.marketplaceapi.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleNotFoundException(ResourceNotFoundException ex) {
        var responseMessage = new APIResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<APIResponse>> handleBindException(BindException ex) {
        List<APIResponse> errors = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .map(errorMessage -> new APIResponse(HttpStatus.BAD_REQUEST, errorMessage))
                .toList();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            NotOwnedException.class,
            WeakPasswordException.class,
            PasswordNotMatchException.class,
            MobileNumberException.class,
            MalformedEmailException.class,
            AlreadExistException.class,
            HasDigitException.class,
            OrderException.class
    })
    public ResponseEntity<APIResponse> handleBadRequestExceptions(RuntimeException ex) {
        var responseMessage = new APIResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({InvalidUserCredentialException.class, NotVerifiedException.class})
    public ResponseEntity<APIResponse> handleForbiddenException(RuntimeException ex) {
        var responseMessage = new APIResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        return new ResponseEntity<>(responseMessage, HttpStatus.FORBIDDEN);
    }
}
