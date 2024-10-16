package com.example.babybook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.R;
import com.example.babybook.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getMessage());
        holder.senderNameTextView.setText(message.getSenderName());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());
        holder.timestampTextView.setText(sdf.format(message.getTimestamp()));

        if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            // Sent messages
            holder.messageTextView.setBackgroundResource(R.drawable.message_sent_bg);

            RelativeLayout.LayoutParams messageParams = (RelativeLayout.LayoutParams) holder.messageTextView.getLayoutParams();
            messageParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            messageParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            holder.messageTextView.setLayoutParams(messageParams);

            RelativeLayout.LayoutParams timestampParams = (RelativeLayout.LayoutParams) holder.timestampTextView.getLayoutParams();
            timestampParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            timestampParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
            holder.timestampTextView.setLayoutParams(timestampParams);
        } else {
            // Received messages
            holder.messageTextView.setBackgroundResource(R.drawable.message_receive_bg);

            RelativeLayout.LayoutParams messageParams = (RelativeLayout.LayoutParams) holder.messageTextView.getLayoutParams();
            messageParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            messageParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            holder.messageTextView.setLayoutParams(messageParams);

            RelativeLayout.LayoutParams timestampParams = (RelativeLayout.LayoutParams) holder.timestampTextView.getLayoutParams();
            timestampParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            timestampParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            holder.timestampTextView.setLayoutParams(timestampParams);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderNameTextView;
        TextView timestampTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}