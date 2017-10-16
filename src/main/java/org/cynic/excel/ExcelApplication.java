package org.cynic.excel;

import org.springframework.boot.SpringApplication;

import java.io.IOException;

public class ExcelApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Configuration.class, args);
    }
}
