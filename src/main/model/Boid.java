package main.model;

import main.behavior.FlockWeights;
import main.simulation.*;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

public class Boid {
    private double x, y;
    private double vx, vy;
    private final int id;
    private final BoidType type;
    private static final double MAX_SPEED = 2.0;
    private static final double MAX_FORCE = 0.03;
    private static final int BOID_SIZE = 8;

    public Boid(int id, double x, double y) {
        this(id, x, y, BoidType.STANDARD);
    }

    public Boid(int id, double x, double y, BoidType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
        this.vx = (Math.random() - 0.5) * 2;
        this.vy = (Math.random() - 0.5) * 2;
    }

    public void update(List<Boid> neighbors, int width, int height) {
        Forces forces = calculateForces(neighbors);

        vx += forces.separation().x() + forces.alignment().x() + forces.cohesion().x();
        vy += forces.separation().y() + forces.alignment().y() + forces.cohesion().y();

        limitVelocity();

        x += vx;
        y += vy;

        wrapAround(width, height);
    }

    private void limitVelocity() {
        double speed = Math.sqrt(vx * vx + vy * vy);
        if (speed > MAX_SPEED) {
            vx = (vx / speed) * MAX_SPEED;
            vy = (vy / speed) * MAX_SPEED;
        }
    }

    private void wrapAround(int width, int height) {
        if (x < 0) x = width;
        if (x > width) x = 0;
        if (y < 0) y = height;
        if (y > height) y = 0;
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(type.getColor());
        
        double angle = Math.atan2(vy, vx);
        AffineTransform oldTransform = g2d.getTransform();
        
        g2d.translate(x, y);
        g2d.rotate(angle);
        
        int[] xPoints = {BOID_SIZE, -BOID_SIZE/2, -BOID_SIZE/2};
        int[] yPoints = {0, BOID_SIZE/2, -BOID_SIZE/2};
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        g2d.setTransform(oldTransform);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public int getId() { return id; }
    public BoidType getType() { return type; }
    
    public double distanceTo(Boid other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Forces calculateForces(List<Boid> neighbors) {
        if (neighbors.isEmpty()) {
            return new Forces();
        }

        FlockWeights weights = getFlockWeights();

        Vector2D separation = calculateSeparation(neighbors, weights);
        Vector2D alignment = calculateAlignment(neighbors, weights);
        Vector2D cohesion = calculateCohesion(neighbors, weights);

        return new Forces(separation, alignment, cohesion);
    }

    public FlockWeights getFlockWeights() {
        return FlockWeights.standard();
    }

    private Vector2D calculateSeparation(List<Boid> neighbors, FlockWeights weights) {
        double steerX = 0, steerY = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(neighbor);
            if (distance > 0 && distance < 25) {
                double diffX = x - neighbor.getX();
                double diffY = y - neighbor.getY();

                diffX /= distance;
                diffY /= distance;

                steerX += diffX;
                steerY += diffY;
                count++;
            }
        }

        if (count > 0) {
            steerX /= count;
            steerY /= count;

            double magnitude = Math.sqrt(steerX * steerX + steerY * steerY);
            if (magnitude > 0) {
                steerX = (steerX / magnitude) * 2.0;
                steerY = (steerY / magnitude) * 2.0;

                steerX -= vx;
                steerY -= vy;

                double force = Math.sqrt(steerX * steerX + steerY * steerY);
                if (force > 0.03) {
                    steerX = (steerX / force) * 0.03;
                    steerY = (steerY / force) * 0.03;
                }
            }
        }

        return new Vector2D(steerX * weights.separation(), steerY * weights.separation());
    }

    private Vector2D calculateAlignment(List<Boid> neighbors, FlockWeights weights) {
        double avgVx = 0, avgVy = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(neighbor);
            if (distance > 0 && distance < 50) {
                avgVx += neighbor.getVx();
                avgVy += neighbor.getVy();
                count++;
            }
        }

        if (count > 0) {
            avgVx /= count;
            avgVy /= count;

            double magnitude = Math.sqrt(avgVx * avgVx + avgVy * avgVy);
            if (magnitude > 0) {
                avgVx = (avgVx / magnitude) * 2.0;
                avgVy = (avgVy / magnitude) * 2.0;

                double steerX = avgVx - vx;
                double steerY = avgVy - vy;

                double force = Math.sqrt(steerX * steerX + steerY * steerY);
                if (force > 0.03) {
                    steerX = (steerX / force) * 0.03;
                    steerY = (steerY / force) * 0.03;
                }

                return new Vector2D(steerX * weights.alignment(), steerY * weights.alignment());
            }
        }

        return Vector2D.ZERO;
    }

    private Vector2D calculateCohesion(List<Boid> neighbors, FlockWeights weights) {
        double centerX = 0, centerY = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(neighbor);
            if (distance > 0 && distance < 50) {
                centerX += neighbor.getX();
                centerY += neighbor.getY();
                count++;
            }
        }

        if (count > 0) {
            centerX /= count;
            centerY /= count;

            double steerX = centerX - x;
            double steerY = centerY - y;

            double magnitude = Math.sqrt(steerX * steerX + steerY * steerY);
            if (magnitude > 0) {
                steerX = (steerX / magnitude) * 2.0;
                steerY = (steerY / magnitude) * 2.0;

                steerX -= vx;
                steerY -= vy;

                double force = Math.sqrt(steerX * steerX + steerY * steerY);
                if (force > 0.03) {
                    steerX = (steerX / force) * 0.03;
                    steerY = (steerY / force) * 0.03;
                }

                return new Vector2D(steerX * weights.cohesion(), steerY * weights.cohesion());
            }
        }

        return Vector2D.ZERO;
    }
}