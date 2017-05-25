package newkokoc.semantics;

import org.newkoko.c.analysis.DepthFirstAdapter;
import org.newkoko.c.node.ACallExpression;
import org.newkoko.c.node.ADoubleConstant;
import org.newkoko.c.node.ADoubleType;
import org.newkoko.c.node.AFunctionFunctionOrStatement;
import org.newkoko.c.node.AIdentifierValue;
import org.newkoko.c.node.AIntType;
import org.newkoko.c.node.AIntegerConstant;
import org.newkoko.c.node.ALongType;
import org.newkoko.c.node.AParameter;
import org.newkoko.c.node.AStringConstant;
import org.newkoko.c.node.TIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class SemanticAnalyzer extends DepthFirstAdapter {
  private final List<String> errors = new ArrayList<>();
  private final Map<String, List<Function>> functions;
  private boolean inCallExpression = false;
  private CalledFunction calledFunction = null;
  private List<Function> foundCalledFunctions = null;
  private Integer functionParamIndex = null;
  private Map<String, Var> functionScope;
  private String lastParamName;

  public SemanticAnalyzer(List<Function> functions) {
    Map<String, List<Function>> functionMap = new HashMap<>();
    functions.forEach(fun -> {
      functionMap.merge(fun.getName(), new ArrayList<>(singletonList(fun)), (firstFunList, secondFunList) -> {
        List<Function> newFuns = new ArrayList<>(firstFunList);
        newFuns.addAll(secondFunList);
        return newFuns;
      });
    });

    this.functions = Collections.unmodifiableMap(functionMap);
  }

  @Override
  public void caseACallExpression(ACallExpression node) {
    inCallExpression = true;
    Position position = new Position(node.getIdentifier().getLine(),
        node.getIdentifier().getPos());
    this.calledFunction = new CalledFunction(node.getIdentifier().getText(), position);
    foundCalledFunctions = this.functions.get(calledFunction.getName());
    if (foundCalledFunctions == null) {
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

  @Override
  public void outAIntegerConstant(AIntegerConstant node) {
    if (inCallExpression) {
      checkParamTypeForConstant("int");
    }
  }

  private void addParamTypeError(CalledFunction calledFunction, String correctType) {
    errors.add("Can't find function with correct parameter types for '" + calledFunction.getName() + "' "
        + calledFunction.getPosition() + ", parameter index " + functionParamIndex + ", type is " + correctType);
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
    if (foundCalledFunctions.stream()
        .noneMatch(fun -> fun.getParams().size() <= functionParamIndex
            || type.equals(fun.getParams().get(functionParamIndex).getType()))) {
      addParamTypeError(calledFunction, type);
    }
  }

  @Override
  public void inAFunctionFunctionOrStatement(AFunctionFunctionOrStatement node) {
    functionScope = new HashMap<>();
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
      functionScope.put(lastParamName, new Var("int", lastParamName));
    }
  }

  @Override
  public void outALongType(ALongType node) {
    if (lastParamName != null) {
      functionScope.put(lastParamName, new Var("long", lastParamName));
    }
  }

  @Override
  public void outADoubleType(ADoubleType node) {
    if (lastParamName != null) {
      functionScope.put(lastParamName, new Var("double", lastParamName));
    }
  }

  @Override
  public void outAFunctionFunctionOrStatement(AFunctionFunctionOrStatement node) {
    functionScope = null;
  }

  @Override
  public void caseAIdentifierValue(AIdentifierValue node) {
    if (inCallExpression) {
      functionParamIndex++;
      String identifier = node.getIdentifier().getText();
      if (functionScope == null) {
        return; // TODO add all scopes
      }
      Var var = functionScope.get(identifier);
      if (var == null) {
        addVarNotFoundError(node.getIdentifier());
        return;
      }
      checkParamType(var.getType());
      // TODO uncomment, nested scopes, assignment statements type checks, function scope on start/end
      // TODO statement scope on start/end var map
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
