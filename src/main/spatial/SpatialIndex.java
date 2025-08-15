package main.spatial;

import main.model.Boid;
import java.util.List;

public interface SpatialIndex {
    void clear();
    void insert(Boid boid);
    List<Boid> findNeighbors(Boid boid, double radius);
    String getName();
}