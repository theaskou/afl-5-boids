package main.spatial;

import main.model.Boid;
import java.util.ArrayList;
import java.util.List;

public class NaiveSpatialIndex implements SpatialIndex {
    private final List<Boid> boids;

    public NaiveSpatialIndex() {
        this.boids = new ArrayList<>();
    }

    @Override
    public void clear() {
        boids.clear();
    }

    @Override
    public void insert(Boid boid) {
        boids.add(boid);
    }

    @Override
    public List<Boid> findNeighbors(Boid targetBoid, double radius) {
        List<Boid> neighbors = new ArrayList<>();
        double radiusSquared = radius * radius;
        
        for (Boid boid : boids) {
            if (boid.getId() != targetBoid.getId()) {
                double dx = targetBoid.getX() - boid.getX();
                double dy = targetBoid.getY() - boid.getY();
                double distanceSquared = dx * dx + dy * dy;
                
                if (distanceSquared <= radiusSquared) {
                    neighbors.add(boid);
                }
            }
        }
        
        return neighbors;
    }

    @Override
    public String getName() {
        return "Naive O(n²)";
    }
}