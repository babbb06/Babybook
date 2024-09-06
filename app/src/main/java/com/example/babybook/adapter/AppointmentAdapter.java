package com.example.babybook.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.babybook.R;
import com.example.babybook.model.AppointmentRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppointmentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AppointmentRequest> appointments;
    private Handler handler = new Handler();

    public AppointmentAdapter(Context context, ArrayList<AppointmentRequest> appointments) {
        this.context = context;
        this.appointments = appointments;

        handler.postDelayed(updateTimeRunnable, 60000);
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
            handler.postDelayed(this, 60000); // Update every minute
        }
    };

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return appointments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        }

        TextView childNameTextView = convertView.findViewById(R.id.child_name_text_view);
        TextView serviceTextView = convertView.findViewById(R.id.service_text_view);
        TextView dateTextView = convertView.findViewById(R.id.date_text_view);
        TextView timeTextView = convertView.findViewById(R.id.time_text_view);
        TextView statusTextView = convertView.findViewById(R.id.status_text_view);
        TextView elapsedTimeTextView = convertView.findViewById(R.id.elapsed_time_text_view);

        AppointmentRequest appointment = appointments.get(position);

        childNameTextView.setText(appointment.getChildName());
        serviceTextView.setText(appointment.getService());
        dateTextView.setText(appointment.getDate());
        timeTextView.setText(appointment.getTime());
        statusTextView.setText(appointment.getStatus());

        long elapsedTime = new Date().getTime() - appointment.getBookingTime().getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
        long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);

        elapsedTimeTextView.setText("Booked " + hours + " hours and " + minutes % 60 + " minutes ago");

        return convertView;
    }
}
