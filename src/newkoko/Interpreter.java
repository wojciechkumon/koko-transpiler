package newkoko;

import org.newkoko.analysis.DepthFirstAdapter;
import org.newkoko.node.AAssignNewDoubleStatement;
import org.newkoko.node.AAssignNewIntStatement;
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

import newkoko.excepions.VarNotExistException;

public class Interpreter extends DepthFirstAdapter {
  private Map<Node, Integer> node2int = new HashMap<>();
  private Map<Node, Double> node2double = new HashMap<>();
  private Map<String, Var> vars = new HashMap<>();

  private Integer getNodeInt(Node node) {
    return node2int.get(node);
  }

  private void setNodeInt(Node node, int val) {
    node2int.put(node, val);
  }

  private Double getNodeDouble(Node node) {
    return node2double.get(node);
  }

  private void setNodeDouble(Node node, double val) {
    node2double.put(node, val);
  }

  @Override
  public void outAGrammar(AGrammar node) {
    System.out.println();
    System.out.println("Final values:");
    vars.forEach((key, value) -> System.out.println(key + " = " + value.getValue()));
  }

  @Override
  public void outAAssignNewIntStatement(AAssignNewIntStatement node) {
    int value = getNodeInt(node.getExp());
    String identifier = node.getIdentifier().getText();
    vars.put(identifier, new Var(Var.INT, value));
    System.out.println(identifier + " = " + value);
  }

  @Override
  public void outAAssignNewDoubleStatement(AAssignNewDoubleStatement node) {
    double value = getNodeDouble(node.getExp());
    String identifier = node.getIdentifier().getText();
    vars.put(identifier, new Var(Var.DOUBLE, value));
    System.out.println(identifier + " = " + value);
  }

  @Override
  public void outAAssignStatement(AAssignStatement node) {
    String identifier = node.getIdentifier().getText();
    Var var = vars.get(identifier);
    if (var == null) {
      throw new VarNotExistException(identifier);
    }

    String type = var.getType();
    if (Var.INT.equals(type)) {
      Integer intValue = getNodeInt(node.getExp());
      if (intValue == null) {
        throw new RuntimeException("Not int value for " + identifier);
      }
      vars.put(identifier, new Var(type, intValue));
      System.out.println(identifier + " = " + intValue);
    } else {
      throw new RuntimeException("Not matching type for: " + identifier + ", " + type);
    }
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
    int left = getNodeInt(node.getL());
    int right = getNodeInt(node.getR());
    if (right == 0) {
      throw new ArithmeticException("You can't divide by zero! (" + left + "/" + right + ")");
    }
    setNodeInt(node, left / right);
  }
}
