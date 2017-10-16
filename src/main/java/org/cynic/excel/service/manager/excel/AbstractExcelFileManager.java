package org.cynic.excel.service.manager.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.cynic.excel.data.CellFormat;
import org.cynic.excel.service.manager.FileManager;

import java.util.Date;
import java.util.Objects;

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

    void setCellFormat(Cell cell, CellFormat cellFormat) {
        switch (cellFormat) {
            case DATE:
                cell.setCellType(CellType.NUMERIC);
                cell.getCellStyle().setDataFormat((short) 14);
                break;
            case BOOLEAN:
                cell.setCellType(CellType.BOOLEAN);
                break;
            case FORMULA:
                cell.setCellType(CellType.FORMULA);
                break;
            case NUMERIC:
                cell.setCellType(CellType.NUMERIC);
                break;
            default:
                cell.setCellType(CellType.STRING);
        }
    }

    void setCellValue(Cell cell, CellFormat cellFormat, Object cellValue) {
        switch (cellFormat) {
            case NUMERIC:
                cell.setCellValue(toNumberValue(cellValue));
                break;
            case DATE:
                cell.setCellValue(toDateValue(cellValue));
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

    private Date toDateValue(Object cellValue) {
        return new Date(Double.class.cast(cellValue).longValue());
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
