package com.pczapiewski.pjatk.mytipproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {



    private double height = 0.0;
    private double weight = 0.0;
    private TextView heightTextView;
    private TextView weightTextView;

    private TextView bmiTextView;
    private TextView catTextView;
    private EditText weightEditText;
    private EditText heightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        heightTextView = (TextView) findViewById(R.id.heightTextView);
        weightTextView= (TextView) findViewById(R.id.weightTextView);
        bmiTextView = (TextView) findViewById(R.id.bmiTextView);
        catTextView = (TextView) findViewById(R.id.totalTextView);


        heightEditText = (EditText) findViewById(R.id.heightEditText);
        heightEditText.addTextChangedListener(heightEditTextWatcher);
        weightEditText = (EditText) findViewById(R.id.weightEditText);
        weightEditText.addTextChangedListener(weightEditTextWatcher);

    }

    // calculate and display tip and total amounts
    private void calculate() {
        double m =height/100;
        double bmi = weight / (m * m);
        bmiTextView.setText(String.valueOf(bmi));

        if (bmi < 15) {
            catTextView.setText(getResources().getString(R.string.cat0));
        } else if (bmi >=15 && bmi <= 16) {
            catTextView.setText(getResources().getString(R.string.cat1));
        } else if (bmi >16 && bmi <= 18.5) {
            catTextView.setText(getResources().getString(R.string.cat2));
        } else if (bmi >18.5 && bmi <= 25) {
            catTextView.setText(getResources().getString(R.string.cat3));
        } else if (bmi >25 && bmi <= 30) {
            catTextView.setText(getResources().getString(R.string.cat4));
        } else if (bmi >30 && bmi <= 35) {
            catTextView.setText(getResources().getString(R.string.cat5));
        } else if (bmi >35 && bmi <= 40) {
            catTextView.setText(getResources().getString(R.string.cat6));
        } else {
            catTextView.setText(getResources().getString(R.string.cat7));
        }
    }

    // listener object for the EditText's text-changed events
    private final TextWatcher heightEditTextWatcher = new TextWatcher() {
        // called when the user modifies the bill amount
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try { // get bill amount and display currency formatted value
                height = Double.parseDouble(s.toString());
                heightTextView.setText(String.valueOf(height));
            }
            catch (NumberFormatException e) { // if s is empty or non-numeric
                heightTextView.setText("");
                height = 0.0;
            }
            if(!weightEditText.getText().toString().equalsIgnoreCase("")) {
                calculate(); // update the tip and total TextViews
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    };
    private final TextWatcher weightEditTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try { // get bill amount and display currency formatted value
                weight = Double.parseDouble(s.toString());
                weightTextView.setText(String.valueOf(weight));
            }
            catch (NumberFormatException e) { // if s is empty or non-numeric
                weightTextView.setText("");
                weight = 0.0;
            }
            if(!heightEditText.getText().toString().equalsIgnoreCase("")) {
                calculate(); // update the tip and total TextViews
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    };
}
