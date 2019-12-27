package com.jihad.app.travelmate.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.common.Functions;
import com.jihad.app.travelmate.models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private ProgressDialog loadingBar;

    private TextInputLayout inputLayoutFullName;
    private TextInputLayout inputLayoutUsername;
    private TextInputLayout inputLayoutPassword;

    private EditText etRegFullName;
    private EditText etRegUsername;
    private EditText etRegPassword;

    private RadioGroup radioGroupGender;
    protected RadioButton radioButtonGender;

    protected Button btnRegister;
    protected TextView tvLinkTogoLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initFunction();
    }

    private void initFunction() {

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        inputLayoutFullName = findViewById(R.id.inputLayoutRegFullName);
        inputLayoutUsername = findViewById(R.id.inputLayoutRegUsername);
        inputLayoutPassword = findViewById(R.id.inputLayoutRegPassword);

        etRegFullName = findViewById(R.id.etFullName);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);

        radioGroupGender = findViewById(R.id.radioGroupGender);

        loadingBar = new ProgressDialog(RegisterActivity.this );

        btnRegister = findViewById(R.id.btnRegister);
        tvLinkTogoLogin = findViewById(R.id.linkTextTogoLogin);

        btnRegister.setOnClickListener(RegisterActivity.this);
        tvLinkTogoLogin.setOnClickListener(RegisterActivity.this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnRegister:
                registerTheNewUser();
                break;
            case R.id.linkTextTogoLogin:
                Functions.sendToActivityAsNew(this, LoginActivity.class);
                break;
        }
    }

    private void registerTheNewUser() {

        radioButtonGender = findViewById(radioGroupGender.getCheckedRadioButtonId());

        final String fullName     = etRegFullName.getText().toString();
        final String username     = etRegUsername.getText().toString();
        String password           = etRegPassword.getText().toString();
        final String gender       = radioButtonGender.getText().toString();

        if (isValid()) {

            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating new account for you.");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        String userUID = mAuth.getCurrentUser().getUid();

                        User user = new User(userUID, fullName, gender, username, "01*********", "online", "");

                        rootRef.child("Users").child(userUID).setValue(user);

                        Functions.sendToActivityAsNew(RegisterActivity.this, MainActivity.class);
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }


    private boolean isValid() {

        if (etRegFullName.getText().toString().isEmpty()){
            inputLayoutFullName.setError("Cannot be blank");
            return false;
        } else {
            inputLayoutFullName.setErrorEnabled(false);
        }

        if (etRegUsername.getText().toString().isEmpty()){
            inputLayoutUsername.setError("Cannot be blank");
            return false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }

        String pwd = etRegPassword.getText().toString().trim();
        if (pwd.length() < 6 ){
            inputLayoutPassword.setError("Minimum 6 characters required");
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }


}
