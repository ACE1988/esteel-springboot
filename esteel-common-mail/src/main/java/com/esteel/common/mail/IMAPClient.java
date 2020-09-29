package com.esteel.common.mail;

import com.esteel.common.core.Page;

import static com.esteel.common.mail.MailClient.log;

/**
 * @version 1.0.0
 * @ClassName IMAPClient.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class IMAPClient {

    public static void main(String[] args) {

        //imap.exmail.qq.com(使用SSL，端口号993)

        String protocol = "imap";
        String host = "imap.exmail.qq.com";
        int port = 993;

        String user = "";
        String password = "";
        MailClient client = new MailClient(protocol, host, port, user, password);
        Page<Mail> page = client.findMailsByPage("INBOX", 1, 10);
        for (Mail mail : page.getItems()) {
            System.out.println(log(mail));
        }
    }
}
