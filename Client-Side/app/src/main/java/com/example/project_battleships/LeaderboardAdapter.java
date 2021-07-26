package com.example.project_battleships;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.util.List;

public class LeaderboardAdapter extends ArrayAdapter<JSONArray> {
    Context context;
    List<JSONArray> playersDataList;
    String username;

    public LeaderboardAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<JSONArray> objects, String username) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.playersDataList = objects;
        this.username = username;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
        The function inserts for every item in the LeaderboardActivity's ListView the information
        from playersDataList which represents the players' data from the database.
         */
        LayoutInflater layoutInflater = ((Activity) this.context).getLayoutInflater();
        View item = layoutInflater.inflate(R.layout.item_leaderboard, parent, false);
        TextView tvUsername = (TextView) item.findViewById(R.id.tvUsername);
        TextView tvHighestScore = (TextView) item.findViewById(R.id.tvHighestScore);
        TextView tvWins = (TextView) item.findViewById(R.id.tvWins);
        try {
            JSONArray playerStats = (JSONArray) this.playersDataList.get(position);
            tvUsername.setText((CharSequence) playerStats.get(0));
            tvHighestScore.setText((CharSequence) playerStats.get(1));
            tvWins.setText((CharSequence) playerStats.get(2));
            if (tvUsername.getText().toString().equals(this.username)) {
                item.setBackgroundColor(this.context.getResources().getColor(R.color.colorLeaderboardUserItem));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
