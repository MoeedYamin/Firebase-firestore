package com.example.task1_ui_firebase_firestore

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class mainScreen : AppCompatActivity() {
    private lateinit var editButton: Button
    private lateinit var email: EditText
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var emailMessage: TextView
    private lateinit var firstnameMessage: TextView
    private lateinit var lastnameMessage: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var switch: Boolean = true
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        initializingViews()
        clickListeners()
        retrivingDataFromFireStore()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()
        if (id == R.id.logout) {
            //dialog box
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.sure_to_logout))
                .setPositiveButton(getString(R.string.yes)) { dialog, whichButton ->
                    firebaseAuth.signOut()
                    val intent = Intent(this, loginScreen::class.java)
                    startActivity(intent)
                    finish()
                }.setNegativeButton(getString(R.string.no)) { dialog, whichButton ->
                    dialog.cancel()
                }
            dialog.show()
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun clickListeners() {

        editButton.setOnClickListener(View.OnClickListener {
            if (switch == true) {
                editButton.text = getString(R.string.done)
                firstname.isFocusableInTouchMode = true
                lastname.isFocusableInTouchMode = true
                switch = false


                fieldsAuthentication()
            }
            else {

                fieldsAuthentication()
                    //disabling textfields again
                    editButton.text = getString(R.string.edit_details)
                    firstname.isFocusableInTouchMode = false
                    lastname.isFocusableInTouchMode = false
                    firstname.isLongClickable = false
                    lastname.isLongClickable = false
                    switch = true
                updatingFireStoreData()
                    this.recreate()
            }
        })
    }

    private fun updatingFireStoreData() {
        progressBar.visibility = View.VISIBLE
        val updatedFirstName = firstname.text.toString()
        val updatedLastName = lastname.text.toString()
        val updatedEmail = email.text.toString()

        val userData = hashMapOf(
            FireBaseKeys.FIRST_NAME_KEY to updatedFirstName,
            FireBaseKeys.LAST_NAME_KEY to updatedLastName,
            FireBaseKeys.EMAIL_KEY to updatedEmail
        )

        val userId = firebaseAuth.currentUser?.uid ?: ""
        firestore.collection(FireBaseKeys.COLLECTION_NAME).document(userId)
            .update(userData as Map<String, Any>)
            .addOnSuccessListener {
                // Data updated successfully
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, getString(R.string.updating), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }


    }

    private fun fieldsAuthentication() {
        if (firstname.text.isEmpty() || email.text.isEmpty() || lastname.text.isEmpty()) {
            if (firstname.text.isEmpty()) {
                firstnameMessage.visibility = View.VISIBLE

            } else {
                firstnameMessage.visibility = View.GONE
            }

            if (email.text.isEmpty()) {
                emailMessage.visibility = View.VISIBLE
            } else {
                emailMessage.visibility = View.GONE
            }

            if (lastname.text.isEmpty()) {
                lastnameMessage.visibility = View.VISIBLE
            } else {
                lastnameMessage.visibility = View.GONE

            }

        }
    }

    private fun initializingViews() {

        firstname = findViewById(R.id.firstnameEditTextMain)
        email = findViewById(R.id.emailEditTextMain)
        lastname = findViewById(R.id.lastnameEditTextMain)
        emailMessage = findViewById(R.id.emailErrorMessageTextViewMain)
        firstnameMessage = findViewById(R.id.firstnameErrorMessageTextViewMain)
        lastnameMessage = findViewById(R.id.lastnameErrorMessageTextViewMain)
        editButton = findViewById(R.id.editButtonMain)
        emailMessage.visibility = View.GONE
        firstnameMessage.visibility = View.GONE
        lastnameMessage.visibility = View.GONE
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBarLogin)


        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        // this will prevent the keyboard to open
        firstname.isFocusableInTouchMode = false
        lastname.isFocusableInTouchMode = false
        email.isFocusableInTouchMode = false

        // this will prevent copy and paste
        firstname.isLongClickable = false
        email.isLongClickable = false
        lastname.isLongClickable = false

        setSupportActionBar(toolbar)
        (this as AppCompatActivity).supportActionBar?.title = getString(R.string.profile)




    }

    private fun retrivingDataFromFireStore() {
        val userId = firebaseAuth.currentUser?.uid ?: ""
        firestore.collection(FireBaseKeys.COLLECTION_NAME).document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val firstNameSet = document.getString(FireBaseKeys.FIRST_NAME_KEY) ?: ""
                    val lastNameSet = document.getString(FireBaseKeys.LAST_NAME_KEY) ?: ""
                    val emailSet = document.getString(FireBaseKeys.EMAIL_KEY) ?: ""

                    // Enable and set text for EditText fields
                    firstname.isEnabled = true
                    lastname.isEnabled = true
                    email.isEnabled = true

                    firstname.setText(firstNameSet)
                    lastname.setText(lastNameSet)
                    email.setText(emailSet)
                } else {
                    Toast.makeText(this, getString(R.string.profile_not_found), Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show()
            }    }

}