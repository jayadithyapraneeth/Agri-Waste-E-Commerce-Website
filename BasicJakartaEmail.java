package com.agriwastetrade.site;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class BasicJakartaEmail {
	public static void main(String[] args) throws AddressException, MessagingException, IOException {
		// TODO Auto-generated method stub
		System.out.println("BasicJakartaEmail main method");

		// Set up the SMTP server properties

	Properties props = new Properties();
	props.put("mail.smtp.host", "smtp.gmail.com");
	props.put("mail.smtp.port", "587");
	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.starttls.enable", "true");

	String sender = "machavarapujayadithyapraneeth@gmail.com";
	String password = "ygwaehbymtrftssk"; // Use an App Password if 2FA is enabled -- without this password, it won't work because gmail blocks less secure apps.
	
	Session session = Session.getInstance(props, new Authenticator() {
	  protected PasswordAuthentication getPasswordAuthentication() {
	    return new PasswordAuthentication(sender, password);
	  }
	});

	Message msg = new MimeMessage(session);
	Address[] toaddresses = new Address[20];
	toaddresses[0]=new InternetAddress("skr7993257687@gmail.com");
	toaddresses[1]=new InternetAddress("machavarapujayadithyapraneeth@outlook.com");//.add(new InternetAddress("skr7993257687@gmail.com"));

	msg.setFrom(new InternetAddress(sender));
	msg.setRecipient(Message.RecipientType.TO, new InternetAddress("machavarapujayadithyapraneeth@outlook.com"));//"o210250@rguktong.ac.in"));////"skr7993257687@gmail.com"));
	msg.setRecipients(Message.RecipientType.TO, toaddresses);//new Address[] {toaddresses[0], toaddresses[1]});
	msg.setSubject("Hello from Jakarta Mail");

	
	MimeBodyPart textpart = new MimeBodyPart();
	textpart.setText("Hello, this is a test email with an attachment sent using Jakarta Mail!");
	
	

	MimeBodyPart filepart = new MimeBodyPart();
	
	
	MimeMultipart multipart = new MimeMultipart();
	multipart.addBodyPart(textpart);
	//multipart.addBodyPart(filepart);
    //multipart.addBodyPart(HTMLpart);
	
	msg.setContent(multipart);
	
	Transport.send(msg);
	
	System.out.println("Email sent successfully!");
	System.out.println("Email sent to: ");
	for (Address address : msg.getAllRecipients()) {
		System.out.println(address.toString());
	}
	
	}

}
