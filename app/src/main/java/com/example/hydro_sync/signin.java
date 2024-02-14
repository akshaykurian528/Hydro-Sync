package com.example.hydro_sync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hydro_sync.databinding.ActivitySigninBinding;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signin extends AppCompatActivity {

    ActivitySigninBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(signin.this);
        dialog.setTitle("Create your account");
        dialog.setMessage("your account is creating");

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.userEmail.getText().toString();
                String password = binding.password.getText().toString();

                // Check if user is an admin
                if (email.equals("admin@gmail.com") && password.equals("Admin123@")) {
                    // Admin login successful, redirect to admin activity
                    Intent adminIntent = new Intent(signin.this, AdminActivity.class);
                    startActivity(adminIntent);
                    finish(); // Finish the current activity to prevent going back to the sign-in screen
                } else {
                    // Authenticate regular user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    dialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(signin.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // Finish the current activity
                                    } else {
                                        Toast.makeText(signin.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        binding.noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signin.this, signup.class);
                startActivity(intent);


            }
        });
    }
}

