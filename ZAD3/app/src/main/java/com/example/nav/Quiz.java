package com.example.nav;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Quiz extends AppCompatActivity {

    private static final String TAG = "FruitsQuiz Activity";

    private static final int QUESTIONS_IN_QUIZ = 10;

    private List<String> fileNameList; // flag file names
    private List<String> quizFruitsList; // countries in current quiz
    private Set<String> regionsSet; // world regions in current quiz
    private String correctAnswer; // correct country for the current flag
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses
    private SecureRandom random; // used to randomize the quiz
    private Handler handler; // used to delay loading next flag
    private Animation shakeAnimation; // animation for incorrect guess

    private LinearLayout quizLinearLayout; // layout that contains the quiz
    private TextView questionNumberTextView; // shows current question #
    private ImageView flagImageView; // displays a flag
    private TextView answerTextView; // displays correct answer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        fileNameList = new ArrayList<>();
        quizFruitsList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        // load the shake animation that's used for incorrect answers
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); // animation repeats 3 times


        quizLinearLayout = (LinearLayout) findViewById(R.id.quizLinearLayout);
        questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        flagImageView = (ImageView) findViewById(R.id.flagImageView);
        answerTextView = (TextView) findViewById(R.id.answerTextView);


        int id = getResources().getIdentifier("again", "id", getPackageName());
        Button btn = (Button) findViewById(id);
        btn.setOnClickListener(finishedButtonListener);

        for (int i = 1; i <= 4; i++) {
            id = getResources().getIdentifier("button_"+i, "id", getPackageName());
            findViewById(id).setOnClickListener(guessButtonListener);
        }
        resetQuiz();
    }
    public void resetQuiz() {
        AssetManager assets = this.getAssets();
        fileNameList.clear(); // empty list of image file names

        try {
            // get a list of all flag image files in this region
            String[] paths = assets.list("fruit");
                for (String path : paths)
                    fileNameList.add(path.replace(".png", ""));
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }

        correctAnswers = 0; // reset the number of correct answers made
        totalGuesses = 0; // reset the total number of guesses the user made
        quizFruitsList.clear(); // clear prior list of quiz countries

        int fruitCounter = 1;
        int numberOfFruits = fileNameList.size();

        // add FLAGS_IN_QUIZ random file names to the quizFruitsList
        while (fruitCounter <= QUESTIONS_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfFruits);

            // get the random file name
            String filename = fileNameList.get(randomIndex);

            // if the region is enabled and it hasn't already been chosen
            if (!quizFruitsList.contains(filename)) {
                quizFruitsList.add(filename); // add the file to the list
                ++fruitCounter;
            }
        }

        loadNextFruit(); // start the quiz by loading the first flag
    }
    private void loadNextFruit() {
        // get file name of the next flag and remove it from the list
        String nextImage = quizFruitsList.remove(0);
        correctAnswer = nextImage; // update the correct answer
        answerTextView.setText(""); // clear answerTextView

        // display current question number
        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), QUESTIONS_IN_QUIZ));

        // extract the region from the next image's name
        String region = nextImage.substring(0, nextImage.indexOf('-'));

        // use AssetManager to load next image from assets folder
        AssetManager assets = this.getAssets();

        // get an InputStream to the asset representing the next flag
        // and try to use the InputStream
        try (InputStream stream =
                     assets.open(region + "/" + nextImage + ".png")) {
            // load the asset as a Drawable and display on the flagImageView
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);

            animate(false); // animate the flag onto the screen
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        Collections.shuffle(fileNameList); // shuffle file names

        // put the correct answer at the end of fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        Button newGuessButton;
        for (int i = 1; i <= 4; i++) {
            int id = getResources().getIdentifier("button_"+i, "id", getPackageName());
            newGuessButton = (Button) findViewById(id);
            newGuessButton.setEnabled(true);

            // get country name and set it as newGuessButton's text
            String filename = fileNameList.get(random.nextInt(fileNameList.size()-1));
            newGuessButton.setText(getFruitName(filename));
        }

        // randomly replace one Button with the correct answer
        int row = random.nextInt(4);
        while (row == 0) {
            row = random.nextInt(4);
        }
        int column = random.nextInt(2); // pick random column
//        LinearLayout randomRow = guessLinearLayouts[row]; // get the row
        String countryName = getFruitName(correctAnswer);
        int id = getResources().getIdentifier("button_"+ row, "id", getPackageName());
        findViewById(id).setContentDescription(countryName);
        Button correctbtn = findViewById(id);
        correctbtn.setText(countryName);

        //((Button) randomRow.getChildAt(column)).setText(countryName);
    }

    // parses the country flag file name and returns the country name
    private String getFruitName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    // animates the entire quizLinearLayout on or off screen
    private void animate(boolean animateOut) {
        // prevent animation into the the UI for the first flag
        if (correctAnswers == 0)
            return;

        // calculate center x and center y
        int centerX = (quizLinearLayout.getLeft() +
                quizLinearLayout.getRight()) / 2; // calculate center x
        int centerY = (quizLinearLayout.getTop() +
                quizLinearLayout.getBottom()) / 2; // calculate center y

        // calculate animation radius
        int radius = Math.max(quizLinearLayout.getWidth(),
                quizLinearLayout.getHeight());

        Animator animator;

        // if the quizLinearLayout should animate out rather than in
        if (animateOut) {
            // create circular reveal animation
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        // called when the animation finishes
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadNextFruit();
                        }
                    }
            );
        }
        else { // if the quizLinearLayout should animate in
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(500); // set animation duration to 500 ms
        animator.start(); // start the animation
    }

    // called when a guess Button is touched
    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getFruitName(correctAnswer);
            ++totalGuesses; // increment number of guesses the user has made

            if (guess.equals(answer)) { // if the guess is correct
                ++correctAnswers; // increment the number of correct answers

                // display correct answer in green text
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer,
                                Quiz.this.getApplicationContext().getTheme()));

                // disable all guess Buttons
                disableButtons();
                // if the user has correctly identified FLAGS_IN_QUIZ flags
                if (correctAnswers == QUESTIONS_IN_QUIZ) {
                    questionNumberTextView.setText(R.string.finished);
                    Button btn;
                    int id;
                    for (int i = 1; i <= 4; i++) {
                        id = getResources().getIdentifier("button_" + i, "id", getPackageName());
                        btn = (Button) findViewById(id);
                        btn.setVisibility(View.GONE);
                    }
                    TextView text = (TextView) findViewById(R.id.guessFruitTextView);
                    text.setVisibility(View.GONE);
                    answerTextView.setText(R.string.congratz);
                    id = getResources().getIdentifier("again", "id", getPackageName());
                    btn = (Button) findViewById(id);
                    btn.setVisibility(View.VISIBLE);
                    btn.setOnClickListener(finishedButtonListener);
                }
                else { // answer is correct but quiz is not over
                    // load the next flag after a 2-second delay
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    animate(true); // animate the flag off the screen
                                }
                            }, 2000); // 2000 milliseconds for 2-second delay
                }
            }
            else { // answer was incorrect
                flagImageView.startAnimation(shakeAnimation); // play shake

                // display "Incorrect!" in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(
                        R.color.incorrect_answer, Quiz.this.getApplicationContext().getTheme()));
                guessButton.setEnabled(false); // disable incorrect answer
            }
        }
    };

    private void disableButtons() {
        Button btn;
        for (int i = 1; i <= 4; i++) {
            int id = getResources().getIdentifier("button_" + i, "id", getPackageName());
            btn = (Button) findViewById(id);
            btn.setEnabled(false);
        }
    }
    private View.OnClickListener finishedButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn;
            int id;
            for (int i = 1; i <= 4; i++) {
                id = getResources().getIdentifier("button_" + i, "id", getPackageName());
                btn = (Button) findViewById(id);
                btn.setVisibility(View.VISIBLE);
            }
            id = getResources().getIdentifier("again", "id", getPackageName());
            btn = (Button) findViewById(id);
            btn.setVisibility(View.GONE);
            TextView text = (TextView) findViewById(R.id.guessFruitTextView);
            text.setVisibility(View.VISIBLE);
            resetQuiz();
        }

    };
}
