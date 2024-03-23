package com.example.hydro_sync;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends Fragment {

    // Declare WaveProgressBar
    private WaveProgressBar waveProgressBar;
    private DatabaseReference levelRef;
    private ValueEventListener valueEventListener;

    public home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize WaveProgressBar
        waveProgressBar = view.findViewById(R.id.waveProgressBar);

        // Retrieve Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Initialize DatabaseReference for level data
            levelRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("level");

            // Add ValueEventListener to listen for changes in the level data
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve level data from Firebase
                        Long levelLong = dataSnapshot.getValue(Long.class);
                        if (levelLong != null) {
                            // Convert Long to int
                            int level = levelLong.intValue();
                            // Set level value to WaveProgressBar using setLevelProgress method
                            waveProgressBar.setLevelProgress(level);
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            };

            levelRef.addValueEventListener(valueEventListener);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove ValueEventListener when the fragment is destroyed to prevent memory leaks
        if (levelRef != null) {
            levelRef.removeEventListener(valueEventListener);
        }
    }
}


