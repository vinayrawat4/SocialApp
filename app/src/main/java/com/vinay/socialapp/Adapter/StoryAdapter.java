package com.vinay.socialapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinay.socialapp.Model.Storymodel;
import com.vinay.socialapp.R;
import com.vinay.socialapp.databinding.StoryDesignRvBinding;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {

    ArrayList<Storymodel> list;
    Context context;

    public StoryAdapter(ArrayList<Storymodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.story_design_rv,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.viewHolder holder, int position) {

        Storymodel model=list.get(position);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

      StoryDesignRvBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

           binding = StoryDesignRvBinding.bind(itemView);

        }
    }
}
