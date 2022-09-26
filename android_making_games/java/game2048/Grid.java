package com.example.k_dev_master.game2048;

import java.util.ArrayList;

public class Grid {
    public Cell[][] field;
    public Cell[][] undoField;
    private Cell[][] bufferField;

    public Grid(int sizeX, int sizeY) {
        field = new Cell[sizeX][sizeY];
        undoField = new Cell[sizeX][sizeY];
        bufferField = new Cell[sizeX][sizeY];
        clearGrid();
        clearUndoGrid();
    }
    public Cell randomAvailableCell() {
        ArrayList<Cell> availableCells = getAvailableCells();
        if (availableCells.size() >= 1) {
            return availableCells.get((int) Math.floor(Math.random() * availableCells.size()));
        }
        return null;
    }

    public ArrayList<Cell> getAvailableCells() {
        ArrayList<Cell> availableCells = new ArrayList<>();
        for (int xx = 0; xx < 4; xx++) {
            for (int yy = 0; yy < 4; yy++) {
                if (field[xx][yy] == null) {
                    availableCells.add(new Cell(xx, yy, 0));
                }
            }
        }
        return availableCells;
    }

    public boolean isCellsAvailable() {
        return (getAvailableCells().size() >= 1);
    }

    public boolean isCellAvailable(Cell cell) {
        return !isCellOccupied(cell);
    }

    public boolean isCellOccupied(Cell cell) {
        return (getCellContent(cell) != null);
    }

    public Cell getCellContent(Cell cell) {
        if (cell != null) {
            return field[cell.getX()][cell.getY()];
        } else {
            return null;
        }
    }

    public Cell getCellContent(int x, int y) {
        return field[x][y];
    }

    public void insertCell(Cell cell) {
        field[cell.getX()][cell.getY()] = cell;
    }

    public void removeCell(Cell cell) {
        field[cell.getX()][cell.getY()] = null;
    }

    public void saveCells() {
        for (int xx = 0; xx < 4; xx++) {
            for (int yy = 0; yy < 4; yy++) {
                if (bufferField[xx][yy] == null) {
                    undoField[xx][yy] = null;
                } else {
                    undoField[xx][yy] = bufferField[xx][yy];
                }
            }
        }
    }

    public void prepareSaveCells() {
        for (int xx = 0; xx < 4; xx++) {
            for (int yy = 0; yy < 4; yy++) {
                if (field[xx][yy] == null) {
                    bufferField[xx][yy] = null;
                } else {
                    bufferField[xx][yy] = new Cell(xx, yy, field[xx][yy].getValue());
                }
            }
        }
    }

    /**
     * Perform undo
     */
    public void revertCell() {
        for (int xx = 0; xx < 4; xx++) {
            for (int yy = 0; yy < 4; yy++) {
                if (undoField[xx][yy] == null) {
                    field[xx][yy] = null;
                } else {
                    field[xx][yy] = new Cell(xx, yy, undoField[xx][yy].getValue());
                }
            }
        }
    }

    public void clearGrid() {
        for (int xx = 0; xx < 4; xx++) {
            for (int yy = 0; yy < 4; yy++) {
                field[xx][yy] = null;
            }
        }
    }

    private void clearUndoGrid() {
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                undoField[xx][yy] = null;
            }
        }
    }

    public Cell[][] getBufferGrid() {
        return bufferField;
    }
}