package com.example.k_dev_master.gomoku;

import java.util.ArrayList;

public class Board {
    public Stone[][] field;
    private final int row = 19;
    private final int col = 19;

    public Board() {
        field = new Stone[row][col];
        clearBoard();
    }

    public Stone getStone(int x, int y) {
        return field[x][y];
    }

    public Stone getStone(Stone stone) {
        if (stone != null) {
            return field[stone.getX()][stone.getY()];
        } else {
            return null;
        }
    }
    public void insertStone(Stone stone) {
        field[stone.getX()][stone.getY()] = stone;
    }

    public ArrayList<Stone> getAvailableStones() {
        ArrayList<Stone> availableStones = new ArrayList<>();
        for (int xx = 0; xx < row; xx++) {
            for (int yy = 0; yy < col; yy++) {
                if (field[xx][yy] == null) {
                    availableStones.add(new Stone(xx, yy, Stone.Color.EMPTY));
                }
            }
        }
        return availableStones;
    }

    public void clearBoard() {
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                field[x][y] = null;
            }
        }
    }

    public boolean isStonesAvailable() {
        return (getAvailableStones().size() >= 1);
    }

    public boolean isStoneAvailable(Stone cell) {
        return !isStoneOccupied(cell);
    }

    public boolean isStoneOccupied(Stone cell) { return (getStone(cell) != null);}
}
