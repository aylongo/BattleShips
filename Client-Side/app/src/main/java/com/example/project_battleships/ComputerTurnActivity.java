package com.example.project_battleships;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class ComputerTurnActivity extends AppCompatActivity {
    Game game;
    LinearLayout boardLinearLayout;
    Buttons buttons;
    Player player;
    Computer computer;
    CharactersBoard playerBoard;
    TextView tvScore;

    AudioManager audioManager;
    int maxMusicVolume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_turn);
        boardLinearLayout = (LinearLayout) findViewById(R.id.board_linear_layout);
        buttons = new Buttons(this, boardLinearLayout);
        tvScore = (TextView) findViewById(R.id.tvScore);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        game = (Game) intent.getSerializableExtra("Game");
        if (game != null) {
            player = game.getPlayer();
            computer = game.getComputer();
            playerBoard = player.getBoard();
            int playerScore = player.getScore();
            tvScore.setText(String.format("Your Score: %d", playerScore));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerButtonsBoard();
        // CountDownTimer Class delays the computer's turn so the player can see the computer's selection.
        new CountDownTimer(1500, 400) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                computer.doTurn(player);
                updatePlayerButtonsBoard();
                if (!game.isGameOver(ComputerTurnActivity.this)) {
                    game.setPlayerTurn(true);
                    Intent battleIntent = new Intent(ComputerTurnActivity.this, PlayerTurnActivity.class);
                    battleIntent.putExtra("Game", (Serializable) game);
                    startActivity(battleIntent);
                    finish();
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() { return; }

    public void updatePlayerButtonsBoard() {
        /*
        The function updates the player's buttons board
        in order to let the player to watch the changes in the game.
        */
        for (int y = 0; y < Constants.BOARD_ARRAY_LENGTH; y++) {
            for (int x = 0; x < Constants.BOARD_ARRAY_LENGTH; x++) {
                switch (playerBoard.getCharactersBoard()[y][x]) {
                    case 'w':
                        buttons.getButtons()[y][x].setBackgroundColor(getResources().getColor(R.color.colorButtonWrecked));
                        break;
                    case 'h':
                        buttons.getButtons()[y][x].setBackgroundColor(getResources().getColor(R.color.colorButtonHit));
                        break;
                    case 'm':
                        buttons.getButtons()[y][x].setBackgroundColor(getResources().getColor(R.color.colorButtonMiss));
                        break;
                    case 's':
                        buttons.getButtons()[y][x].setBackgroundColor(getResources().getColor(R.color.colorButtonShip));
                        break;
                    case 'o':
                        buttons.getButtons()[y][x].setBackgroundColor(getResources().getColor(R.color.colorButtonBackground));
                        break;
                }
            }
        }
    }
}
