package com.example.babybook.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<AppointmentRequest> {

    private final Context context;
    private final List<AppointmentRequest> appointmentList;
    private final FirebaseFirestore db;


    public AppointmentAdapter(@NonNull Context context, List<AppointmentRequest> list) {
        super(context, 0, list);
        this.context = context;
        this.appointmentList = list;
        this.db = FirebaseFirestore.getInstance();
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
        tvAppointmentStatus.setText(appointment.getReason() != null
                ? "Status: " + appointment.getStatus() + appointment.getReason()
                : "Status: " + appointment.getStatus()
        );

        // Set the status text color based on the status
        if ("Accepted".equals(appointment.getStatus())) {
            tvAppointmentStatus.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
        } else if ("Declined".equals(appointment.getStatus())) {
            tvAppointmentStatus.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
        } else if ("Pending".equals(appointment.getStatus())) {
            tvAppointmentStatus.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        }

        // Check if we need to show a popup (toShowPopup == 1)
        if (appointment.getToShowPopup() == 1) {
            showPopupDialog(appointment);
        }


        return convertView;
    }

    private void showPopupDialog(final AppointmentRequest appointment) {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Appointment Accepted")
                .setMessage("Your " + appointment.getService() + " appointment on " + appointment.getDate() + " at " + appointment.getTime() + " has been accepted.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appointment.setToShowPopup(0);
                        db.collection("appointments") // Replace with your actual collection name
                                .document(appointment.getId()) // Use the documentId of the appointment
                                .update("toShowPopup", 0)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("AppointmentAdapter", "Successfully updated toShowPopup to 0 in Firestore");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("AppointmentAdapter", "Error updating toShowPopup in Firestore", e);
                                });




                        notifyDataSetChanged();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
