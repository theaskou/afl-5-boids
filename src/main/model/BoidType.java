package main.model;

import java.awt.Color;

public enum BoidType {
    STANDARD(Color.WHITE);

    private final Color color;

    BoidType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}