package com.example.babybook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.widget.ArrayAdapter;

import com.example.babybook.R;
import com.example.babybook.model.AppointmentRequest;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<AppointmentRequest> {

    private Context context;
    private List<AppointmentRequest> appointmentList;

    public AppointmentAdapter(@NonNull Context context, List<AppointmentRequest> list) {
        super(context, 0, list);
        this.context = context;
        this.appointmentList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the custom layout for each appointment
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.appointment_list_item, parent, false);
        }

        // Get the appointment at the current position
        AppointmentRequest appointment = appointmentList.get(position);

        // Populate the data into the custom layout
        TextView tvAppointmentTitle = convertView.findViewById(R.id.tvAppointmentTitle);
        TextView tvAppointmentDate = convertView.findViewById(R.id.tvAppointmentDate);
        TextView tvAppointmentTime = convertView.findViewById(R.id.tvAppointmentTime);
        TextView tvAppointmentStatus = convertView.findViewById(R.id.tvAppointmentStatus);
        TextView tvChildName = convertView.findViewById(R.id.tvChildName);

        // Set the text for each field
        tvAppointmentTitle.setText("Service: " + appointment.getService());
        tvChildName.setText("Child's Name: " + appointment.getChildFullName());
        tvAppointmentDate.setText("Date: " + appointment.getDate());
        tvAppointmentTime.setText("Time: " + appointment.getTime());
        tvAppointmentStatus.setText("Status: " + appointment.getStatus());

        // Set the status text color based on the status
        if ("Accepted".equals(appointment.getStatus())) {
            tvAppointmentStatus.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
        } else if ("Declined".equals(appointment.getStatus())) {
            tvAppointmentStatus.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
        } else if ("Pending".equals(appointment.getStatus())) {
            tvAppointmentStatus.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        }


        return convertView;
    }
}
