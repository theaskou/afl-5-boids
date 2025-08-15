package main.spatial;

import main.model.Boid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialHashIndex implements SpatialIndex {
    private final Map<Long, List<Boid>> grid;
    private final double cellSize;
    private final int width;
    private final int height;

    public SpatialHashIndex(int width, int height, double cellSize) {
        this.grid = new HashMap<>();
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;
    }

    @Override
    public void clear() {
        grid.clear();
    }

    @Override
    public void insert(Boid boid) {
        long hash = getHash(boid.getX(), boid.getY());
        grid.computeIfAbsent(hash, k -> new ArrayList<>()).add(boid);
    }

    @Override
    public List<Boid> findNeighbors(Boid targetBoid, double radius) {
        List<Boid> neighbors = new ArrayList<>();
        double radiusSquared = radius * radius;
        
        int cellRadius = (int) Math.ceil(radius / cellSize);
        int targetCellX = (int) (targetBoid.getX() / cellSize);
        int targetCellY = (int) (targetBoid.getY() / cellSize);
        
        for (int dx = -cellRadius; dx <= cellRadius; dx++) {
            for (int dy = -cellRadius; dy <= cellRadius; dy++) {
                int cellX = targetCellX + dx;
                int cellY = targetCellY + dy;
                
                long hash = getHash(cellX * cellSize, cellY * cellSize);
                List<Boid> cellBoids = grid.get(hash);
                
                if (cellBoids != null) {
                    for (Boid boid : cellBoids) {
                        if (boid.getId() != targetBoid.getId()) {
                            double dx2 = targetBoid.getX() - boid.getX();
                            double dy2 = targetBoid.getY() - boid.getY();
                            double distanceSquared = dx2 * dx2 + dy2 * dy2;
                            
                            if (distanceSquared <= radiusSquared) {
                                neighbors.add(boid);
                            }
                        }
                    }
                }
            }
        }
        
        return neighbors;
    }
    
    private long getHash(double x, double y) {
        int cellX = (int) (x / cellSize);
        int cellY = (int) (y / cellSize);
        
        cellX = Math.max(0, Math.min(cellX, (int)(width / cellSize)));
        cellY = Math.max(0, Math.min(cellY, (int)(height / cellSize)));
        
        return ((long) cellX << 32) | (cellY & 0xFFFFFFFFL);
    }

    @Override
    public String getName() {
        return "Spatial Hashing";
    }
}