package main.behavior;

import main.model.Boid;
import main.simulation.Forces;

import java.util.List;

public interface BehaviorStrategy {
    Forces calculateForces(Boid boid, List<Boid> neighbors);
}