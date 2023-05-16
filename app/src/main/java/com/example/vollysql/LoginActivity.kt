package com.example.vollysql

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoggedIn = Utils.isUserLoggedIn(this)

        if( isLoggedIn)  startActivity(Intent(this, ReviewListActivity::class.java))

        setContentView(R.layout.activity_login)
        // Find UI elements by their IDs
        val signUpTextView = findViewById<TextView>(R.id.signUpButton)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)

        signUpTextView.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        // Set onClickListener for the login button

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if email and password are correct
            var user = login(email, password) { user ->
                if (user != null) {
                    // Do something with the user object here
                    val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("full_name", user.firstName+" "+user.lastName);
                    editor.putString("profile_picture", user.profile_picture);
                    editor.putString("user_id", user.id.toString());
                    editor.putString("role", user.role);
                    editor.putInt("user_id", user.id);
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()
                    val intent = Intent(this, ReviewListActivity::class.java)
                    startActivity(intent)
                } else {
                    // Show error message to user
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun login(email: String, password: String, onUserReceived: (UserViewModel?) -> Unit) {
        val rootUrl = MainActivity.url
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&get_user=1",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    val userObj = obj.getJSONObject("user")
                    val user = UserViewModel(
                        userObj.getInt("id"),
                        userObj.getString("firstName"),
                        userObj.getString("lastName"),
                        userObj.getString("email"),
                        userObj.getString("profile_picture"),
                        userObj.getString("city"),
                        userObj.getString("password"),
                        userObj.getString("role"),
                        userObj.getString("country"),


                    )
                    onUserReceived(user)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    onUserReceived(null)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                onUserReceived(null)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }
        queue.add(stringRequest)
    }
}