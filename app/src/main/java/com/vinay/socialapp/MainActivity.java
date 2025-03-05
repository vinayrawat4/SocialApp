package com.vinay.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vinay.socialapp.Fragment.HomeFragment;
import com.vinay.socialapp.Fragment.NotoficationsFragment;
import com.vinay.socialapp.Fragment.PostFragment;
import com.vinay.socialapp.Fragment.ProfileFragment;
import com.vinay.socialapp.Fragment.SearchFragment;
import com.vinay.socialapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseUser Currentuser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        Currentuser = auth.getCurrentUser();

        if (Currentuser==null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


       binding.bottomtab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id==R.id.homes){
                    loadFrag(new HomeFragment());
                } else if (id==R.id.notifications) {

                    loadFrag(new NotoficationsFragment());
                } else if (id==R.id.post) {
                    loadFrag(new PostFragment());
                } else if (id==R.id.search) {
                    loadFrag(new SearchFragment());
                }else {
                    loadFrag(new ProfileFragment());
                }

                return true;
           }
       });

       binding.bottomtab.setSelectedItemId(R.id.homes);
    }

    public void loadFrag(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commit();
    }


}