package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PDemo {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		String expr;	// 표현식
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Parser p = new Parser();
		
		System.out.println("Enter an empty expression to stop.");
		
		for(;;) {
			System.out.print("Enter expression: ");
			expr = br.readLine();
			
			// 공백 입력하면 프로그램 중지
			if(expr.equals(""))
				break;
			try {
				System.out.println("Result: " + p.evaluate(expr));
				System.out.println();
			}catch(ParserException exc) {
				System.out.println(exc);
			}
			
		}
	}

}
