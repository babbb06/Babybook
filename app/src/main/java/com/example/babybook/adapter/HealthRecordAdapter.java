package com.example.babybook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.ChildDetailsActivity;
import com.example.babybook.R;
import com.example.babybook.model.HealthRecord;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HealthRecordAdapter extends RecyclerView.Adapter<HealthRecordAdapter.ViewHolder> {

    private final List<HealthRecord> healthRecords;
    private final Context context;

    // Constructor with context and list
    public HealthRecordAdapter(Context context, List<HealthRecord> healthRecords) {
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
        holder.textViewChildAddress.setText(record.getAddress());
        holder.textViewChildSex.setText(record.getSex());

        // Handle card click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChildDetailsActivity.class);
            intent.putExtra("CHILD_ID", record.getId());
            intent.putExtra("FirstName", record.getFirstName()); // Pass the child ID
            intent.putExtra("LastName", record.getLastName());
            intent.putExtra("Sex", record.getSex());
            intent.putExtra("Address", record.getAddress());

            context.startActivity(intent);
        });

        // Handle the menu button click
        holder.imageViewMenu.setOnClickListener(v -> {
            // Show popup menu
            showPopupMenu(holder.imageViewMenu, record);
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

    private void showPopupMenu(View view, HealthRecord record) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_health_record, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_delete_medical_record:
                    // Handle edit action
                    deleteRecord(record);
                    return true;

                default:
                    return false;
            }
        });

        popupMenu.show();
    }


    private void deleteRecord(HealthRecord record) {
        // Update the record's status to "donevaccine" in the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("healthRecords").document(record.getId())
                .update("status", "donevaccine")
                .addOnSuccessListener(aVoid -> {
                    // If successful, update the record in the list
                    int position = healthRecords.indexOf(record);
                    if (position != -1) {
                        healthRecords.get(position).setStatus("donevaccine");
                        notifyItemChanged(position);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(context, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




    // Define an interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(HealthRecord record);
    }


}
