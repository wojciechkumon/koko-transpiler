package newkoko;

import org.newkoko.lexer.Lexer;
import org.newkoko.node.Start;
import org.newkoko.parser.Parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;

public class NewKokoMain {

  public static void main(String[] argv) {
    try {
      InputStream inputStream = NewKokoMain.class.getClassLoader().getResourceAsStream("test.koko");
//      InputStream inputStream = System.in;


      Lexer l = new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(inputStream))));
      Parser p = new Parser(l);
      Start start = p.parse();
//      System.out.println(start.toString());

      start.apply(new Interpreter());

//      start.apply(new AstDisplayer());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
