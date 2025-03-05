package com.vinay.socialapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vinay.socialapp.Model.FollowModel;
import com.vinay.socialapp.Model.Notifications;
import com.vinay.socialapp.Model.User;
import com.vinay.socialapp.R;
import com.vinay.socialapp.databinding.SearchSampleBinding;

import java.util.ArrayList;
import java.util.Date;

public class UserFollowAdapter extends RecyclerView.Adapter<UserFollowAdapter.viewHolder> {

    Context context;
    ArrayList<User> list;

    public UserFollowAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserFollowAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFollowAdapter.viewHolder holder, int position) {
            User user = list.get(position);

        Picasso.get()
                .load(user.getProfile())
                .placeholder(R.drawable.baseline_image)
                .into(holder.binding.followprofileImage);
        holder.binding.followname.setText(user.getName());
        holder.binding.followprof.setText(user.getProfession());

        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserID())
                        .child("followers")
                                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                        holder.binding.followid.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_btn));
                        holder.binding.followid.setText("Following");
                        holder.binding.followid.setTextColor(context.getResources().getColor(R.color.black));
                        holder.binding.followid.setEnabled(false);
                        Toast.makeText(context,"You followed" + user.getName(), Toast.LENGTH_SHORT).show();
                        } else
                        {
                            holder.binding.followid.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FollowModel followModel = new FollowModel();
                                    followModel.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                    followModel.setFollowedAt(new Date().getTime());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followers")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(followModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("Users")
                                                            .child(user.getUserID())
                                                            .child("followersCount")
                                                            .setValue(user.getFollowersCount() +1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.followid.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_btn));
                                                                    holder.binding.followid.setText("Following");
                                                                    holder.binding.followid.setTextColor(context.getResources().getColor(R.color.black));
                                                                    holder.binding.followid.setEnabled(false);
                                                                    Toast.makeText(context,"You followed" + user.getName(), Toast.LENGTH_SHORT).show();

                                                                    Notifications notifications = new Notifications();
                                                                    notifications.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notifications.setNotificationAt(new Date().getTime());
                                                                    notifications.setType("follow");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(user.getUserID())
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




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SearchSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding= SearchSampleBinding.bind(itemView);
        }
    }
}
