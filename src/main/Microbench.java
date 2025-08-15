package main;

import main.simulation.FlockSimulation;
import main.model.BoidType;
import main.spatial.*;

public class Microbench {
    public static void main(String[] args) {
        System.out.println("Starting Microbench...");

        SpatialIndex naive = new NaiveSpatialIndex();
        FlockSimulation simulation = new FlockSimulation(1200, 800);
        simulation.setSpatialIndex(naive);
        simulation.addBoid();
        simulation.addBoid();
        simulation.addBoid();
        simulation.update();
    }
}