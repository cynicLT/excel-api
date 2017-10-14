package org.cynic.excel.service.manager.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.cynic.excel.data.FieldFormat;
import org.cynic.excel.service.manager.FileManager;

import java.util.Date;
import java.util.Objects;

abstract class AbstractExcelFileManager extends FileManager {

    Object toFieldValue(FieldFormat cellType, Cell cell) {
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

    FieldFormat getFieldFormat(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                if (!Objects.isNull(cell.getCellStyle().getDataFormatString())) {
                    return FieldFormat.DATE;
                } else {
                    return FieldFormat.NUMERIC;
                }
            case BOOLEAN:
                return FieldFormat.BOOLEAN;
            case FORMULA:
                return FieldFormat.FORMULA;
            case BLANK:
                if (!Objects.isNull(cell.getCellStyle().getDataFormatString())) {
                    return FieldFormat.DATE;
                } else {
                    return FieldFormat.STRING;
                }
            default:
                return FieldFormat.STRING;
        }
    }

    void setCellValue(Cell hssfCell, Pair<FieldFormat, ?> fieldTypePair) {
        if (!Objects.isNull(fieldTypePair.getValue())) {
            switch (getFieldFormat(hssfCell)) {
                case NUMERIC:
                    hssfCell.setCellValue(toNumberValue(fieldTypePair));
                    break;
                case DATE:
                    hssfCell.setCellValue(toDateValue(fieldTypePair));
                    break;
                case BOOLEAN:
                    hssfCell.setCellValue(toBooleanValue(fieldTypePair));
                    break;
                case FORMULA:
                    hssfCell.setCellFormula(toStringValue(fieldTypePair));
                    break;
                default:
                    hssfCell.setCellValue(toStringValue(fieldTypePair));
            }
        } else {
            hssfCell.setCellValue(toStringValue(fieldTypePair));
        }
    }

    private Date toDateValue(Pair<FieldFormat, ?> fieldTypePair) {
        return Date.class.cast(fieldTypePair.getValue());
    }

    private String toStringValue(Pair<FieldFormat, ?> fieldTypePair) {
        return Objects.toString(fieldTypePair.getValue(), StringUtils.EMPTY);
    }

    private Boolean toBooleanValue(Pair<FieldFormat, ?> fieldTypePair) {
        return Boolean.class.cast(fieldTypePair.getValue());
    }

    private Double toNumberValue(Pair<FieldFormat, ?> fieldTypePair) {
        return Double.class.cast(fieldTypePair.getValue());

    }
}
