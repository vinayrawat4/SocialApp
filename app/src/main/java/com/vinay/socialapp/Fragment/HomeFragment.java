package com.vinay.socialapp.Fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vinay.socialapp.Adapter.PostAdapter;
import com.vinay.socialapp.Adapter.StoryAdapter;
import com.vinay.socialapp.Model.PostModel;
import com.vinay.socialapp.Model.Storymodel;
import com.vinay.socialapp.Model.UserStory;
import com.vinay.socialapp.R;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    RecyclerView rv,dashboardrv;
    ArrayList<Storymodel> list;

    ArrayList<PostModel> dashlist;

    FirebaseDatabase database;
    FirebaseAuth auth;

    FirebaseStorage storage;

    ImageView addStoryImage;

    ActivityResultLauncher<String> gallery;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        rv = view.findViewById(R.id.recylerhome);

        list = new ArrayList<>();


        StoryAdapter adapter = new StoryAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);

        database.getReference()
                .child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot storysnapshot:snapshot.getChildren()){
                                Storymodel storymodel = new Storymodel();
                                storymodel.setStoryBy(snapshot.getKey());
                                storymodel.setStoryAt(storysnapshot.child("postedBy").getValue(Long.class));

                                ArrayList<UserStory> stories = new ArrayList<>();
                                for (DataSnapshot snapshot1 : storysnapshot.child("userStories").getChildren()){
                                    UserStory userStory = snapshot1.getValue(UserStory.class);
                                    stories.add(userStory);
                                }

                                storymodel.setStoriesList(stories);
                                list.add(storymodel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        dashboardrv = view.findViewById(R.id.dashboardrecylerview);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        dashboardrv.addItemDecoration(divider);
        dashlist = new ArrayList<>();

        PostAdapter adapter1 = new PostAdapter(dashlist,getContext());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        dashboardrv.setLayoutManager(linearLayoutManager1);
        dashboardrv.setNestedScrollingEnabled(false);
        dashboardrv.setAdapter(adapter1);


        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dashlist.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    PostModel model = dataSnapshot.getValue(PostModel.class);
                    model.setPostId(dataSnapshot.getKey());
                    dashlist.add(model);
                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStoryImage = view.findViewById(R.id.storyimg);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        gallery.launch("image/*");

            }
        });

        gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                addStoryImage.setImageURI(result);

                StorageReference reference= storage.getReference()
                        .child("stories")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime()+"");
                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Storymodel storymodel = new Storymodel();
                                storymodel.setStoryAt(new Date().getTime());
                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("postedBy")
                                        .setValue(storymodel.getStoryAt())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStory story = new UserStory(uri.toString(),storymodel.getStoryAt());
                                                database.getReference()
                                                        .child("stories")
                                                        .child(FirebaseAuth.getInstance().getUid())
                                                        .child("userStories")
                                                        .push()
                                                        .setValue(story);


                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });


        return  view;
    }
}