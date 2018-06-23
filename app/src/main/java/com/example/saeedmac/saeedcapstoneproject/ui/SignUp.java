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

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    Button btnSignup;
    TextView btnLogin,btnForgotPass;
    EditText input_email,input_pass;
    RelativeLayout activity_sign_up;

    private FirebaseAuth auth;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignup=(Button) findViewById(R.id.signup_btn_register);
        btnLogin=(TextView) findViewById(R.id.signup_btn_login);
        btnForgotPass=(TextView) findViewById(R.id.signup_btn_forgot_pass);
        input_email=(EditText) findViewById(R.id.signup_email);
        input_pass=findViewById(R.id.signup_password);
        activity_sign_up=(RelativeLayout) findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        auth=FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.signup_btn_login){
            startActivity(new Intent(SignUp.this,Login.class));
            finish();
        }
        else if (v.getId()==R.id.signup_btn_forgot_pass){
            try {
                startActivity(new Intent(SignUp.this,ForgotPassword.class));
            }catch (Exception e){
                Snackbar snackbar=Snackbar.make(activity_sign_up,"please enter somthing",Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            finish();
        }
        else if (v.getId()==R.id.signup_btn_register){
           try {
               signUpUser(input_email.getText().toString(),input_pass.getText().toString());
           }catch (Exception e){
               Snackbar snackbar=Snackbar.make(activity_sign_up,"please enter somthing",Snackbar.LENGTH_SHORT);
               snackbar.show();
           }


        }



    }

    private void signUpUser(String email, String password) {

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            snackbar=Snackbar.make(activity_sign_up,"Error"+task.getException(),Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                        else {
                            snackbar=Snackbar.make(activity_sign_up,"Register Success",Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
    }
}
