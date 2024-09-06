package com.example.babybook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.textViewDetails.setText(request.toString()); // Use the toString() method for formatted details

        // Show or hide buttons based on the flag
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
        public final TextView textViewDetails;
        public final Button buttonAccept;
        public final Button buttonDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDetails = itemView.findViewById(R.id.text_view_details);
            buttonAccept = itemView.findViewById(R.id.button_accept);
            buttonDecline = itemView.findViewById(R.id.button_decline);
        }
    }
}
