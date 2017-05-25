package newkokoc.semantics;

import org.newkoko.c.analysis.DepthFirstAdapter;
import org.newkoko.c.node.ADoubleType;
import org.newkoko.c.node.AFunctionDeclaration;
import org.newkoko.c.node.AIntType;
import org.newkoko.c.node.ALongType;
import org.newkoko.c.node.AParameter;
import org.newkoko.c.node.AVoidFunctionType;
import org.newkoko.c.node.Start;
import org.newkoko.c.node.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionFinder extends DepthFirstAdapter {
  private final List<String> errors = new ArrayList<>();
  private final List<Function> functions = new ArrayList<>();
  private Function currentFunction;
  private Var currentParameter;
  private boolean inFunctionDeclaration = false;
  private boolean inParamList = false;

  @Override
  public void outStart(Start node) {
    checkFunctionDuplications();
    functions.forEach(this::checkParams);
  }

  private void checkFunctionDuplications() {
    for (int i = 0; i < functions.size(); i++) {
      for (int j = i + 1; j < functions.size(); j++) {
        compareDeclarations(functions.get(i), functions.get(j));
      }
    }
  }

  private void compareDeclarations(Function first, Function second) {
    if (first.getName().equals(second.getName())
        && sameParamTypes(first.getParams(), second.getParams())) {
      errors.add("Duplicated functions: " + first + ", positions: "
          + first.getPosition() + " and " + second.getPosition());
    }
  }

  private boolean sameParamTypes(List<Var> first, List<Var> second) {
    if (first.size() != second.size()) {
      return false;
    }
    for (int i = 0; i < first.size(); i++) {
      if (first.get(i).getType().equals(second.get(i).getType())) {
        return true;
      }
    }
    return false;
  }

  private void checkParams(Function function) {
    List<Var> params = function.getParams();
    for (int i = 0; i < params.size(); i++) {
      for (int j = i + 1; j < params.size(); j++) {
        compareParams(params.get(i), params.get(j), function);
      }
    }
  }

  private void compareParams(Var first, Var second, Function function) {
    if (first.getName().equals(second.getName())) {
      errors.add("Same function parameter names (" + first.getName() + ") in "
          + function + ", position " + function.getPosition());
    }
  }

  @Override
  public void caseAFunctionDeclaration(AFunctionDeclaration node) {
    inFunctionDeclaration = true;
    Position position = new Position(node.getFun().getLine(), node.getFun().getPos());
    currentFunction = new Function(position);

    node.getFunctionType().apply(this);

    currentFunction.setName(node.getIdentifier().getText());

    if (node.getParameterList() != null) {
      inParamList = true;
      node.getParameterList().apply(this);
      inParamList = false;
    }
    inFunctionDeclaration = false;
    functions.add(currentFunction);

    currentFunction = null;
  }

  @Override
  public void caseAParameter(AParameter node) {
    currentParameter = new Var();
    currentFunction.getParams().add(currentParameter);
    node.getType().apply(this);
    currentParameter.setName(node.getIdentifier().getText());
  }

  @Override
  public void outAVoidFunctionType(AVoidFunctionType node) {
    setReturnType(node.getVoid(), "void");
  }

  @Override
  public void outAIntType(AIntType node) {
    if (!inFunctionDeclaration) {
      return;
    }
    if (inParamList) {
      currentParameter.setType("int");
    } else {
      setReturnType(node.getInt(), "int");
    }
  }

  @Override
  public void outALongType(ALongType node) {
    if (!inFunctionDeclaration) {
      return;
    }
    if (inParamList) {
      currentParameter.setType("long");
    } else {
      setReturnType(node.getLong(), "long");
    }
  }

  @Override
  public void outADoubleType(ADoubleType node) {
    if (!inFunctionDeclaration) {
      return;
    }
    if (inParamList) {
      currentParameter.setType("double");
    } else {
      setReturnType(node.getDouble(), "double");
    }
  }

  private void setReturnType(Token token, String type) {
    if (currentFunction.getReturnType() != null) {
      errors.add("Return type second time " + type + " in function " + getPosition(token));
    }
    currentFunction.setReturnType(type);
  }

  private String getPosition(Token token) {
    return "[" + token.getLine() + "," + token.getPos() + "]";
  }

  public boolean errorsFound() {
    return !errors.isEmpty();
  }

  public String getErrors() {
    return errors.stream()
        .collect(Collectors.joining(System.lineSeparator()));
  }

  public List<Function> getFunctions() {
    return Collections.unmodifiableList(functions);
  }
}
