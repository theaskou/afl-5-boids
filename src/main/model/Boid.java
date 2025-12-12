package main.model;

import java.util.List;

import main.behavior.FlockBehavior;
import main.simulation.Forces;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


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
        FlockBehavior flockBehavior = new FlockBehavior();
        Forces forces = flockBehavior.calculateForces(this, neighbors);

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
        if (x < 0)
            x = width;
        if (x > width)
            x = 0;
        if (y < 0)
            y = height;
        if (y > height)
            y = 0;
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(type.getColor());

        double angle = Math.atan2(vy, vx);
        AffineTransform oldTransform = g2d.getTransform();

        g2d.translate(x, y);
        g2d.rotate(angle);

        int[] xPoints = { BOID_SIZE, -BOID_SIZE / 2, -BOID_SIZE / 2 };
        int[] yPoints = { 0, BOID_SIZE / 2, -BOID_SIZE / 2 };
        g2d.fillPolygon(xPoints, yPoints, 3);

        g2d.setTransform(oldTransform);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public int getId() {
        return id;
    }

    public BoidType getType() {
        return type;
    }

}