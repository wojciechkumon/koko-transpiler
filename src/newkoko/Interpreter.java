package newkoko;

import org.newkoko.analysis.DepthFirstAdapter;
import org.newkoko.node.AAssignStatement;
import org.newkoko.node.ADivExp;
import org.newkoko.node.AGrammar;
import org.newkoko.node.AMinusExp;
import org.newkoko.node.AMulExp;
import org.newkoko.node.ANumberExp;
import org.newkoko.node.APlusExp;
import org.newkoko.node.Node;

import java.util.HashMap;
import java.util.Map;

public class Interpreter extends DepthFirstAdapter {
  private Map<Node, Integer> node2int = new HashMap<>();
  private Map<String, Integer> vars = new HashMap<>();

  private int getNodeInt(Node node) {
    return node2int.get(node);
  }

  private void setNodeInt(Node node, int val) {
    node2int.put(node, val);
  }

  @Override
  public void outAGrammar(AGrammar node) {
//    Iterator<PStatement> it = node.getStatement().iterator();
//    while (it.hasNext()) {
//      PStatement statement = it.next();
//      System.out.print(getNodeInt(statement));
//      if (it.hasNext()) System.out.print("; ");
//    }
    vars.forEach((key, value) -> System.out.println(key + " = " + value));
    System.out.println();
  }

  @Override
  public void outAAssignStatement(AAssignStatement node) {
    int value = getNodeInt(node.getExp());
    String identifier = node.getIdentifier().getText();
    vars.put(identifier, value);
  }


  @Override
  public void outANumberExp(ANumberExp node) {
    setNodeInt(node, Integer.parseInt(node.getNumber().getText()));
  }

  @Override
  public void outAPlusExp(APlusExp node) {
    setNodeInt(node, getNodeInt(node.getL()) + getNodeInt(node.getR()));
  }

  @Override
  public void outAMinusExp(AMinusExp node) {
    setNodeInt(node, getNodeInt(node.getL()) - getNodeInt(node.getR()));
  }

  @Override
  public void outAMulExp(AMulExp node) {
    setNodeInt(node, getNodeInt(node.getL()) * getNodeInt(node.getR()));
  }

  @Override
  public void outADivExp(ADivExp node) {
    // maybe we should check here for division by zero? :)
    setNodeInt(node, getNodeInt(node.getL()) / getNodeInt(node.getR()));
  }
}
