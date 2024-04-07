package com.example.hydro_sync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private boolean isButtonOn = false;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private BottomNavigationView bottomNavigationView;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
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
        // Show confirmation dialog
        if (!isButtonOn) {
            showSendLocationConfirmationDialog();
        } else {
            showCancelRequestConfirmationDialog();
        }
    }

    private void showSendLocationConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Send Service Request ?");
        builder.setMessage("Make sure you are at your home as your current location is shared with the service authorities for installation process");
        builder.setIcon(R.drawable.service_req_ic);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, toggle button state
                isButtonOn = !isButtonOn;
                updateButton();
                showToast();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String requestStatus = isButtonOn ? "send" : "cancel";
                    mDatabase.child(userId).child("request").setValue(requestStatus);

                    // If the request is sent, store the current location
                    if (isButtonOn) {
                        if (ContextCompat.checkSelfPermission(Request.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(Request.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        } else {
                            getLocationAndUpdateDatabase(userId);
                        }
                    }
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, dismiss dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showCancelRequestConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Cancel Request");
        builder.setMessage("Are you sure you want to cancel your request ?");
        builder.setIcon(R.drawable.alert_ic);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, toggle button state
                isButtonOn = !isButtonOn;
                updateButton();
                showToast();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String requestStatus = isButtonOn ? "send" : "cancel";
                    mDatabase.child(userId).child("request").setValue(requestStatus);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, dismiss dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void getLocationAndUpdateDatabase(String userId) {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            // Location permissions granted, proceed to get location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Save the location to Firebase
                        DatabaseReference userLocationRef = mDatabase.child(userId).child("location");
                        userLocationRef.child("latitude").setValue(location.getLatitude());
                        userLocationRef.child("longitude").setValue(location.getLongitude());
                    } else {
                        // Handle null location
                        Log.e("LocationError", "Location is null");
                        Toast.makeText(Request.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle location retrieval failure
                    Log.e("LocationError", "Location retrieval failed: " + e.getMessage(), e);
                    Toast.makeText(Request.this, "Location retrieval failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    getLocationAndUpdateDatabase(userId);
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
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
