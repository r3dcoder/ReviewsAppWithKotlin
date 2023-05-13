package com.example.vollysql

import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomAdapter(private val mList: List<ReviewViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.editButton.setOnClickListener{
            var intent = Intent(holder.itemView.context, EditReviewActivity::class.java)
            intent.putExtra("review_id", ItemsViewModel.id)
            holder.itemView.context.startActivity(intent)
        }
        // Load user image using Glide
        Glide.with(holder.itemView.context)
            .load( MainActivity.mainUrl+ItemsViewModel.user.profile_picture)
            .into(holder.userImageView)

        // sets the text to the textview from our itemHolder class
        holder.bodyTextView.text = ItemsViewModel.body
        holder.ratingBar.rating = ItemsViewModel.rating.toInt().toFloat()
        holder.userNameTextView.text = ItemsViewModel.user.firstName + " "+ ItemsViewModel.user.lastName;
        holder.createdTextView.text =  getTimeAgo(ItemsViewModel.created) ;

        // Access SharedPreferences using the context object
        val prefs = holder.itemView.context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val user_id = prefs.getInt("user_id", -1)
        val role = prefs.getString("role", null);

        // Show or hide the like button based on the value in SharedPreferences
        if (isLoggedIn && (user_id==ItemsViewModel.user.id || role=="admin")) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
            holder.editButton.visibility = View.GONE
        }
        holder.userNameTextView.text = ItemsViewModel.user.firstName + " "+ ItemsViewModel.user.lastName ;

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
//        private val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
         val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        var bodyTextView: TextView = itemView.findViewById(R.id.review_body)
        var createdTextView: TextView = itemView.findViewById(R.id.created_date)
        var editButton:ImageButton = itemView.findViewById(R.id.edit_button)
        var deleteButton:ImageButton = itemView.findViewById(R.id.delete_button)

    }
}
