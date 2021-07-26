package com.example.messengerapp

import android.content.ContentValues
import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.lang.System.currentTimeMillis
import java.util.*
import kotlin.collections.ArrayList


//make the chat a list of objects later
//TODO
//make it work with a messageObject
//pass metadata to the object
//get the username to work
//add messagetime so you can do sortBy(messageTime)

class ChatActivity : AppCompatActivity() {


    lateinit var chatsList: ArrayList<messageObject>
    lateinit var adapter: ChatsListAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var session: DatabaseReference
    private lateinit var chatObject: DatabaseReference
    private lateinit var sessionQuery: Query


    val TAG = "chat"


    private lateinit var getQuery: Query

    lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var roomName = intent.getStringExtra("chat-name")
        var title = findViewById<TextView>(R.id.chatName)
        title.text = "#" + roomName

        //get the username. This will be done with intents later
        user = FirebaseAuth.getInstance().currentUser!!

        chatsList = ArrayList<messageObject>()


        database = FirebaseDatabase.getInstance()

        val nameQuery = database.reference.child("/groups/textonly/")
            .orderByChild("name")
            .equalTo(roomName)

        nameQuery.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val name = snapshot.child("name").getValue(String::class.java)


                // if this is null, makes a child called session
                session = snapshot.ref.child("session")

                sessionQuery = session.orderByChild("messageTime")

                makeAdapter()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    fun makeAdapter() {

        adapter = ChatsListAdapter(chatsList, this)
        var container = findViewById<RecyclerView>(R.id.chatRecycler)
        container.layoutManager = LinearLayoutManager(this)
        container.adapter = adapter


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new child of the list is found / added, add it to the list of chats
                // ...

                var obj = dataSnapshot.getValue(messageObject::class.java)
                refreshList(obj!!)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildChanged: ${dataSnapshot.key}")

                //refresh the list or something idk
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(ContentValues.TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.value

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val commentKey = dataSnapshot.value

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "postComments:onCancelled", databaseError.toException())
            }
        }
        sessionQuery.addChildEventListener(childEventListener)

    }

    fun sendText(view: View) {
        var editText = findViewById<EditText>(R.id.chatEnterText)
        var userName = user.displayName
        var uid = user.uid
        var textToSend = editText.text.toString()


        var textObject = messageObject(userName!!, uid, textToSend, System.currentTimeMillis())


        var eventId = session.push().key
        session.child(eventId!!).setValue(textObject)
    }

    fun refreshList(chatText: messageObject) {

        chatsList.add(chatText)
        adapter.notifyDataSetChanged()
    }

}

class messageObject(name: String = "null", id: String = "null", text: String = "null", time: Long = 0) {
    var userName = name
    var uid = id
    var messageText = text
    var messageTime = time
}

class ChatsListAdapter(private val data: ArrayList<messageObject>, private val context: Context) : RecyclerView.Adapter<ChatsListAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView
        init {

            //make this also display the time and username LATER
            textView = view.findViewById(R.id.chatsContainerText)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.container_row_chats_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        var nameText = data[position].userName
        var bodyText = data[position].messageText
        var niceTime = java.text.SimpleDateFormat("HH:mm")
        var messageDate = Date(data[position].messageTime)
        var textDate = niceTime.format(messageDate)

        viewHolder.textView.text = "(" + textDate + ") " + nameText + ": " + bodyText

        viewHolder.textView.setOnClickListener{view ->

            roomButtonHandler(view as TextView, context)

        }
    }

    override fun getItemCount() = data.size
}