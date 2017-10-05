package org.cynic.excel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

public class ExcelApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Configuration.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Configuration.class);
    }
}
