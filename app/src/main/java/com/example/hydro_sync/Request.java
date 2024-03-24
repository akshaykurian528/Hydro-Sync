package com.example.hydro_sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Request extends AppCompatActivity {

    private Button myButton;
    private boolean isButtonOn = false; // Declare isButtonOn variable here
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private BottomNavigationView bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        myButton = findViewById(R.id.myButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.log_out) {
                    logout();
                    return true;
                } else {
                    return false;
                }
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child(userId).child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String requestStatus = dataSnapshot.getValue(String.class);
                    if (requestStatus != null && requestStatus.equals("send")) {
                        isButtonOn = true;
                    } else {
                        isButtonOn = false;
                    }
                    updateButton();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }



        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this)
                .asGif()
                .load(R.drawable.get_service)
                .into(gifImageView);
    }

    public void toggleButtonState(View view) {
        isButtonOn = !isButtonOn;
        updateButton();
        showToast();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String requestStatus = isButtonOn ? "send" : "cancel";
            mDatabase.child(userId).child("request").setValue(requestStatus);
        }
    }

    private void updateButton() {
        if (isButtonOn) {
            myButton.setText("Cancel Request");
            myButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cancel_send, 0, 0, 0);
        } else {
            myButton.setText("Send Request");
            myButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.send_ic, 0, 0, 0);
        }
    }

    private void showToast() {
        if (isButtonOn) {
            Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Request canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(Request.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Request.this, signin.class));
        finish();
    }
}