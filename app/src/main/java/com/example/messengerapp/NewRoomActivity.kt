package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText

class NewRoomActivity : AppCompatActivity() {

    var NEW_ROOM_REQ = 2410

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_room)
    }

    fun createNewRoom(view: View) {

        var name = findViewById<EditText>(R.id.createRoomEditText).text.toString()
        var intent = Intent()
        intent.putExtra("name", name)
        setResult(NEW_ROOM_REQ, intent)

        finish()
    }
}