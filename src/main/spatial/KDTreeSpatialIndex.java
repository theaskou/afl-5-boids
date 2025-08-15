package main.spatial;

import main.model.Boid;
import java.util.ArrayList;
import java.util.List;

public class KDTreeSpatialIndex implements SpatialIndex {
    private KDNode root;
    
    private static class KDNode {
        Boid boid;
        KDNode left, right;
        int depth;
        
        KDNode(Boid boid, int depth) {
            this.boid = boid;
            this.depth = depth;
        }
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public void insert(Boid boid) {
        root = insertRec(root, boid, 0);
    }
    
    private KDNode insertRec(KDNode node, Boid boid, int depth) {
        if (node == null) {
            return new KDNode(boid, depth);
        }
        
        int cd = depth % 2;
        
        if ((cd == 0 && boid.getX() < node.boid.getX()) || 
            (cd == 1 && boid.getY() < node.boid.getY())) {
            node.left = insertRec(node.left, boid, depth + 1);
        } else {
            node.right = insertRec(node.right, boid, depth + 1);
        }
        
        return node;
    }

    @Override
    public List<Boid> findNeighbors(Boid targetBoid, double radius) {
        List<Boid> neighbors = new ArrayList<>();
        if (root != null) {
            rangeSearch(root, targetBoid.getX(), targetBoid.getY(), radius, targetBoid, neighbors);
        }
        return neighbors;
    }
    
    private void rangeSearch(KDNode node, double x, double y, double radius, Boid targetBoid, List<Boid> neighbors) {
        if (node == null) return;
        
        double dx = x - node.boid.getX();
        double dy = y - node.boid.getY();
        double distanceSquared = dx * dx + dy * dy;
        
        if (distanceSquared <= radius * radius && node.boid.getId() != targetBoid.getId()) {
            neighbors.add(node.boid);
        }
        
        int cd = node.depth % 2;
        double diff = (cd == 0) ? dx : dy;
        
        if (diff <= 0) {
            rangeSearch(node.left, x, y, radius, targetBoid, neighbors);
            if (diff * diff <= radius * radius) {
                rangeSearch(node.right, x, y, radius, targetBoid, neighbors);
            }
        } else {
            rangeSearch(node.right, x, y, radius, targetBoid, neighbors);
            if (diff * diff <= radius * radius) {
                rangeSearch(node.left, x, y, radius, targetBoid, neighbors);
            }
        }
    }

    @Override
    public String getName() {
        return "KD-Tree";
    }
}