package com.example.hydro_sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.hydro_sync.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;


public class signup extends AppCompatActivity {
    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(signup.this);
        dialog.setTitle("Create your account");
        dialog.setMessage("your account is creating");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                // Get user input
                String email = binding.userEmail.getText().toString();
                String password = binding.password.getText().toString();
                String username = binding.userName.getText().toString();

                // Validate email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    dialog.dismiss();
                    Toast.makeText(signup.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate password
                if (password.length() < 8 || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                    dialog.dismiss();
                    Toast.makeText(signup.this, "Password must be at least 8 characters long and contain a special character", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate username
                if (!username.matches("[a-zA-Z]+")) {
                    dialog.dismiss();
                    Toast.makeText(signup.this, "Username must contain only letters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase authentication
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    // If successful, save user info in Firebase database
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("userName", username);
                                    map.put("UserEmail", email);
                                    map.put("password", password);

                                    String id = task.getResult().getUser().getUid();

                                    database.getReference().child("users").child(id).setValue(map);

                                    // Move to Sign-in activity
                                    Intent intent = new Intent(signup.this, signin.class);
                                    startActivity(intent);
                                } else {
                                    // If sign-in fails, display a message to the user.
                                    Toast.makeText(signup.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        binding.alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, signin.class);
                startActivity(intent);
                finish();
            }
        });
    }
}