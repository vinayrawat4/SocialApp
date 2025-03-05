package com.vinay.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vinay.socialapp.Adapter.CommentAdapter;
import com.vinay.socialapp.Model.Comment;
import com.vinay.socialapp.Model.Notifications;
import com.vinay.socialapp.Model.PostModel;
import com.vinay.socialapp.Model.User;
import com.vinay.socialapp.databinding.ActivityCommentBinding;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    Intent intent;
    String postId;
    String postedBy;

    FirebaseAuth auth;
    FirebaseDatabase database;

    ArrayList<Comment> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        setSupportActionBar(binding.toolbarc);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PostModel model = snapshot.getValue(PostModel.class);
                        Picasso.get()
                                .load(model.getPostImage())
                                .placeholder(R.drawable.baseline_image)
                                .into(binding.pic1);
                        binding.postDesc.setText(model.getPostDescription());
                        binding.like1.setText(model.getPostLike() + "");
                        binding.comment1.setText(model.getCommentcount() + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Users").child(postedBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.baseline_image)
                                .into(binding.profileImage1);
                        binding.name1.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.commentpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                comment.setCommentBody(binding.commentbody.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());
                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentcount")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int commentcount = 0;
                                                if (snapshot.exists()) {
                                                    commentcount = snapshot.getValue(Integer.class);
                                                }
                                                database.getReference()
                                                        .child("posts")
                                                        .child(postId)
                                                        .child("commentcount")
                                                        .setValue(commentcount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                binding.comment1.setText("");
                                                                Toast.makeText(CommentActivity.this, "Done", Toast.LENGTH_SHORT).show();

                                                                Notifications notifications = new Notifications();

                                                                notifications.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                notifications.setNotificationAt(new Date().getTime());
                                                                notifications.setPostID(postId);
                                                                notifications.setPostedBy(postedBy);
                                                                notifications.setType("comment");

                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("notification")
                                                                        .child(postedBy)
                                                                        .push()
                                                                        .setValue(notifications);

                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        });

            }
        });

        CommentAdapter adapter = new CommentAdapter(this,list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentrv.setLayoutManager(layoutManager);
        binding.commentrv.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            list.add(comment);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}