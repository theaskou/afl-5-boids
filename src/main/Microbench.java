package main;

import main.simulation.FlockSimulation;
import main.spatial.*;

public class Microbench {

    private static final int WARMUP_ITERATIONS = 50;
    private static final int MEASUREMENT_ITERATIONS = 200;
    private static final int[] BOID_COUNTS = { 50, 100, 200, 500, 1000 };
    private static final double[] SEARCH_RADIUS = { 30.0, 75.0, 150.0 };

    public static void main(String[] args) {

        for (double radius : SEARCH_RADIUS) {
            System.out.println("\n--- Search Radius: " + radius + " pixels ---");
            System.out.println("Boids\tNaive\t\tSpatialHash\tKD-Tree\t\tQuadTree");

            for (int boidCount : BOID_COUNTS) {
                System.out.print(boidCount + "\t");

                double naiveTime = runBenchmark(new NaiveSpatialIndex(), boidCount, radius);
                System.out.print(String.format("%.3f ms\t", naiveTime));

                double hashTime = runBenchmark(new SpatialHashIndex(1200, 800, 50.0), boidCount, radius);
                System.out.print(String.format("%.3f ms\t", hashTime));

                double kdTime = runBenchmark(new KDTreeSpatialIndex(), boidCount, radius);
                System.out.print(String.format("%.3f ms\t", kdTime));

                double quadTime = runBenchmark(new QuadTreeSpatialIndex(1200, 800), boidCount, radius);
                System.out.println(String.format("%.3f ms", quadTime));

            }
        }

    }

    private static double runBenchmark(SpatialIndex spatialIndex, int boidCount, double searchRadius) {
        FlockSimulation simulation = new FlockSimulation(1200, 800);
        simulation.setSpatialIndex(spatialIndex);
        simulation.setNeighborRadius(searchRadius);

        for (int i = 0; i < boidCount; i++) {
            simulation.addBoid();
        }
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            simulation.update();
        }
        long startTime = System.nanoTime();

        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            simulation.update();
        }

        long endTime = System.nanoTime();

        double totalTimeMs = (endTime - startTime) / 1_000_000.0;
        return totalTimeMs / MEASUREMENT_ITERATIONS;
    }
}