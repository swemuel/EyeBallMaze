package com.example.eyeballmaze;

public class Eyeball {
    public int row;
    public int column;
    public Shape shape;
    public Color color;
    public Direction direction;

    public Eyeball(int newRow, int newColumn, Direction newDirection) {
        this.row = newRow;
        this.column = newColumn;
        this.direction = newDirection;
    }
}
