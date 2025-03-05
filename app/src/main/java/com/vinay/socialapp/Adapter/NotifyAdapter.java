package com.vinay.socialapp.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vinay.socialapp.Model.Notifications;
import com.vinay.socialapp.Model.User;
import com.vinay.socialapp.R;
import com.vinay.socialapp.databinding.NotifyrvsampleBinding;

import java.util.ArrayList;
import java.util.Date;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.viewHolder> {

    ArrayList<Notifications> list;
    Context context;

    public NotifyAdapter(ArrayList<Notifications> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public NotifyAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notifyrvsample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyAdapter.viewHolder holder, int position) {
        Notifications model = list.get(position);
        String type = model.getType();

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.baseline_image)
                                .into(holder.binding.profilenotify);

                        if(type.equals("like")){
                            holder.binding.namenotify.setText(user.getName()+"liked your post");
                        } else if (type.equals("comment")) {
                            holder.binding.namenotify.setText(user.getName()+"commented on your post");
                        }else {
                            holder.binding.namenotify.setText(user.getName()+"start following you");
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

        NotifyrvsampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = NotifyrvsampleBinding.bind(itemView);

        }
    }
}
