package com.vinay.socialapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinay.socialapp.Adapter.UserFollowAdapter;
import com.vinay.socialapp.Model.User;
import com.vinay.socialapp.R;
import com.vinay.socialapp.databinding.FragmentSearchBinding;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

ArrayList<User> list = new ArrayList<>();
FirebaseAuth auth;
FirebaseDatabase database;

FragmentSearchBinding binding;
    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentSearchBinding.inflate(inflater, container, false);


        UserFollowAdapter adapter = new UserFollowAdapter(getContext(),list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.searchrv.setLayoutManager(linearLayoutManager);
        binding.searchrv.setAdapter(adapter);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    user.setUserID(dataSnapshot.getKey());
                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();

    }
}