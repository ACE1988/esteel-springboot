package com.esteel.common.spreadsheet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.poi.ss.usermodel.CellType.*;

/**
 * @version 1.0.0
 * @ClassName Spreadsheet.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Slf4j
public class Spreadsheet {

    public static Spreadsheet newSpreadsheet() {
        return newSpreadsheet(false);
    }

    public static Spreadsheet newSpreadsheet(boolean streaming) {
        if (streaming) {
            return new Spreadsheet(new SXSSFWorkbook());
        }
        return new Spreadsheet(new XSSFWorkbook());
    }

    public enum Type {
        XLS("xls"), XLSX("xlsx");

        private String value;

        Type(String value) {
            this.value = value;
        }

        public static Type forType(String value) {
            for (Type type : Type.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    private Workbook workbook;
    private boolean streaming;

    private CreationHelper creationHelper;

    private DataFormat dataFormat;

    private SheetStyles defaultSheetStyles;

    public Spreadsheet(InputStream in, Type type) {
//        Assert.notNull(type, "Type can not be null");
        try {
            if (type == Type.XLS) {
                workbook = new HSSFWorkbook(in);
            } else if (type == Type.XLSX) {
                workbook = new XSSFWorkbook(in);
            }
            streaming = false;
            creationHelper = workbook.getCreationHelper();
        } catch (IOException e) {
            log.error("Read spreadsheet error!", e);
        }
    }

    public Spreadsheet(Workbook workbook) {
//        Assert.notNull(workbook, "Workbook can not be null");
        this.workbook = workbook;
        this.streaming = SXSSFWorkbook.class.isInstance(workbook);
        this.creationHelper = workbook.getCreationHelper();
        this.defaultSheetStyles = new SheetStyles(this);
    }

    public Object read() {
        return parse();
    }

    public SheetBuilder newSheet(String name) {
        Sheet sheet = getWorkbook().createSheet(name);
        return new SheetBuilder(this, sheet);
    }

    public SheetParser getSheet(String name) {
        Sheet sheet = getWorkbook().getSheet(name);
        return new SheetParser(this, sheet);
    }

    public SheetParser getSheet(int index) {
        Sheet sheet = getWorkbook().getSheetAt(index);
        return new SheetParser(this, sheet);
    }

    public short newDataFormat(String format) {
        return getDataFormat().getFormat(format);
    }

    public Font newFont(Consumer<Font> consumer) {
        Font font = getWorkbook().createFont();
        consumer.accept(font);
        return font;
    }

    public CellStyle newStyle(Consumer<CellStyle> consumer) {
        CellStyle style = getWorkbook().createCellStyle();
        consumer.accept(style);
        return style;
    }

    public XSSFColor newColor(int red, int green, int blue) {
        return new XSSFColor(new java.awt.Color(red, green, blue));
    }

    public SheetStyles getDefaultSheetStyles() {
        return defaultSheetStyles;
    }

    public void write(OutputStream out) {
        try {
            getWorkbook().write(out);
        } catch (IOException e) {
            log.error("Write spreadsheet error!", e);
        } finally {
            Workbook temp = getWorkbook();
            if (temp instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) temp).dispose();
            }
        }
    }

    public <T> List<T> mapRows(Function<Row, T> function) {
        Sheet sheet = this.workbook.getSheetAt(0);
        return mapRows(sheet, function);
    }

    public <T> List<T> mapRows(Sheet sheet, Function<Row, T> function) {
        List<T> all = Lists.newArrayList();
        sheet.forEach(row -> {
            T t = function.apply(row);
            if (t != null) {
                all.add(t);
            }
        });
        return all;
    }

    public void forEachCells(Row row, BiConsumer<Integer, Optional<Object>> consumer) {
        row.forEach(cell -> {
            Object value = getValue(cell);
            consumer.accept(cell.getColumnIndex(), Optional.ofNullable(value));
        });
    }

    protected Object parse() {
        return null;
    }

    protected Workbook getWorkbook() {
        return workbook;
    }

    protected DataFormat getDataFormat() {
        if (dataFormat == null) {
            dataFormat = this.creationHelper.createDataFormat();
        }
        return this.dataFormat;
    }

    protected Cell createCell(Row row, int column, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(style);
        return cell;
    }

    protected void setFormula(Cell cell, String formula) {
        if (!streaming) {
            cell.setCellFormula(formula);
        }
    }

    protected void addDropdown(XSSFSheet sheet, int row, int column, String[] values) {
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        XSSFDataValidationConstraint dvConstraint =
                (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(values);
        CellRangeAddressList addressList = new CellRangeAddressList(row, row, column, column);
        XSSFDataValidation validation =
                (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    protected String sumCells(String column, List<String> rows) {
        StringBuilder buffer = new StringBuilder();
        for (String row : rows) {
            buffer.append(column).append(row).append(":");
        }
        if (buffer.length() > 0){
            buffer.delete(buffer.length() - 1, buffer.length());
        }
        return buffer.toString();
    }

    protected String getStringValue(Sheet sheet, int row, int column) {
        Object value = getValue(sheet, row, column, false);
        if (!(value instanceof String)){
            return "";}
        return (String) value;
    }

    protected String getStringValue(Sheet sheet, int row, int column, boolean needed) {
        Object value = getValue(sheet, row, column, needed);
        if (!(value instanceof String)){
            return "";
        }
        return (String) value;
    }

    protected String getStringValue(Row row, int column) {
        Object value = getValue(row, column);
        if (!(value instanceof String)){
            return "";
        }
        return (String) value;
    }

    protected Object getValue(Sheet sheet, int row, int column) {
        return getValue(sheet, row, column, false);
    }

    protected Object getValue(Sheet sheet, int row, int column, boolean needed) {
        Cell cell = sheet.getRow(row).getCell(column);
        return getValue(cell);
    }

    protected Object getStringValue2(Sheet sheet, int row, int column) {
        return getStringValue2(sheet, row, column, false);
    }

    protected Object getStringValue2(Sheet sheet, int row, int column, boolean needed) {
        Cell cell = sheet.getRow(row).getCell(column);
        cell.setCellType(STRING);
        return getValue(cell);
    }

    protected Object getValue(Row row, int column) {
        Cell cell = row.getCell(column);
        return getValue(cell);
    }

    protected Object getStringValue2(Row row, int column) {
        Cell cell = row.getCell(column);
        cell.setCellType(STRING);
        return getValue(cell);
    }

    protected <T> T getTypedValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            for (Map.Entry<Predicate<Cell>, Function<Cell, TypedValue>> entry : VALUE_FUNCTIONS.entrySet()) {
                if (entry.getKey().test(cell)) {
                    TypedValue value = entry.getValue().apply(cell);
                    //noinspection unchecked
                    return (T) value.value();
                }
            }
        } catch (Exception e) {
            log.error("Get value from cell {} error", cell, e);
        }
        return null;
    }

    private static final Map<Predicate<Cell>, Function<Cell, TypedValue>> VALUE_FUNCTIONS = ImmutableMap.of(
            cell -> cell.getCellTypeEnum() == STRING, cell -> () -> cell.getRichStringCellValue().getString(),
            cell -> cell.getCellTypeEnum() == NUMERIC && DateUtil.isCellDateFormatted(cell), cell -> cell::getDateCellValue,
            cell -> cell.getCellTypeEnum() == NUMERIC && !DateUtil.isCellDateFormatted(cell), cell -> cell::getNumericCellValue,
            cell -> cell.getCellTypeEnum() == BOOLEAN, cell -> cell::getBooleanCellValue,
            cell -> cell.getCellTypeEnum() == FORMULA, cell -> cell::getCellFormula
    );

    private interface TypedValue<T> {
        T value();
    }

    protected Object getValue(Cell cell) {
        if (cell == null){
            return null;
        }
        try {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    return cell.getRichStringCellValue().getString();
                case NUMERIC:
                    return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell
                            .getNumericCellValue();
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
