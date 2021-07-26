package com.example.project_battleships;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin, btnRegister;

    AudioManager audioManager;
    int maxMusicVolume;
    Intent musicIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        musicIntent = new Intent(this, PlayService.class);
        startService(musicIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (view == btnRegister) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() { return; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.meInfo) {
            createInfoDialog(this);
        } else if (item.getItemId() == R.id.meSettings) {
            createSettingsDialog(this, musicIntent);
        }
        return true;
    }

    private void createInfoDialog(Context context) {
        final Dialog infoDialog = new Dialog(context);
        final ArrayList<Integer> listIv = new ArrayList<>();
        listIv.add(R.drawable.instructions_part_2);
        listIv.add(R.drawable.instructions_part_3);
        listIv.add(R.drawable.instructions_part_4);
        Window window = infoDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        infoDialog.setContentView(R.layout.dialog_info);
        infoDialog.setCancelable(false);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ImageView ivInstructions = (ImageView) infoDialog.findViewById(R.id.ivInstructions);
        ivInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listIv.isEmpty()) {
                    ivInstructions.setImageResource(listIv.get(0));
                    listIv.remove(0);
                }
                else {
                    infoDialog.dismiss();
                }
            }
        });
        infoDialog.show();
    }

    private void createSettingsDialog(Context context, final Intent musicIntent) {
        Dialog settingsDialog = new Dialog(context);
        settingsDialog.setContentView(R.layout.dialog_settings);
        settingsDialog.setCancelable(true);
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        SeekBar sbMusicVolume = (SeekBar) settingsDialog.findViewById(R.id.sbMusicVolume);
        sbMusicVolume.setMax(maxMusicVolume);
        sbMusicVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
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
        settingsDialog.show();
    }
}