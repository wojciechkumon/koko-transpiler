package calc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import expression.lexer.Lexer;
import expression.node.Start;
import expression.parser.Parser;

public class CalcMain {
  public static void main(String[] argv) {
    try {
      Lexer l = new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(System.in))));
      Parser p = new Parser(l);
      Start start = p.parse();
      System.out.println(start.toString());

      Calculate calculator = new Calculate();
      start.apply(calculator);

      ASTDisplay ad = new ASTDisplay();
      start.apply(ad);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
