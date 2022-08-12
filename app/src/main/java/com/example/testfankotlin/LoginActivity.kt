package com.example.testfankotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.testfankotlin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupView()
        onClickListener()

    }

    private fun setupView() {
        binding.layoutLogin.apply {
            tilName.visibility = View.GONE
            tilRePassword.visibility = View.GONE
            btnForm.text = "Login"
        }

        setupTextInputWatcher()
    }

    private fun setupTextInputWatcher() {
        binding.layoutLogin.apply {
            edtEmail.doAfterTextChanged {
                val isEmailValid = it.toString().isEmailValid()
                if (isEmailValid) {
                    tilEmail.error = null
                } else {
                    tilEmail.error = "Email Tidak Valid"
                }
            }
            edtPassword.doAfterTextChanged {
                if (tilPassword.error?.isNotBlank() == true) {
                    tilPassword.error = null
                }

                checkPasswordValidation(it.toString())
            }

        }
    }

    private fun checkPasswordValidation(password: String) {
        if (password.isBlank()) {
            binding.layoutLogin.tilPassword.error = null
        } else {
            val lengthValid = isMinimalPasswordLength(password)
            val uppercaseValid = isPasswordContainUppercase(password)
            val lowercaseValid = isPasswordContainLowercase(password)
            val numberValid = isPasswordContainNumber(password)

            if (lengthValid && uppercaseValid && lowercaseValid && numberValid) {
                binding.layoutLogin.tilPassword.error = null
            } else {
                binding.layoutLogin.tilPassword.error =
                    "Kata sandi harus mengandung minimal 8 karakter, huruf besar, huruf kecil dan angka"
            }

        }

    }

    private fun checkLoginForm(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    reference = FirebaseDatabase.getInstance().reference.child(auth.currentUser!!.uid)
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            savetoSharedPref(auth.currentUser!!.uid)
                            val intent =
                                Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Authentication failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun onClickListener() {
        binding.layoutLogin.apply {
            txtRegister.setOnClickListener {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }

            btnForm.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                if (email.isEmpty()) {
                    tilEmail.error = "Email Tidak Boleh Kosong"
                }
                if (password.isEmpty()) {
                    tilPassword.error = "Password Tidak Boleh Kosong"
                }
                checkLoginForm(email, password)
            }
        }
    }

    private fun savetoSharedPref(id: String) {
        val prefs: SharedPreferences =
            applicationContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                prefs.edit().putString("profileid", id).apply()
        Log.e("PREFSAWAL", "onCreate: $id")
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser?.email
        Log.e("REFERENCEUSER", "onStart: $user", )

        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
                .putExtra("NAME", user)
            startActivity(intent)
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
        }
    }

}
