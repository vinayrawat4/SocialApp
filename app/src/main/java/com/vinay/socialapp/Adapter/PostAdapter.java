package com.vinay.socialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vinay.socialapp.CommentActivity;
import com.vinay.socialapp.Model.Notifications;
import com.vinay.socialapp.Model.PostModel;
import com.vinay.socialapp.Model.User;
import com.vinay.socialapp.R;
import com.vinay.socialapp.databinding.DashboardRvBinding;

import java.util.ArrayList;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<PostModel> list;
    Context context;

    public PostAdapter(ArrayList<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.viewHolder holder, int position) {
        PostModel model = list.get(position);
        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.baseline_image)
                .into(holder.binding.pic1);
        holder.binding.like1.setText(model.getPostLike()+"");
        holder.binding.comment1.setText(model.getCommentcount()+"" +
                "");
        String description = model.getPostDescription();
        if (description.equals("")){
            holder.binding.postDesc.setVisibility(View.GONE);
        }else {
            holder.binding.postDesc.setText(model.getPostDescription());
            holder.binding.postDesc.setVisibility(View.VISIBLE);
        }
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.baseline_image)
                                .into(holder.binding.profileImage1);
                        holder.binding.name1.setText(user.getName());
                        holder.binding.name2.setText(user.getProfession());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                        .child("likes")
                                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.binding.like1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_hert,0,0,0);
                        }else {
                            holder.binding.like1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(model.getPostId())
                                                            .child("postLikes")
                                                            .setValue(model.getPostLike()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_hert,0,0,0);

                                                                    Notifications notifications = new Notifications();
                                                                    notifications.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notifications.setNotificationAt(new Date().getTime());
                                                                    notifications.setPostID(model.getPostId());
                                                                    notifications.setPostedBy(model.getPostedBy());
                                                                    notifications.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(model.getPostedBy())
                                                                            .push()
                                                                            .setValue(notifications);

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.comment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy",model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        DashboardRvBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DashboardRvBinding.bind(itemView);

        }
    }
}
