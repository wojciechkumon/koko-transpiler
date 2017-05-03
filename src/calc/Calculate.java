package calc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import expression.analysis.DepthFirstAdapter;
import expression.node.ADivExp;
import expression.node.AGrammar;
import expression.node.AMinusExp;
import expression.node.AMultExp;
import expression.node.ANumberExp;
import expression.node.APlusExp;
import expression.node.ARandomX2Exp;
import expression.node.AT1Textual;
import expression.node.AT2Textual;
import expression.node.AT3Textual;
import expression.node.ATextualExp;
import expression.node.Node;
import expression.node.PExp;
import expression.node.PTextual;

public class Calculate extends DepthFirstAdapter {
  private Map<Node, Integer> node2int = new HashMap<>();
  private Random rnd = new Random();

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

  public void outAT1Textual(AT1Textual node) {
    setNodeInt(node, 1);
  }

  public void outAT2Textual(AT2Textual node) {
    setNodeInt(node, 2);
  }

  public void outAT3Textual(AT3Textual node) {
    setNodeInt(node, 3);
  }

  public void outATextualExp(ATextualExp node) {
    int res = 0;
    int mul = 1;
    ListIterator<PTextual> it = node.getTextual().listIterator(node.getTextual().size());
    while (it.hasPrevious()) {
      PTextual t = it.previous();
      res += mul * getNodeInt(t);
      mul *= 10;
    }
    setNodeInt(node, res);
  }

  public void outARandomX2Exp(ARandomX2Exp node) {
    setNodeInt(node, rnd.nextInt(100));
  }
}


