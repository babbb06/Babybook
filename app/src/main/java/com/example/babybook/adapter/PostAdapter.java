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
        holder.doctorName.setText(post.getDoctorName());
        holder.content.setText(post.getContent());
        holder.timestamp.setText(new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(post.getTimestamp()));

          }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, content, timestamp;
        ImageView doctorImg;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.postDoctorName);
            content = itemView.findViewById(R.id.postContent);
            timestamp = itemView.findViewById(R.id.postTimestamp);
            doctorImg = itemView.findViewById(R.id.imageView2);
        }
    }
}
