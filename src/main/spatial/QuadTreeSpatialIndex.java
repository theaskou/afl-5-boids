package main.spatial;

import main.model.Boid;
import java.util.ArrayList;
import java.util.List;

public class QuadTreeSpatialIndex implements SpatialIndex {
    private QuadNode root;
    private final double width;
    private final double height;
    private static final int MAX_BOIDS_PER_NODE = 10;

    public QuadTreeSpatialIndex(double width, double height) {
        this.width = width;
        this.height = height;
        this.root = new QuadNode(0, 0, width, height);
    }

    private static class QuadNode {
        double x, y, w, h;
        List<Boid> boids;
        QuadNode[] children;
        boolean divided;

        QuadNode(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.boids = new ArrayList<>();
            this.divided = false;
        }

        boolean insert(Boid boid) {
            if (!contains(boid.getX(), boid.getY())) {
                return false;
            }

            if (boids.size() < MAX_BOIDS_PER_NODE && !divided) {
                boids.add(boid);
                return true;
            }

            if (!divided) {
                subdivide();
            }

            return children[0].insert(boid) || children[1].insert(boid) ||
                   children[2].insert(boid) || children[3].insert(boid);
        }

        void subdivide() {
            double halfW = w / 2;
            double halfH = h / 2;
            
            children = new QuadNode[4];
            children[0] = new QuadNode(x, y, halfW, halfH);
            children[1] = new QuadNode(x + halfW, y, halfW, halfH);
            children[2] = new QuadNode(x, y + halfH, halfW, halfH);
            children[3] = new QuadNode(x + halfW, y + halfH, halfW, halfH);

            for (Boid boid : boids) {
                if (!children[0].insert(boid)) {
                    if (!children[1].insert(boid)) {
                        if (!children[2].insert(boid)) {
                            children[3].insert(boid);
                        }
                    }
                }
            }
            
            boids.clear();
            divided = true;
        }

        boolean contains(double px, double py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }

        boolean intersects(double centerX, double centerY, double radius) {
            double xDist = Math.abs(centerX - (x + w/2));
            double yDist = Math.abs(centerY - (y + h/2));

            if (xDist > (w/2 + radius) || yDist > (h/2 + radius)) {
                return false;
            }

            if (xDist <= w/2 || yDist <= h/2) {
                return true;
            }

            double cornerDistSq = Math.pow(xDist - w/2, 2) + Math.pow(yDist - h/2, 2);
            return cornerDistSq <= radius * radius;
        }

        void queryRange(double centerX, double centerY, double radius, Boid targetBoid, List<Boid> found) {
            if (!intersects(centerX, centerY, radius)) {
                return;
            }

            double radiusSquared = radius * radius;
            for (Boid boid : boids) {
                if (boid.getId() != targetBoid.getId()) {
                    double dx = centerX - boid.getX();
                    double dy = centerY - boid.getY();
                    double distanceSquared = dx * dx + dy * dy;
                    if (distanceSquared <= radiusSquared) {
                        found.add(boid);
                    }
                }
            }

            if (divided) {
                for (QuadNode child : children) {
                    child.queryRange(centerX, centerY, radius, targetBoid, found);
                }
            }
        }
    }

    @Override
    public void clear() {
        root = new QuadNode(0, 0, width, height);
    }

    @Override
    public void insert(Boid boid) {
        root.insert(boid);
    }

    @Override
    public List<Boid> findNeighbors(Boid targetBoid, double radius) {
        List<Boid> neighbors = new ArrayList<>();
        root.queryRange(targetBoid.getX(), targetBoid.getY(), radius, targetBoid, neighbors);
        return neighbors;
    }

    @Override
    public String getName() {
        return "QuadTree";
    }
}