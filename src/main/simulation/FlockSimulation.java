package main.simulation;

import main.model.Boid;
import main.model.BoidType;
import main.spatial.*;
import java.util.ArrayList;
import java.util.List;

public class FlockSimulation {
    private final List<Boid> boids;
    private SpatialIndex spatialIndex;
    private final int width;
    private final int height;
    private double neighborRadius = 50.0;
    private double lastIterationTimeMs = 0;

    public FlockSimulation(int width, int height) {
        this.width = width;
        this.height = height;
        this.boids = new ArrayList<>();
        this.spatialIndex = new NaiveSpatialIndex();
    }

    public void setSpatialIndex(SpatialIndex spatialIndex) {
        this.spatialIndex = spatialIndex;
    }

    public void addBoid() {
        addBoid(BoidType.STANDARD);
    }

    public void addBoid(BoidType type) {
        int id = boids.size();
        double x = Math.random() * width;
        double y = Math.random() * height;
        boids.add(new Boid(id, x, y, type));
    }

    public void setBoidCount(int count) {
        while (boids.size() < count) {
            addBoid();
        }
        while (boids.size() > count) {
            boids.remove(boids.size() - 1);
        }
        
        for (int i = 0; i < boids.size(); i++) {
            Boid oldBoid = boids.get(i);
            boids.set(i, new Boid(i, oldBoid.getX(), oldBoid.getY(), oldBoid.getType()));
        }
    }

    public void update() {
        long startTime = System.nanoTime();
        
        spatialIndex.clear();
        for (Boid boid : boids) {
            spatialIndex.insert(boid);
        }

        for (Boid boid : boids) {
            List<Boid> neighbors = spatialIndex.findNeighbors(boid, neighborRadius);
            boid.update(neighbors, width, height);
        }
        
        long endTime = System.nanoTime();
        lastIterationTimeMs = (endTime - startTime) / 1_000_000.0;
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public String getSpatialIndexName() {
        return spatialIndex.getName();
    }

    public double getLastIterationTimeMs() {
        return lastIterationTimeMs;
    }

    public int getBoidCount() {
        return boids.size();
    }

    public void setNeighborRadius(double radius) {
        this.neighborRadius = radius;
    }

    public double getNeighborRadius() {
        return neighborRadius;
    }


    public int getCountByType(BoidType type) {
        return (int) boids.stream().filter(b -> b.getType() == type).count();
    }
}