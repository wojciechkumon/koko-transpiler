package org.koko.semantics;

import org.koko.analysis.DepthFirstAdapter;
import org.koko.node.ACallExpression;
import org.koko.node.ADoubleConstant;
import org.koko.node.ADoubleType;
import org.koko.node.AForStatement;
import org.koko.node.AFunctionFunctionOrStatement;
import org.koko.node.AIdentifierValue;
import org.koko.node.AIntType;
import org.koko.node.AIntegerConstant;
import org.koko.node.ALongType;
import org.koko.node.ANewVariable;
import org.koko.node.AParameter;
import org.koko.node.AStatementBlock;
import org.koko.node.AStringConstant;
import org.koko.node.TIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class SemanticAnalyzer extends DepthFirstAdapter {
  private final List<String> errors = new ArrayList<>();
  private final Map<String, List<Function>> functions;
  private boolean inCallExpression = false;
  private CalledFunction calledFunction = null;
  private List<Function> foundCalledFunctions = null;
  private Integer functionParamIndex = null;
  private Scope functionScope;
  private Scope mainScope = new MainScope();
  private Scope currentScope = mainScope;
  private String lastParamName;
  private String newVarName = null;
  private String forLoopIdentifier = null;
  private Position forLoopIdentifierPosition = null;

  public SemanticAnalyzer(List<Function> functions) {
    Map<String, List<Function>> functionMap = new HashMap<>();
    functions.forEach(fun ->
        functionMap.merge(fun.getName(), new ArrayList<>(singletonList(fun)), (firstFunList, secondFunList) -> {
          List<Function> newFuns = new ArrayList<>(firstFunList);
          newFuns.addAll(secondFunList);
          return newFuns;
        }));

    this.functions = Collections.unmodifiableMap(functionMap);
  }

  @Override
  public void caseACallExpression(ACallExpression node) {
    inCallExpression = true;
    Position position = new Position(node.getIdentifier().getLine(),
        node.getIdentifier().getPos());
    this.calledFunction = new CalledFunction(node.getIdentifier().getText(), position);
    foundCalledFunctions = this.functions.get(calledFunction.getName());
    if (!calledFunction.getName().equals("printf") && foundCalledFunctions == null) {
      addCallFunctionError(calledFunction);
      inCallExpression = false;
      return;
    }
    functionParamIndex = -1;
    if (node.getArgList() != null) {
      node.getArgList().apply(this);
    }
    functionParamIndex = null;
    foundCalledFunctions = null;
    inCallExpression = false;
  }

  private void addCallFunctionError(CalledFunction calledFunction) {
    errors.add("Function declaration not found for '" + calledFunction.getName() + "' "
        + calledFunction.getPosition());
  }

  private void addParamTypeError(CalledFunction calledFunction, String correctType) {
    errors.add("Can't find function with correct parameter types for '" + calledFunction.getName() + "' "
        + calledFunction.getPosition() + ", parameter index " + functionParamIndex + ", type is " + correctType);
  }

  @Override
  public void outAIntegerConstant(AIntegerConstant node) {
    if (inCallExpression) {
      checkParamTypeForConstant("int");
    }
  }

  @Override
  public void outADoubleConstant(ADoubleConstant node) {
    if (inCallExpression) {
      checkParamTypeForConstant("double");
    }
  }

  @Override
  public void outAStringConstant(AStringConstant node) {
    if (inCallExpression) {
      checkParamTypeForConstant("string");
    }
  }

  private void checkParamTypeForConstant(String type) {
    functionParamIndex++;
    checkParamType(type);
  }

  private void checkParamType(String type) {
    if (calledFunction.getName().equals("printf")) {
      if (functionParamIndex == 0 && !type.equals("string")) {
        addParamTypeError(calledFunction, type);
      }
    } else if (foundCalledFunctions.stream()
        .noneMatch(fun -> fun.getParams().size() <= functionParamIndex
            || type.equals(fun.getParams().get(functionParamIndex).getType()))) {
      addParamTypeError(calledFunction, type);
    }
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

  @Override
  public void caseAParameter(AParameter node) {
    lastParamName = node.getIdentifier().getText();
    node.getType().apply(this);
    lastParamName = null;
  }

  @Override
  public void outAIntType(AIntType node) {
    Position position = new Position(node.getInt().getLine(), node.getInt().getPos());
    if (lastParamName != null) {
      putVarToScope(currentScope, new Var("int", lastParamName), position);
    } else if (newVarName != null) {
      putVarToScope(currentScope, new Var("int", newVarName), position);
    }
  }

  @Override
  public void outALongType(ALongType node) {
    Position position = new Position(node.getLong().getLine(), node.getLong().getPos());
    if (lastParamName != null) {
      putVarToScope(currentScope, new Var("long", lastParamName), position);
    } else if (newVarName != null) {
      putVarToScope(currentScope, new Var("long", newVarName), position);
    }
  }

  @Override
  public void outADoubleType(ADoubleType node) {
    Position position = new Position(node.getDouble().getLine(), node.getDouble().getPos());
    if (lastParamName != null) {
      putVarToScope(currentScope, new Var("double", lastParamName), position);
    } else if (newVarName != null) {
      putVarToScope(currentScope, new Var("double", newVarName), position);
    }
  }

  @Override
  public void caseAIdentifierValue(AIdentifierValue node) {
    if (inCallExpression) {
      functionParamIndex++;
      String identifier = node.getIdentifier().getText();
      Optional<Var> var = currentScope.get(identifier);
      if (!var.isPresent()) {
        addVarNotFoundError(node.getIdentifier());
        return;
      }
      checkParamType(var.get().getType());
    }
  }

  @Override
  public void inAStatementBlock(AStatementBlock node) {
    currentScope = new InnerScope(currentScope);
    if (forLoopIdentifier != null) {
      putVarToScope(currentScope, new Var("int", forLoopIdentifier), forLoopIdentifierPosition);
      forLoopIdentifier = null;
    }
  }

  @Override
  public void outAStatementBlock(AStatementBlock node) {
    InnerScope innerScope = (InnerScope) currentScope;
    currentScope = innerScope.getOuterScope();
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
  public void inAForStatement(AForStatement node) {
    TIdentifier identifier = node.getIdentifier();
    forLoopIdentifier = identifier.getText();
    forLoopIdentifierPosition = new Position(identifier.getLine(), identifier.getPos());
  }

  @Override
  public void outAForStatement(AForStatement node) {
    forLoopIdentifier = null;
  }

  private void putVarToScope(Scope scope, Var var, Position position) {
    boolean success = !var.getName().equals("printf") && scope.put(var);
    if (!success) {
      errors.add("Redefinition of variable: " + var + " " + position);
    }
  }

  private void addVarNotFoundError(TIdentifier identifier) {
    Position position = new Position(identifier.getLine(), identifier.getPos());
    errors.add("Identifier not found: " + identifier.getText() + " " + position);
  }

  public boolean errorsFound() {
    return !errors.isEmpty();
  }

  public String getErrors() {
    return errors.stream()
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
