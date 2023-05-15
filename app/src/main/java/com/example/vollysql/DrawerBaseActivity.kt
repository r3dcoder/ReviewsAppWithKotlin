package com.example.vollysql

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class DrawerBaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun setContentView(view: View?) {
        val drawerLayout = layoutInflater.inflate(R.layout.activity_drawer_base, null) as DrawerLayout

        val container: FrameLayout = drawerLayout.findViewById(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawerLayout)

        val toolbar: Toolbar = drawerLayout.findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        val navigationView: NavigationView = drawerLayout.findViewById(R.id.naav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.newReviewItem -> startActivity(Intent(this, NewReviewActivity::class.java))
            R.id.reviewsItem -> startActivity(Intent(this, ReviewListActivity::class.java))
            R.id.logOutItem -> {
                val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear() // remove all key-value pairs from the preferences file
                editor.apply()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.profileItem -> startActivity(Intent(this, UpdateProfileActivity::class.java))
        }

        return false
    }

    protected fun allocatedActivityTitle(titleString: String) {
        supportActionBar?.title = titleString
    }


}
