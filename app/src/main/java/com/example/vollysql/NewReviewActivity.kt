package com.example.vollysql

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

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
            val rating = ratingBar.rating.toInt()
            val body = reviewBodyEditText.text.toString()
            val userId = Utils.getUserId(this)
            // Submit the review to the server or database
            if(body.isNotEmpty()){
                submitNewReview(rating , body, userId){
                        response->
                    if(response){
                        startActivity(Intent(this, ReviewListActivity::class.java))
                    }
                    else
                    {
                        Toast.makeText(this, "Failed Try again", Toast.LENGTH_LONG).show()
                    }
                }

                finish()
            }
           // Close the activity after submission
        }
    }

    private fun submitNewReview(rating: Int, body: String,userId:Int, onSuccessSubmit: (Boolean) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.reviewUrl
        val queue = Volley.newRequestQueue(this)
        Log.d("....Rating::: ", "$rating body $body userId : $userId");
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&add_reviews=1",
            Response.Listener { response ->
                Log.d("url:::", "$rootUrl&add_reviews=1")
                Log.d("Response:: ", response.toString());
                try {
                    val obj = JSONObject(response)

                    onSuccessSubmit(!obj.getBoolean("error"))

                } catch (e: JSONException) {
                    e.printStackTrace()
                    onSuccessSubmit(false)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                onSuccessSubmit(false)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["body"] = body
                params["rating"] = rating.toString()
                params["user_id"] = userId.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }
}