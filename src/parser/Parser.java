package parser;

import java.time.temporal.ValueRange;


// 파서 에러 처리를 위한 Exception 클래스
class ParserException extends Exception{
	String errStr;	// 에러 정의 문자열
	
	public ParserException(String str) {
		errStr = str;
	}
	
	public String toString() {
		return errStr;
	}
}



public class Parser {

	// TOKEN 타입 (종류) 상수값
	final int NONE = 0;
	final int DELIMITER = 1;	// 연산자와 괄호
	final int VARIABLE = 2;
	final int NUMBER = 3;
	
	// 에러 종류에 대한 상수값
	final int SYNTAX = 0;	// 문맥에 맞지 않는 표현
	final int UNBALPARENS = 1;	// 괄호 부호에 대한 에러
	final int NOEXP = 2;	// 어떤 표현도 존재하지 않음
	final int DIVBYZERO = 3;	// 어떤 수를 0으로 나눔
	
	// 표현식의 끝을 나타내는 상수
	final String EOE = "\0";
	
	private String exp;	// 표현을 담고 있는 문자열
	private int expIdx; 	// 표현의 현재 인덱스
	private String token;	// 현재 인덱싱 된 토큰
	private int tokType;	// 현재 인덱싱 된 토큰의 타입
	
	
	
	// 파서의 시작점, 연산 시작
	public double evaluate(String expstr) throws ParserException{
		double result;
		exp = expstr;
		expIdx = 0;
		
		getToken();
		if(token.equals(EOE))
			handleErr(NOEXP);	// 표현이 존재하지 않음
		
		// 표현을 파싱하고 값을 구한다
		result = evalExp2();
		
		if(!token.equals(EOE)) 
			handleErr(SYNTAX);
			
		return result;
	}
	
	
	// 더하기, 빼기
	private double evalExp2() throws ParserException{
		char op;	// 연산자
		double result;
		double partialResult;
		
		result = evalExp3();
		
		while((op = token.charAt(0)) == '+' || op == '-') {
			getToken();
			partialResult = evalExp3();
			switch(op) {
				case '-':
					result -= partialResult;
					break;
				case '+':
					result += partialResult;
					break;
			}
		}
		return result;
	}
	
	
	// 곱하기 나누기
	private double evalExp3() throws ParserException{
		char op;
		double result;
		double partialResult;
		
		result = evalExp4();
		
		while((op = token.charAt(0)) == '*' || op == '/' || op == '%') {
			getToken();
			partialResult = evalExp4();
			switch (op) {
			case '*':
				result *= partialResult;
				break;
			case '/':
				if (partialResult == 0.0)	// 0으로 나누면 에러
					handleErr(DIVBYZERO);
				result /= partialResult;
				break;
			case '%':
				if (partialResult == 0.0)	// 0으로 나누면 에러
					handleErr(DIVBYZERO);
				result %= partialResult;
				break;
			}
		}
		return result;
	}
	
	
	// 지수
	private double evalExp4() throws ParserException{
		double result;
		double partialResult;
		double ex;
		int t;
		
		result = evalExp5();
		
		if(token.equals('^')) {
			getToken();
			partialResult = evalExp4();
			ex = result;
			if(partialResult == 0.0) 
				result = 1.0;
			else {
				for(t = (int)partialResult - 1; t > 0; t--)
					result = result * ex;
			}
		}
		return result;
	}
	
	
	// 단항의 +, -를 처리
	private double evalExp5() throws ParserException{
		double result;
		String op;
		
		op = "";
		if((tokType == DELIMITER) && token.equals('+') || token.equals('-')) {
			op = token;
			getToken();
		}
		result = evalExp6();
		
		if(op.equals('-'))
			result = -result;
		
		return result;		
	}
	
	// 괄호를 처리
	private double evalExp6() throws ParserException{
		double result;
		
		if(token.contentEquals("(")) {
			getToken();
			result = evalExp2();
			if(!token.equals(")"))
				handleErr(UNBALPARENS);
			getToken();
		}
		else
			result = atom();
		
		return result;
	}
	
	
	// 숫자값을 구한다
	private double atom() throws ParserException{
		double result = 0.0;
		
		switch(tokType) {
			case NUMBER:
				try {
					result = Double.parseDouble(token);
				}catch(NumberFormatException exc) {
					handleErr(SYNTAX);
				}
				getToken();
				break;
			default:
				handleErr(SYNTAX);
				break;
		}
		return result;
	}
	
	
	// 에러 처리
	private void handleErr(int error) throws ParserException{
		String[] err = {
				"Syntax Error\n",
				"Unbalanced Parentheses\n",
				"No Expression Present\n",
				"Division by Zero\n"
		};
		throw new ParserException(err[error]);
	}
	
	
	
	// 토큰을 얻는 메소드
	private void getToken() {
		
		// 초기화
		tokType = NONE;
		token = "";
		
		// 표현의 끝을 확인
		if(expIdx == exp.length()) {
			token = EOE;
			return;
		}
		
		// 공백이면 다음 표현으로 넘어간다.
		while (expIdx < exp.length() && Character.isWhitespace(exp.charAt(expIdx))) 
			++expIdx;
		
		// 표현의 마지막이라면 메소드 종료
		if (expIdx == exp.length()) {
			token = EOE;
			return;
		}
		
		
		// 연산자인지 판별
		if(isDelim(exp.charAt(expIdx))) {
			token += exp.charAt(expIdx);
			expIdx++;
			tokType = DELIMITER;
		}
		// 변수형인지 판별
		else if(Character.isLetter(exp.charAt(expIdx))) {
			while(!isDelim(exp.charAt(expIdx))) {	//연산자가 나올때까지 수행
				token += exp.charAt(expIdx);
				expIdx++;
				if(expIdx >= exp.length())	// 표현식을 넘어가면 종료
					break;
			}
			tokType = VARIABLE;
		}
		// 숫자형인지 판별
		else if(Character.isDigit(exp.charAt(expIdx))) {
			while(!isDelim(exp.charAt(expIdx))) {	// 연산자가 나올때까지 수행
				token += exp.charAt(expIdx);
				expIdx++;
				if(expIdx >= exp.length())	// 표현식 넘어가면 종료
					break;
			}
			tokType = NUMBER;
		}
		// 그 외 정의되지 않은 형이면 표현식이 종료된 것으로 간주
		else {
			token = EOE;
			return;
		}
	}
	
	
	// 연산자인 경우 true 아니면 false
	private boolean isDelim(char c) {
		if((" +-/*%^=()".indexOf(c) != -1))
			return true;
		return false;
	}
	
	
}
