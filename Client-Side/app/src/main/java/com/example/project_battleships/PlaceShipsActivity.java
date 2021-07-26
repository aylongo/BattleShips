package com.example.project_battleships;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class PlaceShipsActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout boardLinearLayout;
    Game game;
    Player player;
    Computer computer;
    Buttons buttons;

    int playerShipsIndex = 0;
    int shipLength;
    String username;

    TextView tvShipsLeft;
    TextView tvShipLength;
    Button btnRotateShip;

    AudioManager audioManager;
    int maxMusicVolume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_ships);
        boardLinearLayout = (LinearLayout) findViewById(R.id.board_linear_layout);
        username = getIntent().getStringExtra("Username");
        game = new Game(username);
        player = new Player();
        computer = new Computer();
        buttons = new Buttons(this, boardLinearLayout);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {
        super.onStart();
        buttons.setClickable(true);
        buttons.setListener();

        tvShipsLeft = (TextView) findViewById(R.id.tvShipsLeft);
        tvShipLength = (TextView) findViewById(R.id.tvShipLength);
        btnRotateShip = (Button) findViewById(R.id.btnRotateShip);

        tvShipsLeft.setText(Constants.SHIPS_ARRAY_LENGTH + " Ships Left");
        shipLength = player.getShips().get(playerShipsIndex).getLength();
        tvShipLength.setText("Current Ship's Length: " + shipLength);
        btnRotateShip.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        game.createInGameMenuDialog(this, audioManager, maxMusicVolume);
    }

    @Override
    public void onClick(View view) {
        ArrayList<Ship> playerShips = player.getShips();
        if (view == btnRotateShip) {
            boolean shipRotate = playerShips.get(playerShipsIndex).isHorizontal();
            if (shipRotate) {
                playerShips.get(playerShipsIndex).setHorizontal(false);
                btnRotateShip.setHint("Vertical");
            } else {
                playerShips.get(playerShipsIndex).setHorizontal(true);
                btnRotateShip.setHint("Horizontal");
            }
        } else {
            Ship ship = playerShips.get(playerShipsIndex);
            if (handlePlacePlayerShip((Button) view, ship)) {
                tvShipsLeft.setText(Constants.SHIPS_ARRAY_LENGTH - playerShipsIndex + " Ships Left");
                if (playerShipsIndex == Constants.SHIPS_ARRAY_LENGTH) {
                    buttons.setClickable(false);
                    btnRotateShip.setEnabled(false);
                    placeShipsOnCompBoard(computer.getBoard());
                    game.setPlayer(player);
                    game.setComputer(computer);
                    Intent battleIntent;
                    if (game.isPlayerTurn()) {
                        battleIntent = new Intent(this, PlayerTurnActivity.class);
                    } else {
                        battleIntent = new Intent(this, ComputerTurnActivity.class);
                    }
                    battleIntent.putExtra("Game", (Serializable) game);
                    startActivity(battleIntent);
                    finish();
                } else {
                    shipLength = playerShips.get(playerShipsIndex).getLength();
                    tvShipLength.setText("Current Ship's Length: " + shipLength);
                    btnRotateShip.setHint("Horizontal");
                }
            }
        }
    }

    public void placeShipsOnCompBoard(CharactersBoard compBoard) {
        int compShipsIndex = 0;
        ArrayList<Ship> compShips = computer.getShips();
        Character[][] board = compBoard.getCharactersBoard();
        while (compShipsIndex < Constants.SHIPS_ARRAY_LENGTH) {
            Ship ship = compShips.get(compShipsIndex);
            while (!ship.isPlaced()) {
                int x = (int) (Math.random() * (Constants.BOARD_ARRAY_LENGTH - 1));
                int y = (int) (Math.random() * (Constants.BOARD_ARRAY_LENGTH - 1));
                ship.setHorizontal((new Random()).nextBoolean());
                if (isLegalShipPlace(board, ship, x, y)) {
                    ship.setX(x);
                    ship.setY(y);
                    if (placeShipOnCharBoard(ship, board)) {
                        ship.setPlaced(true);
                        compShipsIndex++;
                    }
                }
            }
        }
    }

    public boolean handlePlacePlayerShip(Button button, Ship ship) {
        Character[][] board = player.getBoard().getCharactersBoard();
        Pair<Integer, Integer> point = buttons.getButtonPos(button);
        int x = point.first, y = point.second;
        if (!ship.isPlaced() && isLegalShipPlace(board, ship, x, y)) {
            ship.setX(x); ship.setY(y);
            if (placeShipOnCharBoard(ship, board)) {
                placeShipOnButtonsBoard(ship);
                ship.setPlaced(true);
                playerShipsIndex++;
            }
            return true;
        } else {
            Toast.makeText(this, "Please place the ships on a legal place", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean placeShipOnCharBoard(Ship ship, Character[][] board) {
        /*
        The function gets the ship and the characters board which the ship will be placed on.
        It fills the places which the ship is placed on, according to its length and rotation,
        with the character 's'.
        */
        int xStart = ship.getX(), yStart = ship.getY(), shipLength = ship.getLength();
        if (ship.isHorizontal()) { // For a horizontal ship
            try {
                for (int x = xStart; x < xStart + shipLength; x++) {
                    board[yStart][x] = 's';
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else { // For a vertical ship
            try {
                for (int y = yStart; y < yStart + shipLength; y++) {
                    board[y][xStart] = 's';
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void placeShipOnButtonsBoard(Ship ship) {
        /*
        The function gets the ship which was placed.
        It fills the places which the ship is placed on, according to its length and rotation,
        with a gray color.
        */
        int xStart = ship.getX(), yStart = ship.getY(), shipLength = ship.getLength();;
        Button[][] buttons = this.buttons.getButtons();
        if (ship.isHorizontal()) { // For a horizontal ship
            for (int x = ship.getX(); x < xStart + shipLength; x++) {
                buttons[yStart][x].setBackgroundColor(getResources().getColor(R.color.colorButtonShip));
            }
        } else { // For a vertical ship
            for (int y = ship.getY(); y < yStart + shipLength; y++) {
                buttons[y][xStart].setBackgroundColor(getResources().getColor(R.color.colorButtonShip));
            }
        }
    }

    public boolean isLegalShipPlace(Character[][] board,Ship ship, int x, int y) {
        /*
        The function checks if the places the ship will be placed on are in the area of the board
        and if it collides with another ship. returns true if it meets the conditions and
        false if it doesn't.
        */
        int shipLength = ship.getLength();
        if (ship.isHorizontal()) {
            if (x + shipLength <= Constants.BOARD_ARRAY_LENGTH) {
                for (int xStart = x; x < xStart + shipLength; x++) {
                    if (board[y][x] != 'o') {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            if (y + shipLength <= Constants.BOARD_ARRAY_LENGTH) {
                for (int yStart = y; y < yStart + shipLength; y++) {
                    if (board[y][x] != 'o') {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        game.createInGameMenuDialog(this, audioManager, maxMusicVolume);
        return true;
    }
}