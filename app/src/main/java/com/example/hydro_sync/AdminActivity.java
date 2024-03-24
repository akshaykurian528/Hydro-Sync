package com.example.hydro_sync;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hydro_sync.databinding.ActivityAdminBinding;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    View notificationIndicator;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.admin_drawer_layout);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Call method to set notification count
        setNotificationCountFromDatabase();

        binding.adminBottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menuusers) {
                replaceFragment(new UsersFragment());
            } else if (item.getItemId() == R.id.menurequests) {
                replaceFragment(new RequestsFragment());
            } else if (item.getItemId() == R.id.menusettings) {
                showBottomSheet();
            }
            return true;
        });

        binding.adminBottomNavigationView.setSelectedItemId(R.id.menuusers);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.admin_frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout logout = dialog.findViewById(R.id.layoutlogout);

        logout.setOnClickListener(v -> {
            logout();
            Toast.makeText(AdminActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AdminActivity.this, signin.class));
        finish();
    }

    private void setNotificationCountFromDatabase() {
        // Listen for changes in the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int notificationCount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getRequest().equals("send")) {
                        notificationCount++;
                    }
                }
                // Set the notification count
                showNotificationIndicator(notificationCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(AdminActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNotificationIndicator(int notificationCount) {
        // Get the menu of the BottomNavigationView
        Menu menu = binding.adminBottomNavigationView.getMenu();

        // Find the MenuItem with the ID "menurequests"
        MenuItem requestsMenuItem = menu.findItem(R.id.menurequests);

        if (requestsMenuItem != null) {
            // Get the view for the MenuItem
            View itemView = binding.adminBottomNavigationView.findViewById(requestsMenuItem.getItemId());

            // Inflate the indicator layout
            notificationIndicator = LayoutInflater.from(this).inflate(R.layout.layout_indicator, null);

            // Find the TextView inside the indicator layout
            TextView notificationCountTextView = notificationIndicator.findViewById(R.id.notification_count_text_view);

            // Set the notification count text
            notificationCountTextView.setText(String.valueOf(notificationCount));

            // Add the indicator to the MenuItem's view
            if (itemView instanceof FrameLayout) {
                ((FrameLayout) itemView).addView(notificationIndicator);
            }
        }
    }
}

