package com.example.babybook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.model.AppointmentRequest;

import java.util.List;

public class AppointmentRequestAdapter extends RecyclerView.Adapter<AppointmentRequestAdapter.ViewHolder> {

    private final List<AppointmentRequest> appointmentRequests;
    private final OnItemClickListener listener;
    private final boolean showButtons; // Flag to show/hide buttons

    public AppointmentRequestAdapter(List<AppointmentRequest> appointmentRequests, OnItemClickListener listener, boolean showButtons) {
        this.appointmentRequests = appointmentRequests;
        this.listener = listener;
        this.showButtons = showButtons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentRequest request = appointmentRequests.get(position);

        holder.textViewChildName.setText("Child's Name: " + request.getChildFullName());
        holder.textViewService.setText("Service: " + request.getService());
        holder.textViewDate.setText("Date: " + request.getDate());
        holder.textViewTime.setText("Time: " + request.getTime());
        holder.textViewStatus.setText("Status: " + request.getStatus());
        // Set the status text color based on the status
        int statusColor;
        switch (request.getStatus()) {
            case "Accepted":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark);
                break;
            case "Declined":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark);
                break;
            case "Pending":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black);
                break;
            default:
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black); // Default color if status is unknown
                break;
        }
        holder.textViewStatus.setTextColor(statusColor);

        if (showButtons) {
            holder.buttonAccept.setVisibility(View.VISIBLE);
            holder.buttonDecline.setVisibility(View.VISIBLE);

            holder.buttonAccept.setOnClickListener(v -> listener.onAcceptClick(request));
            holder.buttonDecline.setOnClickListener(v -> listener.onDeclineClick(request));
        } else {
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonDecline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentRequests.size();
    }

    public interface OnItemClickListener {
        void onAcceptClick(AppointmentRequest request);
        void onDeclineClick(AppointmentRequest request);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewChildName;
        public final TextView textViewService;
        public final TextView textViewDate;
        public final TextView textViewTime;
        public final TextView textViewStatus;
        public final Button buttonAccept;
        public final Button buttonDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewChildName = itemView.findViewById(R.id.text_view_child_name);
            textViewService = itemView.findViewById(R.id.text_view_service);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            textViewStatus = itemView.findViewById(R.id.text_view_status);
            buttonAccept = itemView.findViewById(R.id.button_accept);
            buttonDecline = itemView.findViewById(R.id.button_decline);
        }
    }
}
