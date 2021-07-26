package com.example.project_battleships;

import java.io.Serializable;
import java.util.ArrayList;

public class Computer implements Serializable {
    private CharactersBoard board;
    private ArrayList<Ship> ships;
    private int score;
    private int turns;

    public Computer() {
        this.board = new CharactersBoard();
        this.ships = shipsList();
        this.score = 0;
        this.turns = 0;
    }

    public CharactersBoard getBoard() { return this.board; }

    public void setBoard(CharactersBoard board) { this.board = board; }

    public ArrayList<Ship> getShips() { return this.ships; }

    public void setShips(ArrayList<Ship> ships) { this.ships = ships; }

    public int getScore() { return this.score; }

    public int getTurns() { return this.turns; }

    public void incTurns() { this.turns++; }

    public Ship getShipOnPlace(int xPlace, int yPlace) {
         /*
         The function gets a list of ships and a place on the board.
         It checks for a ship that one of its parts were placed in this place and returns it.
         If it can't find any ship, it returns null.
         */
        for (int i = 0; i < Constants.SHIPS_ARRAY_LENGTH; i++) {
            Ship ship = this.ships.get(i);
            if (ship.isHorizontal()) {
                if (yPlace == ship.getY() && (xPlace >= ship.getX() && xPlace < ship.getX() + ship.getLength())) {
                    return ship;
                }
            } else {
                if (xPlace == ship.getX() && (yPlace >= ship.getY() && yPlace < ship.getY() + ship.getLength())) {
                    return ship;
                }
            }
        }
        return null;
    }

    private ArrayList<Ship> shipsList() {
        ArrayList<Ship> ships = new ArrayList<>();
        ships.add(new Ship(2));
        ships.add(new Ship(3));
        ships.add(new Ship(3));
        ships.add(new Ship(4));
        ships.add(new Ship(5));
        return ships;
    }

    public void doTurn(Player player) {
        /*
        The function finds a free place on the player's board, and makes the computer's turn.
        A free place on the board is a place which the computer did not choose on last turns.
        */
        CharactersBoard playerBoard = player.getBoard();
        boolean foundPos = false;
        while (!foundPos) {
            int x = (int) (Math.random() * (Constants.BOARD_ARRAY_LENGTH));
            int y = (int) (Math.random() * (Constants.BOARD_ARRAY_LENGTH));
            switch (playerBoard.getCharactersBoard()[y][x]) {
                case 'o':
                    playerBoard.getCharactersBoard()[y][x] = 'm';
                    this.incTurns();
                    foundPos = true;
                    break;
                case 's':
                    playerBoard.getCharactersBoard()[y][x] = 'h';
                    this.incTurns();
                    Ship ship = player.getShipOnPlace(x, y);
                    if (ship != null) {
                        if (ship.checkIfWrecked(playerBoard)) {
                            playerBoard.updateWreckedShip(ship);
                        }
                    }
                    foundPos = true;
                    break;
                default:
                    foundPos = false;
            }
        }
    }

    public void doGodTurn(Player player) {
        /*
        GOD'S TURN
        The computer is getting touched by god himself and finds immediately the player's ships.
        The function finds 's' place on the player's board, and makes the computer's turn.
        */
        CharactersBoard playerBoard = player.getBoard();
        boolean foundPos = false;
        while (!foundPos) {
            int x = (int) (Math.random() * (Constants.BOARD_ARRAY_LENGTH));
            int y = (int) (Math.random() * (Constants.BOARD_ARRAY_LENGTH));
            if (playerBoard.getCharactersBoard()[y][x] == 's') {
                    playerBoard.getCharactersBoard()[y][x] = 'h';
                    this.incTurns();
                    Ship ship = player.getShipOnPlace(x, y);
                    if (ship != null) {
                        if (ship.checkIfWrecked(playerBoard)) {
                            playerBoard.updateWreckedShip(ship);
                        }
                    }
                    foundPos = true;
            }
        }
    }
}
