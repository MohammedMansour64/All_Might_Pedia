package com.mohammedev.allmightpedia.ui.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.mohammedev.allmightpedia.Activities.LoginActivity;
import com.mohammedev.allmightpedia.Adapters.ChatAdapter;
import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Messages;
import com.mohammedev.allmightpedia.utils.CurrentUserData;
import com.mohammedev.allmightpedia.utils.ViewSpaces;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    ArrayList<Messages> messagesArrayList = new ArrayList<>();
    RecyclerView messagesRecyclerView;
    EditText messageEdt;
    ImageButton sendBtn;
    ChatAdapter chatAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        messagesRecyclerView = view.findViewById(R.id.messages_recyclerView);
        messageEdt = view.findViewById(R.id.message_edt);
        sendBtn = view.findViewById(R.id.send_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        getMessages();

        return view;
    }

    private void getMessages() {
        Query query = FirebaseDatabase.getInstance().getReference().child("chats");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot messages : snapshot.getChildren()){
                    Messages message = messages.getValue(Messages.class);
                    messagesArrayList.add(message);
                }
                chatAdapter = new ChatAdapter(messagesArrayList);
                messagesRecyclerView.setAdapter(chatAdapter);
                messagesRecyclerView.addItemDecoration(new ViewSpaces(10));
                messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(){
        if (CurrentUserData.USER_DATA != null ){
            if (!messageEdt.getText().toString().equals("")){
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chats");
                String messageID = databaseReference.push().getKey();

                Messages newMessage = new Messages(messageEdt.getText().toString() , CurrentUserData.USER_DATA.getUserName());

                databaseReference.child(messageID).setValue(newMessage);
                messagesArrayList.clear();
                getMessages();
                chatAdapter.notifyDataSetChanged();
                messageEdt.setText("");
            }
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.sign_in_required)
                    .setMessage(R.string.sign_in_required_to_send_messages)
                    .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent signInIntent = new Intent(getActivity() , LoginActivity.class);
                            startActivity(signInIntent);
                        }
                    }).show();

            alertDialog.create();
        }

    }
}