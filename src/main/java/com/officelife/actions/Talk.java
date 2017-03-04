package com.officelife.actions;

import com.officelife.Coords;
import com.officelife.actors.Actor;
import com.officelife.actors.Person;
import com.officelife.goals.State;
import com.officelife.items.Coffee;
import com.officelife.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Talk<T> extends Action {

  private final Actor target;

  public Talk(State state, Actor target) {
    super(state);
    this.target = target;
  }

  @Override
  public boolean accept() {
    Coords targetLocation = state.world.actorLocation(target.id())
            .orElseThrow(() -> new RuntimeException("Target location is not found"));
    Coords actorLocation = state.world.actorLocation(state.person.id())
            .orElseThrow(() -> new RuntimeException("Actor location is not found"));
    if (!isBeside(targetLocation, actorLocation))  {
      return false;
    }

    // take effect

    Person person = state.person;
    incrementRelationshipValue(person, (Person) target);
    incrementRelationshipValue((Person) target, person);

    return true;
  }

  private void incrementRelationshipValue(Person person, Person target) {
    String targetId = target.id();
    if (!person.relationships.containsKey(targetId)) {
      person.relationships.put(targetId, 0);
    }
    int currentRelationshipValue = person.relationships.get(targetId);
    person.relationships.put(targetId, currentRelationshipValue + 5);
  }

  private boolean isBeside(Coords first, Coords second) {
    if (first.equals(second)) {
      return false;
    }
    return Math.abs(first.x - second.x) <= 1 && first.y == second.y
            || Math.abs(first.y - second.y) <= 1 && first.x == second.x;
  }

  @Override
  public String toString() {
    return String.format("Talk to Person %s", target.id());
  }
}