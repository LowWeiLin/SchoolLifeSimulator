package com.officelife.scenarios.wood;


import static com.officelife.core.planning.Node.cast;
import static com.officelife.utility.Utility.list;
import static java.util.stream.Collectors.toList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import com.officelife.core.Action;
import com.officelife.core.WorldState;
import com.officelife.core.planning.Facts;
import com.officelife.core.planning.Node;
import com.officelife.core.planning.Op;
import com.officelife.core.planning.Search;
import com.officelife.scenarios.wood.ops.GetAxe;

import astar.AStar;
import astar.IGoalNode;
import astar.ISearchNode;

public class WoodcutterSearch implements Search {

  @Override
  public List<Op<Node>> operations() {
//        return list(new ChopLog(), new GetAxe(), new CollectBranches());
    return list(new GetAxe());
  }

  public Deque<Action> determineActions(WorldState state, Facts goal) {
    Facts facts = state.toFacts(new WoodcutterReduction());

    // TODO there's no point to IGoalNodes; they're just predicates
    IGoalNode goalCondition = node -> {
      // we're at the goal if the goal is completely contained in this node
      return goal.isSubsetOf(cast(node).facts);
    };

    List<ISearchNode> path = new AStar()
      .shortestPath(
        new Node(this,
          0,
          null,
          facts, operations()),
        goalCondition);

    if (path == null) {
      return new ArrayDeque<>();
    }

    return new ArrayDeque<>(path.stream()
      .map(ISearchNode::op)
      .filter(Objects::nonNull)
      .map(Op::action)
      .collect(toList()));
  }
}
