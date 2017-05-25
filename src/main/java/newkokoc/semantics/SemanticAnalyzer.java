package newkokoc.semantics;

import org.newkoko.c.analysis.DepthFirstAdapter;

import java.util.List;

public class SemanticAnalyzer extends DepthFirstAdapter {
  private final List<Function> functions;

  public SemanticAnalyzer(List<Function> functions) {
    this.functions = functions;
  }
}
