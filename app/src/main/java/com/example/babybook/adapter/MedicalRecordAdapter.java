package com.example.babybook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.ViewMedicalRecord;
import com.example.babybook.model.HealthChecklist;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private List<HealthChecklist> healthChecklists;
    private final Context context;

    // Constructor updated to accept Context
    public MedicalRecordAdapter(List<HealthChecklist> healthChecklists, Context context) {
        this.context = context;
        this.healthChecklists = healthChecklists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for the individual medical record
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthChecklist checklist = healthChecklists.get(position);

        // Ensure you have unique data for each item
        holder.name.setText(checklist.getFirstName() + " " + checklist.getLastName());
        holder.clinic_day.setText(checklist.getDate());

        // Handling item click to pass all data to ViewMedicalRecord
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewMedicalRecord.class);
            intent.putExtra("childId", checklist.getChildId());
            intent.putExtra("FirstName", checklist.getFirstName());
            intent.putExtra("LastName", checklist.getLastName());
            intent.putExtra("dateToday", checklist.getDate()); // Send the specific record data
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return healthChecklists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, clinic_day;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);  // Display name (first + last)
            clinic_day = itemView.findViewById(R.id.clinic_day);  // Display the date of the clinic visit
        }
    }
}
