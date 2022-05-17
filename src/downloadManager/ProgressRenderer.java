package downloadManager;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

// 테이블 셀에 JProgressBar 렌더링
class ProgressRenderer extends JProgressBar
  implements TableCellRenderer
{
  // 생성자
  public ProgressRenderer(int min, int max) {
    super(min, max);
  }

  // 테이블셀에 대해 JProgressBar 객체를 렌더러로서 리턴
  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int column)
  {
    // JProgressBar의 완료 백분율 값을 설정
    setValue((int) ((Float) value).floatValue());
    return this;
  }
}
