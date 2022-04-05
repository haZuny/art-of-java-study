package parser;

import java.util.ArrayList;

public class Algorithm1 {
	
	String str;
	ArrayList<String> list;
	
	
	// 생성자, 계산과 출력까지 수행
	 int operate(String str) {
		this.str = str;
		list = new ArrayList<String>();
		
		int a, b;	// 피연산자
		int result;
		String operator;	// 연산자
		
		// 표현 수식의 구성원들을 분리
		getToken(str);
		
		// 피연산자가 존재하지 않을 때 까지 반복
		while (list.size() > 1) {
			
			// 리스트 상태 출력
			System.out.println(list);
			
			a = Integer.parseInt(list.get(0));	// 피연산자를 정수로 바꿔서 전환
			b = Integer.parseInt(list.get(2));
			
			operator = list.get(1);
			
			// 연산된 피연산자 제거
			list.remove(0);
			list.remove(0);
			list.remove(0);
			
			// switch 구문으로 연산자에 따른 연산 수행
			switch (operator) {
			case "+":
				a = a + b;
				break;
				
			case "-":
				a = a - b;
				break;
				
			case "/":
				a = a / b;
				break;
				
			case "*":
				a = a * b;
				break;
				
			case "^":
				a = a ^ b;
				break;
				
			case "%":
				a = a % b;
				break;

			default:
				break;
			}
			
			// 리스트 맨 앞에 피연산자 추가
			list.add(0, "" + a);					
		}
		
		// 피연산자가 하나만 있으면 리턴
		return(Integer.parseInt(list.get(0)));	

	}
	
	
	// 토큰 저장
	public void getToken(String exp) {
		
		// 연산자 피연산자 상태 구분
		String buf = "";
		int idx = 0;
		
		while(idx < exp.length()) {
			// 피연산자인 경우
			if (("1234567890".indexOf(exp.charAt(idx))) != -1){
				buf += exp.charAt(idx);
			}
			// 연산자인 경우
			else if (("+-/*%^=()".indexOf(exp.charAt(idx))) != -1){
				list.add(buf);
				buf = "";
				list.add("" + exp.charAt(idx));
			}
			// 공백 무시
			else if(exp.charAt(idx) == ' '){
				
			}
			idx++;
		}
		list.add(buf);
	}

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String str = "10 - 2 * 3";
		int result;
		
		System.out.println("입력 수식: " + str);
		
		Algorithm1 obj = new Algorithm1();
		result = obj.operate(str);
		
		System.out.println("결과값: " + result);
	}

}
