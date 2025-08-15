package main.behavior;

public record FlockWeights(double separation, double alignment, double cohesion) {
    public static FlockWeights standard() {
        return new FlockWeights(1.5, 1.0, 1.0);
    }
}