package com.jihad.app.travelmate.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.common.Functions;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private TextInputLayout inputLayoutUsername, inputLayoutPassword;

    private EditText etLoginUsername;
    private EditText etLoginPassword;

    protected Button btnLogin;
    protected TextView tvLinkTogoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFunction();
    }

    private void initFunction() {

        mAuth = FirebaseAuth.getInstance();

        inputLayoutUsername = findViewById(R.id.inputLayoutLoginUsername);
        inputLayoutPassword = findViewById(R.id.inputLayoutLoginPassword);

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);

        loadingBar = new ProgressDialog(LoginActivity.this );

        btnLogin = findViewById(R.id.btnLogin);
        tvLinkTogoRegister = findViewById(R.id.linkTextTogoRegister);

        btnLogin.setOnClickListener(LoginActivity.this);
        tvLinkTogoRegister.setOnClickListener(LoginActivity.this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                loginTheUser();
                break;
            case R.id.linkTextTogoRegister:
                Functions.sendToActivityAsNew(this, RegisterActivity.class);
                break;
        }
    }

    private void loginTheUser() {

        String email    = etLoginUsername.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (isValid()) {

            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Functions.sendToActivityAsNew(LoginActivity.this, MainActivity.class);
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private boolean isValid() {

        if (etLoginUsername.getText().toString().isEmpty()){
            inputLayoutUsername.setError("Cannot be blank");
            return false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }

        String pwd = etLoginPassword.getText().toString().trim();
        if (pwd.length() < 6 ){
            inputLayoutPassword.setError("Minimum 6 characters required");
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }


}
