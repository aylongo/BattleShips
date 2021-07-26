package com.example.project_battleships;

import java.io.Serializable;
import java.util.Arrays;

public class CharactersBoard implements Serializable {
    private Character[][] board;

    public CharactersBoard() {
        this.board = new Character[Constants.BOARD_ARRAY_LENGTH][Constants.BOARD_ARRAY_LENGTH];

        for (Character[] rowChar : this.board) {
            Arrays.fill(rowChar, 'o');
        }
    }

    public Character[][] getCharactersBoard() { return board; }

    public void setBoard(Character[][] board) { this.board = board; }

    public void updateWreckedShip(Ship ship) {
        /*
        The function updates a wrecked ship on the characters board,
        and changes its parts mark on the board from 'h' to 'w',
        so that the update of the buttons board will show the wrecked ships in a different color.
        A wrecked ship is a ship which all of her parts are marked on the board as 'h'.
        */
        if (ship.isHorizontal()) {
            for (int x = ship.getX(); x < ship.getX() + ship.getLength(); x++) {
                this.board[ship.getY()][x] = 'w';
            }
        } else {
            for (int y = ship.getY(); y < ship.getY() + ship.getLength(); y++) {
                this.board[y][ship.getX()] = 'w';
            }
        }
    }

    @Override
    public String toString() {
        String strBoard = "";
        for (int y = 0; y < Constants.BOARD_ARRAY_LENGTH; y++) {
            String strRowBoard = "";
            for (int x = 0; x < Constants.BOARD_ARRAY_LENGTH; x++) {
                strRowBoard += String.valueOf(this.board[y][x]) + "  ";
            }
            strBoard += strRowBoard + "\n";
        }
        return "Board {\n" + strBoard + "\n}";
    }
}
