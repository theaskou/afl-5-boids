package main.simulation;

public record Vector2D(double x, double y) {
    public static final Vector2D ZERO = new Vector2D(0, 0);

    public Vector2D() {
        this(0, 0);
    }
}