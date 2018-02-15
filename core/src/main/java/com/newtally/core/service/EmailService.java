package com.newtally.core.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService{
    private static String userName = "hemasundar@sageabletech.com";
    private static String password = "dhamayanthi";
    private static String from= "hemasundar@sageabletech.com";
    
    
   public static void main(String[] args) {
       EmailService.sendEmail("hemasundar.j2ee@gmail.com", "Email test",
         "send from Java App");
   }

   public static void sendEmail(String to, String subject, String msg) {
      Properties properties = new Properties();
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.gmail.com");
      properties.put("mail.smtp.port", "587");
      Session session = Session.getInstance(properties, new Authenticator() {
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
          }
      });

      try {
         Message message = new MimeMessage(session);
         message.setFrom(new InternetAddress(from));
         message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(to));
         message.setSubject(subject);
         message.setText(msg);
         Transport.send(message);
         System.out.println("Message send successfully....");
      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }
}
