package com.example.babybook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babybook.R;
import com.example.babybook.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.doctorName.setText(post.getDoctorName()); // Set the doctor's name
        holder.content.setText(post.getContent()); // Set the content of the post

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy   HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Manila")); // Set timezone to PHT
        holder.timestamp.setText(sdf.format(new java.util.Date(post.getTimestamp())));
        holder.specialization.setText(post.getSpecialization());


        // Load the doctor's profile image using Glide
        Glide.with(holder.itemView.getContext())
                .load(post.getProfileImageUrl()) // Load the profile image URL
                .placeholder(R.drawable.baby_book_logo) // Optional: Add a placeholder image
                .error(R.drawable.add) // Optional: Add an error image in case of failure
                .into(holder.doctorImg); // Set the image into the ImageView
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, content, timestamp, specialization;
        ImageView doctorImg;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.postDoctorName);
            content = itemView.findViewById(R.id.postContent);
            timestamp = itemView.findViewById(R.id.postTimestamp);
            specialization = itemView.findViewById(R.id.postDocSpecialization);
            doctorImg = itemView.findViewById(R.id.imageView2);
        }
    }
}
