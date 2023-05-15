package com.example.vollysql

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.vollysql.databinding.ActivityMessageBinding
import org.json.JSONException
import org.json.JSONObject

class MessageActivity : DrawerBaseActivity() {

    private lateinit var activityBinding: ActivityMessageBinding
    var user = UserViewModel(1, "", "", "", "", "", "", "", "")
    var review = ReviewViewModel(-1, user, "", -1, "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

//        setContentView(R.layout.activity_message)
        val reviewId = intent.getIntExtra("review_id", -1)
        fetchReview(reviewId)
        val queue = Volley.newRequestQueue(this)
//        fetchReview(reviewId)

        val submitButton = findViewById<Button>(R.id.submitButton);
        submitButton.setOnClickListener{

            val messageInput = findViewById<EditText>(R.id.messageInput).text
            if(messageInput.isNotEmpty()){
                var senderId = Utils.getUserId(this);
                var receiverId = review.user.id
                var reviewId = reviewId;
                sendMessage(reviewId, senderId, receiverId, messageInput.toString()){response ->
                    if (response!!.toString()!=null){
                        Toast.makeText(this, "Message has been sent!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ReviewListActivity::class.java))
                    }
                }
            }
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
                var userObj = jsonObject.getJSONObject("user");
                Log.d("reviewObj::..........", reviewObj.toString())
                var ratingBar = findViewById<RatingBar>(R.id.rating_bar)
                var reviewEditText = findViewById<TextView>(R.id.review_body)
                var imageView = findViewById<ImageView>(R.id.user_image);

                Glide.with(this)
                    .load(MainActivity.mainUrl + userObj.getString("profile_picture"))
                    .into(imageView)

                var userNameTextView = findViewById<TextView>(R.id.user_name);
                userNameTextView.text = userObj.getString("firstName").toString() + " "+ userObj.getString("lastName").toString()
                ratingBar?.rating = (reviewObj.getDouble("rating")?.toFloat() as Float ?: 1.0) as Float
//
                review.body = reviewObj.getString("body")
                review.rating = reviewObj.getInt("rating")
                review.id = reviewObj.getInt("id");
                review.user.id =reviewObj.getInt("user_id");
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

    private fun sendMessage(reviewId:Int, senderId: Int, receiverId: Int,messageBody:String, onResponseReceived: (Boolean?) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.reviewUrl
        val queue = Volley.newRequestQueue(this)
        Log.d("user:::...", "$rootUrl&send_message=1")
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&send_message=1",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    Log.d("messageobj::: ", obj.toString())
                    val errorStatus = obj.getBoolean("error")
                    onResponseReceived(errorStatus)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    onResponseReceived(null)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                onResponseReceived(null)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["review_id"] = reviewId.toString()
                params["sender_id"] = senderId.toString()
                params["receiver_id"] = receiverId.toString()
                params["message"] = messageBody

                return params
            }
        }
        queue.add(stringRequest)
    }
}