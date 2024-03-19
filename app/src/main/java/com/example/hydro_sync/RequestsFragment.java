package com.example.hydro_sync;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference database;
    RequestAdapter myAdapter;
    ArrayList<UserRequests> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);

        recyclerView = rootView.findViewById(R.id.requestList);
        if (recyclerView == null) {
            Log.e("RequestsFragment", "RecyclerView is null!");
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        myAdapter = new RequestAdapter(getActivity(), list);
        recyclerView.setAdapter(myAdapter);

        database = FirebaseDatabase.getInstance().getReference("users");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear the list before adding new items
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserRequests request = dataSnapshot.getValue(UserRequests.class);
                    String userId = dataSnapshot.getKey(); // Get the userId from the snapshot
                    // Set the userId for the UserRequests object
                    if (request != null) {
                        request.setUserId(userId);
                    }
                    // Check if the request field is equal to "send"
                    if (request != null && "send".equals(request.getRequest())) {
                        list.add(request);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        return rootView;
    }

}
