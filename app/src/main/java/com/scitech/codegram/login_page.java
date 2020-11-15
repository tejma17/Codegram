package com.scitech.codegram;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class login_page extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    MaterialButton login;
    ImageView logo;
    TextView action;
    TextInputLayout email_field, pass_field;
    ProgressDialog progressDialog;
    FirebaseUser user;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        progressDialog = new ProgressDialog(this);

        register = (TextView) findViewById(R.id.signup_button);
        login = findViewById(R.id.login_button);

        firebaseAuth = FirebaseAuth.getInstance();
        email_field = findViewById(R.id.email);
        pass_field = findViewById(R.id.password);
        action = findViewById(R.id.action);
        logo = findViewById(R.id.logo);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(logo, "logo");
                pairs[1] = new Pair<View, String>(action, "action");
                pairs[2] = new Pair<View, String>(email_field, "email");
                pairs[3] = new Pair<View, String>(pass_field, "pass");
                pairs[4] = new Pair<View, String>(login, "button");
                pairs[5] = new Pair<View, String>(register, "message");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login_page.this, pairs);
                startActivity(new Intent(login_page.this, Signup_page.class), options.toBundle());
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin();
            }
        });
    }


    @Override
    protected void onStart() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), profile_view.class));
            finish();
        }
        super.onStart();
    }

    private void UserLogin()
    {
        String email, password;

        email = email_field.getEditText().getText().toString();
        password = pass_field.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(login_page.this, "Please enter the Email !!!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(login_page.this, "Password field cannot be kept blank!!!", Toast.LENGTH_SHORT).show();
        }

        else {

            progressDialog.setMessage("Signing in...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = firebaseAuth.getCurrentUser();
                        Toast.makeText(login_page.this, "Login Successful !!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login_page.this, profile_view.class));
                        finish();
                    }
                    else {
                        Exception e = task.getException();
                        Toast.makeText(login_page.this, "Login Failed !!! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
                }
            });
        }
    }
}
