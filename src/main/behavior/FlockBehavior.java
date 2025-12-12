package main.behavior;

import java.util.List;

import main.model.Boid;
import main.simulation.Forces;
import main.simulation.Vector2D;

public class FlockBehavior implements BehaviorStrategy {

    public double distanceTo(Boid current, Boid other) {
        double dx = current.getX() - other.getX();
        double dy = current.getY() - other.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private Vector2D calculateAlignment(Boid current, List<Boid> neighbors, FlockWeights weights) {
        double avgVx = 0, avgVy = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(current, neighbor);
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

                double steerX = avgVx - current.getVx();
                double steerY = avgVy - current.getVy();

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

    private Vector2D calculateCohesion(Boid current, List<Boid> neighbors, FlockWeights weights) {
        double centerX = 0, centerY = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(current, neighbor);
            if (distance > 0 && distance < 50) {
                centerX += neighbor.getX();
                centerY += neighbor.getY();
                count++;
            }
        }

        if (count > 0) {
            centerX /= count;
            centerY /= count;

            double steerX = centerX - current.getX();
            double steerY = centerY - current.getY();

            double magnitude = Math.sqrt(steerX * steerX + steerY * steerY);
            if (magnitude > 0) {
                steerX = (steerX / magnitude) * 2.0;
                steerY = (steerY / magnitude) * 2.0;

                steerX -= current.getVx();
                steerY -= current.getVy();

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

    private Vector2D calculateSeparation(Boid current, List<Boid> neighbors, FlockWeights weights) {
        double steerX = 0, steerY = 0;
        int count = 0;

        for (Boid neighbor : neighbors) {
            double distance = distanceTo(current, neighbor);
            if (distance > 0 && distance < 25) {
                double diffX = current.getX() - neighbor.getX();
                double diffY = current.getY() - neighbor.getY();

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

                    steerX -= current.getVx();
                    steerY -= current.getVy();

                    double force = Math.sqrt(steerX * steerX + steerY * steerY);
                    if (force > 0.03) {
                        steerX = (steerX / force) * 0.03;
                        steerY = (steerY / force) * 0.03;
                    }
                }
            }
        return new Vector2D(steerX * weights.separation(), steerY * weights.separation());

    }

    public FlockWeights getFlockWeights() {
        return FlockWeights.standard();
    }

    @Override
    public Forces calculateForces(Boid boid, List<Boid> neighbors) {
        if (neighbors.isEmpty()) {
            return new Forces();
        }

        FlockWeights weights = getFlockWeights();

        Vector2D separation = calculateSeparation(boid, neighbors, weights);
        Vector2D alignment = calculateAlignment(boid, neighbors, weights);
        Vector2D cohesion = calculateCohesion(boid, neighbors, weights);

        return new Forces(separation, alignment, cohesion);
    }
}
