package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.messengerapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)

        if(auth.currentUser != null) {
            var intent = Intent(this, RoomsActivity::class.java)
            startActivity(intent)
        }
        else {
            Log.d("null", "auth is null")
        }

        setContentView(R.layout.activity_main)
    }

    fun onLoginButtonClick(view: View) {
        //pass this to firebase later
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}