package com.zara.Zara.services.mail;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

public interface EmailService {
    
    void sendEmail(String from, String to, String subject, String message);

    void sendEmailWithCC(String from, String to, String subject, String message, String[] cc);

    void sendEmailWithBCC(String from, String to, String subject, String message, String[] bcc);

    void sendEmailWithCCAndBCC(String from, String to, String subject, String message, String[] cc, String[] bcc);

    void sendEmailWithBCCOnly(String from, String subject, String message, String[] bcc);

    void sendManagementTokenExpiredEmailAlert(String serviceName);

    void sendHTMLEmail(String from, String to, String subject, String htmlMessage)
            throws MessagingException, UnsupportedEncodingException;

    void sendHTMLEmailWithBCC(String from,
                              String to, String subject, String htmlMessage, Set<String> bcc)
            throws MessagingException, UnsupportedEncodingException;

    void setToEmail(String toEmail);

    void sendHTMLEmailWithFileAttachment(String from, String to, String subject,
                                         String htmlMessage, String fileName, String fileExtension,
                                         String fileContentType, byte[] b) throws MessagingException;
}
