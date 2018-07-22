package com.crossover.page;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SubjectTerm;

public class SearchMailPage {

	public Boolean searchEmail() throws Exception {

		Properties properties = new Properties();
		properties.load(new FileReader(new File("test.properties")));

		// Set Connection
		Properties props = System.getProperties();
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(properties.getProperty("username"),
						properties.getProperty("password"));
			}
		});

		Store store = session.getStore("imaps");
		store.connect("imap.gmail.com", properties.getProperty("username"), properties.getProperty("password"));

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);

		System.out.println("Total Messages:" + folder.getMessageCount());
		System.out.println("Unread Messages:" + folder.getUnreadMessageCount());

		// Search for relevant mail
		String bodyText = properties.getProperty("content");

		Message[] foundMessages = null;
		String subjectText = properties.getProperty("subject");
		foundMessages = folder.search(new SubjectTerm(subjectText));
		System.out.println("Total Relevant Messages:" + foundMessages.length);

		// Validate relevant mail
		Boolean isSubjectCorrect, isBodyCorrect, isFileCorrect, isMailFound;
		isSubjectCorrect = isBodyCorrect = isFileCorrect = isMailFound = false;

		for (Message message : foundMessages) {
			if (!message.isSet(Flags.Flag.SEEN) || message.isSet(Flags.Flag.RECENT)) {

				isSubjectCorrect = message.getSubject().contains(subjectText);

				if (getText(message).contains(bodyText) == true) {
					System.out.println("FOUND!!");
					System.out.println(getText(message));
					isBodyCorrect = true;
				}

				if (message.getDataHandler() != null)
					isFileCorrect = message.getDataHandler().getName().contains("Resume");
				System.out.println(message.getDataHandler() + "---" + isFileCorrect);
				
				if (isSubjectCorrect && isBodyCorrect) {
					isMailFound = true;
					break;
				}
			}
		}
		System.out.println("isMailFound :: " + isMailFound);
		return isMailFound;
	}

	private String getText(Part p) throws MessagingException, IOException {
		boolean textIsHtml = false;
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			textIsHtml = p.isMimeType("text/html");
			return s;
		}
		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}
		return null;
	}
	

}
