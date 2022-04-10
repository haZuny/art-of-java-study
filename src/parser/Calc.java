package parser;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

/*
	<applet code = "Calc" width = 200 height = 150>
	</applet>
 */

public class Calc extends Applet implements ActionListener{
	
	TextField expText, resText;
	Parser p;
	
	public void init() {
		
		// Title
		Label heading = new Label("Expression Calculator ", Label.CENTER);
		
		// Sub Title
		Label explab = new Label("Expression ", Label.CENTER);
		Label reslab = new Label("Result	 ", Label.CENTER);
		
		// 빈칸
		expText = new TextField(24);
		resText = new TextField(24);
		
		// 결과 텍스트 필드는 수정 불가
		resText.setEditable(false);	// 화면 출력을 위한 결과 필드
		
		add(heading);
		add(explab);
		add(expText);
		add(reslab);
		add(resText);
		
		// 텍스트 필드 액션 리스너로 등록
		expText.addActionListener(this);
		
		// 파서 생성
		p = new Parser();
	}
		
	// 엔터 누르면 처리
	public void  actionPerformed(ActionEvent ae) {
		repaint();
	}
	
	
	
	public void paint(Graphics g) {
		double result = 0.0;
		String expstr = expText.getText();
		
		try {
			if(expstr.length() != 0)
				result = p.evaluate(expstr);
		
		// 다음을 통해 엔터키가 눌려진 이후 표현식 무시하기
		// expText.setText("");
		
		resText.setText(Double.toString(result));
		
		showStatus("");	// 에러 메시지 삭제
		} catch (ParserException exc) {
			showStatus(exc.toString());
			resText.setText("");
		}
	}
}
