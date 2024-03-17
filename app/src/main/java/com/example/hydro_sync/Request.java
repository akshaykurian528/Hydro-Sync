package com.example.hydro_sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        myButton = findViewById(R.id.myButton);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Fetch "request" field value from the database and set button state accordingly
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
    }

    public void toggleButtonState(View view) {
        isButtonOn = !isButtonOn; // Toggle the state
        updateButton();
        showToast();

        // Update the "request" field in the database
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
        } else {
            myButton.setText("Send Request");
        }
    }

    private void showToast() {
        if (isButtonOn) {
            Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Request canceled", Toast.LENGTH_SHORT).show();
        }
    }
}