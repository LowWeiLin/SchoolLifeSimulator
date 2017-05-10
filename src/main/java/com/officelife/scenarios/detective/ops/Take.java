package com.officelife.scenarios.detective.ops;

import static com.officelife.core.Action.State.CONTINUE;
import static com.officelife.core.planning.Facts.fact;
import static com.officelife.core.planning.Facts.facts;
import static com.officelife.core.planning.Facts.v;
import static com.officelife.utility.Utility.fit;

import com.officelife.core.Action;
import com.officelife.core.planning.Facts;
import com.officelife.core.planning.Node;
import com.officelife.core.planning.Op;
import com.officelife.utility.Utility;

import javaslang.collection.Map;

public class Take implements Op<Node> {

  private final String item = v();

  @Override
  public Facts preconditions() {
    return facts(
      fact("ground", "has", item));
  }

  @Override
  public int weight(Node state) {
    return 0;
  }

  @Override
  public Facts postconditions(Map<String, Object> bindings) {
    return facts(
      fact("actor", "has", bindings.get(item).getOrElseThrow(fit)));
  }

  @Override
  public Action action() {
    return state -> CONTINUE;
  }
}
