package newkoko;

public class Var {
  public static final String INT = "int";
  public static final String DOUBLE = "double";

  private final String type;
  private final Object value;

  public Var(String type, Object value) {
    this.type = type;
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }
}
