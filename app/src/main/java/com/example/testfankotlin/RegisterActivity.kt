package com.example.testfankotlin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.testfankotlin.databinding.ActivityRegisterBinding
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlin.collections.HashMap


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setupView()
        onClickListener()
    }

    private fun setupView() {
        binding.layoutRegister.apply {
            txtRegister.visibility = GONE
            btnForm.text = "Registrasi"
        }

        setupTextInputWatcher()
    }

    private fun setupTextInputWatcher() {
        binding.layoutRegister.apply {
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

            edtRePassword.doAfterTextChanged {
                if (tilRePassword.error?.isNotBlank() == true) {
                    tilRePassword.error = null
                }

                checkPasswordValidation(it.toString())
            }

        }
    }

    private fun checkPasswordValidation(password: String) {
        binding.layoutRegister.apply {
            if (password.isBlank()) {
                tilPassword.error = null
            } else {
                val lengthValid = isMinimalPasswordLength(password)
                val uppercaseValid = isPasswordContainUppercase(password)
                val lowercaseValid = isPasswordContainLowercase(password)
                val numberValid = isPasswordContainNumber(password)

                if (lengthValid && uppercaseValid && lowercaseValid && numberValid) {
                    tilPassword.error = null
                } else {
                    tilPassword.error =
                        "Kata sandi harus mengandung minimal 8 karakter, huruf besar, huruf kecil dan angka"
                }

            }
        }
    }

    private fun checkFormRegister(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val userid = firebaseUser?.uid
                    reference = FirebaseDatabase.getInstance().reference.child("Users")
                        .child(userid!!)
                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["id"] = userid
                    hashMap["name"] = name
                    reference?.setValue(hashMap)?.addOnCompleteListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun onClickListener() {
        binding.layoutRegister.apply {
            btnForm.setOnClickListener {
                val name = edtName.text.toString().trim()
                val email = edtEmail.text.toString().trim()
                val password = edtPassword.text.toString()
                val repassword = edtRePassword.text.toString()

                if (name.isEmpty()) {
                    tilName.error = "Nama Tidak Boleh Kosong"
                    tilName.requestFocus()
                    return@setOnClickListener
                }
                if (email.isEmpty()) {
                    tilEmail.error = "Email Tidak Boleh Kosong"
                    tilEmail.requestFocus()
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    tilPassword.error = "Password Tidak Boleh Kosong"
                    tilPassword.requestFocus()
                    return@setOnClickListener
                }
                if (repassword.isEmpty()) {
                    tilRePassword.error = "Konfirmasi Password Tidak Boleh Kosong"
                    tilRePassword.requestFocus()
                    return@setOnClickListener
                } else {
                    Log.e("EMAILABUS", "onClickListener: $email")
                    checkFormRegister(name, email, password)
                }
            }
        }
    }

}