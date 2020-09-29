package com.esteel.common.mail;

import java.util.Date;
import java.util.List;

/**
 * @version 1.0.0
 * @ClassName Mail.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class Mail {

    private Date sentDate;
    private Date receivedDate;

    private List<MailAddress> from;
    private List<MailAddress> to;
    private List<MailAddress> cc;
    private List<MailAddress> bcc;

    private String subject;
    private String body;

    private List<MailAttachment> attachments;

    public Mail(Date sentDate, Date receivedDate, List<MailAddress> from, List<MailAddress> to, List<MailAddress> cc, List<MailAddress> bcc, String subject, String body, List<MailAttachment> attachments) {
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
    }

    public boolean hasAttachment() {
        return attachments != null && !attachments.isEmpty();
    }

    public Date getSentDate() {
        return sentDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public List<MailAddress> getFrom() {
        return from;
    }

    public List<MailAddress> getTo() {
        return to;
    }

    public List<MailAddress> getCc() {
        return cc;
    }

    public List<MailAddress> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public List<MailAttachment> getAttachments() {
        return attachments;
    }
}
