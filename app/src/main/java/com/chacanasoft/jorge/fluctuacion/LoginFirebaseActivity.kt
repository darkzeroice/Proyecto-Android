package com.chacanasoft.jorge.fluctuacion

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_firebase.*


class LoginFirebaseActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_firebase)


        btn_sign_in.setOnClickListener() {
            fnLoguearse()
        }
        btn_forgot.setOnClickListener() {
            forgotPasswordClicked()
        }
        btn_register.setOnClickListener() {
            goToSignUp()
        }

        mAuth = FirebaseAuth.getInstance()

    }

    fun fnLoguearse() {
        val email = email.getText().toString().trim({ it <= ' ' })
        val password = password.getText().toString().trim({ it <= ' ' })

        progressBar.setVisibility(View.VISIBLE)

        mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful)

                    if (!task.isSuccessful) {
                        Log.w("TAG", "signInWithEmail:failed", task.exception)
                        Toast.makeText(this, R.string.auth_failed,Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        this.startActivity(intent)

                    }
                }

        val user = FirebaseAuth.getInstance().currentUser
        progressBar.setVisibility(View.GONE)

    }

    fun forgotPasswordClicked() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        this.startActivity(intent)
    }

    fun goToSignUp() {
        val intent = Intent(this, RegisterActivity::class.java)
        this.startActivity(intent)
    }

}
