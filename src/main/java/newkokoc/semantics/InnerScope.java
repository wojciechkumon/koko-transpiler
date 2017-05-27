package newkokoc.semantics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InnerScope implements Scope {
  private final Map<String, Var> vars = new HashMap<>();
  private final Scope outer;

  public InnerScope(Scope outer) {
    this.outer = outer;
  }

  @Override
  public Optional<Var> get(String name) {
    Optional<Var> var = Optional.ofNullable(vars.get(name));
    if (var.isPresent()) {
      return var;
    }
    return outer.get(name);
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

  public Scope getOuterScope() {
    return outer;
  }
}
