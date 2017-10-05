package org.cynic.excel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExcelControllerAdvice {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleException(Throwable throwable) {
        return ResponseEntity.badRequest().body(throwable.getMessage());
    }
}
