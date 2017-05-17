package newkokoc;

import org.newkoko.c.analysis.DepthFirstAdapter;
import org.newkoko.c.node.AGtRelationOp;
import org.newkoko.c.node.AIfStatement;
import org.newkoko.c.node.AIntegerConstant;
import org.newkoko.c.node.ARelationConditionalExpression;
import org.newkoko.c.node.AReturnValueStopStatement;
import org.newkoko.c.node.AStatementBlock;

public class KokoCodeGenerator extends DepthFirstAdapter {

  @Override
  public void inAIfStatement(AIfStatement node) {
    System.out.println();
    System.out.print("if ");
  }

  @Override
  public void outAGtRelationOp(AGtRelationOp node) {
    System.out.print(" > ");
  }

  @Override
  public void outAIntegerConstant(AIntegerConstant node) {
    System.out.print(node.getIntegerConstant().getText());
  }

  @Override
  public void inARelationConditionalExpression(ARelationConditionalExpression node) {
    System.out.print("(");
  }

  @Override
  public void outARelationConditionalExpression(ARelationConditionalExpression node) {
    System.out.print(")");
  }

  @Override
  public void inAStatementBlock(AStatementBlock node) {
    System.out.println(" {");
  }

  @Override
  public void outAStatementBlock(AStatementBlock node) {
    System.out.println("}");
  }

  @Override
  public void inAReturnValueStopStatement(AReturnValueStopStatement node) {
    System.out.print("return ");
  }

  @Override
  public void outAReturnValueStopStatement(AReturnValueStopStatement node) {
    System.out.println(";");
  }

  @Override
  public void outAIfStatement(AIfStatement node) {
    System.out.println();
  }
}
