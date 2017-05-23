package newkokoc;

import org.newkoko.c.analysis.DepthFirstAdapter;
import org.newkoko.c.node.AAmpersandAmpersandBinaryOp;
import org.newkoko.c.node.AArgListTail;
import org.newkoko.c.node.AAssignVariable;
import org.newkoko.c.node.ABarBarBinaryOp;
import org.newkoko.c.node.ABinaryExpressionBinaryExpression;
import org.newkoko.c.node.ACallExpression;
import org.newkoko.c.node.ADivBinaryOp;
import org.newkoko.c.node.ADivEqAssignOp;
import org.newkoko.c.node.ADoubleConstant;
import org.newkoko.c.node.ADoubleType;
import org.newkoko.c.node.AEqAssignOp;
import org.newkoko.c.node.AEqEqRelationOp;
import org.newkoko.c.node.AExclMarkUnaryOp;
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
import org.newkoko.c.node.AMinusBinaryOp;
import org.newkoko.c.node.AMinusEqAssignOp;
import org.newkoko.c.node.AMinusUnaryOp;
import org.newkoko.c.node.AModBinaryOp;
import org.newkoko.c.node.AModEqAssignOp;
import org.newkoko.c.node.ANewVariable;
import org.newkoko.c.node.ANextParameter;
import org.newkoko.c.node.ANotEqRelationOp;
import org.newkoko.c.node.AParameter;
import org.newkoko.c.node.APlusBinaryOp;
import org.newkoko.c.node.APlusEqAssignOp;
import org.newkoko.c.node.ARelationConditionalExpression;
import org.newkoko.c.node.AReturnStopStatement;
import org.newkoko.c.node.AReturnValueStopStatement;
import org.newkoko.c.node.ASimpleStatementStatement;
import org.newkoko.c.node.AStarBinaryOp;
import org.newkoko.c.node.AStarEqAssignOp;
import org.newkoko.c.node.AStatementBlock;
import org.newkoko.c.node.AStatementFunctionOrStatement;
import org.newkoko.c.node.AStringConstant;
import org.newkoko.c.node.AUnaryOpUnaryExpression;
import org.newkoko.c.node.AVoidFunctionType;
import org.newkoko.c.node.AWhileStatement;
import org.newkoko.c.node.Start;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.stream.IntStream;

public class KokoCodeGenerator extends DepthFirstAdapter implements Closeable {
  private final OutputStreamWriter out;
  private final StringBuilder functionDeclarations = new StringBuilder();
  private final StringBuilder body = new StringBuilder();
  private final StringBuilder main = new StringBuilder();
  private StringBuilder currentBuilder = body;
  private int tabCount = 0;
  private boolean inFunctionDeclaration = false;


  public KokoCodeGenerator(OutputStream out) {
    this.out = new OutputStreamWriter(out);
  }

  @Override
  public void outStart(Start node) {
    currentBuilder = main;
    printlnWithTabs("return 0;");
    try {
      out.write("#include <stdio.h>\n\n\n");
      out.write(functionDeclarations.toString());
      out.write("\n\n");
      out.write(body.toString());
      out.write("int main() {\n");
      out.write(main.toString());
      out.write("}");
    } catch (IOException e) {
      throw new RuntimeException("Error while saving C code", e);
    }
  }

  @Override
  public void inAIfStatement(AIfStatement node) {
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
  public void outADoubleConstant(ADoubleConstant node) {
    print(node.getFloatingConstant().getText());
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
  public void caseAFunctionDeclaration(AFunctionDeclaration node) {
    inAFunctionDeclaration(node);
    inFunctionDeclaration = true;
    if (node.getFunctionType() != null) {
      node.getFunctionType().apply(this);
    }
    print(node.getIdentifier().getText());
    functionDeclarations.append(node.getIdentifier().getText());
    print("(");
    functionDeclarations.append("(");
    if (node.getParameterList() != null) {
      node.getParameterList().apply(this);
    }
    print(")");
    functionDeclarations.append(");\n");
    inFunctionDeclaration = false;
    outAFunctionDeclaration(node);
  }

  @Override
  public void outAVoidFunctionType(AVoidFunctionType node) {
    print("void ");
    functionDeclarations.append("void ");
  }

  @Override
  public void caseAParameter(AParameter node) {
    inAParameter(node);
    node.getType().apply(this);

    print(node.getIdentifier().getText());
    functionDeclarations.append(node.getIdentifier().getText());
    outAParameter(node);
  }

  @Override
  public void caseANextParameter(ANextParameter node) {
    inANextParameter(node);
    print(", ");
    functionDeclarations.append(", ");
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
    if (inFunctionDeclaration) {
      functionDeclarations.append("int ");
    }
  }

  @Override
  public void outALongType(ALongType node) {
    print("long long ");
    if (inFunctionDeclaration) {
      functionDeclarations.append("long long ");
    }
  }

  @Override
  public void outADoubleType(ADoubleType node) {
    print("double ");
    if (inFunctionDeclaration) {
      functionDeclarations.append("double ");
    }
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
  public void outAPlusEqAssignOp(APlusEqAssignOp node) {
    print(node.getPlusEqual().getText());
  }

  @Override
  public void outAMinusEqAssignOp(AMinusEqAssignOp node) {
    print(node.getMinusEqual().getText());
  }

  @Override
  public void outAStarEqAssignOp(AStarEqAssignOp node) {
    print(node.getStarEqual().getText());
  }

  @Override
  public void outADivEqAssignOp(ADivEqAssignOp node) {
    print(node.getDivEqual().getText());
  }

  @Override
  public void outAModEqAssignOp(AModEqAssignOp node) {
    print(node.getModEqual().getText());
  }

  @Override
  public void outAStarBinaryOp(AStarBinaryOp node) {
    print(node.getStar().getText());
  }

  @Override
  public void outADivBinaryOp(ADivBinaryOp node) {
    print(node.getDiv().getText());
  }

  @Override
  public void outAModBinaryOp(AModBinaryOp node) {
    print(node.getMod().getText());
  }

  @Override
  public void outAPlusBinaryOp(APlusBinaryOp node) {
    print(node.getPlus().getText());
  }

  @Override
  public void outAMinusBinaryOp(AMinusBinaryOp node) {
    print(node.getMinus().getText());
  }

  @Override
  public void outAAmpersandAmpersandBinaryOp(AAmpersandAmpersandBinaryOp node) {
    print(node.getAmpersandAmpersand().getText());
  }

  @Override
  public void outABarBarBinaryOp(ABarBarBinaryOp node) {
    print(node.getBarBar().getText());
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

  public void inAArgListTail(AArgListTail node) {
    print(", ");
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

    currentBuilder.deleteCharAt(currentBuilder.length() - 1);
    print(" else");
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

  @Override
  public void outAMinusUnaryOp(AMinusUnaryOp node) {
    print("-");
  }

  @Override
  public void outAExclMarkUnaryOp(AExclMarkUnaryOp node) {
    print("!");
  }

  @Override
  public void outAUnaryOpUnaryExpression(AUnaryOpUnaryExpression node) {
    print(node.getIdentifier().getText());
  }

  @Override
  public void caseABinaryExpressionBinaryExpression(ABinaryExpressionBinaryExpression node) {
    inABinaryExpressionBinaryExpression(node);
    node.getLValue().apply(this);
    print(" ");
    node.getBinaryOp().apply(this);
    print(" ");
    node.getRValue().apply(this);
    outABinaryExpressionBinaryExpression(node);
  }

  public void inAStatementFunctionOrStatement(AStatementFunctionOrStatement node) {
    currentBuilder = main;
  }

  public void outAStatementFunctionOrStatement(AStatementFunctionOrStatement node) {
    currentBuilder = body;
  }

  private void printTabs() {
    int tabs = 2 * tabCount;
    if (currentBuilder == main) {
      tabs += 2;
    }
    IntStream.range(0, tabs)
        .forEach(x -> print(" "));
  }

  private void printWithTabs(String text) {
    printTabs();
    print(text);
  }

  private void print(String text) {
    currentBuilder.append(text);
  }

  private void println() {
    currentBuilder.append('\n');
  }

  private void println(String text) {
    print(text);
    println();
  }

  private void printlnWithTabs(String text) {
    printWithTabs(text);
    println();
  }

  @Override
  public void close() throws IOException {
    this.out.close();
  }
}
