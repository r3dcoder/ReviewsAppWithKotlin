package com.example.vollysql

import android.annotation.SuppressLint
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

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Find UI elements by their IDs

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val signInButton = findViewById<Button>(R.id.signInButton)


        signInButton.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        // Set onClickListener for the login button


        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
                var user = signup(email, password, firstName, lastName) { user ->
                    if (user != null && user?.email?.isNotEmpty() == true) {
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
//                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{
                Toast.makeText(this, "Fill up  all the required fields, please!!", Toast.LENGTH_LONG).show()
            }
        }


    }



    private fun signup(email: String, password: String,firstName:String, lastName:String, onUserReceived: (UserViewModel?) -> Unit) {
        val rootUrl = MainActivity.url + MainActivity.userUrl;
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&add_user=1",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    val userObj = obj.getJSONObject("user")
                    val errorStatus = obj.getBoolean("error")
                    if(!errorStatus){

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
                    }
                    else {
                        Toast.makeText(this, "Try a different email!!", Toast.LENGTH_LONG).show()
                        onUserReceived(null)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    onUserReceived(null)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Try with a different email!!", Toast.LENGTH_LONG).show()
                onUserReceived(null)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                params["firstName"] = firstName
                params["lastName"] = lastName
                return params
            }
        }
        queue.add(stringRequest)
    }


}