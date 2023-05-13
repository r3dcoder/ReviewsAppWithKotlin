package com.example.vollysql


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.vollysql.databinding.ActivityEditReviewBinding

import org.json.JSONObject

class EditReviewActivity : DrawerBaseActivity()  {

    private lateinit var activityBinding: ActivityEditReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityEditReviewBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)


        // Find UI elements by their IDs
        var ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        var reviewEditText = findViewById<EditText>(R.id.review_body)
        var saveButton = findViewById<Button>(R.id.submit_review_button)

        // Get the review ID passed from the previous activity
        val reviewId = intent.getIntExtra("review_id", -1)
        fetchReview(reviewId)
        val queue = Volley.newRequestQueue(this)
        // Set onClickListener for the save button
        saveButton.setOnClickListener {
            val rating = ratingBar.rating
            val reviewBody = reviewEditText.text.toString()

            // Update the review data in the database
            val updateUrl = "${MainActivity.url+MainActivity.reviewUrl}&update_review=1&review_id=$reviewId"
            val updateRequest = object : StringRequest(
                Method.POST,
                updateUrl,
                Response.Listener { response ->
                    Log.d("response", response)
                    Toast.makeText(this, "Review updated successfully", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, ReviewListActivity::class.java ))
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["rating"] = rating.toString()
                    params["body"] = reviewBody
                    return params
                }
            }
            queue.add(updateRequest)
        }
    }

    private fun fetchReview(reviewId:Int) {
        // Fetch the review data from the database using the review ID
        val queue = Volley.newRequestQueue(this)
        val url = "${MainActivity.url+MainActivity.reviewUrl}&get_review=1&review_id=$reviewId"
        Log.d("url::......", url)

        val request = StringRequest(
            url,
            { response ->
                val jsonObject = JSONObject(response)
                Log.d("jsonObject::..........", jsonObject.toString())

                var reviewObj = jsonObject.getJSONObject("review");
                Log.d("reviewObj::..........", reviewObj.toString())
                var ratingBar = findViewById<RatingBar>(R.id.rating_bar)
                var reviewEditText = findViewById<EditText>(R.id.review_body)
                ratingBar?.rating = (reviewObj.getDouble("rating")?.toFloat() as Float ?: 1.0) as Float
//                reviewEditText.setText(jsonObject.getString("body") ?: "")
                val reviewBody = reviewObj.optString("body", "")
                if (reviewBody.isNotEmpty()) {
                    reviewEditText.setText(reviewBody)
                }

            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(request)

    }
}
