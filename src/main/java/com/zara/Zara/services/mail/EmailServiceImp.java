package com.zara.Zara.services.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {

    private String toEmail;
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String from, String to, String subject, String message) {
        to = (toEmail != null) ? toEmail : to;
        mailSender.send(createMessage(from, to, subject, message));
        log.info("EMAIL send to "+to+". With content: "+message);
    }

    @Override
    public void sendEmailWithCC(String from, String to, String subject, String message, String[] cc) {
        to = (toEmail != null) ? toEmail : to;
        SimpleMailMessage email = createMessage(from, to, subject, message);
        setCC(email, cc);
        mailSender.send(email);
    }

    @Override
    public void sendEmailWithBCC(String from, String to, String subject, String message, String[] bcc) {
        to = (toEmail != null) ? toEmail : to;
        SimpleMailMessage email = createMessage(from, to, subject, message);
        setBCC(email, bcc);
        mailSender.send(email);
    }

    @Override
    public void sendEmailWithCCAndBCC(String from, String to, String subject, String message, String[] cc, String[] bcc) {
        to = (toEmail != null) ? toEmail : to;
        SimpleMailMessage email = createMessage(from, to, subject, message);
        setCC(email, cc);
        setBCC(email, bcc);
        mailSender.send(email);
    }

    @Override
    public void sendEmailWithBCCOnly(String from, String subject, String message, String[] bcc) {
        SimpleMailMessage email = createBCCOnlyMessage(from, subject, message);
        setBCC(email, bcc);
        mailSender.send(email);
    }

    private void setCC(SimpleMailMessage email, String[] cc) {
        if (cc.length > 0) {
            email.setCc(cc);
        }
    }

    private void setBCC(SimpleMailMessage email, String[] bcc) {
        if (bcc.length > 0) {
            email.setBcc(bcc);
        }
    }

    @Override
    public void sendManagementTokenExpiredEmailAlert(String serviceName) {
        String from = "system@torapos.com";
        String to = "support@torapos.com";
        to = (toEmail != null) ? toEmail : to;
        String subject = serviceName + " management Token Expired";
        String message = "The management token for " + serviceName + " has expired. Please update it ASAP!";
        mailSender.send(createMessage(from, to, subject, message));
    }

    @Override
    public void sendHTMLEmail(String from, String to, String subject, String htmlMessage) throws MessagingException, UnsupportedEncodingException {
        to = (toEmail != null) ? toEmail : to;
        MimeMessage message = mailSender.createMimeMessage();
        message.setContent(htmlMessage, "text/html; charset=utf-8");
        message.setFrom(new InternetAddress(from, "ToraPos"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        mailSender.send(message);
    }

    @Override
    public void sendHTMLEmailWithBCC(String from,
                                     String to,
                                     String subject, String htmlMessage, Set<String> bcc) throws MessagingException, UnsupportedEncodingException {

        to = (toEmail != null) ? toEmail : to;
        MimeMessage message = mailSender.createMimeMessage();
        message.setContent(htmlMessage, "text/html; charset=utf-8");
        message.setFrom(new InternetAddress(from, "ToraPos"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);

        List<Address> addresses = new ArrayList<>();
        for (String email : bcc) {
            addresses.add(new InternetAddress(email));
        }

        message.setRecipients(Message.RecipientType.BCC, addresses.toArray(new Address[0]));
        mailSender.send(message);
    }

    private SimpleMailMessage createMessage(String from, String to, String subject, String message) {

        to = (toEmail != null) ? toEmail : to;
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom(from);
        emailMessage.setTo(to);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        return emailMessage;
    }

    private SimpleMailMessage createBCCOnlyMessage(String from, String subject, String message) {

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom(from);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        return emailMessage;
    }

    @Override
    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    @Override
    public void sendHTMLEmailWithFileAttachment(String from, String to, String subject,
                                                String htmlMessage, String fileName, String fileExtension,
                                                String fileContentType, byte[] b) throws MessagingException {
        to = (toEmail != null) ? toEmail : to;
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(from);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        MimeMultipart mimeMultipart = getMimeMultipart(b, fileName, fileExtension, fileContentType);
        String htmlContentType = "text/html; charset=utf-8";
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlMessage, htmlContentType);
        mimeMultipart.addBodyPart(messageBodyPart);
        message.setContent(mimeMultipart);

        mailSender.send(message);
    }

    private MimeMultipart getMimeMultipart(byte[] b, String filename, String fileExtension,
                                           String contentType) throws MessagingException {
        MimeMultipart parts = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(b, contentType);
        messageBodyPart.setFileName(filename + fileExtension);
        parts.addBodyPart(messageBodyPart);

        return parts;
    }
}
