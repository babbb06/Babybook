package com.example.babybook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.ViewMedicalRecord;
import com.example.babybook.model.HealthChecklist;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private List<HealthChecklist> healthChecklists;
    private String childId,FirstName,LastName,dateToday, name,clinic_day; // Store childId
    private Context context; // Store the context

    // Updated constructor to accept Context
    public MedicalRecordAdapter(List<HealthChecklist> healthChecklists, String childId,String FirstName,String LastName,String dateToday, Context context) {
        this.healthChecklists = healthChecklists;
        this.childId = childId;
        this.FirstName = FirstName;
        this.LastName = LastName;

        this.context = context; // Set context
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HealthChecklist checklist = healthChecklists.get(position);
        holder.name.setText(FirstName +  " " + LastName);
        holder.clinic_day.setText(dateToday);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewMedicalRecord.class); // Use the stored context
            intent.putExtra("childId", childId); // Pass the specific child's ID
            intent.putExtra("FirstName", FirstName);
            context.startActivity(intent); // Start the activity using the context
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
            name = itemView.findViewById(R.id.name);
            clinic_day = itemView.findViewById(R.id.clinic_day);
        }
    }
}
