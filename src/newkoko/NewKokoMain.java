package newkoko;

import org.newkoko.lexer.Lexer;
import org.newkoko.node.Start;
import org.newkoko.parser.Parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PushbackReader;

public class NewKokoMain {

  public static void main(String[] argv) {
    try {
      Lexer l = new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(System.in))));
      Parser p = new Parser(l);
      Start start = p.parse();
//      System.out.println(start.toString());

      Interpreter interpreter = new Interpreter();
      start.apply(interpreter);

      AstDisplayer displayer = new AstDisplayer();
      start.apply(displayer);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
