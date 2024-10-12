
package com.example.babybook.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.model.Doctor;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private final List<Doctor> doctors;
    private final OnDoctorClickListener onDoctorClickListener;

    public DoctorAdapter(List<Doctor> doctors, OnDoctorClickListener onDoctorClickListener) {
        this.doctors = doctors;
        this.onDoctorClickListener = onDoctorClickListener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.fullNameTextView.setText(doctor.getFullName());
        holder.specializationTextView.setText(doctor.getSpecialization());
        holder.clinicAddressTextView.setText(doctor.getClinicAddress());

        if(doctor.getSchedStartTime() == null && doctor.getSchedEndTime() == null){
            holder.schedTimeTextView.setText("No scheduled time.");
        } else{
            holder.schedTimeTextView.setText(doctor.getSchedStartTime() + " - " + doctor.getSchedEndTime());
        }

        List<String> schedDays = doctor.getSchedDays();
        if(schedDays != null) {
            holder.schedDaysTextview.setText(String.join(", ", schedDays));
        } else {
            holder.schedDaysTextview.setText("No scheduled days. ");
        }


        holder.bookAppointmentButton.setOnClickListener(v -> {
            if (onDoctorClickListener != null) {
                onDoctorClickListener.onDoctorClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView;
        TextView specializationTextView;
        TextView clinicAddressTextView;
        TextView schedTimeTextView, schedDaysTextview;
        Button bookAppointmentButton;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.full_name_text_view);
            specializationTextView = itemView.findViewById(R.id.specialization_text_view);
            clinicAddressTextView = itemView.findViewById(R.id.clinic_address_text_view);
            bookAppointmentButton = itemView.findViewById(R.id.book_appointment_button);
            schedTimeTextView = itemView.findViewById(R.id.tvSchedTime);
            schedDaysTextview = itemView.findViewById(R.id.tvSchedDays);
        }
    }
}


