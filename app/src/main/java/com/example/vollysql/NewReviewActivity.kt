package com.example.vollysql

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar

class NewReviewActivity : AppCompatActivity() {
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewBodyEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_review);

        ratingBar = findViewById(R.id.rating_bar)
        reviewBodyEditText = findViewById(R.id.review_body)
        submitButton = findViewById(R.id.submit_review_button)

        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val body = reviewBodyEditText.text.toString()

            // Submit the review to the server or database
            // TODO: Implement review submission logic

            finish() // Close the activity after submission
        }
    }
}