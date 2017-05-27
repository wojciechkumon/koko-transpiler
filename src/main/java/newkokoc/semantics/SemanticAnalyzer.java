package newkokoc.semantics;

import org.newkoko.c.analysis.DepthFirstAdapter;
import org.newkoko.c.node.ACallExpression;
import org.newkoko.c.node.ADoubleConstant;
import org.newkoko.c.node.ADoubleType;
import org.newkoko.c.node.AForStatement;
import org.newkoko.c.node.AFunctionFunctionOrStatement;
import org.newkoko.c.node.AIdentifierValue;
import org.newkoko.c.node.AIntType;
import org.newkoko.c.node.AIntegerConstant;
import org.newkoko.c.node.ALongType;
import org.newkoko.c.node.ANewVariable;
import org.newkoko.c.node.AParameter;
import org.newkoko.c.node.AStatementBlock;
import org.newkoko.c.node.AStringConstant;
import org.newkoko.c.node.TIdentifier;

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
    node.getArgList().apply(this);
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
    if (lastParamName != null) {
      currentScope.put(new Var("int", lastParamName));
    } else if (newVarName != null) {
      currentScope.put(new Var("int", newVarName));
    }
  }

  @Override
  public void outALongType(ALongType node) {
    if (lastParamName != null) {
      currentScope.put(new Var("long", lastParamName));
    } else if (newVarName != null) {
      currentScope.put(new Var("long", newVarName));
    }
  }

  @Override
  public void outADoubleType(ADoubleType node) {
    if (lastParamName != null) {
      currentScope.put(new Var("double", lastParamName));
    } else if (newVarName != null) {
      currentScope.put(new Var("double", newVarName));
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
      // TODO type checks for right hand value when assigning
    }
  }

  @Override
  public void inAStatementBlock(AStatementBlock node) {
    currentScope = new InnerScope(currentScope);
    if (forLoopIdentifier != null) {
      currentScope.put(new Var("int", forLoopIdentifier));
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
    forLoopIdentifier = node.getIdentifier().getText();
  }

  @Override
  public void outAForStatement(AForStatement node) {
    forLoopIdentifier = null;
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
