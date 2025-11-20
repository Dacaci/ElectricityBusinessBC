package com.eb.signup.mail;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class Mailer {
  private final Session session;
  private final String from;
  public Mailer(String host, int port, String from) {
    var props = new Properties();
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", String.valueOf(port));
    session = Session.getInstance(props);
    this.from = from;
  }
  public void sendCode(String to, String code) throws MessagingException {
    var msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(from));
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    msg.setSubject("Votre code de validation EB");
    msg.setText("Bonjour,\n\nVotre code est : " + code + " (valable 15 minutes)\n\n— Electricity Business");
    Transport.send(msg);
  }
}
