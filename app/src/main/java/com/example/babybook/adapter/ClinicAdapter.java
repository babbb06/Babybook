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
import com.example.babybook.model.Clinic;
import com.example.babybook.ClinicDetailsActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ClinicViewHolder> {

    private final List<Clinic> clinicList;

    public ClinicAdapter(List<Clinic> clinicList) {
        this.clinicList = clinicList;
    }

    @NonNull
    @Override
    public ClinicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clinic_view, parent, false);
        return new ClinicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClinicViewHolder holder, int position) {
        Clinic clinic = clinicList.get(position);
        holder.bind(clinic, holder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return clinicList.size();
    }

    static class ClinicViewHolder extends RecyclerView.ViewHolder {

        TextView clinicName, clinicPhoneNumber, clinicTime, clinicDay, clinicVaccines;

        public ClinicViewHolder(@NonNull View itemView) {
            super(itemView);
            clinicName = itemView.findViewById(R.id.clinic_name);
            clinicTime = itemView.findViewById(R.id.clinic_time);
            clinicDay = itemView.findViewById(R.id.clinic_day);
            clinicPhoneNumber = itemView.findViewById(R.id.clinic_address);
            clinicVaccines = itemView.findViewById(R.id.vaccines);

        }

        public void bind(Clinic clinic, Context context) {
            clinicName.setText(clinic.getClinicName());
            clinicPhoneNumber.setText(clinic.getClinicPhoneNumber());
            clinicTime.setText("Open: " + clinic.getSchedStartTime() + " - " + clinic.getSchedEndTime());
            clinicDay.setText(String.join(", ", clinic.getSchedDays()));

            // Retrieve vaccines and format the string
            StringBuilder vaccinesDisplay = new StringBuilder();
            for (Map.Entry<String, Integer> entry : clinic.getVaccines().entrySet()) {
                vaccinesDisplay.append(entry.getKey())  // Vaccine name
                        //.append(": ")
                        //.append(entry.getValue())  // Quantity
                        .append("\n"); // New line for each vaccine
            }

            // Remove the last newline character if any
            if (vaccinesDisplay.length() > 0) {
                vaccinesDisplay.setLength(vaccinesDisplay.length() - 1);
            }

            clinicVaccines.setText(vaccinesDisplay.toString());


            // Set the click listener for the item
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ClinicDetailsActivity.class);
                // Pass all necessary clinic data as extras
                intent.putExtra("clinicId", clinic.getClinicId());
                intent.putExtra("clinicName", clinic.getClinicName());
                intent.putExtra("clinicPhoneNumber", clinic.getClinicPhoneNumber());
                intent.putExtra("clinicProfileUrl", clinic.getClinicProfileUrl());
                intent.putStringArrayListExtra("schedDays", new ArrayList<>(clinic.getSchedDays()));
                intent.putExtra("schedStartTime", clinic.getSchedStartTime());
                intent.putExtra("schedEndTime", clinic.getSchedEndTime());
                intent.putExtra("doctorId", clinic.getDoctorId());
                intent.putExtra("doctorName", clinic.getDoctorName());
                intent.putExtra("latitude", clinic.getLatitude());
                intent.putExtra("longitude", clinic.getLongitude());
                intent.putExtra("clinicAddress", clinic.getClinicAddress());
                intent.putExtra("timestamp", clinic.getTimestamp());
                intent.putExtra("profileImageUrl", clinic.getProfileImageUrl());
                intent.putExtra("specialization", clinic.getSpecialization());
                intent.putExtra("vaccines", (Serializable) clinic.getVaccines());

                context.startActivity(intent);
            });
        }
    }
}
