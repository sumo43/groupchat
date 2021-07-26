package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {


    lateinit var user: FirebaseUser
    lateinit private var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //firebase auth
        var auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
        if(auth.currentUser == null) {

            this.signInLauncher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) { res ->
                LoginResultHandler(res)
            }

            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)

        }
        else {

            //the user is already logged in
            var currentUser = auth.currentUser
            LoginSuccessHandler(currentUser)
        }

    }

    private fun LoginSuccessHandler(currentUser: FirebaseUser?) {
        //login was a success, pass currentUser
        var roomsIntent = Intent(this, RoomsActivity::class.java)
        startActivity(roomsIntent)
    }


    private fun LoginResultHandler(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully made a new account (or logged in?)
            val user = FirebaseAuth.getInstance().currentUser

            LoginSuccessHandler(user)

            Log.d("debuggg", user.toString())
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...

            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
        }
    }
}