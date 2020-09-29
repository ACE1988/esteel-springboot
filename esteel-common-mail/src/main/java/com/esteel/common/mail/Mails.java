package com.esteel.common.mail;

import java.util.List;

/**
 * @version 1.0.0
 * @ClassName Mails.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class Mails {

    private List<Mail> values;
    private int total;

    Mails(List<Mail> values, int total) {
        this.values = values;
        this.total = total;
    }

    public List<Mail> getValues() {
        return values;
    }

    public int getTotal() {
        return total;
    }
}
