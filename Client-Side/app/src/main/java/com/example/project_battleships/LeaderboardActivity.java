package com.example.project_battleships;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {
    ListView lvLeaderboard;
    Button btnCloseLeaderboard;
    LeaderboardAdapter leaderboardAdapter;
    String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Intent dataIntent = getIntent();
        username = dataIntent.getStringExtra("Username");
        ArrayList<JSONArray> playersDataList = getPlayersDataList();
        leaderboardAdapter = new LeaderboardAdapter(this, 0, 0, playersDataList, username);
        lvLeaderboard = (ListView) findViewById(R.id.lvLeaderboard);
        btnCloseLeaderboard = (Button) findViewById(R.id.btnCloseLeaderboard);
        lvLeaderboard.setAdapter(leaderboardAdapter);
        btnCloseLeaderboard.setOnClickListener(this);
    }

    public ArrayList<JSONArray> getPlayersDataList() {
        /*
        The function sends to the server a request, and gets the first 20 players with the most
        wins. It puts the data from every user in a list which
        will be used in LeaderboardAdapter.
        */
        JSONObject getLeaderboard = new JSONObject();
        try {
            getLeaderboard.put("request", "get_players_by_wins");
            Client client = new Client(getLeaderboard);
            JSONObject received = client.execute().get();
            String response = received.getString("response");
            System.out.println("received data: " + response);
            JSONArray playersData = received.getJSONArray("players_order");
            ArrayList<JSONArray> playersDataList = new ArrayList<JSONArray>();
            for (int i = 0; i < playersData.length(); i++) {
                try {
                    playersDataList.add(playersData.getJSONArray(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return playersDataList;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Couldn't upload the leaderboard", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnCloseLeaderboard) {
            finish();
        }
    }

    @Override
    public void onBackPressed() { }
}