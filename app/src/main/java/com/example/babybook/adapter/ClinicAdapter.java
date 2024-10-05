package com.example.babybook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.model.Clinic;
import java.util.List;
import java.util.Map;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ClinicViewHolder> {

    private List<Clinic> clinicList;

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
        holder.clinicName.setText(clinic.getClinicName());
        holder.clinicPhoneNumber.setText(clinic.getClinicPhoneNumber());
        holder.clinicTime.setText("Open: " + clinic.getSchedStartTime() + " - " + clinic.getSchedEndTime());
        holder.clinicDay.setText(String.join(", ", clinic.getSchedDays()));


    }

    @Override
    public int getItemCount() {
        return clinicList.size();
    }

    static class ClinicViewHolder extends RecyclerView.ViewHolder {

        TextView clinicName, clinicAddress, clinicPhoneNumber, clinicTime, clinicDay;
        //ImageView clinicImg;

        public ClinicViewHolder(@NonNull View itemView) {
            super(itemView);
            clinicName = itemView.findViewById(R.id.clinic_name);
            //clinicAddress = itemView.findViewById(R.id.clinic_address);
            clinicTime = itemView.findViewById(R.id.clinic_time);
            clinicDay = itemView.findViewById(R.id.clinic_day);
            clinicPhoneNumber = itemView.findViewById(R.id.clinic_address);



        }
    }
}
