package com.example.babybook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.babybook.ChildDetailsActivity;
import com.example.babybook.ChildDetailsParentActivity;

import com.example.babybook.R;
import com.example.babybook.model.HealthRecord;

import java.util.List;

public class HealthRecordParentAdapter extends RecyclerView.Adapter<HealthRecordParentAdapter.ViewHolder> {

    private final List<HealthRecord> healthRecords;
    private final Context context;

    // Constructor with context and list
    public HealthRecordParentAdapter(Context context, List<HealthRecord> healthRecords) {
        this.context = context;
        this.healthRecords = healthRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthRecord record = healthRecords.get(position);
        holder.textViewChildName.setText(record.getFirstName() + " " + record.getLastName());
        holder.textViewChildAddress.setText("Address: "+record.getAddress());
        holder.textViewChildSex.setText("Sex: "+record.getSex());
        // Handle card click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChildDetailsParentActivity.class);
            intent.putExtra("CHILD_ID", record.getId()); // Pass the child ID
            intent.putExtra("FirstName", record.getFirstName()); // Pass the child ID
            intent.putExtra("LastName", record.getLastName());
            intent.putExtra("LastName", record.getLastName());
            intent.putExtra("Sex", record.getSex());
            intent.putExtra("Birthday", record.getBirthDay());
            context.startActivity(intent);
        });

        // Handle the menu button click
        holder.imageViewMenu.setOnClickListener(v -> {
            // Show menu or handle click event here
        });
    }

    @Override
    public int getItemCount() {
        return healthRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewChildName,textViewChildSex,textViewChildAddress;
        ImageView imageViewMenu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewChildName = itemView.findViewById(R.id.textViewChildName);
            textViewChildSex = itemView.findViewById(R.id.textViewChildSex);
            textViewChildAddress = itemView.findViewById(R.id.textViewChildAddress);
            imageViewMenu = itemView.findViewById(R.id.imageViewMenu);
        }
    }

    // Define an interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(HealthRecord record);
    }
}
