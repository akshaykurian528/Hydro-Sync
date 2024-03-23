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
                String housename = binding.houseName.getText().toString();
                String houseno = binding.houseNo.getText().toString();
                String mobileno = binding.mobileNo.getText().toString();

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

                if (!username.matches("[a-zA-Z ]+")) {
                    dialog.dismiss();
                    Toast.makeText(signup.this, "Name must contain only letters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!housename.matches("[a-zA-Z ]+")) {
                    dialog.dismiss();
                    Toast.makeText(signup.this, "House name must contain only letters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!houseno.matches("[a-zA-Z0-9]+")) {
                    dialog.dismiss();
                    Toast.makeText(signup.this, "House number must contain only letters and numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mobileno.matches("\\d{10}")) {
                    dialog.dismiss();
                    Toast.makeText(signup.this, "Mobile number must contain exactly 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    // If successful, save user info in Firebase database
                                    String id = task.getResult().getUser().getUid();

                                    // Create a HashMap to hold user data including the "request" field
                                    HashMap<String, Object> userData = new HashMap<>();
                                    userData.put("userName", username);
                                    userData.put("houseName", housename);
                                    userData.put("UserEmail", email);
                                    userData.put("password", password);
                                    userData.put("houseNo", houseno);
                                    userData.put("mobileNo", mobileno);
                                    userData.put("level",-1);
                                    userData.put("request", "");

                                    // Save user data to Firebase under "users" node
                                    database.getReference().child("users").child(id).setValue(userData);

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