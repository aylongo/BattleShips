package com.example.project_battleships;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class Buttons {
    private Context context;
    private LinearLayout linearLayout;
    private Button[][] buttons;

    public Buttons(Context context, LinearLayout linearLayout) {
        this.context = context;
        this.linearLayout = linearLayout;
        this.buttons = new Button[Constants.BOARD_ARRAY_LENGTH][Constants.BOARD_ARRAY_LENGTH];
        // Setting every button in the matrix and its parameters.
        for (Button[] rowButtons : this.buttons) {
            for (int i = 0; i < Constants.BOARD_ARRAY_LENGTH; i++) {
                rowButtons[i] = new Button(context);
                setButtonParameters(rowButtons[i]);
            }
        }
        // Placing the buttons in the layout.
        placeButtonsInLayout();
    }

    public Context getContext() { return this.context; }

    public void setContext(Context context) { this.context = context; }

    public LinearLayout getLinearLayout() { return this.linearLayout; }

    public void setLinearLayout(LinearLayout linearLayout) { this.linearLayout = linearLayout; }

    public Button[][] getButtons() { return this.buttons; }

    public void setButtons(Button[][] buttons) { this.buttons = buttons; }

    public void setListener() {
        for (Button[] rowButtons : this.buttons) {
            for (Button button : rowButtons) {
                button.setOnClickListener((View.OnClickListener) this.context);
            }
        }
    }

    public void setClickable(boolean isClickable) {
        for (Button[] rowButtons : this.buttons) {
            for (Button button : rowButtons) {
                button.setEnabled(isClickable);
            }
        }
    }

    public Pair<Integer, Integer> getButtonPos(Button button) {
        int x, y;
        for (y = 0; y < Constants.BOARD_ARRAY_LENGTH; y++) {
            for (x = 0; x < Constants.BOARD_ARRAY_LENGTH; x++) {
                if (this.buttons[y][x] == button) {
                    return new Pair<>(x,y);
                }
            }
        }
        return null;
    }

    private void placeButtonsInLayout() {
        /*
        The function puts every row of buttons from the matrix in a new linear layout,
        and adds the linear layout with the buttons into the linear layout which appears
        on the screen.
         */
        for (Button[] rowButtons : this.buttons) {
            LinearLayout rowLinearLayout = new LinearLayout(this.context);
            LinearLayout.LayoutParams layoutParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParameters.setMargins(0, 6, 0, 0);
            rowLinearLayout.setLayoutParams(layoutParameters);
            for (Button button : rowButtons) {
                rowLinearLayout.addView(button);
            }
            this.linearLayout.addView(rowLinearLayout);
        }
    }

    private void setButtonParameters(Button button) {
        button.setBackgroundColor(context.getResources().getColor(R.color.colorButtonBackground));
        /*
        The program uses DisplayMetrics to get width of the user screen.
        By that it adjusts the board to a comfortable size.
        */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) this.context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        LinearLayout.LayoutParams buttonParameters = new LinearLayout.LayoutParams(width/12,width/12); // 95 was the default size.
        buttonParameters.setMargins(4, 0, 4, 0);
        button.setLayoutParams(buttonParameters);
    }
}
