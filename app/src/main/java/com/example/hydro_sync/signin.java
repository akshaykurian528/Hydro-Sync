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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        dialog.setTitle("Signing in");
        dialog.setMessage("Please wait...");

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignIn();
            }
        });

        binding.noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signin.this, signup.class);
                startActivity(intent);
            }
        });

        // Setup forgot password click listener
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signin.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void performSignIn() {
        String email = binding.userEmail.getText().toString();
        String password = binding.password.getText().toString();
        dialog.show();

        if (email.isEmpty()) {
            dialog.dismiss();
            binding.userEmail.setError("Email cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            dialog.dismiss();
            binding.password.setError("Password cannot be empty");
            return;
        }

        if (email.equals("admin@gmail.com") && password.equals("Admin123@")) {
            Intent adminIntent = new Intent(signin.this, AdminActivity.class);
            startActivity(adminIntent);
            finish();
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                checkRequestStatus();
                            } else {
                                Toast.makeText(signin.this, "Sign in failed: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void checkRequestStatus() {
        String userId = auth.getCurrentUser().getUid();
        database.getReference().child("users").child(userId).child("request")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String requestStatus = dataSnapshot.getValue(String.class);
                        if (requestStatus != null && requestStatus.equals("accepted")) {
                            // Redirect to MainActivity
                            Intent intent = new Intent(signin.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Finish the current activity
                        } else {
                            // Redirect to Request activity
                            Intent intent = new Intent(signin.this, Request.class);
                            startActivity(intent);
                            finish(); // Finish the current activity
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }
}
