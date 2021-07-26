package com.example.project_battleships;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private CharactersBoard board;
    private ArrayList<Ship> ships;
    private int score;
    private int turns;
    private int turnsUntilWreck;

    public Player() {
        this.board = new CharactersBoard();
        this.ships = shipsList();
        this.score = 0;
        this.turns = 0;
        this.turnsUntilWreck = 0;
    }

    public CharactersBoard getBoard() { return this.board; }

    public void setBoard(CharactersBoard board) { this.board = board; }

    public ArrayList<Ship> getShips() { return this.ships; }

    public void setShips(ArrayList<Ship> ships) { this.ships = ships; }

    public int getScore() { return this.score; }

    public int getTurns() { return this.turns; }

    public void incTurns() {
        this.turns++;
        this.turnsUntilWreck++;
    }

    public void updateScore() {
        /*
        The function adds points after to the player's score after he wrecks an opponent's ship.
        It adds 100 points minus the number of turns the player have made until he wrecked the ship.
        */
        this.score += (Constants.POINTS_FOR_WRECK - this.turnsUntilWreck);
        this.turnsUntilWreck = 0;
        /*
        After the addition of points, turnsUntilWreck is being zeroed, and increases again until
        a new ship will get wrecked.
        */
    }

    public Ship getShipOnPlace(int xPlace, int yPlace) {
         /*
         The function gets a list of ships and a place on the board.
         It checks for a ship that one of its parts were placed in this place and returns it.
         If it can't find any ship on that place, it returns null.
         */
        for (int i = 0; i < Constants.SHIPS_ARRAY_LENGTH; i++) {
            Ship ship = this.ships.get(i);
            if (ship.isHorizontal()) {
                if (yPlace == ship.getY() && (xPlace >= ship.getX() && xPlace <= ship.getX() + ship.getLength())) {
                    return ship;
                }
            } else {
                if (xPlace == ship.getX() && (yPlace >= ship.getY() && yPlace <= ship.getY() + ship.getLength())) {
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

    public boolean handleTurn(Computer computer, int x, int y) {
        CharactersBoard compBoard = computer.getBoard();
        switch (compBoard.getCharactersBoard()[y][x]) {
            case 'o':
                compBoard.getCharactersBoard()[y][x] = 'm';
                this.incTurns();
                return true;
            case 's':
                compBoard.getCharactersBoard()[y][x] = 'h';
                this.incTurns();
                Ship ship = computer.getShipOnPlace(x, y);
                if (ship != null && ship.checkIfWrecked(compBoard)) {
                    compBoard.updateWreckedShip(ship);
                    this.updateScore();
                }
                return true;
            default:
                return false;
        }
    }
}
