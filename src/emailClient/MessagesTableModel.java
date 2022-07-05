package emailClient;

import java.util.*;
import javax.mail.*;
import javax.swing.*;
import javax.swing.table.*;

// 이메일 테이블의 데이터를 관리하는 클래스
public class MessagesTableModel extends AbstractTableModel
{
  // 테이블 열 이름
  private static final String[] columnNames = {"Sender",
    "Subject", "Date"};

  // 메시지 리스트
  private ArrayList messageList = new ArrayList();

  // 테이블에 메시지들을 추가
  public void setMessages(Message[] messages) {
    for (int i = messages.length - 1; i >= 0; i--) {
      messageList.add(messages[i]);
    }

    // 테이블의 데이터에 변화가 있음을 알리는 이벤트 발생
    fireTableDataChanged();
  }

  // 특정 행에 해당하는 메시지를 리턴
  public Message getMessage(int row) {
    return (Message) messageList.get(row);
  }

  // 메시지 리스트에서 해당 메시지를 삭제
  public void deleteMessage(int row) {
    messageList.remove(row);

    // 테이블의 특정 행이 삭제되었음을 알리는 이벤트 발생
    fireTableRowsDeleted(row, row);
  }

  // 테이블 열 개수를 리턴
  public int getColumnCount() {
    return columnNames.length;
  }

  // 테이블 열 이름을 리턴
  public String getColumnName(int col) {
     return columnNames[col];
  }

  // 테이블 행 개수를 리턴
  public int getRowCount() {
    return messageList.size();
  }

  // 테이블의 특정 셀에 해당하는 값을 리턴
  public Object getValueAt(int row, int col) {
    try {
      Message message = (Message) messageList.get(row);
      switch (col) {
        case 0: // Sender
          Address[] senders = message.getFrom();
          if (senders != null || senders.length > 0) {
            return senders[0].toString();
          } else {
            return "[none]";
          }
        case 1: // Subject
          String subject = message.getSubject();
          if (subject != null && subject.length() > 0) {
            return subject;
          } else {
            return "[none]";
          }
        case 2: // Date
          Date date = message.getSentDate();
          if (date != null) {
            return date.toString();
          } else {
            return "[none]";
          }
      }
    } catch (Exception e) {
      // 예외 발생
      return "";
    }

    return "";
  }
}
