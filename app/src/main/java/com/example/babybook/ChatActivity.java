package com.example.babybook;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.adapter.MessageAdapter;
import com.example.babybook.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String currentUserID;
    private String receiverId;

    private FirebaseFirestore db;
    private CollectionReference messagesRef;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show the up arrow
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(""); // Set title to empty, or you can set the receiver's name here
        }

        // Initialize UI elements
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        // Setup RecyclerView and Adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Get current user ID and receiver ID from Intent
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverId = getIntent().getStringExtra("receiverId");

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        messagesRef = db.collection("chats");

        // Send button click listener
        buttonSend.setOnClickListener(v -> sendMessage());

        // Load messages
        loadMessages();

        // Fetch and display receiver's full name in the Toolbar
        fetchReceiverFullName(receiverId);
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText)) {
            // Fetch the sender's fullName from doctorUsers first, then from parentUsers if needed
            fetchSenderFullName(currentUserID, senderName -> {
                if (senderName == null) {
                    senderName = "Unknown"; // Default value if fullName is null
                }

                // Create Message object
                Message message = new Message(currentUserID, receiverId, messageText, new Date(), senderName);

                // Add message to Firestore
                messagesRef.add(message).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Add the sent message to the list
                        messageList.add(message);
                        messageAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
                        editTextMessage.setText("");
                    } else {
                        Log.e("ChatActivity", "Failed to send message: " + task.getException());
                        Toast.makeText(ChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void fetchSenderFullName(String userId, final OnFullNameFetched callback) {
        // Try fetching from doctorUsers collection
        db.collection("doctorUsers").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String fullName = document.getString("fullName");
                            callback.onFullNameFetched(fullName);
                        } else {
                            // If not found in doctorUsers, try parentUsers
                            fetchFromParentUsers(userId, callback);
                        }
                    } else {
                        Log.e("ChatActivity", "Failed to fetch from doctorUsers: " + task.getException());
                        fetchFromParentUsers(userId, callback);
                    }
                });
    }

    private void fetchFromParentUsers(String userId, final OnFullNameFetched callback) {
        db.collection("parentUsers").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String fullName = document.getString("fullName");
                            callback.onFullNameFetched(fullName);
                        } else {
                            callback.onFullNameFetched(null); // If not found in parentUsers
                        }
                    } else {
                        Log.e("ChatActivity", "Failed to fetch from parentUsers: " + task.getException());
                        callback.onFullNameFetched(null);
                    }
                });
    }

    private void fetchReceiverFullName(String userId) {
        // Try fetching from doctorUsers collection
        db.collection("doctorUsers").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String fullName = document.getString("fullName");
                            if (fullName != null) {
                                toolbarTitle.setText(fullName);
                            }
                        } else {
                            // If not found in doctorUsers, try parentUsers
                            fetchReceiverFromParentUsers(userId);
                        }
                    } else {
                        Log.e("ChatActivity", "Failed to fetch from doctorUsers: " + task.getException());
                        fetchReceiverFromParentUsers(userId);
                    }
                });
    }

    private void fetchReceiverFromParentUsers(String userId) {
        db.collection("parentUsers").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String fullName = document.getString("fullName");
                            if (fullName != null) {
                                toolbarTitle.setText(fullName);
                            }
                        } else {
                            toolbarTitle.setText("Unknown"); // If not found in parentUsers
                        }
                    } else {
                        Log.e("ChatActivity", "Failed to fetch from parentUsers: " + task.getException());
                        toolbarTitle.setText("Unknown");
                    }
                });
    }

    private void loadMessages() {
        messagesRef.whereIn("senderId", List.of(currentUserID, receiverId))
                .whereIn("receiverId", List.of(currentUserID, receiverId))
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    messageList.clear();
                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                    messageAdapter.notifyDataSetChanged();
                    recyclerViewMessages.smoothScrollToPosition(messageList.size());
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the up arrow click here
            finish(); // Close the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    interface OnFullNameFetched {
        void onFullNameFetched(@Nullable String fullName);
    }
}
