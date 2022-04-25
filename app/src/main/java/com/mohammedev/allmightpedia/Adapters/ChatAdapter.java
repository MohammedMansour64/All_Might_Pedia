package com.mohammedev.allmightpedia.Adapters;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammedev.allmightpedia.R;
import com.mohammedev.allmightpedia.data.Messages;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    List<Messages> messagesList;

    public ChatAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout , parent , false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Messages message = messagesList.get(position);

        holder.messageTxt.setText(message.getMessageText());
        holder.userNameMessageTxt.setText(message.getMessageUser());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView userNameMessageTxt , messageTxt;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameMessageTxt = itemView.findViewById(R.id.user_name_message_txt);
            messageTxt = itemView.findViewById(R.id.message);
        }
    }
}
