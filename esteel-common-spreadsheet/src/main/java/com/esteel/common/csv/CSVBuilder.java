package com.esteel.common.csv;

import com.google.common.collect.Lists;
import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * @version 1.0.0
 * @ClassName CSVBuilder.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
@Slf4j
public class CSVBuilder {

    private List<String[]> rows;

    public CSVBuilder() {
        this.rows = Lists.newArrayList();
    }

    public CSVBuilder newRow(String... values) {
        if (values.length > 0) {
            this.rows.add(values);
        }
        return this;
    }

    public void build(OutputStream out) {
        CsvWriter csvWriter = new CsvWriter();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        PrintWriter printWriter = new PrintWriter(writer);
        try (CsvAppender appender = csvWriter.append(printWriter)) {
            printWriter.print('\uFEFF');
            for (String[] values : rows) {
                appender.appendLine(values);
            }
        } catch (Exception e) {
            log.error("Write csv error.", e);
        }
    }
}
