package com.zara.Zara.utils;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {

    public static void sendmail(String otp, String email) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("pesapaydev@gmail.com", "kalengakilungu2712D!");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("pesapaydev@gmail.com", "PesaPay Ltd"));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("EMAIL VERIFICATION");
        msg.setContent(otp, "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Email verification", "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

//        attachPart.attachFile(new File(EmailService.class.getClassLoader().getResource("uploads/logo.jpg").getFile()));
//        multipart.addBodyPart(attachPart);
//        msg.setContent(multipart);
        Transport.send(msg);
    }
}
