package com.example.vollysql

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class UpdateProfileActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        val userId = Utils.getUserId(this);

        val firstNameEditText = findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditText = findViewById<EditText>(R.id.editTextLastName)
        val cityEditText = findViewById<EditText>(R.id.editTextCity)
        val countryEditText = findViewById<EditText>(R.id.editTextCountry)
        val submitButton = findViewById<Button>(R.id.buttonSubmit)


        getUserById(userId) { user ->
            if (user != null) {
                firstNameEditText.setText(user.firstName)
                lastNameEditText.setText(user.lastName)
                countryEditText.setText(user.country)
                cityEditText.setText(user.city)
            } else {
                // Show error message to user
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
        // Set onClickListener for the submit button
        submitButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val city = cityEditText.text.toString()
            val country = countryEditText.text.toString()



            update(firstName, lastName, city, country) { errorStatus ->
                if (errorStatus != null && !errorStatus) {
                    Toast.makeText(this, "Profile Update Successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ReviewListActivity::class.java)
                    startActivity(intent)
                } else {
                    // Show error message to user
                    Toast.makeText(this, "Failed!! Try again please", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun getUserById(id: Int,  onUserReceived: (UserViewModel?) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.userUrl
        val userId = Utils.getUserId(this);
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&get_user_by_id=1&user_id=$userId",
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

                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun update(firstName: String, lastName: String, city: String, country: String, onResponseReceived: (Boolean?) -> Unit) {
        val rootUrl = MainActivity.url+MainActivity.userUrl
        val queue = Volley.newRequestQueue(this)
        val userId = Utils.getUserId(this);
        val stringRequest = object : StringRequest(
            Method.POST,
            "$rootUrl&update_user=1&user_id=$userId",
            Response.Listener { response ->
                try {
                    val obj = JSONObject(response)
                    Log.d("response:::::.......", obj.toString())
                    Log.d("url:::::.......", "$rootUrl&update_user=1&user_id=$userId")
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
                params["firstName"] = firstName
                params["lastName"] = lastName
                params["city"] = city
                params["country"] = country
                return params
            }
        }
        queue.add(stringRequest)
    }
}