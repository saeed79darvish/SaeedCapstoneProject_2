package com.example.saeedmac.saeedcapstoneproject.ui;

import android.content.Intent;
import android.media.MediaPlayer;
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
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText input_email;
    private Button btnResetPass;
    private TextView btnBack;
    private RelativeLayout activity_forgot;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        input_email = (EditText) findViewById(R.id.forgot_email);
        btnResetPass = (Button) findViewById(R.id.forgot_btn_reset);
        btnBack = (TextView) findViewById(R.id.forgot_btn_back);
        activity_forgot = (RelativeLayout) findViewById(R.id.forgot_password);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);


        auth = FirebaseAuth.getInstance();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.forgot_btn_back) {
            try {
                startActivity(new Intent(this, Login.class));
            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(activity_forgot, "please enter somthing", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            finish();
        } else if (v.getId() == R.id.forgot_btn_reset) {
            try {
                resetPassword(input_email.getText().toString());
            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(activity_forgot, "please enter somthing", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    private void resetPassword(final String email) {

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar snackbar = Snackbar.make(activity_forgot, "we have sent password to email " + email, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {

                            Snackbar snackbar = Snackbar.make(activity_forgot, "Failed to send password", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
    }
}
