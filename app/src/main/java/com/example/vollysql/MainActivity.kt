package com.example.vollysql

import android.app.DownloadManager.Request
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

class MainActivity : AppCompatActivity() {


    companion object {
//        Global Varialbe
        var url = "http://192.168.1.151/reviews/api/";
        var mainUrl = "http://192.168.1.151/reviews/";
        var userUrl = "users.php?op=1";
        var reviewUrl = "reviews.php?op=1";



    }

    private var edtRankName: EditText? = null
    private var btnAddRank: Button? = null


    var addRanks = url + "&addRanks=1"
    var viewRanks = url + "&viewRanks=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtRankName = findViewById<EditText>(R.id.edtRankName);
        btnAddRank = findViewById<Button>(R.id.btnAddRank);

        btnAddRank!!.setOnClickListener {
//            addRank()
            // Create an Intent to start SecondActivity
            val intent = Intent(this, NewReviewActivity::class.java)

            // Add data to the Intent (optional)
//            intent.putExtra("key", "value")

            // Start SecondActivity
            startActivity(intent)
        }

    }


    private fun addRank(){


        var strRankName = edtRankName!!.text.toString()
        val queue = Volley.newRequestQueue(this);
        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.GET,
            addRanks+"&rankName="+strRankName,
            Response.Listener<String>{ response ->
                try {
                    val obj = JSONObject(response)
                    Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show()
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener{
                override fun onErrorResponse(error: VolleyError?) {
                    if (error != null) {
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                    };
                }
            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                Log.d("strRankName: ", strRankName);
//                params.put("rankName", strRankName)
                return params
            }
        }

        queue.add(stringRequest)
    }

    //adding a new record to database
    private fun getRanks() {
        //getting the record values
        val rankName = edtRankName?.text.toString()
        val queue = Volley.newRequestQueue(this);
        //val txtData = findViewById(R.id.txtData) as TextView

        //creating volley string request
        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, viewRanks,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    val viewAllranksArray = obj.getJSONArray("viewAllranks")
                    //Toast.makeText(applicationContext, dataRank.toString(), Toast.LENGTH_LONG).show()
                    for (i in 0..viewAllranksArray.length() - 1) {

                        val ObjectsInranksArray = viewAllranksArray.getJSONObject(i)
//                        val strRankName = ObjectsInranksArray.getString("Rankname");
//                        val strRankID = ObjectsInranksArray.getString("RankID");
//                        /// Create a an instance of a rank class and get the information that is being returned

                    }

                    // Print response into a TextView
                    //txtData.setText(viewAllranksArray.toString());





                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val paramamters = HashMap<String, String>()
                paramamters.put("rankName", rankName)
                return params
            }
        }
        queue.add(stringRequest)

    }
}
