package com.example.hydro_sync;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MenuFragment extends Fragment {
    private Dialog dialog;
    private Dialog successDialog;
    private long lastFeedbackTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        GridView gridView = rootView.findViewById(R.id.gridView);
        List<CustomMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new CustomMenuItem("Weather", R.drawable.weather_ic));
        menuItems.add(new CustomMenuItem("Feedback", R.drawable.feedback_ic));
        menuItems.add(new CustomMenuItem("Call Support", R.drawable.call_center_ic));
        menuItems.add(new CustomMenuItem("Chat Support", R.drawable.whatsapp_ic));

        MenuAdapter adapter = new MenuAdapter(getContext(), menuItems);
        gridView.setAdapter(adapter);

        setupDialogs();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 2: // Call Support
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:8138890474")));
                    break;
                case 3: // Chat Support
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+916235880096"));
                    intent.setPackage("com.whatsapp");
                    startActivity(intent);
                    break;
                case 1: // Feedback
                    showFeedbackDialogIfNeeded();
                    break;
            }
        });

        return rootView;
    }

    private void setupDialogs() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button okay = dialog.findViewById(R.id.btn_okay);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        EditText editText = dialog.findViewById(R.id.editTextTextMultiLine);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        setupRatingBar(ratingBar);

        okay.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = editText.getText().toString().trim();
            if (rating == 0 || comment.isEmpty()) {
                Toast.makeText(getActivity(), "Please rate and provide feedback before submitting.", Toast.LENGTH_SHORT).show();
            } else {
                storeFeedback(rating, comment, System.currentTimeMillis());
                dialog.dismiss();
                ratingBar.setRating(0);
                editText.setText("");
                successDialog.show();
            }
        });

        cancel.setOnClickListener(v -> {
            ratingBar.setRating(0);
            editText.setText("");
            Toast.makeText(getActivity(), "Feedback Canceled", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        successDialog = new Dialog(getContext());
        successDialog.setContentView(R.layout.success_dialog_layout);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCancelable(true);
    }

    private void showFeedbackDialogIfNeeded() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("lastFeedbackTime");
            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().getValue() != null) {
                    lastFeedbackTime = task.getResult().getValue(Long.class);
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastFeedbackTime < 604800000) { // 604800000 milliseconds in a week
                        successDialog.show();
                    } else {
                        dialog.show();
                    }
                } else {
                    dialog.show();
                }
            });
        } else {
            Toast.makeText(getContext(), "You need to be logged in to submit feedback.", Toast.LENGTH_LONG).show();
        }
    }

    private void storeFeedback(float rating, String comment, long currentTime) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            String key = database.child("users").child(userId).child("feedbacks").push().getKey();
            if (key != null) {
                Map<String, Object> feedback = new HashMap<>();
                feedback.put("date", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(currentTime)));
                feedback.put("time", new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(currentTime)));
                feedback.put("rating", rating);
                feedback.put("comment", comment);
                database.child("users").child(userId).child("feedbacks").child(key).setValue(feedback);
                database.child("users").child(userId).child("lastFeedbackTime").setValue(currentTime);
            }
        } else {
            Toast.makeText(getContext(), "You need to be logged in to submit feedback.", Toast.LENGTH_LONG).show();
        }
    }

    private void setupRatingBar(RatingBar ratingBar) {
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(getContext(), R.color.blue_main), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(getContext(), R.color.alabaster), PorterDuff.Mode.SRC_ATOP);
        ratingBar.setStepSize(1.0f);
    }
}
