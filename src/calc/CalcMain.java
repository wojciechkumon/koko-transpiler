package calc;

import java.lang.*;
import java.io.*;
import java.util.*;

import expression.parser.*;
import expression.lexer.*;
import expression.node.*;

public class CalcMain {
  public static void main (String[] argv)
  {
    try {
      Lexer l = new Lexer (new PushbackReader (new BufferedReader(new InputStreamReader (System.in))));
      Parser p = new Parser (l);
      Start start = p.parse ();
//      System.out.println (start.toString());

      Calculate calculator = new Calculate ();
      start.apply (calculator);

      ASTDisplay ad = new ASTDisplay ();
      start.apply (ad);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
};

