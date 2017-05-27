package newkokoc.semantics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MainScope implements Scope {
  private final Map<String, Var> vars = new HashMap<>();

  @Override
  public Optional<Var> get(String name) {
    return Optional.ofNullable(vars.get(name));
  }

  @Override
  public boolean put(Var var) {
    Optional<Var> varWithSameName = get(var.getName());
    if (varWithSameName.isPresent()) {
      return false;
    }
    vars.put(var.getName(), var);
    return true;
  }
}
