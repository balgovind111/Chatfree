package com.example.chatfreeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatfreeapp.databinding.ActivityChatactivityBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class chatactivity extends AppCompatActivity {
    MessageAdapter adapter;
    ArrayList<Message> messages;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;

    ActivityChatactivityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages=new ArrayList<>();
        adapter=new MessageAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView2.setAdapter(adapter);

        String name=getIntent().getStringExtra("name");
        String receiverUid =getIntent().getStringExtra("uid");
        String senderUid= FirebaseAuth.getInstance().getUid();



        senderRoom=senderUid+receiverUid;
        receiverRoom=receiverUid+senderUid;
        database=FirebaseDatabase.getInstance();

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Message message=snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageTxt = binding.editTextTextPersonName3.getText().toString();
                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());
                binding.editTextTextPersonName3.setText("");



                    String randomKey = database.getReference().push().getKey();
                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                    lastMsgObj.put("lastMsg", message.getMessage());
                    lastMsgObj.put("lastMsgTime", date.getTime());

                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(randomKey)
                                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                        }
                    });
                }

        });

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}