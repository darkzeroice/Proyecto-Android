package com.chacanasoft.jorge.fluctuacion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btn_reset_password.setOnClickListener() {
            fnResetPasswordPressed()
        }
        btn_back.setOnClickListener() {
            fnBackButtonPressed()
        }

        mAuth = FirebaseAuth.getInstance()
    }

    private fun fnResetPasswordPressed() {
        val userEmail = email.getText().toString().trim { it <= ' ' }

        if (userEmail.isEmpty()) {
            Toast.makeText(this, R.string.email_reset_password, Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        //Realizamos la comunicación con Firebase
        mAuth?.sendPasswordResetEmail(userEmail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this,
                        R.string.send_password_reset_email,
                        Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this,
                        R.string.auth_failed,
                        Toast.LENGTH_SHORT).show()
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun fnBackButtonPressed() {
        finish()
    }

}
