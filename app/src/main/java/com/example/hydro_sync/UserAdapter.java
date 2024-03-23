package com.example.hydro_sync;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Scanner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context context;
    ArrayList<User> list;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.useritem, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.userName.setText(user.getUsername());
        holder.houseNo.setText(user.getHouseNo());
        holder.houseName.setText(user.getHouseName());

        // Check if the user has a level field and it's within the range of 0-100
        Integer level = user.getLevel();
//        System.out.println(level);
        if(level != null && level >= 0 && level <= 100)
        {
            // If the user has a non-null and level field within the range of 0-100, set the button text to "Active" and color to green
            holder.activeButton.setText("Active   ");
            holder.activeButton.setTextColor(ContextCompat.getColor(context, R.color.blue_main));
            holder.activeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.active_ic, 0, 0, 0);
        } else {
            // If the user doesn't have a level field, or it's outside the range of 0-100, set the button text to "Inactive" and color to red
            holder.activeButton.setText("Inactive");
            holder.activeButton.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.activeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.inactive_ic, 0, 0, 0);
        }
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName, houseNo, houseName, activeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            houseNo = itemView.findViewById(R.id.house_no);
            houseName = itemView.findViewById(R.id.house_name);
            activeButton = itemView.findViewById(R.id.status_button);
        }
    }
}
