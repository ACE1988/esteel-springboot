package com.esteel.common.spreadsheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @version 1.0.0
 * @ClassName SheetParser.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class SheetParser {

    private Spreadsheet spreadsheet;

    private Sheet sheet;

    public SheetParser(Spreadsheet spreadsheet, Sheet sheet) {
        this.spreadsheet = spreadsheet;
        this.sheet = sheet;
    }

    public int getLastRowNo() {
        return sheet.getLastRowNum();
    }

    public <T> T getValue(int row, int column) {
        Cell cell = sheet.getRow(row).getCell(column);
        return this.spreadsheet.getTypedValue(cell);
    }
}
