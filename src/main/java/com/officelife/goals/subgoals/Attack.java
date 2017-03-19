package com.officelife.goals.subgoals;

import com.officelife.Coords;
import com.officelife.World;
import com.officelife.actions.*;
import com.officelife.actions.prerequisite.LocationBeside;
import com.officelife.actors.Actor;
import com.officelife.actors.Person;
import com.officelife.goals.Goal;
import com.officelife.goals.Outcome;
import com.officelife.goals.State;
import com.officelife.goals.effects.Effect;
import com.officelife.goals.effects.TerminalAction;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ouch.
 */
public class Attack extends Goal {
    private enum Status {
        INIT_GOAL, FINDING, COMPLETED
    }

    private Status status;

    private boolean failed = false;

    private Person target;

    public Attack(Person target) {
        this.target = target;
        this.status = Status.FINDING;
    }

    public Attack() {
        this.target = null; // :(
        this.status = Status.INIT_GOAL;
    }

    @Override
    public Outcome outcome() {
        if (failed) {
            return Outcome.FAILURE;
        }

        if (status == Status.COMPLETED) {
            return Outcome.SUCCESS;
        }

        return Outcome.CONTINUE;
    }

    @Override
    public Effect effect(State state) {
        switch (status) {
            case INIT_GOAL:
                // search the map. return move action
                // TODO extract this into a class?

                Coords personCoords = state.world.actorLocation(state.actor)
                        .orElseThrow(() -> new RuntimeException("Actor not found"));
                Optional<Actor> possibleTarget = chooseTarget(state, personCoords);

                if (!possibleTarget.isPresent()) {
                    failed = true;
                    return new TerminalAction(new Languish(state));
                }
                Person targetPerson = (Person) possibleTarget.get();

                Coords currentCoords = state.world.actorLocation(state.actor)
                        .orElseThrow(() -> new RuntimeException("actor " + state.actor.id() + " is nowhere"));

                Optional<List<Coords>> path = state.world.actorLocation(targetPerson)
                        .flatMap(coords -> state.world.findPath(currentCoords, new World.EndCoords(coords)));

                if (!path.isPresent()) {
                    failed = true;
                    return new TerminalAction(new Languish(state));
                }

                status = Status.FINDING;
                target = targetPerson;

                return new TerminalAction(
                        new Move(state, Move.Direction.directionToMove(currentCoords, path.get().get(0)))
                );

            case FINDING:

                Optional<List<Coords>> pathToTarget = state.world.actorLocation(target)
                    .flatMap(coords ->
                        state.world.findPath(
                            state.world.actorLocation(state.actor).get(),
                            new World.EndCoords(coords))
                    );

                if (!pathToTarget.isPresent()) {
                    failed = true;
                    return new TerminalAction(new Languish(state));
                }
                if (new LocationBeside(state.actor, target, state.world)
                        .satisfied()) {
                    status = Status.COMPLETED;

                    return new TerminalAction(new com.officelife.actions.Attack(state, target));
                }

                return new TerminalAction(
                    new Move(
                        state,
                        Move.Direction.directionToMove(
                            state.world.actorLocation(state.actor).get(), pathToTarget.get().get(0)
                        )
                    )
                );

            default:
                return new TerminalAction(new Languish(state));
        }
    }

    private Optional<Actor> chooseTarget(State state, Coords personCoords) {
        List<Actor> nearby = new ArrayList<>();
        for (int i = personCoords.x - 5; i < personCoords.x + 5; i++) {
            for (int j = personCoords.y - 5; j < personCoords.y + 5; j++) {
                Coords coords = new Coords(i, j);
                if (state.world.actorLocations.containsKey(new Coords(i, j))) {
                    nearby.add(state.world.actorLocations.get(coords));
                }
            }
        }
        if (nearby.isEmpty()) {
            return Optional.empty();
        }

        Person actingPerson = (Person) state.actor;

        // select actor with the lowest relationship score
        return nearby.stream()
                .filter(actor -> !actor.id().equals(state.actor.id()))
                .filter(actor -> actor instanceof Person)
                .min((actor1, actor2) -> {
                    int relationship1 = actingPerson.relationships.containsKey(actor1.id())
                            ? actingPerson.relationships.get(actor1.id())
                            : 0;
                    int relationship2 = actingPerson.relationships.containsKey(actor2.id())
                            ? actingPerson.relationships.get(actor2.id())
                            : 0;
                    return Integer.compare(relationship1, relationship2);
                });
    }
}