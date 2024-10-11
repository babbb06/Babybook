package com.example.babybook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.model.Doctor;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DoctorAdapter2 extends RecyclerView.Adapter<DoctorAdapter2.DoctorViewHolder> {

    private List<Doctor> doctorList;
    private OnDoctorClickListener onDoctorClickListener;
    private FirebaseFirestore db;

    public DoctorAdapter2(List<Doctor> doctorList, OnDoctorClickListener onDoctorClickListener) {
        this.doctorList = doctorList;
        this.onDoctorClickListener = onDoctorClickListener;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor2, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.fullNameTextView.setText(doctor.getFullName());
        holder.specializationTextView.setText(doctor.getSpecialization());
        holder.clinicAddressTextView.setText(doctor.getClinicAddress());

        // Fetch message count for the specific doctor
        fetchMessageCountForDoctor(doctor.getId(), holder.messageCountTextView);

        holder.itemView.setOnClickListener(v -> {
            if (onDoctorClickListener != null) {
                onDoctorClickListener.onDoctorClick(doctor);
            }
        });

        holder.messageImageView.setOnClickListener(v -> {
            if (onDoctorClickListener != null) {
                onDoctorClickListener.onDoctorClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    private void fetchMessageCountForDoctor(String doctorId, TextView messageCountTextView) {
        // Query Firestore for unread messages count for this specific doctor
        db.collection("messages")
                .whereEqualTo("receiverId", doctorId)
                .whereEqualTo("isRead", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int unreadCount = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            unreadCount++;
                        }

                        if (unreadCount > 0) {
                            messageCountTextView.setText(String.valueOf(unreadCount));
                            messageCountTextView.setVisibility(View.VISIBLE);
                        } else {
                            messageCountTextView.setVisibility(View.GONE);
                        }
                    } else {
                        messageCountTextView.setVisibility(View.GONE);
                    }
                });
    }

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView;
        TextView specializationTextView;
        TextView clinicAddressTextView;
        ImageView messageImageView;
        TextView messageCountTextView;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.textViewFullName);
            specializationTextView = itemView.findViewById(R.id.textViewSpecialization);
            clinicAddressTextView = itemView.findViewById(R.id.textViewClinicAddress);
            messageImageView = itemView.findViewById(R.id.imageViewMessage);
            messageCountTextView = itemView.findViewById(R.id.textViewMessageCount);
        }
    }
}
