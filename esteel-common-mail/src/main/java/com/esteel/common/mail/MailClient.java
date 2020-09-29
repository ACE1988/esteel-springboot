package com.esteel.common.mail;

import com.esteel.common.core.ErrorCode;
import com.esteel.common.core.Page;
import com.esteel.common.core.ProcessBizException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import javax.mail.search.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.mail.Message.RecipientType.*;

/**
 * @version 1.0.0
 * @ClassName MailClient.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */

public class MailClient {

    private Store store;

    public MailClient(String protocol, String host, int port, String user, String password) {
        try {
            Session session = session(protocol, host, port);
//            session.setDebug(true);
            URLName urlName = new URLName(protocol, host, port, null,user,password);
            store = session.getStore(urlName);
            store.connect();
        } catch (MessagingException e) {
            throw new ProcessBizException(new ErrorCode("M500", e.getMessage()));
        }
    }

    public void onConnected(Consumer<Store> consumer) {
        consumer.accept(store);
    }

    public Page<Mail> findMailsByPage(String folderName, int pageNo, int pageSize) {
        int begin = (pageNo - 1) * pageSize + 1;
        int end = (pageNo - 1) * pageSize + pageSize;
        Mails mails = findMailsByIndex(folderName, begin, end);
        return new Page<>(mails.getValues(), pageNo, pageSize, mails.getTotal());
    }

    public Mails findMailsByIndex(String folderName, int begin, int end) {
        Folder folder = open(store, folderName);
        try {
            int total = folder.getMessageCount();
            int beginIndex = begin < 1 ? 1 : (begin > total ? total : begin);
            int endIndex = end < 1 ? 1 : (end > total ? total : end);
            Message[] messages = folder.getMessages(beginIndex, endIndex);
            List<Mail> mails = Stream.of(messages).map(MailClient::getMail).collect(Collectors.toList());
            return new Mails(mails, total);
        } catch (MessagingException e) {
            throw new ProcessBizException(new ErrorCode("M501", e.getMessage()));
        }
    }

    public List<Mail> searchMails(String folderName, String from) {
        Folder folder = open(store, folderName);
        try {
            Message[] messages = folder.search(new FromTerm(new InternetAddress(from)));
            return Stream.of(messages).map(MailClient::getMail).collect(Collectors.toList());
        } catch (MessagingException e) {
            throw new ProcessBizException(new ErrorCode("M501", e.getMessage()));
        }
    }

    public List<Mail> searchMails(String folderName, String from, String subjectPattern, String bodyPattern) {
        Folder folder = open(store, folderName);
        try {
            AndTerm and = new AndTerm(new SearchTerm[] {
                    new FromTerm(new InternetAddress(from)),
                    new SubjectTerm(subjectPattern),
                    new BodyTerm(bodyPattern)
            });
            Message[] messages = folder.search(and);
            return Stream.of(messages).map(MailClient::getMail).collect(Collectors.toList());
        } catch (MessagingException e) {
            throw new ProcessBizException(new ErrorCode("M501", e.getMessage()));
        }
    }

    public static String log(Mail mail) {
        return "Subject: " + mail.getSubject() + "\n" +
                "Sent: " + mail.getSentDate() + ", Received: " + mail.getReceivedDate() + "\n" +
                "From: " + mail.getFrom() + "\n" +
                "To: " + mail.getTo() + "\n" +
                "Cc: " + mail.getCc() + "\n" +
                "Bcc: " + mail.getBcc() + "\n" +
                "----------------------------------------------------------------------------------------" + "\n" +
                mail.getBody() + "\n" +
                "----------------------------------------------------------------------------------------" + "\n" +
                mail.getAttachments() + "\n" +
                "########################################################################################" + "\n";
    }

    protected static Folder open(Store store, String folderName) {
        try {
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            return folder;
        } catch (MessagingException e) {
            throw new ProcessBizException(new ErrorCode("M501", e.getMessage()));
        }
    }

    protected static Message[] getMessages(Store store, String folderName) {
        Folder folder = open(store, folderName);
        try {
            return folder.getMessages();
        } catch (MessagingException e) {
            throw new ProcessBizException(new ErrorCode("M501", e.getMessage()));
        }
    }

    protected static Mail getMail(Message message) {
        try {
            Date sentDate = message.getSentDate();
            Date receivedDate = message.getReceivedDate();

            List<MailAddress> from = getFrom(message);
            List<MailAddress> to = getRecipients(message, TO);
            List<MailAddress> cc = getRecipients(message, CC);
            List<MailAddress> bcc = getRecipients(message, BCC);

            String subject = message.getSubject();

            Object content = message.getContent();
//            String body = content instanceof String ? (String) content : "";
            StringBuilder body = new StringBuilder();
            if (content instanceof String) {
                body.append(content);
            }

            List<MailAttachment> attachments = Lists.newArrayList();
            String starts = "*******************************************************************************************\n";
            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                processMultipart(multipart, text -> body.append("\n").append(body.toString().trim().length() == 0 ? "" : starts).append(text), attachments::add);
            }

            return new Mail(sentDate, receivedDate, from, to, cc, bcc, subject, body.toString(), attachments);
        } catch (MessagingException | IOException e) {
            throw new ProcessBizException(new ErrorCode("M504", e.getMessage()));
        }
    }

    protected static boolean isAttachment(BodyPart part) {
        try {
            return Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition());
        } catch (MessagingException e) {
            throw new ProcessBizException(new ErrorCode("M502", e.getMessage()));
        }
    }

    private static void processMultipart(Multipart multipart, Consumer<String> bodyConsumer, Consumer<MailAttachment> attachmentConsumer) {
        try {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (isAttachment(part)) {
                    String fileName = part.getFileName();
                    String decoded =
                            StringUtils.isBlank(fileName) || !fileName.startsWith("=?") ?
                                    fileName : MimeUtility.decodeWord(fileName);
                    InputStream in = part.getInputStream();
                    attachmentConsumer.accept(new MailAttachment(decoded, in));
                } else {
                    boolean text = part.isMimeType("text/plain");
                    boolean html = part.isMimeType("text/html");
                    Object partContent = part.getContent();
                    if (text) {
                        bodyConsumer.accept((String) partContent);
                    }
                    if (html) {
                        bodyConsumer.accept(Jsoup.parse((String) partContent).text());
                    }
                    if (partContent instanceof MimeMultipart) {
                        processMultipart((MimeMultipart) partContent, bodyConsumer, attachmentConsumer);
                    }
                }
            }
        } catch (MessagingException | IOException e) {
            throw new ProcessBizException(new ErrorCode("M503", e.getMessage()));
        }
    }

    private static List<MailAddress> getFrom(Message message) throws MessagingException {
        Address[] addresses = message.getFrom();
        return getAddresses(addresses);
    }

    private static List<MailAddress> getRecipients(Message message, Message.RecipientType type) throws MessagingException {
        Address[] addresses = message.getRecipients(type);
        return getAddresses(addresses);
    }

    private static List<MailAddress> getAddresses(Address[] addresses) {
        List<MailAddress> values = Lists.newArrayList();
        if (addresses == null) {
            return values;
        }
        for (Address address : addresses) {
            values.add(getAddress(address));
        }
        return values;
    }

    private static MailAddress getAddress(Address address) {
        try {
            String fullAddress = MimeUtility.decodeWord(address.toString());
            if (address instanceof InternetAddress) {
                InternetAddress internetAddress = (InternetAddress) address;
                return new MailAddress(internetAddress.getAddress(), internetAddress.getPersonal(), fullAddress);
            }
            return new MailAddress(fullAddress, null, fullAddress);
        } catch (ParseException | UnsupportedEncodingException e) {
            throw new ProcessBizException(new ErrorCode("M510", e.getMessage()));
        }
    }

    private static Properties getServerProperties(String protocol, String host, String port) {
        Properties properties = new Properties();

        properties.setProperty("mail.store.protocol", protocol);
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        properties.setProperty(String.format("mail.%s.starttls.enable", protocol),"true");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), port);

        return properties;
    }

    private static Session session(String protocol, String host, int port) {
        return Session.getDefaultInstance(getServerProperties(protocol, host, String.valueOf(port)));
    }

    //    private static Properties getServerProperties(String protocol, String host, String port) throws GeneralSecurityException {
//        Properties properties = new Properties();
//
//        properties.setProperty("mail.store.protocol", protocol);
//        // server setting
//        properties.put(String.format("mail.%s.host", protocol), host);
//        properties.put(String.format("mail.%s.port", protocol), port);
//
////        MailSSLSocketFactory sf = new MailSSLSocketFactory();
////        sf.setTrustAllHosts(true);
//
//        // SSL setting
//        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
//        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
//        properties.setProperty(String.format("mail.%s.starttls.enable", protocol),"true");
////        properties.setProperty(String.format("mail.%s.ssl.enable", protocol), "true");
////        properties.put(String.format("mail.%s.socketFactory", protocol), sf);
//        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), port);
//
//        return properties;
//    }
//
//    private static Session session(String protocol, String host, int port) throws GeneralSecurityException {
//        return Session.getDefaultInstance(getServerProperties(protocol, host, String.valueOf(port)));
//    }

    //        Security.setProperty("jdk.tls.disabledAlgorithms", "TLSv1.2, RC4, RSA keySize < 2048");
//        Security.setProperty("jdk.tls.legacyAlgorithms", "RC4_128");
//        Security.setProperty("jdk.tls.legacyAlgorithms", "TLSv1.2, RC4_128, RSA keySize < 2048");
}
