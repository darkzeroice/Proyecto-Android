package com.chacanasoft.jorge.fluctuacion

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import java.util.HashMap


class RegisterActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    // Access a Cloud Firestore instance from your Activity
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener() {
            fnRegisterUser()
        }
        btn_reset_password.setOnClickListener(){
            fnForgotPassword()
        }
        btn_sign_in.setOnClickListener(){
            fnGoToSignIn()
        }

        mAuth = FirebaseAuth.getInstance()



    }


    private fun fnRegisterUser() {
        val nombre = nombre.getText().toString().trim({ it <= ' ' })
        val email = email.getText().toString().trim({ it <= ' ' })
        val password = password.getText().toString().trim({ it <= ' ' })

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, R.string.hint_name, Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.hint_email, Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.hint_password, Toast.LENGTH_SHORT).show()
        }

        if (password.length < 6) {
            Toast.makeText(this, R.string.minimum_password, Toast.LENGTH_SHORT).show()
        }

        progressBar.visibility = View.VISIBLE

        //Creamos el usuario en Firebase
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                    //Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful)
                    progressBar.visibility = View.GONE

                    if (!task.isSuccessful) {
                        val error = task.exception!!.toString()
                        Toast.makeText(this, error,
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.user_created,
                                Toast.LENGTH_SHORT).show()
                        fnCrearUsuario(nombre, mAuth?.uid.toString(), email)

                        //Enviar correo de validación
                        fnSendValidationEmail()

                        //Logueamos al usuario
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
    }

    private fun fnCrearUsuario(sNombre: String, sMiUsuario: String, sEmail: String) {
        // Create a new user with a first and last name
        val users = HashMap<String, Any>()
        users.put("nombre", sNombre)
        users.put("email", sEmail)
        users.put("uid", sMiUsuario)

        // Add a new document with a generated ID
        db.collection("users").document(mAuth?.uid.toString())
                .set(users)
                .addOnSuccessListener { documentReference ->
                    //Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.id)
                }
                .addOnFailureListener { e ->
                    //Log.w("TAG", "Error adding document", e)
                }
    }


    private fun fnForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        this.startActivity(intent)
    }


    private fun fnGoToSignIn() {
        val intent = Intent(this, LoginFirebaseActivity::class.java)
        this.startActivity(intent)
    }


    //Enviar correo de validación
    private fun fnSendValidationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this,
                        R.string.email_check,
                        Toast.LENGTH_SHORT).show()
            }
        }
    }
}
