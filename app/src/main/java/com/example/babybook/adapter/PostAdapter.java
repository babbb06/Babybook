package com.example.babybook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy   HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Manila"));
        holder.timestamp.setText(sdf.format(new java.util.Date(post.getTimestamp())));
        holder.specialization.setText(post.getSpecialization());

        Glide.with(holder.itemView.getContext())
                .load(post.getProfileImageUrl() + "?t=" + System.currentTimeMillis()) // Force refresh
                .placeholder(R.drawable.baby_book_logo)
                .error(R.drawable.add)
                .into(holder.doctorImg);


        // Set initial visibility and number of lines
        if (post.isExpanded()) {
            holder.content.setMaxLines(Integer.MAX_VALUE);
            holder.seeMore.setText("See Less");
        } else {
            holder.content.setMaxLines(5);
            holder.seeMore.setText("See More");
        }

        // Check if the content length exceeds a certain threshold to determine visibility
        if (post.getContent().length() > 100) { // Adjust the length check as necessary
            holder.seeMore.setVisibility(View.VISIBLE); // Show "See More" if content is more than threshold
        } else {
            holder.seeMore.setVisibility(View.GONE); // Hide "See More" if content is less than or equal to threshold
        }

        // Toggle the "See More" / "See Less" functionality
        holder.seeMore.setOnClickListener(v -> {
            boolean expanded = post.isExpanded();
            post.setExpanded(!expanded); // Toggle expanded state
            notifyItemChanged(position); // Update the specific item
        });
    }



    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, content, timestamp, specialization, seeMore;
        ImageView doctorImg;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.postDoctorName);
            content = itemView.findViewById(R.id.postContent);
            timestamp = itemView.findViewById(R.id.postTimestamp);
            specialization = itemView.findViewById(R.id.postDocSpecialization);
            doctorImg = itemView.findViewById(R.id.imageView2);
            seeMore = itemView.findViewById(R.id.seeMore);


        }
    }
}
