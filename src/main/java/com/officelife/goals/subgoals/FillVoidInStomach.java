package com.officelife.goals.subgoals;

import java.util.ArrayList;

import com.officelife.goals.Goal;
import com.officelife.goals.State;
import com.officelife.goals.effects.Alternatives;
import com.officelife.goals.effects.Effect;

// OrGoal
public class FillVoidInStomach extends Goal {
  @Override
  public Effect effect(State state) {
    // This will always fail
    return new Alternatives(new ArrayList<>());
  }
}


