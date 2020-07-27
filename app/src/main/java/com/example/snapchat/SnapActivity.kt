package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var snapsListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)

        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        snapsListView?.adapter = adapter


        mAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference().child("users").child(it).child("snaps").addChildEventListener(object:
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                    emails.add(snapshot?.child("from")?.value as String)
                    snaps.add(snapshot!!)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
                override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}
                override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    var index = 0
                    for (snap: DataSnapshot in snaps) {
                        if (snap.key == snapshot?.key) {
                            snaps.removeAt(index)
                            emails.removeAt(index)
                        }
                        index++
                    }
                    adapter.notifyDataSetChanged()
                }
            })
        }

        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapshot = snaps.get(i)

            var intent = Intent(this, ViewSnapActivity::class.java)

            intent.putExtra("imageName",snapshot.child("imageName").value as String)
            intent.putExtra("imageURL",snapshot.child("imageURL").value as String)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("snapKey",snapshot.key)

            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater =menuInflater
        inflater.inflate(R.menu.snapmenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId==R.id.createSnap){
            val intent =Intent(this,CreateSnapActivity::class.java)
            startActivity(intent)

        }else if (item?.itemId==R.id.logout){
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
    }

}