package newkokoc;

import org.newkoko.c.lexer.Lexer;
import org.newkoko.c.node.Start;
import org.newkoko.c.parser.Parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import newkoko.NewKokoMain;

public class NewKokoC {

  public static void main(String[] argv) {
    try {
      InputStream inputStream = NewKokoMain.class.getClassLoader().getResourceAsStream("new_koko_c.koko");
//      InputStream inputStream = System.in;


      Lexer l = new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(inputStream))));
      Parser p = new Parser(l);
      Start start = p.parse();
      System.out.println(start.toString());

//      start.apply(new Interpreter());
//      start.apply(new KokoCompiler());

//      start.apply(new AstDisplayer());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}