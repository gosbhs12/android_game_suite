package com.example.k_dev_master.gomoku;

public class Stone {
    //x,y is for array
    private int x;
    private int y;
    //coordinatex, y is for drawing bitmap
    private int coordinateX;
    private int coordinateY;
    private Color color;

    enum Color {
        BLACK,
        WHITE,
        EMPTY
    }

    public Stone(int x, int y, Color c) {
        this.x = x;
        this.y = y;
        this.color = c;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public Color getColor() {
        return color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
