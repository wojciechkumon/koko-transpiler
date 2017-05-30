package org.koko.semantics;

public class CalledFunction {
  private final String name;
  private final Position position;

  public CalledFunction(String name, Position position) {
    this.name = name;
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public Position getPosition() {
    return position;
  }
}
