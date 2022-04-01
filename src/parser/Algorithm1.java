package parser;

import java.util.ArrayList;

/*
 알고리즘1
 a = 첫번째 피연산자
 while (피연산자가 존재_
 	op = 연산자
 	b = 두번째 피연산자
 	a = a op b
 */
public class Algorithm1 {
	
	String str;
	ArrayList<String> list;
	
	
	// 생성자, 계산과 출력까지 수행
	public Algorithm1(String str) {
		this.str = str;
		list = new ArrayList<String>();
		
		int idx1 = 0;
		int idx2 = 0;
		
		int a, b;	// 피연산자
		int result;
		String operator;	// 연산자
		
		// 표현 수식의 구성원들을 분리
		while(idx2 < str.length()) {
			if(str.charAt(idx2) == ' ') {	// 공백으로 연산자 구분
				list.add(str.substring(idx1, idx2));
				idx1 = idx2 + 1;
			}
			idx2++;
		}
		list.add(str.substring(idx1, idx2));
		
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
		
		// 피연산자가 하나만 있으면 출력
		System.out.println("결과값: " + list.get(0));		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String str = "10 - 2 * 3";
		
		System.out.println("입력 수식: " + str);
		
		Algorithm1 obj = new Algorithm1(str);
	}

}
