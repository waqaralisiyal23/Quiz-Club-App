package com.codewithwaqar.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.codewithwaqar.quizapp.databinding.ActivityCreateAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import models.UserModel;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding binding;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(CreateAccountActivity.this);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));

        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameBox.getText().toString();
                String email = binding.emailBox.getText().toString();
                String password = binding.passwordBox.getText().toString();
//                String refer = binding.referBox.getText().toString();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)
                        && TextUtils.isEmpty(name)){
                    Toast.makeText(CreateAccountActivity.this,
                            "Please fill up the details", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(name)){
                    Toast.makeText(CreateAccountActivity.this,
                            "Please enter your name", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(email)){
                    Toast.makeText(CreateAccountActivity.this,
                            "Please enter your email", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(CreateAccountActivity.this,
                            "Please enter your password", Toast.LENGTH_LONG).show();
                }
                else{

                    progressDialog.setTitle("Creating Account");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

//                    if(TextUtils.isEmpty(refer))
//                        createAccount(name, email, password, "");
//                    else
//                        createAccount(name, email, password, refer);
                    createAccount(name, email, password);

                }
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccountActivity.this,
                        LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createAccount(final String name, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    saveUserInfoToFirestore(task.getResult().getUser(), name, email, password);
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(CreateAccountActivity.this,
                            task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveUserInfoToFirestore(final FirebaseUser user, String name, String email, String password) {

        UserModel userModel = new UserModel(name, email, password);

        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mAuth.signOut();
                                progressDialog.dismiss();
                                Toast.makeText(CreateAccountActivity.this,
                                        "Account Created Successfully\nPlease verify your email address",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CreateAccountActivity.this,
                                        LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(CreateAccountActivity.this,
                                        task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(CreateAccountActivity.this,
                            task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}