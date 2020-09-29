package com.esteel.common.spreadsheet;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * @version 1.0.0
 * @ClassName RowBuilder.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class RowBuilder {

    private Spreadsheet spreadsheet;
    private SheetBuilder sheet;

    private Row row;
    private CellStyle defaultStyle;

    private int index;

    public RowBuilder(Spreadsheet spreadsheet, SheetBuilder sheet, Row row, CellStyle defaultStyle) {
//        Assert.notNull(spreadsheet, "Spreadsheet can not be null");
//        Assert.notNull(sheet, "Sheet can not be null");
//        Assert.notNull(row, "Row can not be null");
//        Assert.notNull(defaultStyle, "Default style can not be null");
        this.spreadsheet = spreadsheet;
        this.sheet = sheet;
        this.row = row;
        this.defaultStyle = defaultStyle;
        this.index = 0;
    }

    public RowBuilder style(CellStyle style) {
        defaultStyle = style;
        return this;
    }

    public RowBuilder skip() {
        return this.skip(0);
    }

    public RowBuilder skip(int cells) {
        index = sheet.addCell(index, cells);
        return this;
    }

    public RowBuilder blank() {
        return this.blank(1);
    }

    public RowBuilder blank(int cells) {
        for (int i = 0; i < cells; i++) {
            cell("");
        }
        return this;
    }

    public RowBuilder digit(Number digit) {
        digit(digit, null);
        return this;
    }

    public RowBuilder digit(Number digit, CellStyle style) {
        Cell cell = spreadsheet.createCell(row, index, style == null ? defaultStyle : style);
        cell.setCellValue(digit.doubleValue());
        index = sheet.addCell(index);
        return this;
    }

    public RowBuilder cells(String... values) {
        Stream.of(values).forEach(this::cell);
        return this;
    }

    public RowBuilder cell(String value) {
        return text(value == null ? "" : value, null);
    }

    public RowBuilder text(String value, CellStyle style) {
        Cell cell = spreadsheet.createCell(row, index, style == null ? defaultStyle : style);
        cell.setCellValue(value == null ? "" : value);
        index = sheet.addCell(index);
        return this;
    }

    public RowBuilder decimal(BigDecimal value) {
        return decimal(value.doubleValue(), null);
    }

    public RowBuilder decimal(BigDecimal value, CellStyle style) {
        return decimal(value.doubleValue(), style);
    }

    public RowBuilder date(Date value) {
        return date(value, spreadsheet.getDefaultSheetStyles().getDateStyle());
    }

    public RowBuilder date(Date value, CellStyle style) {
        if (value == null) {
            return cell("");
        }
        Cell cell = spreadsheet.createCell(row, index, style == null ? defaultStyle : style);
        cell.setCellValue(value);
        index = sheet.addCell(index);
        return this;
    }

    public RowBuilder sum(String columnPrefix, List<String> rows) {
        return sum(columnPrefix, rows, null);
    }

    public RowBuilder sum(String columnPrefix, List<String> rows, CellStyle style) {
        Cell cell = spreadsheet.createCell(row, index, style == null ? defaultStyle : style);
        spreadsheet.setFormula(cell, "SUM(" + spreadsheet.sumCells(columnPrefix, rows) + ")");
        index = sheet.addCell(index);
        return this;
    }

    public RowBuilder sum(String startRow, String endRow) {
        return sum(startRow, endRow, null);
    }

    public RowBuilder sum(String startRow, String endRow, CellStyle style) {
        if (StringUtils.isBlank(startRow) || StringUtils.isBlank(endRow))
            return this;
        Cell cell = spreadsheet.createCell(row, index, style == null ? defaultStyle : style);
        spreadsheet.setFormula(cell, String.format("SUM(%s:%s)", startRow, endRow));
        index = sheet.addCell(index);
        return this;
    }

    protected RowBuilder decimal(double value) {
        return decimal(value, null);
    }

    protected RowBuilder decimal(double value, CellStyle style) {
        Cell cell = spreadsheet.createCell(row, index, style == null ? defaultStyle : style);
        cell.setCellValue(value);
        index = sheet.addCell(index);
        return this;
    }
}
