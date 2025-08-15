package main.simulation;

public record Forces(
    Vector2D separation,
    Vector2D alignment,
    Vector2D cohesion
) {
    public Forces() {
        this(Vector2D.ZERO, Vector2D.ZERO, Vector2D.ZERO);
    }
}
