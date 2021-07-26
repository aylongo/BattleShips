package com.example.project_battleships;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Game implements Serializable {
    private Player player;
    private Computer computer;
    private String username;
    private boolean isPlayerTurn;

    public Game(String username) {
        this.player = null;
        this.computer = null;
        this.username = username;
        this.isPlayerTurn = (new Random()).nextBoolean();
    }

    public Player getPlayer() { return this.player; }

    public void setPlayer(Player player) { this.player = player; }

    public Computer getComputer() { return this.computer; }

    public void setComputer(Computer computer) { this.computer = computer; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public boolean isPlayerTurn() { return this.isPlayerTurn; }

    public void setPlayerTurn(boolean playerTurn) { this.isPlayerTurn = playerTurn; }

    public boolean isGameOver(Context context) {
        /*
        The function calculates how many ships the player and the computer have wrecked,
        and if one of them wrecked all of the other's ships, a dialog that declares the winner
        will be shown and the game will end.
         */
        ArrayList<Ship> playerShips = player.getShips(), compShips = computer.getShips();
        int playerWreckedShipsCounter = 0, compWreckedShipsCounter = 0;
        for (int i = 0; i < Constants.SHIPS_ARRAY_LENGTH; i++) {
            if (playerShips.get(i).isWrecked()) {
                playerWreckedShipsCounter++;
            } if (compShips.get(i).isWrecked()) {
                compWreckedShipsCounter++;
            }
        }
        if (playerWreckedShipsCounter == Constants.SHIPS_ARRAY_LENGTH) {
            // Computer Won
            createGameOverDialog(context, false);
            return true;
        } else if (compWreckedShipsCounter == Constants.SHIPS_ARRAY_LENGTH) {
            // Player Won
            createGameOverDialog(context, true);
            return true;
        }
        // Game isn't over yet
        return false;
    }

    public void createInGameMenuDialog(final Context context, final AudioManager audioManager, int maxMusicVolume) {
        /*
        The function creates and shows a dialog where the user can quit in the middle of the game,
        or change some settings in the app.
         */
        final Dialog inGameMenuDialog = new Dialog(context);
        inGameMenuDialog.setContentView(R.layout.dialog_in_game_menu);
        inGameMenuDialog.setCancelable(true);
        inGameMenuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnReturnToStart = (Button) inGameMenuDialog.findViewById(R.id.btnReturnToMain);
        SeekBar sbMusicVolume = (SeekBar) inGameMenuDialog.findViewById(R.id.sbMusicVolume);
        sbMusicVolume.setMax(maxMusicVolume);
        sbMusicVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        btnReturnToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoggedMainActivity.class);
                intent.putExtra("Username", username);
                intent.putExtra("Request", 1);
                inGameMenuDialog.dismiss();
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        inGameMenuDialog.show();
        sbMusicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void createGameOverDialog(final Context context, boolean isPlayerWon) {
        /*
        The function creates and shows a dialog which declares the game winner, and from that dialog
        the player can start a new game or move to the main menu (MainActivity).
        */
        final String username = this.username;
        final Dialog gameOverDialog = new Dialog(context);
        gameOverDialog.setContentView(R.layout.dialog_game_over);
        gameOverDialog.setCancelable(false);
        gameOverDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tvGameOver = (TextView) gameOverDialog.findViewById(R.id.tvGameOver);
        TextView tvTurns = (TextView) gameOverDialog.findViewById(R.id.tvTurns);
        Button btnReturnToStart = (Button) gameOverDialog.findViewById(R.id.btnReturnToMain);
        Button btnStartBattle = (Button) gameOverDialog.findViewById(R.id.btnStartBattle);
        btnReturnToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoggedMainActivity.class);
                intent.putExtra("Username", username);
                intent.putExtra("Request", 1);
                gameOverDialog.dismiss();
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        btnStartBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceShipsActivity.class);
                intent.putExtra("Username", username);
                intent.putExtra("Request", 1);
                gameOverDialog.dismiss();
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        if (isPlayerWon) {
            tvGameOver.setText("You Win!");
            tvTurns.setText(String.format("You've sunk the enemy ships in %d turns!", player.getTurns()));
        } else {
            tvGameOver.setText("You Lose!");
            tvTurns.setText(String.format("The enemy has sunk your ships in %d turns!", computer.getTurns()));
        }
        updateLastGameData(isPlayerWon);
        gameOverDialog.show();
    }

    public void updateLastGameData(boolean isPlayerWon) {
        JSONObject lastGameData = new JSONObject();
        try {
            lastGameData.put("request", "update_last_game_data");
            lastGameData.put("username", this.username);
            lastGameData.put("is_win", String.valueOf(isPlayerWon));
            lastGameData.put("score", String.valueOf(this.player.getScore()));
            Client client = new Client(lastGameData);
            JSONObject received = client.execute().get();
            System.out.println("Updated: " + received.getString("response"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
