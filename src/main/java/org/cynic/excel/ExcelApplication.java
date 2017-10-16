package org.cynic.excel;

import org.springframework.boot.SpringApplication;

import java.io.IOException;

public class ExcelApplication {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Configuration.class, args);

//        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream("data/source.xlsx"));
//        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
//        XSSFRow xssfRow = xssfSheet.getRow(0);
//
//        System.out.println(xssfRow);
    }
}
