package com.example.project_battleships;

import java.io.Serializable;

public class Ship implements Serializable {
    private int x;
    private int y;
    private int length;
    private boolean horizontal;
    private boolean isPlaced;
    private boolean isWrecked;

    public Ship(int length) {
        this.x = -1;
        this.y = -1;
        this.length = length;
        this.horizontal = true;
        this.isPlaced = false;
        this.isWrecked = false;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLength() {
        return this.length;
    }

    public boolean isHorizontal() { return this.horizontal; }

    public void setHorizontal(boolean horizontal) { this.horizontal = horizontal; }

    public boolean isPlaced() { return this.isPlaced; }

    public void setPlaced(boolean placed) { this.isPlaced = placed; }

    public boolean isWrecked() { return this.isWrecked; }

    public boolean checkIfWrecked(CharactersBoard charactersBoard) {
        /*
        The function returns if the ship, which called the function, is wrecked or not,
        by checking if all of its parts on the board have been hit.
        */
        Character[][] board = charactersBoard.getCharactersBoard();
        if (this.horizontal) {
            for (int x = this.x; x < this.x + this.length; x++) {
                if (board[this.y][x] != 'h') {
                    this.isWrecked = false;
                    return false;
                }
            }
        } else {
            for (int y = this.y; y < this.y + this.length; y++) {
                if (board[y][this.x] != 'h') {
                    this.isWrecked = false;
                    return false;
                }
            }
        }
        this.isWrecked = true;
        return true;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "x=" + x +
                ", y=" + y +
                ", length=" + length +
                ", horizontal=" + horizontal +
                ", isPlaced=" + isPlaced +
                ", isWrecked=" + isWrecked +
                '}';
    }
}