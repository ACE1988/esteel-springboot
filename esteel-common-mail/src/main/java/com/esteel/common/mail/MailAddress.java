package com.esteel.common.mail;

/**
 * @version 1.0.0
 * @ClassName MailAddress.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class MailAddress {

    private String address;
    private String personal;

    private String toString;

    public MailAddress(String address, String personal, String toString) {
        this.address = address;
        this.personal = personal;
        this.toString = toString;
    }

    public String getAddress() {
        return address;
    }

    public String getPersonal() {
        return personal;
    }

    @Override
    public String toString() {
        return toString;
    }
}
