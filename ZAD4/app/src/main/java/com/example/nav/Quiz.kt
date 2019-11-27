package com.example.nav

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom
import java.util.ArrayList
import java.util.Collections

class Quiz : AppCompatActivity() {

    private var fileNameList: MutableList<String>? = null // flag file names
    private var quizFruitsList: MutableList<String>? = null // countries in current quiz
    private val regionsSet: Set<String>? = null // world regions in current quiz
    private var correctAnswer: String? = null // correct country for the current flag
    private var totalGuesses: Int = 0 // number of guesses made
    private var correctAnswers: Int = 0 // number of correct guesses
    private var random: SecureRandom? = null // used to randomize the quiz
    private var handler: Handler? = null // used to delay loading next flag
    private var shakeAnimation: Animation? = null // animation for incorrect guess

    private var quizLinearLayout: LinearLayout? = null // layout that contains the quiz
    private var questionNumberTextView: TextView? = null // shows current question #
    private var flagImageView: ImageView? = null // displays a flag
    private var answerTextView: TextView? = null // displays correct answer

    // called when a guess Button is touched
    private val guessButtonListener = View.OnClickListener { v ->
        val guessButton = v as Button
        val guess = guessButton.text.toString()
        val answer = getFruitName(correctAnswer!!)
        ++totalGuesses // increment number of guesses the user has made

        if (guess == answer) { // if the guess is correct
            ++correctAnswers // increment the number of correct answers

            // display correct answer in green text
            answerTextView!!.text = "$answer!"
            answerTextView!!.setTextColor(
                    resources.getColor(R.color.correct_answer,
                            this@Quiz.applicationContext.theme))

            // disable all guess Buttons
            disableButtons()
            // if the user has correctly identified FLAGS_IN_QUIZ flags
            if (correctAnswers == QUESTIONS_IN_QUIZ) {
                questionNumberTextView!!.setText(R.string.finished)
                var btn: Button
                var id: Int
                for (i in 1..4) {
                    id = resources.getIdentifier("button_$i", "id", packageName)
                    btn = findViewById<View>(id) as Button
                    btn.visibility = View.GONE
                }
                val text = findViewById<View>(R.id.guessFruitTextView) as TextView
                text.visibility = View.GONE
                answerTextView!!.setText(R.string.congratz)
                id = resources.getIdentifier("again", "id", packageName)
                btn = findViewById<View>(id) as Button
                btn.visibility = View.VISIBLE
                btn.setOnClickListener(finishedButtonListener)
            } else { // answer is correct but quiz is not over
                // load the next flag after a 2-second delay
                handler!!.postDelayed(
                        {
                            animate(true) // animate the flag off the screen
                        }, 2000) // 2000 milliseconds for 2-second delay
            }
        } else { // answer was incorrect
            flagImageView!!.startAnimation(shakeAnimation) // play shake

            // display "Incorrect!" in red
            answerTextView!!.setText(R.string.incorrect_answer)
            answerTextView!!.setTextColor(resources.getColor(
                    R.color.incorrect_answer, this@Quiz.applicationContext.theme))
            guessButton.isEnabled = false // disable incorrect answer
        }
    }
    private val finishedButtonListener = View.OnClickListener {
        var btn: Button
        var id: Int
        for (i in 1..4) {
            id = resources.getIdentifier("button_$i", "id", packageName)
            btn = findViewById<View>(id) as Button
            btn.visibility = View.VISIBLE
        }
        id = resources.getIdentifier("again", "id", packageName)
        btn = findViewById<View>(id) as Button
        btn.visibility = View.GONE
        val text = findViewById<View>(R.id.guessFruitTextView) as TextView
        text.visibility = View.VISIBLE
        resetQuiz()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        fileNameList = ArrayList()
        quizFruitsList = ArrayList()
        random = SecureRandom()
        handler = Handler()

        // load the shake animation that's used for incorrect answers
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.incorrect_shake)
        shakeAnimation!!.repeatCount = 3 // animation repeats 3 times


        quizLinearLayout = findViewById<View>(R.id.quizLinearLayout) as LinearLayout
        questionNumberTextView = findViewById<View>(R.id.questionNumberTextView) as TextView
        flagImageView = findViewById<View>(R.id.flagImageView) as ImageView
        answerTextView = findViewById<View>(R.id.answerTextView) as TextView


        var id = resources.getIdentifier("again", "id", packageName)
        val btn = findViewById<View>(id) as Button
        btn.setOnClickListener(finishedButtonListener)

        for (i in 1..4) {
            id = resources.getIdentifier("button_$i", "id", packageName)
            findViewById<View>(id).setOnClickListener(guessButtonListener)
        }
        resetQuiz()
    }

    fun resetQuiz() {
        val assets = this.assets
        fileNameList!!.clear() // empty list of image file names

        try {
            // get a list of all flag image files in this region
            val paths = assets.list("fruit")
            for (path in paths!!)
                fileNameList!!.add(path.replace(".png", ""))
        } catch (exception: IOException) {
            Log.e(TAG, "Error loading image file names", exception)
        }

        correctAnswers = 0 // reset the number of correct answers made
        totalGuesses = 0 // reset the total number of guesses the user made
        quizFruitsList!!.clear() // clear prior list of quiz countries

        var fruitCounter = 1
        val numberOfFruits = fileNameList!!.size

        // add FLAGS_IN_QUIZ random file names to the quizFruitsList
        while (fruitCounter <= QUESTIONS_IN_QUIZ) {
            val randomIndex = random!!.nextInt(numberOfFruits)

            // get the random file name
            val filename = fileNameList!![randomIndex]

            // if the region is enabled and it hasn't already been chosen
            if (!quizFruitsList!!.contains(filename)) {
                quizFruitsList!!.add(filename) // add the file to the list
                ++fruitCounter
            }
        }

        loadNextFruit() // start the quiz by loading the first flag
    }

    private fun loadNextFruit() {
        // get file name of the next flag and remove it from the list
        val nextImage = quizFruitsList!!.removeAt(0)
        correctAnswer = nextImage // update the correct answer
        answerTextView!!.text = "" // clear answerTextView

        // display current question number
        questionNumberTextView!!.text = getString(
                R.string.question, correctAnswers + 1, QUESTIONS_IN_QUIZ)

        // extract the region from the next image's name
        val region = nextImage.substring(0, nextImage.indexOf('-'))

        // use AssetManager to load next image from assets folder
        val assets = this.assets

        // get an InputStream to the asset representing the next flag
        // and try to use the InputStream
        try {
            assets.open(region + "/" + nextImage + ".png").use { stream ->
                // load the asset as a Drawable and display on the flagImageView
                val flag = Drawable.createFromStream(stream, nextImage)
                flagImageView!!.setImageDrawable(flag)

                animate(false) // animate the flag onto the screen
            }
        } catch (exception: IOException) {
            Log.e(TAG, "Error loading $nextImage", exception)
        }

        Collections.shuffle(fileNameList) // shuffle file names

        // put the correct answer at the end of fileNameList
        val correct = fileNameList!!.indexOf(correctAnswer.toString())
        fileNameList!!.add(fileNameList!!.removeAt(correct))

        var newGuessButton: Button
        for (i in 1..4) {
            val id = resources.getIdentifier("button_$i", "id", packageName)
            newGuessButton = findViewById<View>(id) as Button
            newGuessButton.isEnabled = true

            // get country name and set it as newGuessButton's text
            val filename = fileNameList!![random!!.nextInt(fileNameList!!.size - 1)]
            newGuessButton.text = getFruitName(filename)
        }

        // randomly replace one Button with the correct answer
        var row = random!!.nextInt(4)
        while (row == 0) {
            row = random!!.nextInt(4)
        }
        val column = random!!.nextInt(2) // pick random column
        //        LinearLayout randomRow = guessLinearLayouts[row]; // get the row
        val countryName = getFruitName(correctAnswer!!)
        val id = resources.getIdentifier("button_$row", "id", packageName)
        findViewById<View>(id).contentDescription = countryName
        val correctbtn = findViewById<Button>(id)
        correctbtn.text = countryName

        //((Button) randomRow.getChildAt(column)).setText(countryName);
    }

    // parses the country flag file name and returns the country name
    private fun getFruitName(name: String): String {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ')
    }

    // animates the entire quizLinearLayout on or off screen
    private fun animate(animateOut: Boolean) {
        // prevent animation into the the UI for the first flag
        if (correctAnswers == 0)
            return

        // calculate center x and center y
        val centerX = (quizLinearLayout!!.left + quizLinearLayout!!.right) / 2 // calculate center x
        val centerY = (quizLinearLayout!!.top + quizLinearLayout!!.bottom) / 2 // calculate center y

        // calculate animation radius
        val radius = Math.max(quizLinearLayout!!.width,
                quizLinearLayout!!.height)

        val animator: Animator

        // if the quizLinearLayout should animate out rather than in
        if (animateOut) {
            // create circular reveal animation
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, radius.toFloat(), 0f)
            animator.addListener(
                    object : AnimatorListenerAdapter() {
                        // called when the animation finishes
                        override fun onAnimationEnd(animation: Animator) {
                            loadNextFruit()
                        }
                    }
            )
        } else { // if the quizLinearLayout should animate in
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centerX, centerY, 0f, radius.toFloat())
        }

        animator.duration = 500 // set animation duration to 500 ms
        animator.start() // start the animation
    }

    private fun disableButtons() {
        var btn: Button
        for (i in 1..4) {
            val id = resources.getIdentifier("button_$i", "id", packageName)
            btn = findViewById<View>(id) as Button
            btn.isEnabled = false
        }
    }

    companion object {

        private val TAG = "FruitsQuiz Activity"

        private val QUESTIONS_IN_QUIZ = 10
    }
}
