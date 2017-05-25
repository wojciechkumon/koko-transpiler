package newkokoc.semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Function {
  private String name;
  private String returnType;
  private List<Var> params = new ArrayList<>();
  private final Position position;

  public Function(Position position) {
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public List<Var> getParams() {
    return params;
  }

  public Position getPosition() {
    return position;
  }

  @Override
  public String toString() {
    return "fun " + name + "("
        + getParamsString(params) + ") " + returnType;
  }

  private String getParamsString(List<Var> params) {
    return params.stream()
        .map(Var::toString)
        .collect(Collectors.joining(", "));
  }
}
