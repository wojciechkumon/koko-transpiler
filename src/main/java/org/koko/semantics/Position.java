package org.koko.semantics;

public class Position {
  private final int line;
  private final int pos;

  public Position(int line, int pos) {
    this.line = line;
    this.pos = pos;
  }

  public int getLine() {
    return line;
  }

  public int getPos() {
    return pos;
  }

  @Override
  public String toString() {
    return "[" + line + "," + pos + "]";
  }
}
