package com.example.babybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.model.Parent;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {

    private final List<Parent> parents;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onMessageClick(Parent parent);
    }

    public ParentAdapter(List<Parent> parents, OnItemClickListener onItemClickListener) {
        this.parents = parents;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        Parent parent = parents.get(position);
        holder.parentNameTextView.setText(parent.getFullName());
        holder.emailTextView.setText(parent.getEmail());

        holder.messageImageView.setOnClickListener(v -> onItemClickListener.onMessageClick(parent));
    }

    @Override
    public int getItemCount() {
        return parents.size();
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView parentNameTextView;
        TextView emailTextView;
        ImageView messageImageView;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            parentNameTextView = itemView.findViewById(R.id.parent_name_text_view);
            emailTextView = itemView.findViewById(R.id.parent_email_text_view);
            messageImageView = itemView.findViewById(R.id.message_icon);
        }
    }
}
