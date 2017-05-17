package newkoko.excepions;

public class VarNotExistException extends RuntimeException {

  public VarNotExistException(String varName) {
    super(varName);
  }
}
