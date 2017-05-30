package org.koko.semantics;

import java.util.Optional;

public interface Scope {

  Optional<Var> get(String name);

  boolean put(Var var);
}
