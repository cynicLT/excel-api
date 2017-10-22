package org.cynic.excel.service.manager.excel;

import org.apache.poi.ss.usermodel.*;
import org.cynic.excel.data.CellFormat;
import org.cynic.excel.service.manager.FileManager;

import java.util.*;

abstract class AbstractExcelFileManager implements FileManager {

    Object getCellValue(CellFormat cellType, Cell cell) {
        switch (cellType) {
            case FORMULA:
                return cell.getCellFormula();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case DATE:
                return cell.getDateCellValue();
            default:
                return cell.getStringCellValue();
        }
    }

    CellFormat getCellFormat(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return CellFormat.DATE;
                } else {
                    return CellFormat.NUMERIC;
                }
            case BOOLEAN:
                return CellFormat.BOOLEAN;
            case FORMULA:
                return CellFormat.FORMULA;
            default:
                return CellFormat.STRING;
        }
    }


    void setCellValue(Cell cell, CellFormat cellFormat, Object cellValue) {
        switch (cellFormat) {
            case NUMERIC:
                cell.setCellValue(toNumberValue(cellValue));
                break;
            case DATE:
                cell.setCellValue(toCalendar(cellValue));
                break;
            case BOOLEAN:
                cell.setCellValue(toBooleanValue(cellValue));
                break;
            case FORMULA:
                cell.setCellFormula(toStringValue(cellValue));
                break;
            default:
                cell.setCellValue(toStringValue(cellValue));
        }
    }

    void setCellStyle(Workbook workbook, Cell cell, String format) {
        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));

        copyCellStyle(cell.getCellStyle(), cellStyle);
        cell.setCellStyle(cellStyle);
    }

    private void copyCellStyle(CellStyle from, CellStyle to) {
        to.setBorderBottom(from.getBorderBottomEnum());
        to.setBorderLeft(from.getBorderLeftEnum());
        to.setBorderRight(from.getBorderRightEnum());
        to.setBorderTop(from.getBorderTopEnum());
        to.setTopBorderColor(from.getTopBorderColor());
        to.setBottomBorderColor(from.getBottomBorderColor());
        to.setRightBorderColor(from.getRightBorderColor());
        to.setLeftBorderColor(from.getLeftBorderColor());
        to.setFillBackgroundColor(from.getFillBackgroundColor());
        to.setFillForegroundColor(from.getFillForegroundColor());
        to.setFillPattern(from.getFillPatternEnum());
        to.setIndention(from.getIndention());
        to.setShrinkToFit(from.getShrinkToFit());
        to.setRotation(from.getRotation());
        to.setAlignment(from.getAlignmentEnum());
        to.setVerticalAlignment(from.getVerticalAlignmentEnum());
        to.setWrapText(from.getWrapText());
    }

    private Calendar toCalendar(Object cellValue) {
        Calendar result = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
        result.setTime(Date.class.cast(cellValue));

        return result;
    }

    private String toStringValue(Object cellValue) {
        return Objects.toString(cellValue);
    }

    private Boolean toBooleanValue(Object cellValue) {
        return Boolean.class.cast(cellValue);
    }

    private Double toNumberValue(Object cellValue) {
        return Double.class.cast(cellValue);
    }
}
