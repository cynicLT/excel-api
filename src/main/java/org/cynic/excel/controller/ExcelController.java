package org.cynic.excel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ExcelController {

    @GetMapping
    public String test() {
        return LocalDateTime.now().toString();
    }
}
