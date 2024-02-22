package com.example.task1_ui_firebase_firestore

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class loginScreen : AppCompatActivity() {

    private lateinit var login: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var showHide: ImageView
    private lateinit var emailMessage: TextView
    private lateinit var passMessage: TextView
    private lateinit var signupTextButton: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        initializingViews()
        clickListeners()
    }

    private fun clickListeners() {
        showHide.setOnClickListener(View.OnClickListener {
            if (password.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                showHide.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.eye_close));
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                showHide.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.eye));
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        })

        login.setOnClickListener(View.OnClickListener {
            loginUser()
        })
        signupTextButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, signupScreen::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun loginUser() {
        if (email.text.isEmpty()) {
            emailMessage.visibility = View.VISIBLE
        } else {
            emailMessage.visibility = View.GONE
        }

        if (password.text.isEmpty()) {
            passMessage.visibility = View.VISIBLE
        } else {
            passMessage.visibility = View.GONE

        }
        if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            val email1 = email.text.toString()
            val password1 = password.text.toString()

            firebaseAuth.signInWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        Toast.makeText(

                            this,
                            getString(R.string.login_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, mainScreen::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                            .show()
                    }
                    // Handle login failure
                }
        }
    }

    private fun initializingViews() {
        login = findViewById(R.id.loginButton)
        email = findViewById(R.id.emailEditTextLogin)
        password = findViewById(R.id.passwordEditTextLogin)
        showHide = findViewById(R.id.showHideLogin)
        emailMessage = findViewById(R.id.emailErrorMessageTextViewLogin)
        passMessage = findViewById(R.id.passErrorMessageTextViewLogin)
        signupTextButton = findViewById(R.id.signupTextViewLogin)
        progressBar = findViewById(R.id.progressBarLogin)


        emailMessage.visibility = View.GONE
        passMessage.visibility = View.GONE


        firebaseAuth = FirebaseAuth.getInstance()

    }
}