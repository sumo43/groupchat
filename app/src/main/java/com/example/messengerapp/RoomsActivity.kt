package com.example.messengerapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import org.w3c.dom.Comment

class RoomsActivity : AppCompatActivity() {
    lateinit var roomsList: ArrayList<Room>
    lateinit var adapter: RoomsListAdapter
    var NEW_ROOM_REC = 2410
    lateinit var groups: DatabaseReference

    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        //make this use objects later
        roomsList = ArrayList<Room>()

        database = FirebaseDatabase.getInstance().reference
        groups = database.child("groups/textonly")

        //initialize the adapter for the list of rooms
        adapter = RoomsListAdapter(roomsList, this)
        var container = findViewById<RecyclerView>(R.id.recycler)
        container.layoutManager = LinearLayoutManager(this)
        container.adapter = adapter


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!! + dataSnapshot.getValue(Room::class.java).toString())

                // A new child of the list is found / added, add it to the list of chats
                // ...

                var obj = dataSnapshot.getValue(Room::class.java)
                if(obj == null) {
                    Log.e("nulltest", "this is null")
                }
                refreshList(obj!!)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                //refresh the list or something idk
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())

            }
        }
        groups.addChildEventListener(childEventListener)
    }


    class Room (roomName: String = "null", metaData: String = "2", sess: Any? = null, userList: Any? = null) {
        var name = roomName
        var meta = metaData
        var session = sess
        var users = userList
    }

    fun onNewRoomCreate(view: View) {
        //redirect to
        var intent = Intent(this, NewRoomActivity::class.java)

        startActivityForResult(intent, NEW_ROOM_REC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == NEW_ROOM_REC && data != null) {
            //create a new room with this name
            //refreshList with the room

            var roomName = data?.getStringExtra("name")!!
            var newRoom = Room(roomName)
            addRoomToDatabase(newRoom)
        }
    }

    fun refreshList(room: Room) {

        roomsList.add(room)
        adapter.notifyDataSetChanged()
    }

    fun addRoomToDatabase(room: Room) {
        var eventId = groups.push().key
        groups.child(eventId!!).setValue(room)
    }
}

fun roomButtonHandler(view: TextView, context: Context) {

    //launch intent to rooms
    var intent = Intent(context, ChatActivity::class.java)
    var textView = view.findViewById<TextView>(R.id.roomsContainerText)
    var text = textView.text
    intent.putExtra("chat-name", text)
    var act = context as AppCompatActivity

    act.startActivityForResult(intent, 1111)

}


class RoomsListAdapter(private val data: ArrayList<RoomsActivity.Room>, private val context: Context) : RecyclerView.Adapter<RoomsListAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val textView: TextView
        init {
            textView = view.findViewById(R.id.roomsContainerText)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.container_row_rooms_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = data[position].name
        viewHolder.textView.setOnClickListener{view ->

            roomButtonHandler(view as TextView, context)

        }
    }

    override fun getItemCount() = data.size
}

