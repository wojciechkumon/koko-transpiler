package newkokoc;

import org.newkoko.c.analysis.DepthFirstAdapter;
import org.newkoko.c.node.AAssignVariable;
import org.newkoko.c.node.ACallExpression;
import org.newkoko.c.node.ADoubleType;
import org.newkoko.c.node.AEqAssignOp;
import org.newkoko.c.node.AEqEqRelationOp;
import org.newkoko.c.node.AForStatement;
import org.newkoko.c.node.AFunctionBody;
import org.newkoko.c.node.AFunctionDeclaration;
import org.newkoko.c.node.AGtEqRelationOp;
import org.newkoko.c.node.AGtRelationOp;
import org.newkoko.c.node.AIdentifierValue;
import org.newkoko.c.node.AIfElseStatement;
import org.newkoko.c.node.AIfStatement;
import org.newkoko.c.node.AIntType;
import org.newkoko.c.node.AIntegerConstant;
import org.newkoko.c.node.ALongType;
import org.newkoko.c.node.ALtEqRelationOp;
import org.newkoko.c.node.ALtRelationOp;
import org.newkoko.c.node.ANewVariable;
import org.newkoko.c.node.ANextParameter;
import org.newkoko.c.node.ANotEqRelationOp;
import org.newkoko.c.node.AParameter;
import org.newkoko.c.node.ARelationConditionalExpression;
import org.newkoko.c.node.AReturnStopStatement;
import org.newkoko.c.node.AReturnValueStopStatement;
import org.newkoko.c.node.ASimpleStatementStatement;
import org.newkoko.c.node.AStatementBlock;
import org.newkoko.c.node.AStringConstant;
import org.newkoko.c.node.AVoidFunctionType;
import org.newkoko.c.node.AWhileStatement;
import org.newkoko.c.node.Start;

import java.util.stream.IntStream;

public class KokoCodeGenerator extends DepthFirstAdapter {
  private int tabCount = 0;

  @Override
  public void inStart(Start node) {
    println("#include <stdio.h>");
    println();
    println();
  }

  @Override
  public void inAIfStatement(AIfStatement node) {
    println();
    printWithTabs("if ");
  }

  @Override
  public void outAEqEqRelationOp(AEqEqRelationOp node) {
    print(" == ");
  }

  @Override
  public void outANotEqRelationOp(ANotEqRelationOp node) {
    print(" != ");
  }

  @Override
  public void outALtRelationOp(ALtRelationOp node) {
    print(" < ");
  }

  @Override
  public void outALtEqRelationOp(ALtEqRelationOp node) {
    print(" <= ");
  }

  @Override
  public void outAGtRelationOp(AGtRelationOp node) {
    print(" > ");
  }

  @Override
  public void outAGtEqRelationOp(AGtEqRelationOp node) {
    print(" >= ");
  }

  @Override
  public void outAIntegerConstant(AIntegerConstant node) {
    print(node.getIntegerConstant().getText());
  }

  @Override
  public void inARelationConditionalExpression(ARelationConditionalExpression node) {
    print("(");
  }

  @Override
  public void outARelationConditionalExpression(ARelationConditionalExpression node) {
    print(")");
  }

  @Override
  public void inAStatementBlock(AStatementBlock node) {
    println(" {");
    tabCount++;
  }

  @Override
  public void outAStatementBlock(AStatementBlock node) {
    tabCount--;
    printlnWithTabs("}");
  }

  @Override
  public void inAReturnValueStopStatement(AReturnValueStopStatement node) {
    printWithTabs("return ");
  }

  @Override
  public void outAReturnValueStopStatement(AReturnValueStopStatement node) {
    println(";");
  }

  public void outAReturnStopStatement(AReturnStopStatement node) {
    printlnWithTabs("return;");
  }

  @Override
  public void outAIfStatement(AIfStatement node) {
    println();
  }

  @Override
  public void caseAFunctionDeclaration(AFunctionDeclaration node) {
    inAFunctionDeclaration(node);
    if (node.getFunctionType() != null) {
      node.getFunctionType().apply(this);
    }
    print(node.getIdentifier().getText());
    print("(");
    if (node.getParameterList() != null) {
      node.getParameterList().apply(this);
    }
    print(")");
    outAFunctionDeclaration(node);
  }

  @Override
  public void outAVoidFunctionType(AVoidFunctionType node) {
    print("void ");
  }

  @Override
  public void caseAParameter(AParameter node) {
    inAParameter(node);
    node.getType().apply(this);

    print(node.getIdentifier().getText());
    outAParameter(node);
  }

  @Override
  public void caseANextParameter(ANextParameter node) {
    inANextParameter(node);
    print(", ");
    node.getParameter().apply(this);
    outANextParameter(node);
  }

  @Override
  public void outAIdentifierValue(AIdentifierValue node) {
    print(node.getIdentifier().getText());
  }

  @Override
  public void outAIntType(AIntType node) {
    print("int ");
  }

  @Override
  public void outALongType(ALongType node) {
    print("long long ");
  }

  @Override
  public void outADoubleType(ADoubleType node) {
    print("double ");
  }

  @Override
  public void inAFunctionBody(AFunctionBody node) {
    println(" {");
    tabCount++;
  }

  @Override
  public void outAFunctionBody(AFunctionBody node) {
    tabCount--;
    printlnWithTabs("}");
    println();
  }

  @Override
  public void inASimpleStatementStatement(ASimpleStatementStatement node) {
    printTabs();
  }

  @Override
  public void outASimpleStatementStatement(ASimpleStatementStatement node) {
    println(";");
  }

  @Override
  public void caseANewVariable(ANewVariable node) {
    inANewVariable(node);
    node.getType().apply(this);
    print(node.getIdentifier().getText());
    print(" = ");
    node.getRightHandSide().apply(this);
    outANewVariable(node);
  }

  @Override
  public void caseAAssignVariable(AAssignVariable node) {
    inAAssignVariable(node);

    print(node.getIdentifier().getText());
    print(" ");
    node.getAssignOp().apply(this);
    print(" ");
    node.getRightHandSide().apply(this);
    outAAssignVariable(node);
  }

  @Override
  public void outAEqAssignOp(AEqAssignOp node) {
    print(node.getEq().getText());
  }

  @Override
  public void caseACallExpression(ACallExpression node) {
    inACallExpression(node);
    print(node.getIdentifier().getText());
    print("(");
    node.getArgList().apply(this);
    print(")");
    outACallExpression(node);
  }

  @Override
  public void outAStringConstant(AStringConstant node) {
    print(node.getStringLiteral().getText());
  }

  @Override
  public void caseAIfElseStatement(AIfElseStatement node) {
    inAIfElseStatement(node);
    printWithTabs("if ");
    node.getConditionalExpression().apply(this);
    node.getIfBlock().apply(this);

    printWithTabs("else");
    node.getElseBlock().apply(this);
    outAIfElseStatement(node);
  }

  @Override
  public void inAWhileStatement(AWhileStatement node) {
    printWithTabs("while ");
  }

  @Override
  public void caseAForStatement(AForStatement node) {
    inAForStatement(node);
    printWithTabs("int ");
    print(node.getIdentifier().getText());
    println(";");
    printWithTabs("for (");
    print(node.getIdentifier().getText());
    print(" = ");
    node.getFrom().apply(this);
    print("; ");
    print(node.getIdentifier().getText());
    print(" <= ");
    node.getTo().apply(this);
    print("; ");
    print(node.getIdentifier().getText());
    print("++)");
    node.getStatementBlock().apply(this);
    outAForStatement(node);
  }

  private void printTabs() {
    IntStream.range(0, 2 * tabCount)
        .forEach(x -> print(" "));
  }

  private void printWithTabs(String text) {
    printTabs();
    print(text);
  }

  private void print(String text) {
    System.out.print(text);
  }

  private void println() {
    System.out.println();
  }

  private void println(String text) {
    System.out.println(text);
  }

  private void printlnWithTabs(String text) {
    printWithTabs(text);
    println();
  }
}