package com.example.nav

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.text.TextWatcher


class MainActivity : AppCompatActivity() {
    private var mTextMessage: TextView? = null
    private var height = 0.0
    private var weight = 0.0
    private var age = 0.0
    private var miffinGender = 0.0
    private var result_miffin = 0.0
    private var mass2 = 0.0
    private var height2 = 0.0


    private var heightTextView: TextView? = null
    private var weightTextView: TextView? = null
    private var bmiTextView: TextView? = null
    private var catTextView: TextView? = null
    private var caloriesTextView: TextView? = null
    private var bmi: ConstraintLayout? = null
    private var Mifflin: ConstraintLayout? = null
    private var welcome: ConstraintLayout? = null
    private var stats: ConstraintLayout? = null
    private var rb1: RadioButton? = null
    private var rb2: RadioButton? = null
    private var Img: ImageView? = null

    private var heightEditText: EditText? = null
    private var weightEditText: EditText? = null
    private var heightEditMiffin: EditText? = null
    private var weightEditMiffin: EditText? = null
    private var Age: EditText? = null
    private var webView: WebView? = null


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                mTextMessage!!.setText(R.string.title_home)
                welcome!!.visibility = View.VISIBLE
                bmi!!.visibility = View.INVISIBLE
                Mifflin!!.visibility = View.INVISIBLE
                stats!!.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_bmi -> {
                mTextMessage!!.setText(R.string.bmi)
                welcome!!.visibility = View.INVISIBLE
                bmi!!.visibility = View.VISIBLE
                stats!!.visibility = View.INVISIBLE
                Mifflin!!.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_miffin -> {
                mTextMessage!!.setText(R.string.Miffin)
                stats!!.visibility = View.INVISIBLE
                welcome!!.visibility = View.INVISIBLE
                bmi!!.visibility = View.INVISIBLE
                Mifflin!!.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stats -> {
                welcome!!.visibility = View.INVISIBLE
                bmi!!.visibility = View.INVISIBLE
                Mifflin!!.visibility = View.INVISIBLE
                stats!!.visibility = View.VISIBLE
                draw_chart()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_quiz -> {
                val a = Intent(this@MainActivity, Quiz::class.java)
                startActivity(a)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private val heightEditTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            try {
                height = java.lang.Double.parseDouble(s.toString())

            } catch (e: NumberFormatException) {

                height = 0.0
            }

            if (!weightEditText!!.text.toString().equals("", ignoreCase = true)) {
                calculate() // update the tip and total TextViews
            }
        }

        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
    private val heightEditMiffinTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            try {
                height2 = java.lang.Double.parseDouble(s.toString())

            } catch (e: NumberFormatException) {

                height2 = 0.0
            }

            if (!weightEditMiffin!!.text.toString().equals("", ignoreCase = true) && !Age!!.text.toString().equals("", ignoreCase = true) && (rb1!!.isChecked || rb2!!.isChecked)) {
                calculateMiffin()
            }
        }

        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
    private val AgeTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            try {
                age = java.lang.Double.parseDouble(s.toString())

            } catch (e: NumberFormatException) {

                age = 0.0
            }

            if (!heightEditMiffin!!.text.toString().equals("", ignoreCase = true) && !weightEditMiffin!!.text.toString().equals("", ignoreCase = true) && (rb1!!.isChecked || rb2!!.isChecked)) {
                calculateMiffin()
            }
        }

        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }


    private val weightEditTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            try {
                weight = java.lang.Double.parseDouble(s.toString())

            } catch (e: NumberFormatException) {

                weight = 0.0
            }

            if (!heightEditText!!.text.toString().equals("", ignoreCase = true)) {
                calculate() // update the tip and total TextViews
            }
        }


        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
    private val weightEditMiffinTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            try {
                mass2 = java.lang.Double.parseDouble(s.toString())

            } catch (e: NumberFormatException) {

                mass2 = 0.0
            }

            if (!heightEditMiffin!!.text.toString().equals("", ignoreCase = true) && !Age!!.text.toString().equals("", ignoreCase = true) && (rb1!!.isChecked || rb2!!.isChecked)) {
                calculateMiffin()
            }
        }


        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        mTextMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        heightTextView = findViewById<View>(R.id.heightTextView) as TextView
        weightTextView = findViewById<View>(R.id.weightTextView) as TextView
        caloriesTextView = findViewById<View>(R.id.calories) as TextView

        bmiTextView = findViewById<View>(R.id.bmiTextView) as TextView
        catTextView = findViewById<View>(R.id.categoryTextView) as TextView
        welcome = findViewById<View>(R.id.welcome) as ConstraintLayout
        bmi = findViewById<View>(R.id.bmi) as ConstraintLayout
        Mifflin = findViewById<View>(R.id.Mifflin) as ConstraintLayout
        stats = findViewById<View>(R.id.stats) as ConstraintLayout
        welcome!!.visibility = View.VISIBLE
        bmi!!.visibility = View.INVISIBLE
        Mifflin!!.visibility = View.INVISIBLE
        stats!!.visibility = View.INVISIBLE

        heightEditText = findViewById<View>(R.id.heightEditText) as EditText
        heightEditText!!.addTextChangedListener(heightEditTextWatcher)
        weightEditText = findViewById<View>(R.id.weightEditText) as EditText
        weightEditText!!.addTextChangedListener(weightEditTextWatcher)
        heightEditMiffin = findViewById<View>(R.id.heightEditMiffin) as EditText
        heightEditMiffin!!.addTextChangedListener(heightEditMiffinTextWatcher)
        weightEditMiffin = findViewById<View>(R.id.weightEditMiffin) as EditText
        weightEditMiffin!!.addTextChangedListener(weightEditMiffinTextWatcher)
        Age = findViewById<View>(R.id.Age) as EditText
        Age!!.addTextChangedListener(AgeTextWatcher)
        rb1 = findViewById<View>(R.id.radiofemale) as RadioButton
        rb2 = findViewById<View>(R.id.radioMale) as RadioButton
        Img = findViewById<View>(R.id.imageView) as ImageView
        Img!!.visibility = View.INVISIBLE

        webView = findViewById<View>(R.id.webview) as WebView


    }

    private fun calculate() {
        val m = height / 100
        val bmi = weight / (m * m)
        bmiTextView!!.text = bmi.toString()

        if (bmi < 15) {
            catTextView!!.text = resources.getString(R.string.cat0)
        } else if (bmi >= 15 && bmi <= 16) {
            catTextView!!.text = resources.getString(R.string.cat1)
        } else if (bmi > 16 && bmi <= 18.5) {
            catTextView!!.text = resources.getString(R.string.cat2)
        } else if (bmi > 18.5 && bmi <= 25) {
            catTextView!!.text = resources.getString(R.string.cat3)
        } else if (bmi > 25 && bmi <= 30) {
            catTextView!!.text = resources.getString(R.string.cat4)
        } else if (bmi > 30 && bmi <= 35) {
            catTextView!!.text = resources.getString(R.string.cat5)
        } else if (bmi > 35 && bmi <= 40) {
            catTextView!!.text = resources.getString(R.string.cat6)
        } else {
            catTextView!!.text = resources.getString(R.string.cat7)
        }
    }

    fun onRadioButtonClicked(v: View) {
        val checked = (v as RadioButton).isChecked

        when (v.getId()) {

            R.id.radiofemale -> {
                if (checked)
                    rb1!!.setTypeface(null, Typeface.BOLD_ITALIC)
                rb2!!.setTypeface(null, Typeface.NORMAL)
                rb2!!.isChecked = false
            }

            R.id.radioMale -> {
                if (checked)
                    rb2!!.setTypeface(null, Typeface.BOLD_ITALIC)
                rb1!!.setTypeface(null, Typeface.NORMAL)
                rb1!!.isChecked = false
            }
        }
        if (!heightEditMiffin!!.text.toString().equals("", ignoreCase = true) && !weightEditMiffin!!.text.toString().equals("", ignoreCase = true) && !Age!!.text.toString().equals("", ignoreCase = true)) {
            calculateMiffin()
        }
    }

    private fun calculateMiffin() {
        if (rb2!!.isChecked) {
            miffinGender = 5.0
        } else
            miffinGender = -161.0
        result_miffin = mass2 * 10 + height2 * 6.25 - age * 5 + miffinGender
        caloriesTextView!!.text = result_miffin.toString()
        Img!!.visibility = View.VISIBLE

        if (result_miffin < 1000) {
            Img!!.setImageResource(R.drawable.watermelon)
        } else if (result_miffin >= 1000 && result_miffin <= 1500) {
            Img!!.setImageResource(R.drawable.brocsouce)
        } else if (result_miffin > 1500 && result_miffin <= 2000) {
            Img!!.setImageResource(R.drawable.omlette)
        } else if (result_miffin > 2000) {
            Img!!.setImageResource(R.drawable.lasagne)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    private fun draw_chart() {
        val webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true
        val htmlData = ("<html>"
                + "  <head>"
                + "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>"
                + "    <script type=\"text/javascript\">"
                + "      google.charts.load('current', {'packages':['corechart']});"
                + "      google.charts.setOnLoadCallback(drawChart);"

                + "      function drawChart() {"
                + "         var data = google.visualization.arrayToDataTable(["
                + "          ['Week', 'Weight(kg)'],"
                + "          ['1',  120],"
                + "          ['2',  115],"
                + "          ['3',  113],"
                + "          ['4', 100],"
                + "          ['5', 102],"
                + "          ['6', 97],"
                + "          ['7', 93],"
                + "          ['8', 93],"
                + "          ['9',  90]"
                + "        ]);"

                + "        var options = {"
                + "          title: 'Weight loss chart (weekly)',"
                + "          curveType: 'function',"
                + "          legend: { position: 'bottom' }"
                + "        };"

                + "        var chart = new google.visualization.LineChart(document.getElementById('chart'));"

                + "        chart.draw(data, options);"
                + "      }"
                + "    </script>"
                + "  </head>"
                + "  <body>"
                + "    <div id=\"chart\" style=\"width: 390px; height: 380px;\"></div>"
                + "  </body>"
                + "</html>")
        webView!!.loadData(htmlData, "text/html", "UTF-8")
    }
}
