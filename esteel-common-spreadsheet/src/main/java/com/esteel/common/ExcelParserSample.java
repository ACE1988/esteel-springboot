package com.esteel.common;

import com.hankcs.hanlp.HanLP;
import com.esteel.common.spreadsheet.SheetParser;
import com.esteel.common.spreadsheet.Spreadsheet;
import com.esteel.common.spreadsheet.Spreadsheet.Type;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExcelParserSample {

    public static void main(String[] args) {
//        String text = HanLP.convertToSimplifiedChinese("在职");
//        System.out.println(text);
        try {
            InputStream in = Files.newInputStream(Paths.get("4泰州優才.xlsx"));
            Spreadsheet spreadsheet = new Spreadsheet(in, Type.XLSX);
//            SheetParser parser = spreadsheet.getSheet(0);
            SheetParser parser = spreadsheet.getSheet("在職");

            int lastRowNo = parser.getLastRowNo();
            for (int i = 3; i <= lastRowNo; i ++) {
                String value = parser.getValue(i, 2);
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                String simple = HanLP.convertToSimplifiedChinese(value);
                System.out.println((i - 2) + ": " + simple);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
