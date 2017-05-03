package newkoko;

import org.newkoko.analysis.DepthFirstAdapter;
import org.newkoko.node.ADivExp;
import org.newkoko.node.AGrammar;
import org.newkoko.node.AMinusExp;
import org.newkoko.node.AMultExp;
import org.newkoko.node.ANumberExp;
import org.newkoko.node.APlusExp;
import org.newkoko.node.Node;
import org.newkoko.node.PExp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Interpreter extends DepthFirstAdapter {
  private Map<Node, Integer> node2int = new HashMap<>();

  private int getNodeInt(Node node) {
    return node2int.get(node);
  }

  private void setNodeInt(Node node, int val) {
    node2int.put(node, val);
  }

  public void outAGrammar(AGrammar node) {
    Iterator<PExp> it = node.getExp().iterator();
    while (it.hasNext()) {
      PExp exp = it.next();
      System.out.print(getNodeInt(exp));
      if (it.hasNext()) System.out.print("; ");
    }
    System.out.println();
  }

  public void outANumberExp(ANumberExp node) {
    setNodeInt(node, Integer.parseInt(node.getNumber().getText()));
  }

  public void outAPlusExp(APlusExp node) {
    setNodeInt(node, getNodeInt(node.getL()) + getNodeInt(node.getR()));
  }

  public void outAMinusExp(AMinusExp node) {
    setNodeInt(node, getNodeInt(node.getL()) - getNodeInt(node.getR()));
  }

  public void outAMultExp(AMultExp node) {
    setNodeInt(node, getNodeInt(node.getL()) * getNodeInt(node.getR()));
  }

  public void outADivExp(ADivExp node) {
    // maybe we should check here for division by zero? :)
    setNodeInt(node, getNodeInt(node.getL()) / getNodeInt(node.getR()));
  }
}
