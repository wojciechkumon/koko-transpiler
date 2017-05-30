package org.koko.generator;

import org.koko.analysis.DepthFirstAdapter;
import org.koko.node.AAmpersandAmpersandBinaryOp;
import org.koko.node.AArgListTail;
import org.koko.node.AAssignVariable;
import org.koko.node.ABarBarBinaryOp;
import org.koko.node.ABinaryExpressionBinaryExpression;
import org.koko.node.ACallExpression;
import org.koko.node.ADivBinaryOp;
import org.koko.node.ADivEqAssignOp;
import org.koko.node.ADoubleConstant;
import org.koko.node.ADoubleType;
import org.koko.node.AEqAssignOp;
import org.koko.node.AEqEqRelationOp;
import org.koko.node.AExclMarkUnaryOp;
import org.koko.node.AForStatement;
import org.koko.node.AFunctionBody;
import org.koko.node.AFunctionDeclaration;
import org.koko.node.AFunctionFunctionOrStatement;
import org.koko.node.AGtEqRelationOp;
import org.koko.node.AGtRelationOp;
import org.koko.node.AIdentifierValue;
import org.koko.node.AIfElseStatement;
import org.koko.node.AIfStatement;
import org.koko.node.AIntType;
import org.koko.node.AIntegerConstant;
import org.koko.node.ALongType;
import org.koko.node.ALtEqRelationOp;
import org.koko.node.ALtRelationOp;
import org.koko.node.AMinusBinaryOp;
import org.koko.node.AMinusEqAssignOp;
import org.koko.node.AMinusUnaryOp;
import org.koko.node.AModBinaryOp;
import org.koko.node.AModEqAssignOp;
import org.koko.node.ANewVariable;
import org.koko.node.ANextParameter;
import org.koko.node.ANotEqRelationOp;
import org.koko.node.AParameter;
import org.koko.node.APlusBinaryOp;
import org.koko.node.APlusEqAssignOp;
import org.koko.node.ARelationConditionalExpression;
import org.koko.node.AReturnStopStatement;
import org.koko.node.AReturnValueStopStatement;
import org.koko.node.ASimpleStatementStatement;
import org.koko.node.AStarBinaryOp;
import org.koko.node.AStarEqAssignOp;
import org.koko.node.AStatementBlock;
import org.koko.node.AStatementFunctionOrStatement;
import org.koko.node.AStringConstant;
import org.koko.node.AUnaryOpUnaryExpression;
import org.koko.node.AVoidFunctionType;
import org.koko.node.AWhileStatement;
import org.koko.node.Start;
import org.koko.semantics.InnerScope;
import org.koko.semantics.MainScope;
import org.koko.semantics.Scope;
import org.koko.semantics.Var;

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
  private boolean inFunctionNameGenerating = false;
  private Scope functionScope;
  private Scope mainScope = new MainScope();
  private Scope currentScope = mainScope;
  private String forLoopIdentifier = null;
  private String lastParamName;
  private String newVarName = null;


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
    if (inFunctionNameGenerating) {
      print("int_");
      return;
    }
    print(node.getIntegerConstant().getText());
  }

  @Override
  public void outADoubleConstant(ADoubleConstant node) {
    if (inFunctionNameGenerating) {
      print("double_");
      return;
    }
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
    currentScope = new InnerScope(currentScope);
    if (forLoopIdentifier != null) {
      currentScope.put(new Var("int", forLoopIdentifier));
      forLoopIdentifier = null;
    }
  }

  @Override
  public void outAStatementBlock(AStatementBlock node) {
    tabCount--;
    printlnWithTabs("}");
    InnerScope innerScope = (InnerScope) currentScope;
    currentScope = innerScope.getOuterScope();
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
    print("_");
    functionDeclarations.append(node.getIdentifier().getText());
    functionDeclarations.append("_");
    if (node.getParameterList() != null) {
      inFunctionNameGenerating = true;
      node.getParameterList().apply(this);
      inFunctionNameGenerating = false;
    }

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
    lastParamName = node.getIdentifier().getText();
    node.getType().apply(this);
    lastParamName = null;

    if (!inFunctionNameGenerating) {
      print(node.getIdentifier().getText());
      functionDeclarations.append(node.getIdentifier().getText());
    }
    outAParameter(node);
  }

  @Override
  public void caseANextParameter(ANextParameter node) {
    inANextParameter(node);
    if (!inFunctionNameGenerating) {
      print(", ");
      functionDeclarations.append(", ");
    }
    node.getParameter().apply(this);
    outANextParameter(node);
  }

  @Override
  public void outAIdentifierValue(AIdentifierValue node) {
    if (inFunctionNameGenerating) {
      print(currentScope.get(node.getIdentifier().getText()).get().getType());
      print("_");
      return;
    }
    print(node.getIdentifier().getText());
  }

  @Override
  public void outAIntType(AIntType node) {
    if (lastParamName != null) {
      currentScope.put(new Var("int", lastParamName));
    } else if (newVarName != null) {
      currentScope.put(new Var("int", newVarName));
    }
    if (inFunctionNameGenerating) {
      print("int_");
      functionDeclarations.append("int_");
      return;
    }
    print("int ");
    if (inFunctionDeclaration) {
      functionDeclarations.append("int ");
    }
  }

  @Override
  public void outALongType(ALongType node) {
    if (lastParamName != null) {
      currentScope.put(new Var("long", lastParamName));
    } else if (newVarName != null) {
      currentScope.put(new Var("long", newVarName));
    }
    if (inFunctionNameGenerating) {
      print("long_");
      functionDeclarations.append("long_");
      return;
    }
    print("long long ");
    if (inFunctionDeclaration) {
      functionDeclarations.append("long long ");
    }
  }

  @Override
  public void outADoubleType(ADoubleType node) {
    if (lastParamName != null) {
      currentScope.put(new Var("double", lastParamName));
    } else if (newVarName != null) {
      currentScope.put(new Var("double", newVarName));
    }
    if (inFunctionNameGenerating) {
      print("double_");
      functionDeclarations.append("double_");
      return;
    }
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
  public void inANewVariable(ANewVariable node) {
    newVarName = node.getIdentifier().getText();
  }

  @Override
  public void outANewVariable(ANewVariable node) {
    newVarName = null;
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
    if (!node.getIdentifier().getText().equals("printf")) {
      print("_");
      inFunctionNameGenerating = true;
      if (node.getArgList() != null) {
        node.getArgList().apply(this);
      }
      inFunctionNameGenerating = false;
    }
    print("(");
    if (node.getArgList() != null) {
      node.getArgList().apply(this);
    }
    print(")");
    outACallExpression(node);
  }

  public void inAArgListTail(AArgListTail node) {
    if (!inFunctionNameGenerating) {
      print(", ");
    }
  }

  @Override
  public void outAStringConstant(AStringConstant node) {
    if (inFunctionNameGenerating) {
      print("string_");
      return;
    }
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
  public void inAForStatement(AForStatement node) {
    forLoopIdentifier = node.getIdentifier().getText();
  }

  @Override
  public void outAForStatement(AForStatement node) {
    forLoopIdentifier = null;
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

  @Override
  public void inAFunctionFunctionOrStatement(AFunctionFunctionOrStatement node) {
    functionScope = new MainScope();
    currentScope = functionScope;
  }

  @Override
  public void outAFunctionFunctionOrStatement(AFunctionFunctionOrStatement node) {
    functionScope = null;
    currentScope = mainScope;
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
