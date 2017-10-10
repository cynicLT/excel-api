package org.cynic.excel.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
public class ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<String> handleException(@RequestAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) Throwable exception) {
        return ResponseEntity.
                badRequest().
                body(ExceptionUtils.getRootCause(exception).getMessage());
    }
}
