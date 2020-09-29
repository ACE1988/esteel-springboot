package com.esteel.common.spreadsheet;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 * @version 1.0.0
 * @ClassName SheetStyles.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class SheetStyles {

    public static CellStyle newDateStyle(Spreadsheet spreadsheet, String format) {
        short dateFormat = spreadsheet.newDataFormat(format);
        return spreadsheet.newStyle(cellStyle -> {
            cellStyle.setDataFormat(dateFormat);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setFont(spreadsheet.getDefaultSheetStyles().bodyFont);
        });
    }

    private Spreadsheet spreadsheet;

    private Font bodyFont;
    private short dateFormat;

    private CellStyle headStyle;
    private CellStyle bodyStyle;
    private CellStyle dateStyle;

    public SheetStyles(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;

        this.bodyFont = spreadsheet.newFont(font -> {
            font.setFontName("微软雅黑");
            font.setFontHeightInPoints((short) 10);
        });
        this.dateFormat = spreadsheet.newDataFormat("yyyy-mm-dd hh:mm:ss");
        this.headStyle = spreadsheet.newStyle(cellStyle -> {
            XSSFColor foregroundColor = spreadsheet.newColor(242, 242, 242);
            ((XSSFCellStyle) cellStyle).setFillForegroundColor(foregroundColor);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFont(bodyFont);
            cellStyle.setWrapText(true);
        });
        this.bodyStyle = spreadsheet.newStyle(cellStyle -> {
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setFont(bodyFont);
        });
        this.dateStyle = spreadsheet.newStyle(cellStyle -> {
            cellStyle.setDataFormat(dateFormat);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setFont(bodyFont);
        });
    }

    public Font getBodyFont() {
        return bodyFont;
    }

    public short getDateFormat() {
        return dateFormat;
    }

    public CellStyle getHeadStyle() {
        return headStyle;
    }

    public CellStyle getBodyStyle() {
        return bodyStyle;
    }

    public CellStyle getDateStyle() {
        return dateStyle;
    }
}
