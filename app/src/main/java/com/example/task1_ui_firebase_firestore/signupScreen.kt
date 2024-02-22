package com.example.task1_ui_firebase_firestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class signupScreen : AppCompatActivity() {

    private lateinit var signup: Button
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var showHide: ImageView
    private lateinit var firstnameMessage: TextView
    private lateinit var lastnameMessage: TextView
    private lateinit var emailMessage: TextView
    private lateinit var passMessage: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_screen)

        initializingViews()
        clickListerners()
    }

    private fun clickListerners() {
        showHide.setOnClickListener(View.OnClickListener {
            if (password.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                showHide.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.eye_close));
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                showHide.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.eye));
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        })


        signup.setOnClickListener(View.OnClickListener {

            fieldsAuthentication()
            SignupUser()


        })

    }
    private fun SignupUser() {
        if (firstName.text.isNotEmpty() && lastName.text.isNotEmpty() && email.text.isNotEmpty() && password.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            val firstname1 = firstName.text.toString()
            val lastname1 = lastName.text.toString()
            val email1 = email.text.toString()
            val password1 = password.text.toString()

            firebaseAuth.createUserWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val userId = user?.uid ?: ""
                        val userData = hashMapOf(
                            FireBaseKeys.FIRST_NAME_KEY to firstname1,
                            FireBaseKeys.LAST_NAME_KEY to lastname1,
                            FireBaseKeys.EMAIL_KEY to email1
                        )

                        firestore.collection(FireBaseKeys.COLLECTION_NAME).document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                // User data stored in Firestore
                                progressBar.visibility = View.INVISIBLE

                                val intent = Intent(this, mainScreen::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    getString(R.string.failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun fieldsAuthentication() {
        if (firstName.text.isEmpty()) {
            firstnameMessage.visibility = View.VISIBLE
        } else {
            firstnameMessage.visibility = View.GONE
        }
        if (lastName.text.isEmpty()) {
            lastnameMessage.visibility = View.VISIBLE
        } else {
            lastnameMessage.visibility = View.GONE
        }

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

    }


    private fun initializingViews() {
        signup = findViewById(R.id.signupButton)
        firstName = findViewById(R.id.firstnameEditTextSignup)
        lastName = findViewById(R.id.lastnameEditTextSignup)
        email = findViewById(R.id.emailEditTextSignup)
        password = findViewById(R.id.passwordEditTextSignup)
        showHide = findViewById(R.id.showHideSignup)
        firstnameMessage = findViewById(R.id.firstnameErrorMessageTextViewSignup)
        lastnameMessage = findViewById(R.id.lastnameErrorMessageTextViewSignup)
        emailMessage = findViewById(R.id.emailErrorMessageTextViewSignup)
        passMessage = findViewById(R.id.passwordErrorMessageTextViewSignup)
        progressBar = findViewById(R.id.progressBarLogin)

        firstnameMessage.visibility = View.GONE
        lastnameMessage.visibility = View.GONE
        emailMessage.visibility = View.GONE
        passMessage.visibility = View.GONE

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }
}