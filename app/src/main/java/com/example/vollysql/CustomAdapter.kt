package com.example.vollysql

import android.app.AlertDialog
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import org.json.JSONException
import org.json.JSONObject

class CustomAdapter(private val mList: List<ReviewViewModel>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.editButton.setOnClickListener {
            var intent = Intent(holder.itemView.context, EditReviewActivity::class.java)
            intent.putExtra("review_id", ItemsViewModel.id)
            holder.itemView.context.startActivity(intent)
        }
//        increase like


        val userId = Utils.getUserId(holder.itemView.context);
        val reviewId = ItemsViewModel.id;


        holder.likeButton.setOnClickListener {

            updateReviewStatus(userId, reviewId, 1, holder.itemView.context){
                response ->
                val currentTotalLikes = holder.totalLikeText.text.toString().toInt()
                val newTotalLikes = currentTotalLikes + 1
                holder.totalLikeText.text = newTotalLikes.toString()

                if(response == true){
                        Log.d("response", response.toString())
                    }
            }

        }
        holder.dislikeButton.setOnClickListener {

            updateReviewStatus(userId, reviewId, 0, holder.itemView.context){
                    response ->
                val currentTotalLikes = holder.totalDisLikeText.text.toString().toInt()
                val newTotalLikes = currentTotalLikes + 1
                holder.totalDisLikeText.text = newTotalLikes.toString()

                if(response == true){
                    Log.d("response::::", response.toString())
                }
            }

        }

        holder.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to delete this item?")

            // Set positive button action
            builder.setPositiveButton("Delete") { dialog, which ->
                deleteReview(reviewId, holder.itemView.context){
                    response->
                    var intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
                    holder.itemView.context.startActivity(intent)
                }
                dialog.dismiss() // Dismiss the dialog after performing the delete operation
            }

            // Set negative button action
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss() // Dismiss the dialog if the user cancels the delete operation
            }

            val dialog = builder.create()
            dialog.show()
        }

        // Load user image using Glide
        Glide.with(holder.itemView.context)
            .load(MainActivity.mainUrl + ItemsViewModel.user.profile_picture)
            .into(holder.userImageView)

        // sets the text to the textview from our itemHolder class
        holder.bodyTextView.text = ItemsViewModel.body
        holder.ratingBar.rating = ItemsViewModel.rating.toInt().toFloat()
        holder.userNameTextView.text =
            ItemsViewModel.user.firstName + " " + ItemsViewModel.user.lastName;
        holder.createdTextView.text = getTimeAgo(ItemsViewModel.created);

        getReviewLike(reviewId, holder.itemView.context){response->
            Log.d("likereview:::::", response.toString())
            holder.totalLikeText.text = response!!.totalLike.toString();
            holder.totalDisLikeText.text = response!!.totalDislike.toString()
        }

        // Access SharedPreferences using the context object
        val prefs = holder.itemView.context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val user_id = prefs.getInt("user_id", -1)
        val role = prefs.getString("role", null);

        // Show or hide the like button based on the value in SharedPreferences
        if (isLoggedIn && (user_id == ItemsViewModel.user.id || role == "admin")) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
            holder.editButton.visibility = View.GONE
        }
        holder.userNameTextView.text =
            ItemsViewModel.user.firstName + " " + ItemsViewModel.user.lastName;

    }

    private  fun updateReviewStatus(userId: Int, reviewId: Int, reviewStatus:Int,context:Context, onResponseReceived: (Boolean?) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.reviewUrl
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&like_review=1",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    val errorResponse = obj.getBoolean("error")

                    onResponseReceived(errorResponse)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    onResponseReceived(null)
                }
            },
            Response.ErrorListener { error ->

                onResponseReceived(null)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId.toString()
                params["review_id"] = reviewId.toString()
                params["review_status"] = reviewStatus.toString()

                return params
            }
        }
        queue.add(stringRequest)
    }

    private  fun deleteReview(reviewId: Int, context:Context, onResponseReceived: (Boolean?) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.reviewUrl
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&delete_review=1&review_id=$reviewId",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    val errorResponse = obj.getBoolean("error")

                    onResponseReceived(errorResponse)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    onResponseReceived(null)
                }
            },
            Response.ErrorListener { error ->

                onResponseReceived(null)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                return params
            }
        }
        queue.add(stringRequest)
    }


    private  fun getReviewLike(reviewId: Int, context:Context, onResponseReceived: (ReviewLikeDislikeModel?) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.reviewUrl
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&get_review_likes=1&review_id=$reviewId",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    Log.d("obj:::::", obj.toString());
                    val likes = obj.getInt("likes")
                    val dislikes = obj.getInt("dislikes")

                    var resp = ReviewLikeDislikeModel(likes,dislikes);
                    onResponseReceived(resp)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    onResponseReceived(null)
                }
            },
            Response.ErrorListener { error ->

                onResponseReceived(null)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                return params
            }
        }
        queue.add(stringRequest)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    fun getTimeAgo(timestamp: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
        val date = dateFormat.parse(timestamp)
        val now = Date()

        val seconds = abs(now.time - date.time) / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "$seconds seconds ago"
        }
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val userImageView: ImageView = itemView.findViewById(R.id.user_image)
        val userNameTextView: TextView = itemView.findViewById(R.id.user_name)
        val totalLikeText: TextView = itemView.findViewById(R.id.totalLikeText)
        val totalDisLikeText: TextView = itemView.findViewById(R.id.totalDisLikeText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        var bodyTextView: TextView = itemView.findViewById(R.id.review_body)
        var createdTextView: TextView = itemView.findViewById(R.id.created_date)
        var editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        var deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        var likeButton: ImageButton = itemView.findViewById(R.id.like_button)
        var dislikeButton: ImageButton = itemView.findViewById(R.id.dislike_button)

    }
}
