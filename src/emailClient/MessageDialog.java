package emailClient;

import java.awt.*;
import java.awt.event.*;
import javax.mail.*;
import javax.swing.*;

// 메일 메시지 작성을 위한 클래스
public class MessageDialog extends JDialog
{
  // 메시지 타입 식별자
  public static final int NEW = 0;
  public static final int REPLY = 1;
  public static final int FORWARD = 2;

  // 송신자, 수신자, 제목 텍스트 필드
  private JTextField fromTextField, toTextField;
  private JTextField subjectTextField;

  // 메시지 내용 텍스트 영역
  private JTextArea contentTextArea;

  // 대화상자가 취소 버튼을 클릭해서 닫혔는지 여부를 나타내는 플래그
  private boolean cancelled;

  // 생성자
  public MessageDialog(Frame parent, int type, Message message)
    throws Exception
  {
    // 상위 클래스 생성자, 대화상자가 모달임을 명세
    super(parent, true);

    /* 타이틀 설정 및 메시지 타입에 따른 수신자, 제목, 내용 값을 얻음 */
    String to = "", subject = "", content = "";
    switch (type) {
      // 답장 메시지
      case REPLY:
        setTitle("Reply To Message");

        // 수신자
        Address[] senders = message.getFrom();
        if (senders != null || senders.length > 0) {
          to = senders[0].toString();
        }
        to = message.getFrom()[0].toString();

        // 제목
        subject = message.getSubject();
        if (subject != null && subject.length() > 0) {
          subject = "RE: " + subject;
        } else {
          subject = "RE:";
        }

        // 메시지 내용
        content = "\n----------------- " +
                  "REPLIED TO MESSAGE" +
                  " -----------------\n" +
                  EmailClient.getMessageContent(message);
        break;

      // 전달 메시지
      case FORWARD:
        setTitle("Forward Message");

        // 제목
        subject = message.getSubject();
        if (subject != null && subject.length() > 0) {
          subject = "FWD: " + subject;
        } else {
          subject = "FWD:";
        }

        // 메시지 내용
        content = "\n----------------- " +
                  "FORWARDED MESSAGE" +
                  " -----------------\n" +
                  EmailClient.getMessageContent(message);
        break;

      // 새 메시지
      default:
        setTitle("New Message");
    }

    // closing 이벤트 핸들링
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        actionCancel();
      }
    });

    // 메시지 필드 판넬 설정
    JPanel fieldsPanel = new JPanel();
    GridBagConstraints constraints;
    GridBagLayout layout = new GridBagLayout();
    fieldsPanel.setLayout(layout);
    JLabel fromLabel = new JLabel("From:");
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.insets = new Insets(5, 5, 0, 0);
    layout.setConstraints(fromLabel, constraints);
    fieldsPanel.add(fromLabel);
    fromTextField = new JTextField();
    constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 0, 0);
    layout.setConstraints(fromTextField, constraints);
    fieldsPanel.add(fromTextField);
    JLabel toLabel = new JLabel("To:");
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.insets = new Insets(5, 5, 0, 0);
    layout.setConstraints(toLabel, constraints);
    fieldsPanel.add(toLabel);
    toTextField = new JTextField(to);
    constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 0, 0);
    constraints.weightx = 1.0D;
    layout.setConstraints(toTextField, constraints);
    fieldsPanel.add(toTextField);
    JLabel subjectLabel = new JLabel("Subject:");
    constraints = new GridBagConstraints();
    constraints.insets = new Insets(5, 5, 5, 0);
    layout.setConstraints(subjectLabel, constraints);
    fieldsPanel.add(subjectLabel);
    subjectTextField = new JTextField(subject);
    constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 5, 0);
    layout.setConstraints(subjectTextField, constraints);
    fieldsPanel.add(subjectTextField);

    // 메시지 내용 판낼 설정
    JScrollPane contentPanel = new JScrollPane();
    contentTextArea = new JTextArea(content, 10, 50);
    contentPanel.setViewportView(contentTextArea);

    // 버튼 판넬 설정
    JPanel buttonsPanel = new JPanel();
    JButton sendButton = new JButton("Send");
    sendButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        actionSend();
      }
    });
    buttonsPanel.add(sendButton);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        actionCancel();
      }
    });
    buttonsPanel.add(cancelButton);

    // 패널을 컨테이너에 붙임
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(fieldsPanel, BorderLayout.NORTH);
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

    // 컴포넌트에 맞춰서 대화상자의 크기를 설정
    pack();

    // 대화상자를 어플리케이션의 중앙에 맞춤
    setLocationRelativeTo(parent);
  }

  // 메시지 필드를 확인하고 대화상자를 닫는다.
  private void actionSend() {
    if (fromTextField.getText().trim().length() < 1
        || toTextField.getText().trim().length() < 1
        || subjectTextField.getText().trim().length() < 1
        || contentTextArea.getText().trim().length() < 1) {
      JOptionPane.showMessageDialog(this,
        "One or more fields is missing.",
        "Missing Field(s)", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // 대화상자를 닫음
    dispose();
  }

  // 메시지 생성을 취소하고 대화상자를 닫는다.
  private void actionCancel() {
    cancelled = true;

    // 대화상자를 닫음
    dispose();
  }

  // 대화상자를 보여준다.
  public boolean display() {
    show();

    // 전송 버튼과 취소 버튼 중 어떤 것에 의해 닫히는가를 리턴
    return !cancelled;
  }

  // 송신자 필드 접근자
  public String getFrom() {
    return fromTextField.getText();
  }

  // 수신자 필드 접근자
  public String getTo() {
    return toTextField.getText();
  }

  // 제목 필드 접근자
  public String getSubject() {
    return subjectTextField.getText();
  }

  // 메시지 내용 필드 접근자
  public String getContent() {
    return contentTextArea.getText();
  }
}
