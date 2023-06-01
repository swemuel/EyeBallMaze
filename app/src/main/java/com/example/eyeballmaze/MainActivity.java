package com.example.eyeballmaze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.SecurityLog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.widget.GridLayout;

import android.widget.FrameLayout;

import android.widget.Button;

import android.widget.ImageView;

import android.view.View;
import android.widget.TextView;

import java.io.Console;
import java.text.MessageFormat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public Game newGame = new Game();
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
    }

    public void handleClick(int row, int column) {
        TextView textView = findViewById(R.id.errorMessage);
        TextView goalCount = findViewById(R.id.goalCountText);

        if (newGame.canMoveTo(row, column)) {
            // Reset error text
            textView.setText("");
            //check if goal complete to update view (model will already know)
            if (newGame.hasGoalAt(row, column)) {
                removeGoalImage(row, column);
                // Check if game complete
                textView.setText("Goal Reached!");
                try {
                    goalCount.setText("Goals: " + (newGame.getGoalCount() - 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            newGame.moveTo(row, column);
            removePlayerImage();
            addPlayerImage(row, column, newGame.getEyeballDirection());

            if (newGame.getGoalCount() == 0) {
                textView.setText("Game Complete!");
                gridLayout.setBackgroundColor(android.graphics.Color.GREEN);
            }
        } else {
            textView.setText("Eyeball can only move to the same COLOUR or SHAPE, and CANNOT move backwards!!!");
        }
        System.out.println(newGame.getEyeballRow());
        System.out.println(newGame.getEyeballColumn());
    }

    public String setSquareBackground(int row, int column) {
        ArrayList<Square> squares = newGame.currentLevel.mazeGrid;
        String result = "";
        for (Square square : squares) {
            if (row == square.getRow() && column == square.getColumn()) {
                String squareColor = square.color.toString();
                String squareShape = square.shape.toString();
                result = squareColor.toLowerCase() + "_" + squareShape.toLowerCase();
            }
        }
        return result;
    }

    private void removePlayerImage() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View v = gridLayout.getChildAt(i);
            if (v instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) v;
                for (int j = 0; j < frameLayout.getChildCount(); j++) {
                    View subView = frameLayout.getChildAt(j);
                    if (subView.getTag() != null && subView.getTag().equals("playerImage")) {
                        frameLayout.removeView(subView);
                        return;
                    }
                }
            }
        }
    }

    private void removeGoalImage(int row, int col) {
        String goalTag = "goalImage" + row + "_" + col;
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View v = gridLayout.getChildAt(i);
            if (v instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) v;
                for (int j = 0; j < frameLayout.getChildCount(); j++) {
                    View subView = frameLayout.getChildAt(j);
                    if (subView.getTag() != null && subView.getTag().equals(goalTag)) {
                        System.out.println(subView);
                        frameLayout.removeView(subView);
                        return;
                    }
                }
            }
        }
    }

    private void addPlayerImage(int row, int column, Direction direction) {
        FrameLayout frameLayout = (FrameLayout) gridLayout.getChildAt(row * gridLayout.getColumnCount() + column);
        ImageView playerImage = new ImageView(this);
        String eyeballImage = "";
        switch (direction) {
            case UP -> eyeballImage = "eyesu";
            case DOWN -> eyeballImage = "eyesd";
            case LEFT -> eyeballImage = "eyesl";
            case RIGHT -> eyeballImage = "eyesr";
        }

        int playerImageResId = getResources().getIdentifier(eyeballImage, "drawable", getPackageName());
        playerImage.setImageResource(playerImageResId);
        playerImage.setTag("playerImage");
        frameLayout.addView(playerImage);
    }

    private void restartGame() {
        newGame = new Game();
        gridLayout.removeAllViews();
        start();
    }

    private void start() {
        gridLayout = findViewById(R.id.mazeGridLayout);
        TextView goalCount = findViewById(R.id.goalCountText);

        newGame.addLevel(4, 3);
        int height = newGame.currentLevel.height;
        int width = newGame.currentLevel.width;

        gridLayout.setRowCount(height);
        gridLayout.setColumnCount(width);
        gridLayout.setBackgroundColor(0xC9C9FFFF);

        newGame.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 0, 0);
        newGame.addSquare(new PlayableSquare(Color.BLUE, Shape.CROSS), 0, 1);
        newGame.addSquare(new PlayableSquare(Color.RED, Shape.CROSS), 0, 2);
        newGame.addSquare(new PlayableSquare(Color.BLUE, Shape.STAR), 1, 0);
        newGame.addSquare(new BlankSquare(), 1, 1);
        newGame.addSquare(new PlayableSquare(Color.YELLOW, Shape.STAR), 1, 2);
        newGame.addSquare(new PlayableSquare(Color.YELLOW, Shape.FLOWER), 2, 0);
        newGame.addSquare(new PlayableSquare(Color.GREEN, Shape.CROSS), 2, 1);
        newGame.addSquare(new PlayableSquare(Color.GREEN, Shape.STAR), 2, 2);
        newGame.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 3, 0);
        newGame.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 3, 1);
        newGame.addSquare(new BlankSquare(), 3, 2);

        newGame.addEyeball(3, 0, Direction.UP);
        newGame.addGoal(0, 2);
        newGame.addGoal(2, 1);
        try {
            goalCount.setText("Goals: " + newGame.getGoalCount());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                // Create a new Button for the view
                Button button = new Button(this);
                button.setId(View.generateViewId());
                // Assign the row and column to the button
                button.setTag(new int[]{row, col});
                // Sets background images
                String fileName = setSquareBackground(row, col);
                int resId = getResources().getIdentifier(fileName, "drawable", getPackageName());
                button.setBackgroundResource(resId);

                // Set button click listener
                button.setOnClickListener(view -> {
                    int[] position = (int[]) view.getTag();
                    System.out.println(position[0]);
                    System.out.println(position[1]);
                    handleClick(position[0], position[1]);
                });

                //Reset Button
                Button resetButton = findViewById(R.id.resetButton);
                resetButton.setOnClickListener(view -> {
                    restartGame();
                });

                // Create a new FrameLayout to hold both the button and player image
                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.addView(button);

                // If this is the square where you want to add your player image:
                if (row == newGame.getEyeballRow() && col == newGame.getEyeballColumn()) {
                    // Create a new ImageView for the player
                    ImageView playerImage = new ImageView(this);
                    playerImage.setTag("playerImage");

                    // Set the player image
                    int playerImageResId = getResources().getIdentifier("eyesu", "drawable", getPackageName());
                    playerImage.setImageResource(playerImageResId);

                    // Add the player image to the FrameLayout
                    frameLayout.addView(playerImage);
                }

                // Add goal image
                if (newGame.hasGoalAt(row, col)) {
                    // Create a new ImageView for the player
                    ImageView goalImage = new ImageView(this);
                    goalImage.setTag("goalImage" + row + "_" + col);

                    // Set the player image
                    int goalImageResId = getResources().getIdentifier("xgoal", "drawable", getPackageName());
                    goalImage.setImageResource(goalImageResId);

                    // Add the player image to the FrameLayout
                    frameLayout.addView(goalImage);
                }

                gridLayout.addView(frameLayout);
            }
        }
    }
}