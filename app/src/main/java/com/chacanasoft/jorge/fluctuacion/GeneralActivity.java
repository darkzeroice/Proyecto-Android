package com.chacanasoft.jorge.fluctuacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GeneralActivity extends AppCompatActivity {

    private Button btnChangeEmail;
    private Button btnPasswordChange;
    private Button btnSendResetEmail;
    private Button btnRemoveUser;
    private Button changeEmail;
    private Button changePassword;
    private Button sendEmail;
    private Button remove;
    private Button signOut;

    private EditText oldEmail;
    private EditText newEmail;
    private EditText password;
    private EditText newPassword;

    private ProgressBar progressBar;

    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(GeneralActivity.this, LoginFirebaseActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };


    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        btnChangeEmail = (Button)findViewById(R.id.change_email_button);
        btnPasswordChange = (Button)findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button)findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button)findViewById(R.id.remove_user_button);
        changeEmail = (Button)findViewById(R.id.changeEmail);
        changePassword = (Button)findViewById(R.id.changePass);
        sendEmail = (Button)findViewById(R.id.send);
        remove = (Button)findViewById(R.id.remove);
        signOut = (Button)findViewById(R.id.sign_out);

        oldEmail = (EditText)findViewById(R.id.old_email);
        newEmail = (EditText)findViewById(R.id.new_email);
        password = (EditText)findViewById(R.id.password);
        newPassword = (EditText)findViewById(R.id.newPassword);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        //Creamos instancia de Firebase
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    public void btnChangeEmailPressed(View view) {
        changeBtnsVisibility(2);
    }

    public void changeEmailPressed(View view) {
        if (user != null && !newEmail.getText().toString().trim().equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            //Actualizar el correo del usuario
            user.updateEmail(newEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(GeneralActivity.this,
                                "El email ha sido modificado. Por favor vuelve a loguearte",
                                Toast.LENGTH_SHORT).show();
                        signOutUser();
                    } else {
                        Toast.makeText(GeneralActivity.this,
                                "Error. Vuélvelo a intentar", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void btnChangePasswordPressed(View view) {
        changeBtnsVisibility(3);
    }

    public void changePasswordPressed(View view) {
        if (user != null && !newPassword.getText().toString().trim().equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            //Actualizamos la contraseña del usuario
            user.updatePassword(newPassword.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(GeneralActivity.this,
                                "El password ha sido modificado. " +
                                        "Por favor vuelve a loguearte",
                                Toast.LENGTH_SHORT).show();
                        signOutUser();
                    } else {
                        Toast.makeText(GeneralActivity.this,
                                "Error. Vuélvelo a intentar", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void btnSendResetEmailPressed(View view) {
        changeBtnsVisibility(4);
    }

    public void sendEmailPressed(View view) {
        if (user != null) {
            oldEmail.setText(user.getEmail().toString());
            if (!oldEmail.getText().toString().trim().equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(GeneralActivity.this,
                                    "Te hemos enviado un correo para resetear la password",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GeneralActivity.this,
                                    "Error. Vuélvelo a intentar", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    public void btnRemoveUserPressed(View view) {
        changeBtnsVisibility(1);
    }

    public void removeUserPressed(View view) {
        if (user != null && !password.getText().toString().trim().equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            //Comprobar contraseña para confirmar acción
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), password.getText().toString().trim());
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(GeneralActivity.this,
                                            "Cuenta eliminada",
                                            Toast.LENGTH_SHORT).show();
                                    signOutUser();
                                } else {
                                    Toast.makeText(GeneralActivity.this,
                                            "Error. Cuenta no eliminada.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        Toast.makeText(GeneralActivity.this,
                                "Error. Datos incorrectos", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void signOutPressed(View view) {
        signOutUser();
    }

    public void signOutUser() {
        auth.signOut();
    }

    public void changeBtnsVisibility(int btnCode) {
        oldEmail.setVisibility(btnCode == 4 ?  View.VISIBLE : View.GONE);
        newEmail.setVisibility(btnCode == 2 ?  View.VISIBLE : View.GONE);
        password.setVisibility(btnCode == 1 ?  View.VISIBLE : View.GONE);
        newPassword.setVisibility(btnCode == 3 ?  View.VISIBLE : View.GONE);
        changeEmail.setVisibility(btnCode == 2 ?  View.VISIBLE : View.GONE);
        changePassword.setVisibility(btnCode == 3 ?  View.VISIBLE : View.GONE);
        sendEmail.setVisibility(btnCode == 4 ?  View.VISIBLE : View.GONE);
        remove.setVisibility(btnCode == 1 ?  View.VISIBLE : View.GONE);
    }

}
















