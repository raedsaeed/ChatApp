package com.example.raed.chatapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Raed on 17/10/2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>{
    private static final String TAG = "MessageListAdapter";
    private List<Message> list;

    public MessageListAdapter(List<Message> list) {
        Log.d(TAG, "MessageListAdapter: Constructor called");
        this.list = list;
        Collections.reverse(this.list);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        if(list.size() == 0) {
            holder.text.setText(R.string.empty_chat);
            holder.pics.setVisibility(View.GONE);
        }else {
            Message message = list.get(position);
            if(message != null) {
                boolean findImage = message.getPhotoUrl()!= null;
                if(findImage) {
                    holder.text.setVisibility(View.GONE);
                    holder.pics.setVisibility(View.VISIBLE);
                    Glide.with(holder.pics.getContext())
                            .load(message.getPhotoUrl())
                            .into(holder.pics);
                    holder.name.setText(message.getUserName());
                }else {
                    holder.pics.setVisibility(View.GONE);
                    holder.text.setVisibility(View.VISIBLE);
                    holder.text.setText(message.getMessage());
                    holder.name.setText(message.getUserName());

                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if(list.size() > 0){
            return list.size();
        }
        return 1;
    }

    public int addItem (Message item) {
        this.list.add(item);
        notifyDataSetChanged();
        return list.indexOf(item);
    }

    public void clearData () {
        if(list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView name = null;
        TextView text = null;
        ImageView pics = null;

        public MessageViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.view_name);
            this.text = itemView.findViewById(R.id.view_message);
            this.pics = itemView.findViewById(R.id.view_image);
        }
    }
}
