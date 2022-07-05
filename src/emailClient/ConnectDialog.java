package emailClient;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* 이메일 서버에 접속하기 위한 연결 설정 정보를
 * 입력하게 해주는 클래스 */
public class ConnectDialog extends JDialog
{
  // 이메일 서버 타입
  private static final String[] TYPES = {"pop3", "imap"};

  // 이메일 서버 타입을 설정하기 위한 콤보박스
  private JComboBox typeComboBox;

  // 서버, 사용자 이름, SMTP 서버 텍스트 필드
  private JTextField serverTextField, usernameTextField;
  private JTextField smtpServerTextField;

  // 비밀번호 텍스트 필드
  private JPasswordField passwordField;

  //생성자
  public ConnectDialog(Frame parent)
  {
    // 상위 클래스 생성자, 대화상자가 모달(modal)임을 명세
    super(parent, true);

    // 타이틀 설정
    setTitle("Connect");

    // closing 이벤트 핸들링
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        actionCancel();
      }
    });

    // 설정 패널
    JPanel settingsPanel = new JPanel();
    settingsPanel.setBorder(
      BorderFactory.createTitledBorder("Connection Settings"));
    GridBagConstraints constraints;
    GridBagLayout layout = new GridBagLayout();
    settingsPanel.setLayout(layout);
    JLabel typeLabel = new JLabel("Type:");
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.insets = new Insets(5, 5, 0, 0);
    layout.setConstraints(typeLabel, constraints);
    settingsPanel.add(typeLabel);
    typeComboBox = new JComboBox(TYPES);
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 0, 5);
    constraints.weightx = 1.0D;
    layout.setConstraints(typeComboBox, constraints);
    settingsPanel.add(typeComboBox);
    JLabel serverLabel = new JLabel("Server:");
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.insets = new Insets(5, 5, 0, 0);
    layout.setConstraints(serverLabel, constraints);
    settingsPanel.add(serverLabel);
    serverTextField = new JTextField(25);
    constraints = new GridBagConstraints();
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 0, 5);
    constraints.weightx = 1.0D;
    layout.setConstraints(serverTextField, constraints);
    settingsPanel.add(serverTextField);
    JLabel usernameLabel = new JLabel("Username:");
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.insets = new Insets(5, 5, 0, 0);
    layout.setConstraints(usernameLabel, constraints);
    settingsPanel.add(usernameLabel);
    usernameTextField = new JTextField();
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 0, 5);
    constraints.weightx = 1.0D;
    layout.setConstraints(usernameTextField, constraints);
    settingsPanel.add(usernameTextField);
    JLabel passwordLabel = new JLabel("Password:");
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.insets = new Insets(5, 5, 5, 0);
    layout.setConstraints(passwordLabel, constraints);
    settingsPanel.add(passwordLabel);
    passwordField = new JPasswordField();
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 5, 5);
    constraints.weightx = 1.0D;
    layout.setConstraints(passwordField, constraints);
    settingsPanel.add(passwordField);
    JLabel smtpServerLabel = new JLabel("SMTP Server:");
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.EAST;
    constraints.insets = new Insets(5, 5, 5, 0);
    layout.setConstraints(smtpServerLabel, constraints);
    settingsPanel.add(smtpServerLabel);
    smtpServerTextField = new JTextField(25);
    constraints = new GridBagConstraints();
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.insets = new Insets(5, 5, 5, 5);
    constraints.weightx = 1.0D;
    layout.setConstraints(smtpServerTextField, constraints);
    settingsPanel.add(smtpServerTextField);

    // 버튼 패널 설정
    JPanel buttonsPanel = new JPanel();
    JButton connectButton = new JButton("Connect");
    connectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        actionConnect();
      }
    });
    buttonsPanel.add(connectButton);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        actionCancel();
      }
    });
    buttonsPanel.add(cancelButton);

    // 패널을 컨테이너에 붙임
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(settingsPanel, BorderLayout.CENTER);
    getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

    // 컴포넌트에 맞춰서 대화
    pack();

    // 대화상자를 어플리케이션의 중앙에 맞춤
    setLocationRelativeTo(parent);
  }

  // 연결 설정을 확인하고 대화상자를 닫음
  private void actionConnect() {
    if (serverTextField.getText().trim().length() < 1
        || usernameTextField.getText().trim().length() < 1
        || passwordField.getPassword().length < 1
        || smtpServerTextField.getText().trim().length() < 1) {
      JOptionPane.showMessageDialog(this,
        "One or more settings is missing.",
        "Missing Setting(s)", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // 대화상자를 닫음
    dispose();
  }

  // 연결을 취소하고 프로그램을 종료
  private void actionCancel() {
    System.exit(0);
  }

  // 이메일 서버 타입 접근자
  public String getServerType() {
    return (String) typeComboBox.getSelectedItem();
  }

  // 이메일 서버 주소 접근자
  public String getServer() {
    return serverTextField.getText();
  }

  // 이메일 사용자 이름 접근자
  public String getUsername() {
	  return usernameTextField.getText();
  }

  // 비밀번호 접근자
  public String getPassword() {
    return new String(passwordField.getPassword());
  }

  // SMTP 서버 주소 접근자
  public String getSmtpServer() {
    return smtpServerTextField.getText();
  }
}
