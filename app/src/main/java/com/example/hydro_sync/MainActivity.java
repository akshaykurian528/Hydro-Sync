package com.example.hydro_sync;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hydro_sync.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DrawerLayout drawerLayout;
    TextView userNameTextView;
    TextView userEmailTextView;
    Toolbar toolbar;
    private static final String CHANNEL_ID = "MyNotificationChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new home());
        binding.bottomNavigationView.setSelectedItemId(R.id.menuhome);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menuprofile) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.menuhome) {
                replaceFragment(new home());
            }else if (item.getItemId() == R.id.menumenu) {
                replaceFragment(new MenuFragment());
            } else if (item.getItemId() == R.id.menusettings) {
                showBottomSheet();
            }
            return true;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.userNameTextView);
        userEmailTextView = headerView.findViewById(R.id.userEmailTextView);

        setupNavigationDrawerHeader();

        // Create notification upon user login
        createNotification();
    }

    private void createNotification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Integer level = dataSnapshot.child("level").getValue(Integer.class);
                        String mode = dataSnapshot.child("mode").getValue(String.class);
                        String switchstatus = dataSnapshot.child("switch").getValue(String.class);

                        // Check conditions for low field level and switch off
                        if (level != null && level < 30 && "Manual".equals(mode) && "Off".equals(switchstatus)) {
                            // Conditions met, proceed to create low level notification
                            createLowLevelNotification();
                        }

                        // Check conditions for high field level and switch on
                        if (level != null && level > 80 && "Manual".equals(mode) && "On".equals(switchstatus)) {
                            // Conditions met, proceed to create high level notification
                            createHighLevelNotification();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MainActivity", "Database Error: " + databaseError.getMessage());
                }
            });
        }
    }

    private void createLowLevelNotification() {
        // Check if the notification has already been shown
        if (!isNotificationAlreadyShown("low_level_notification")) {
            // Create notification for low field level
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

            // Create Notification Channel for Android Oreo and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            // Create MainActivity Intent
            Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
            // Add extra to indicate the action
            mainIntent.putExtra("action", "low_level_notification");
            PendingIntent mainPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create Notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_ic)
                    .setContentTitle("Low Water Level")
                    .setContentText("Your Water tank level is less than 30%")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(mainPendingIntent) // Set the PendingIntent for MainActivity
                    .setAutoCancel(true); // Automatically removes the notification when tapped

            // Show Notification
            notificationManager.notify(1, builder.build());
        }
    }

    private void createHighLevelNotification() {
        // Check if the notification has already been shown
        if (!isNotificationAlreadyShown("high_level_notification")) {
            // Create notification for high field level
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

            // Create Notification Channel for Android Oreo and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            // Create MainActivity Intent
            Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
            // Add extra to indicate the action
            mainIntent.putExtra("action", "high_level_notification");
            PendingIntent mainPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create Notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_ic)
                    .setContentTitle("Pump Switch off Alert")
                    .setContentText("Your Water tank is about to get filled")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(mainPendingIntent) // Set the PendingIntent for MainActivity
                    .setAutoCancel(true); // Automatically removes the notification when tapped

            // Show Notification
            notificationManager.notify(2, builder.build());
        }
    }

    // Check if the notification has already been shown
    private boolean isNotificationAlreadyShown(String action) {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("action")) {
            String extra = intent.getStringExtra("action");
            return extra != null && extra.equals(action);
        }
        return false;
    }







    private void setupNavigationDrawerHeader() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            userEmailTextView.setText(userEmail);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("userName").getValue(String.class);
                        String houseName = dataSnapshot.child("houseName").getValue(String.class);
                        String houseNo = dataSnapshot.child("houseNo").getValue(String.class);
                        String mobileNo = dataSnapshot.child("mobileNo").getValue(String.class);

                        if (userName == null || userName.isEmpty()) {
                            userName = "User";
                        }
                        if (houseName == null || houseName.isEmpty()) {
                            houseName = "Unknown";
                        }
                        if (houseNo == null || houseNo.isEmpty()) {
                            houseNo = "Unknown";
                        }
                        if (mobileNo == null || mobileNo.isEmpty()) {
                            mobileNo = "Unknown";
                        }

                        userNameTextView.setText(userName);

                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        MenuItem houseNameItem = menu.findItem(R.id.nav_house_name);
                        MenuItem houseNoItem = menu.findItem(R.id.nav_house_no);
                        MenuItem mobileNoItem = menu.findItem(R.id.nav_mobile_no);

                        houseNameItem.setTitle(houseName);
                        houseNoItem.setTitle(houseNo);
                        mobileNoItem.setTitle(mobileNo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout logout = dialog.findViewById(R.id.layoutlogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, signin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
