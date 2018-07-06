package com.example.saeedmac.saeedcapstoneproject.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.saeedmac.saeedcapstoneproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class Login extends AppCompatActivity implements View.OnClickListener {


    Button btnLogin;
    EditText input_email, input_password;
    TextView btnsignup, btnforgotpass;

    RelativeLayout activity_login;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.login_btn_login);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        btnsignup = (TextView) findViewById(R.id.login_btn_signup);
        btnforgotpass = (TextView) findViewById(R.id.login_btn_forgot_password);
        activity_login = (RelativeLayout) findViewById(R.id.activity_main);

        btnsignup.setOnClickListener(this);
        btnforgotpass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));


        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.login_btn_forgot_password) {

            try {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(activity_login,R.string.empty_error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            finish();
        } else if (v.getId() == R.id.login_btn_signup) {

            try {
                startActivity(new Intent(Login.this, SignUp.class));
            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(activity_login, R.string.empty_error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            finish();
        } else if (v.getId() == R.id.login_btn_login) {


            try {
                loginUser(input_email.getText().toString(), input_password.getText().toString());
            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(activity_login,R.string.empty_error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }


    }


    private void loginUser(final String email, final String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {

                                Snackbar snackbar = Snackbar.make(activity_login, R.string.password_length, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar.make(activity_login,R.string.must_signup, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }

                        } else {


                            startActivity(new Intent(Login.this, MainActivity.class));


                        }

                    }
                });


    }

}

