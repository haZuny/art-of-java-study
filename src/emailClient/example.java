package emailClient;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class example {
	public static void main(String[] args) {
		naverMailSend();
	}

	// smtp 예제
	public static void naverMailSend() {
		String host = "smtp.naver.com"; // 네이버일 경우 네이버 계정, gmail경우 gmail 계정
		String user = "gkwns5791@naver.com"; // 패스워드
		String password = "hajun12o99!";

		// SMTP 서버 정보를 설정한다.
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", 587);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("gkwns5791@naver.com"));

			// 메일 제목
			message.setSubject("KTKO SMTP TEST1111");

			// 메일 내용
			message.setText("KTKO Success!!");

			// send the message
			Transport.send(message);
			System.out.println("Success Message Send");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	// pop3 예제
	public static void open() throws AddressException, MessagingException {

		String host = "pop.naver.com";

		final String username = "gkwns5791"; // @naver.com 은 제외하고 아이디만.
		final String password = "hajun12o99!";
		int port = 995;

		Properties props = System.getProperties();

		props.put("mail.pop3.host", host);
		props.put("mail.pop3.port", port);
		props.put("mail.pop3.auth", "true");
		props.put("mail.pop3.ssl.enable", "true");
		props.put("mail.pop3.ssl.trust", host);
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {

			String un = username;
			String pw = password;

			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(un, pw);
			}

		});

		session.setDebug(false);
		Store store = session.getStore("pop3");
		store.connect();
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message[] messages = folder.getMessages();

		for (Message message : messages) {
			System.out.print(":::::::::::::::::::::::::::::::::::");
			System.out.println(message.getSubject());
		}

		store.close();
	}

}