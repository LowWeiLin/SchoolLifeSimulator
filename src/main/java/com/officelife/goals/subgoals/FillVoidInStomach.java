package com.officelife.goals.subgoals;

import static com.officelife.Utility.deque;

import com.officelife.actors.Person;
import com.officelife.goals.Goal;
import com.officelife.goals.State;
import com.officelife.goals.effects.Alternatives;
import com.officelife.goals.effects.Effect;
import com.officelife.items.Coffee;

// OrGoal
public class FillVoidInStomach extends Goal {


  @Override
  public Effect effect(State state) {
    if (!(state.actor instanceof Person)) {
      throw new RuntimeException("FillVoidInSoul requires person as actor");
    }
    Person person = (Person)state.actor;

    // TODO proper decision making
    if (person.belonging > 5
            && state.world.itemLocation(i -> i instanceof Coffee).isPresent()) {
      return new Alternatives(deque(new GetCoffee()));
    } else {
      return new Alternatives(deque(new PunchPeopleForFood()));
    }

  }
}


