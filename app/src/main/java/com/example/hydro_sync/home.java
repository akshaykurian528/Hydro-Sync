package com.example.hydro_sync;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
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

    private SwitchCompat modeSwitch;
    private TextView modeStatusTextView;
    private ViewGroup autoLayout;
    private ViewGroup manualLayout;
    private ToggleButton manualButton;

    public home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        waveProgressBar = view.findViewById(R.id.waveProgressBar);
        modeSwitch = view.findViewById(R.id.modeswitch);
        modeStatusTextView = view.findViewById(R.id.modestatus);
        autoLayout = view.findViewById(R.id.autolayout);
        manualLayout = view.findViewById(R.id.manuallayout);
        autoLayout.setVisibility(View.GONE);
        manualLayout.setVisibility(View.GONE);
        manualButton = view.findViewById(R.id.manualButton);

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

            // Retrieve pumpstatus data
            DatabaseReference pumpStatusRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("pumpstatus");
            pumpStatusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String pumpStatus = dataSnapshot.getValue(String.class);
                        if (pumpStatus != null) {
                            Button pumpStatusButton = view.findViewById(R.id.pumpstatus);
                            pumpStatusButton.setText(pumpStatus);

                            if (pumpStatus.equalsIgnoreCase("On")) {
                                pumpStatusButton.setTextColor(Color.GREEN);
                            } else {
                                pumpStatusButton.setTextColor(Color.WHITE);
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });

            // Retrieve mode data
            DatabaseReference modeRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("mode");
            modeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String mode = dataSnapshot.getValue(String.class);
                        if (mode != null) {
                            // Update switch based on mode value
                            if (mode.equalsIgnoreCase("Auto")) {
                                modeSwitch.setChecked(true);
                            } else {
                                modeSwitch.setChecked(false);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }

        // Toggle additional views visibility based on switch state
        toggleAdditionalViewsVisibility(modeSwitch.isChecked());

        // Update mode status text
        updateModeStatusText(modeSwitch.isChecked());

        // Toggle additional views visibility based on switch state
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateModeStatusText(isChecked);
                toggleAdditionalViewsVisibility(isChecked);

                // Update mode field in Firebase
                if (user != null) {
                    DatabaseReference modeRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("mode");
                    if (isChecked) {
                        // Update mode to "Auto" when the switch is checked
                        modeRef.setValue("Auto");
                    } else {
                        // Update mode to "Manual" when the switch is unchecked
                        modeRef.setValue("Manual");
                    }
                }
            }
        });

        // Set listener for manualButton
        manualButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update switch field in Firebase
                if (user != null) {
                    DatabaseReference switchRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("switch");
                    if (isChecked) {
                        // Update switch to "On" when the button is checked
                        switchRef.setValue("On");
                    } else {
                        // Update switch to "Off" when the button is unchecked
                        switchRef.setValue("Off");
                    }
                }
            }
        });

        CardView autoLayoutCardView = view.findViewById(R.id.autolayout);
        TextView pointsTextView = autoLayoutCardView.findViewById(R.id.autotext);
        String pointsHtml = getResources().getString(R.string.points_text);
        pointsTextView.setText(Html.fromHtml(pointsHtml));

        return view;
    }



    // Method to update mode status text based on switch state
    private void updateModeStatusText(boolean isChecked) {
        if (isChecked) {
            modeStatusTextView.setText("Auto");
        } else {
            modeStatusTextView.setText("Manual");
        }
    }

    // Method to toggle additional views visibility based on switch state
    private void toggleAdditionalViewsVisibility(boolean isChecked) {
        if (isChecked) {
            autoLayout.setVisibility(View.VISIBLE);
            manualLayout.setVisibility(View.GONE);
        } else {
            manualLayout.setVisibility(View.VISIBLE);
            autoLayout.setVisibility(View.GONE);
        }
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
