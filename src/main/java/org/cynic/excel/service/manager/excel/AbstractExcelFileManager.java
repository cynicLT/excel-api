package org.cynic.excel.service.manager.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
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
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return CellFormat.DATE;
                } else {
                    return CellFormat.NUMERIC;
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return CellFormat.BOOLEAN;
            case Cell.CELL_TYPE_FORMULA:
                return CellFormat.FORMULA;
            default:
                return CellFormat.STRING;
        }
    }

    void setCellStyle(Cell cell, String format) {
        CreationHelper creationHelper =  cell.getRow().getSheet().getWorkbook().getCreationHelper();



        cell.getCellStyle().setDataFormat(creationHelper.createDataFormat().getFormat(format));

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
