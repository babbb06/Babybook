package com.example.babybook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.ViewMedicalRecord;
import com.example.babybook.model.HealthChecklist;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private final List<HealthChecklist> healthChecklists;
    private final Context context;

    // Constructor updated to accept Context
    public MedicalRecordAdapter(List<HealthChecklist> healthChecklists, Context context) {
        this.healthChecklists = healthChecklists;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HealthChecklist checklist = healthChecklists.get(position);

        // Displaying data from the checklist
        holder.name.setText(checklist.getFirstName() + " " + checklist.getLastName());
        holder.clinic_day.setText(checklist.getDateToday());  // Display the dateToday from HealthChecklist

        // Handling item click to pass all data to ViewMedicalRecord
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewMedicalRecord.class);
            intent.putExtra("childId", checklist.getChildId());
            intent.putExtra("FirstName",  checklist.getFirstName());
            intent.putExtra("LastName",  checklist.getLastName());
            intent.putExtra("dateToday", checklist.getDateToday()); // Send the specific record data
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return healthChecklists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, clinic_day, service, status, address;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            clinic_day = itemView.findViewById(R.id.clinic_day);

        }
    }
}
