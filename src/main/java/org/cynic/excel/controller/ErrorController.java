package org.cynic.excel.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@RestController
public class ErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);

    @RequestMapping(path = "/error")
    public ResponseEntity<String> handleException(@RequestAttribute(required = false, name = WebUtils.ERROR_EXCEPTION_ATTRIBUTE) Throwable throwable,
                                                  @RequestAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE) String errorMessage) {
        Throwable exception = Optional.ofNullable(throwable).orElse(new IllegalArgumentException(errorMessage));

        logError(exception);

        return ResponseEntity.
                badRequest().
                body(Optional.ofNullable(ExceptionUtils.getRootCause(exception)).orElse(exception).getMessage());
    }

    private void logError(Throwable exception) {
        LOGGER.error("", exception);
    }
}
