package com.example.vollysql

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.vollysql.databinding.ActivityReviewListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class ReviewListActivity : DrawerBaseActivity() {

    private lateinit var activityBinding:ActivityReviewListBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoggedIn = Utils.isUserLoggedIn(this)
        if(!isLoggedIn) startActivity(Intent(this, LoginActivity::class.java))
        activityBinding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)



        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        var data: ArrayList<ReviewViewModel>

       getAllReviews { reviews->
           if (reviews != null) {
               Log.d("reviews............", reviews.toString())

               // This will pass the ArrayList to our Adapter
               val adapter = CustomAdapter(reviews)

               // Setting the Adapter with the recyclerview
               recyclerview.adapter = adapter
           } else {
               // Show error message to user
               Log.d("reviews............", "null")
           }
        }


        // This loop will create 20 Views containing
        // the image with the count of view
//        data = getReviews();

//        Log.d("Reviews......................:", data.toString());

        // This will pass the ArrayList to our Adapter
//        val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
//        recyclerview.adapter = adapter
    }


    private fun getAllReviews(onReviewReceived: (ReviewViewModel: MutableList<ReviewViewModel>
    ?) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.reviewUrl;
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.GET,
            "$rootUrl&view_all_reviews=1",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    Log.d("object.......:", obj.toString());

                    val reviewsArray = obj.getJSONArray("reviews")
                    val usersArray = obj.getJSONArray("users")

                    Log.d("reviewsArray.......:", reviewsArray.toString());
                    Log.d("usersArray.......:", usersArray.toString());

                    val reviewsList = mutableListOf<ReviewViewModel>()



                    for (i in 0 until reviewsArray.length()) {
                        val review = reviewsArray.getJSONObject(i)
                        val userObj = usersArray.getJSONObject(i);
                        Log.d("review item:.....", review.toString());
                        Log.d("user item:.....", userObj.toString());
                        val user = UserViewModel(
                            userObj.getInt("id"),
                            userObj.getString("firstName"),
                            userObj.getString("lastName"),
                            userObj.getString("email"),
                            userObj.getString("profile_picture"),
                            userObj.getString("password"),

                            userObj.getString("city"),
                            userObj.getString("role"),
                            userObj.getString("country"
                            )
                        )
                        val reviewViewModel = ReviewViewModel(
                            user,
                            review.getString("body"),
                            review.getInt("rating"),
                            review.getString("created"),
                        )
                        reviewsList.add(reviewViewModel)
                    }


                    onReviewReceived(reviewsList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    onReviewReceived(null)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                onReviewReceived(null)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                return params
            }
        }
        queue.add(stringRequest)
    }

}