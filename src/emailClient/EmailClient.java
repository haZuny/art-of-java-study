package emailClient;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.mail.*;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.*;

// 이메일 클라이언트
public class EmailClient extends JFrame {
	// M메시지 테이블의 데이터 모델
	private MessagesTableModel tableModel;

	// 메시지 목록을 가지는 테이블
	private JTable table;

	// 메시지 목록을 가지는 텍스트 영역
	private JTextArea messageTextArea;

	/* 메시지 테이블과 메시지 뷰 판넬을 가지는 스플릿 판넬 */
	private JSplitPane splitPane;

	// 답장, 전달, 삭제 버튼
	private JButton replyButton, forwardButton, deleteButton;

	// 테이블에서 현재 선택된 메시지
	private Message selectedMessage;

	// 어떤 메시지가 현재 삭제되고 있는지 여부를 나타내는 플래그
	private boolean deleting;

	// JavaMail 세션
	private Session session;

	// 생성자
	public EmailClient() {
		// 타이틀 설정
		setTitle("E-mail Client");

		// 윈도우 크기 설정
		setSize(640, 480);

		// 윈도우 종료 이벤트 처리
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionExit();
			}
		});

		// 파일 메뉴 설정
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		fileExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit();
			}
		});
		fileMenu.add(fileExitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		// 버튼 패널 설정
		JPanel buttonPanel = new JPanel();
		JButton newButton = new JButton("New Message");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNew();
			}
		});
		buttonPanel.add(newButton);

		// 메시지 테이블 설정
		tableModel = new MessagesTableModel();
		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				tableSelectionChanged();
			}
		});
		// 한 번에 하나의 행만 선택되도록 함
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// 이메일 패널 설정
		JPanel emailsPanel = new JPanel();
		emailsPanel.setBorder(BorderFactory.createTitledBorder("E-mails"));
		messageTextArea = new JTextArea();
		messageTextArea.setEditable(false);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), new JScrollPane(messageTextArea));
		emailsPanel.setLayout(new BorderLayout());
		emailsPanel.add(splitPane, BorderLayout.CENTER);

		// 버튼 패널 2 설정
		JPanel buttonPanel2 = new JPanel();
		replyButton = new JButton("Reply");
		replyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionReply();
			}
		});
		replyButton.setEnabled(false);
		buttonPanel2.add(replyButton);
		forwardButton = new JButton("Forward");
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionForward();
			}
		});
		forwardButton.setEnabled(false);
		buttonPanel2.add(forwardButton);
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionDelete();
			}
		});
		deleteButton.setEnabled(false);
		buttonPanel2.add(deleteButton);

		// 패널을 컨테이너에 붙임
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		getContentPane().add(emailsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel2, BorderLayout.SOUTH);
	}

	// 프로그램 종료
	private void actionExit() {
		System.exit(0);
	}

	// 새 메시지 작성
	private void actionNew() {
		sendMessage(MessageDialog.NEW, null);
	}

	// 테이블의 행이 선택될 때마다 호출
	private void tableSelectionChanged() {
		// 선택된 행에 들어있는 메시지가 삭제중이 아니라면, 사용자에게 보여줌
		if (!deleting) {
			selectedMessage = tableModel.getMessage(table.getSelectedRow());
			showSelectedMessage();
			updateButtons();
		}
	}

	// 답장 메시지 보내기
	private void actionReply() {
		sendMessage(MessageDialog.REPLY, selectedMessage);
	}

	// 메시지 전달하기
	private void actionForward() {
		sendMessage(MessageDialog.FORWARD, selectedMessage);
	}

	// 선택된 메시지 삭제
	private void actionDelete() {
		deleting = true;

		try {
			// 서버에서 메시지 삭제
			selectedMessage.setFlag(Flags.Flag.DELETED, true);
			Folder folder = selectedMessage.getFolder();
			folder.close(true);
			folder.open(Folder.READ_WRITE);
		} catch (Exception e) {
			showError("Unable to delete message.", false);
		}

		// 테이블에서 메시지 삭제
		tableModel.deleteMessage(table.getSelectedRow());

		// GUI 갱신
		messageTextArea.setText("");
		deleting = false;
		selectedMessage = null;
		updateButtons();
	}

	// 메시지 보내기
	private void sendMessage(int type, Message message) {
		// 메시지 대화상자를 띄운다
		MessageDialog dialog;
		try {
			dialog = new MessageDialog(this, type, message);
			if (!dialog.display()) {
				// 취소 버튼에 의해 리턴되는 경우
				return;
			}
		} catch (Exception e) {
			showError("Unable to send message.", false);
			return;
		}

		try {
			// 메시지 대화상자의 접근자를 이용해 새 메시지 작성
			Message newMessage = new MimeMessage(session);
			newMessage.setFrom(new InternetAddress(dialog  .getFrom()));
			newMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(dialog.getTo()));
			newMessage.setSubject(dialog.getSubject());
			newMessage.setSentDate(new Date());
			newMessage.setText(dialog.getContent());

			// 새 메시지를 보냄
			Transport.send(newMessage);
		} catch (Exception e) {
			showError("Unable to send message.", false);
			System.out.println("메세지 전송 에러====================");
			e.printStackTrace();
			System.out.println("메세지 전송 에러====================");
		}
	}

	// 선택된 메시지를 보여줌
	private void showSelectedMessage() {
		// 메시지가 로딩되는 동안 커서를 모래시계로 바꾼다
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			messageTextArea.setText(getMessageContent(selectedMessage));
			messageTextArea.setCaretPosition(0);
		} catch (Exception e) {
			showError("Unabled to load message.", false);
		} finally {
			// 커서를 원래대로 되돌린다
			setCursor(Cursor.getDefaultCursor());
		}
	}

	/* 각 버튼의 상태를 테이블에 현재 선택된 메시지가 잇는지 여부에 따라 갱신 */
	private void updateButtons() {
		if (selectedMessage != null) {
			replyButton.setEnabled(true);
			forwardButton.setEnabled(true);
			deleteButton.setEnabled(true);
		} else {
			replyButton.setEnabled(false);
			forwardButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
	}

	// 화면에 어플리케이션 윈도우를 띄운다
	public void show() {
		super.show();

		// 스플릿 판넬의 비율을 50대 50으로 맞춘다
		splitPane.setDividerLocation(.5);
	}

	// 이메일 서버에 접속
	public void connect() {
		// 연결 대화상자를 띄운다
		ConnectDialog dialog = new ConnectDialog(this);
		dialog.show();

		// 연결 대화상자로부터 접속 url을 만든다
		StringBuffer connectionUrl = new StringBuffer();
		connectionUrl.append(dialog.getServerType() + "://");
		connectionUrl.append(dialog.getUsername() + ":");
		connectionUrl.append(dialog.getPassword() + "@");
		System.out.println(dialog.getServerType());
		if(dialog.getServerType().equals("pop3"))		
			connectionUrl.append("pop.");
		else
			connectionUrl.append("imap.");
		connectionUrl.append(dialog.getServer() + "/");

		// 메시지를 다운로드하고 있음을 알리는 다운로딩 대화상자를 띄운다.
		final DownloadingDialog downloadingDialog = new DownloadingDialog(this);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				downloadingDialog.show();
			}
		});

		// jAVAmAIL 세션을 초기화 한 뒤 서버에 접속
		Store store = null;
		try {
			// javaMail 세션을 SMTP 서버로 초기화
			Properties props = new Properties();
			props.put("mail.smtp.host", dialog.getSmtpServer());
			props.put("mail.smtp.port", 587); // smtp 포트 설정
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // 서버, 클라이언트간 SSL/TLS 버전을 맞춤 / smtp

			props.put("mail.pop3.host", dialog.getServer());
			props.put("mail.pop3.port", 995); // pop3 포트 설정
			props.put("mail.pop3.ssl.protocols", "TLSv1.2"); // 서버, 클라이언트간 SSL/TLS 버전을 맞춤 / pop3
			props.put("mail.pop3.ssl.enable", "true");

			props.put("mail.imap.host", dialog.getServer());
			props.put("mail.imap.port", 995); // imap 포트 설정
			props.put("mail.imap.ssl.protocols", "TLSv1.2"); // 서버, 클라이언트간 SSL/TLS 버전을 맞춤 / imap
			props.put("mail.imap.ssl.enable", "true");

			session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(dialog.getUsername() + "@" + dialog.getServer(),
							dialog.getPassword());
				}
			});

			// 이메일 서버에 접속
			URLName urln = new URLName(connectionUrl.toString());
			store = session.getStore(urln);
			store.connect();
		} catch (Exception e) {
			System.out.println("연결 에러====================");
			e.printStackTrace();
			System.out.println("연결 에러====================");
			// 다운로딩 대화상자를 닫는다.
			downloadingDialog.dispose();

			// 에러 출력
			showError("Unable to connect.", true);
		}

		// 서버로부터 메시지 헤더를 다운로드
		try {
			// 받은 편지함 폴더를 연다.
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

			// 메시지 리스트를 받아온다.
			Message[] messages = folder.getMessages();

			// 폴더의 각 메시지에 대해 헤더 정보를 가져온다
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.ENVELOPE);
			folder.fetch(messages, profile);

			// 테이블에 메시지를 넣는다.
			tableModel.setMessages(messages);
		} catch (Exception e) {
			// 다운로딩 대화상자를 닫는다.
			downloadingDialog.dispose();

			// 에러 출력
			showError("Unable to download messages.", true);
		}

		// 다운로딩 대화상자를 닫는다.
		downloadingDialog.dispose();
	}

	// 필요하다면 에러를 출력하고 프로그램을 종료한다
	private void showError(String message, boolean exit) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		if (exit)
			System.exit(0);
	}

	// 메시지 내용을 얻는다.
	public static String getMessageContent(Message message) throws Exception {
		Object content = message.getContent();
		if (content instanceof Multipart) {
			StringBuffer messageContent = new StringBuffer();
			Multipart multipart = (Multipart) content;
			for (int i = 0; i < multipart.getCount(); i++) {
				Part part = (Part) multipart.getBodyPart(i);
				if (part.isMimeType("text/plain")) {
					messageContent.append(part.getContent().toString());
				}
			}
			return messageContent.toString();
		} else {
			return content.toString();
		}
	}

	// 이메일 클라이언트 실행
	public static void main(String[] args) {
		EmailClient client = new EmailClient();
		client.show();

		// 연결 대화상자를 띄운다
		client.connect();
	}
}
