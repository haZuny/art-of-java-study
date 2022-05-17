package downloadManager;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

// 다운로드 테이블의 데이터를 관리하는 클래스
class DownloadsTableModel extends AbstractTableModel
  implements Observer
{
  // 테이블의 각 열에 대한 이름들
  private static final String[] columnNames = {"URL", "Size",
    "Progress", "Status"};

  // 각 열의 값에 대한 클래스들
  private static final Class[] columnClasses = {String.class,
    String.class, JProgressBar.class, String.class};

  // 테이블의 다운로드 리스트
  private ArrayList downloadList = new ArrayList();

  // 새로운 다운로드를 테이블에 추가
  public void addDownload(Download download) {
    // 다운로드가 변경될 대 통보받도록 등록
    download.addObserver(this);

    downloadList.add(download);

    // 테이블 행 삽입을 테이블에게 통보
    fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
  }

  // 지정한 행에 대한 다운로드를 얻음
  public Download getDownload(int row) {
    return (Download) downloadList.get(row);
  }

  // 리스트에서 다운로드를 삭제
  public void clearDownload(int row) {
    downloadList.remove(row);

    // 테이블 행 삭제를 테이블에게 통보
    fireTableRowsDeleted(row, row);
  }

  // 테이블의 열 개수를 얻음
  public int getColumnCount() {
    return columnNames.length;
  }

  // 열의 이름을 얻음
  public String getColumnName(int col) {
     return columnNames[col];
  }

  // 열의 클래스를 얻음
  public Class getColumnClass(int col) {
    return columnClasses[col];
  }

  // 테이블의 행 개수를 얻음
  public int getRowCount() {
    return downloadList.size();
  }

  // 지정한 열과 행의 조합에 대한 값을 얻음
  public Object getValueAt(int row, int col) {
    Download download = (Download) downloadList.get(row);
    switch (col) {
      case 0: // URL
        return download.getUrl();
      case 1: // Size
        int size = download.getSize();
        return (size == -1) ? "" : Integer.toString(size);
      case 2: // Progress
        return new Float(download.getProgress());
      case 3: // Status
        return Download.STATUSES[download.getStatus()];
    }
    return "";
  }

  // Download에 대한 변화가 일어나서 Download 객체가 관찰자들에게 통보할 때 호출
  public void update(Observable o, Object arg) {
    int index = downloadList.indexOf(o);

    // 테이블 행 갱신을 테이블에게 통보
    fireTableRowsUpdated(index, index);
  }
}
