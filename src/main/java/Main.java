import org.koko.lexer.Lexer;
import org.koko.node.Start;
import org.koko.parser.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PushbackReader;

public class Main {

  public static void main(String arguments[]) {

    boolean verbose = false;
    if (arguments.length < 1) {
      System.out.println("usage:");
      System.exit(1);
    }

    if (arguments.length == 2) {
      if (arguments[1].contentEquals("-v")) {
        verbose = true;
      }
    }

    try {
      FileReader infile = new FileReader(arguments[0]);
      Lexer l = new Lexer(new PushbackReader(new BufferedReader(infile), 1024));
      Parser p = new Parser(l);
      Start tree = p.parse();
      Emitter emitter = new Emitter(verbose);
      tree.apply(emitter);
    } catch (Exception e) {
      throw new RuntimeException("\n" + e.getMessage());
    }
  }
}
