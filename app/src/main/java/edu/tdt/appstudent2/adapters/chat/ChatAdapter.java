package edu.tdt.appstudent2.adapters.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.firebase.Chat;
import edu.tdt.appstudent2.models.firebase.ChatIn;
import edu.tdt.appstudent2.models.firebase.ChatOut;
import edu.tdt.appstudent2.utils.StringUtil;

/**
 * Created by bichan on 8/29/17.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    private static final int TYPE_IN = 1;
    private static final int TYPE_OUT = 2;
    public ArrayList<Object> chats;
    public Context mContext;

    public ChatAdapter(Context mContext){
        this.mContext = mContext;
        chats = new ArrayList<Object>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_IN:
                View v1 = inflater.inflate(R.layout.item_chat_in, parent, false);
                viewHolder = new ChatInViewHolder(v1);
                break;
            case TYPE_OUT:
                View v2 = inflater.inflate(R.layout.item_chat_out, parent, false);
                viewHolder = new ChatOutViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat preChat = position - 1 >= 0 ? (Chat) chats.get(position - 1): null;
        Chat chat = (Chat) chats.get(position);
        boolean hideName = false;
        boolean hideTime = false;
        boolean showTimeFull = false;

        if(!DateUtils.isToday(chat.time))
            showTimeFull = true;

        if(preChat != null && preChat.mssv.equals(chat.mssv)){
            hideName = true;
        }

        if(preChat != null && (chat.time - preChat.time) < 10 * 60 * 1000)
            hideTime = true;

        switch (holder.getItemViewType()){
            case TYPE_IN:
                ChatInViewHolder chatInViewHolder = (ChatInViewHolder) holder;
                chatInViewHolder.tvMSSV.setText(chat.mssv);
                chatInViewHolder.tvBody.setText(chat.body);
                chatInViewHolder.tvTime.setText(StringUtil.getDate(chat.time, showTimeFull?"dd/MM - HH:mm":"HH:mm"));
                chatInViewHolder.tvMSSV.setVisibility(hideName?View.GONE:View.VISIBLE);
                chatInViewHolder.tvTime.setVisibility(hideTime?View.GONE:View.VISIBLE);
                break;
            case TYPE_OUT:
                ChatOutViewHolder chatOutViewHolder = (ChatOutViewHolder) holder;
                chatOutViewHolder.tvBody.setText(chat.body);
                chatOutViewHolder.tvTime.setText(StringUtil.getDate(chat.time, showTimeFull?"dd/MM - HH:mm":"HH:mm"));
                chatOutViewHolder.tvTime.setVisibility(hideTime?View.GONE:View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chats.get(position) instanceof ChatIn)
            return TYPE_IN;
        if (chats.get(position) instanceof ChatOut)
            return TYPE_OUT;
        return 0;
    }

    public class ChatInViewHolder extends RecyclerView.ViewHolder{
        public TextView tvMSSV, tvBody, tvTime;
        public ChatInViewHolder(View itemView) {
            super(itemView);
            tvMSSV = (TextView) itemView.findViewById(R.id.text_message_name);
            tvBody = (TextView) itemView.findViewById(R.id.text_message_body);
            tvTime = (TextView) itemView.findViewById(R.id.text_message_time);
        }
    }

    public class ChatOutViewHolder extends RecyclerView.ViewHolder{
        public TextView tvBody, tvTime;
        public ChatOutViewHolder(View itemView) {
            super(itemView);
            tvBody = (TextView) itemView.findViewById(R.id.text_message_body);
            tvTime = (TextView) itemView.findViewById(R.id.text_message_time);
        }
    }
}
