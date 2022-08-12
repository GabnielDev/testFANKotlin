package com.example.testfankotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testfankotlin.databinding.ActivityMainBinding
import com.example.testfankotlin.model.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var profileid: String
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val prefs: SharedPreferences =
            applicationContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        profileid = prefs.getString("profileid", null).toString()

        getData()
        onClickListener()

    }

    private fun getData() {
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: UserResponse? = snapshot.getValue(UserResponse::class.java)
                    binding.txtUsername.text = "Selamat Datang : ${user?.name}"
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendEmailVerification() {
        val user = firebaseAuth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Verifikasi Telah Dikirim", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "${it.exception}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickListener() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        binding.bnVerify.setOnClickListener {
            sendEmailVerification()
        }

    }


}