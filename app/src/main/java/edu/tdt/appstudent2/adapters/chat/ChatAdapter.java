package edu.tdt.appstudent2.adapters.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.firebase.Chat;
import edu.tdt.appstudent2.models.firebase.ChatDateShow;
import edu.tdt.appstudent2.models.firebase.ChatShow;
import edu.tdt.appstudent2.utils.StringUtil;
import edu.tdt.appstudent2.views.widget.CircleImageView;

/**
 * Created by bichan on 8/29/17.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    private Context mContext;

    private static final int TYPE_IN = 1;
    private static final int TYPE_IN_ADMIN = 4;
    private static final int TYPE_OUT = 2;
    private static final int TYPE_DATE = 3;

    private ArrayList<Object> lists;

    public ChatAdapter(Context mContext){
        lists = new ArrayList<>();
        this.mContext = mContext;
    }

    public void clear(){
        lists.clear();
        notifyDataSetChanged();
    }

    public void addItem(ChatShow chatShow){
        lists.add(chatShow);
        ChatDateShow chatDateShow = new ChatDateShow();
        chatDateShow.time = chatShow.time;
        lists.add(chatDateShow);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType){
            case TYPE_DATE:
                view = inflater.inflate(R.layout.item_chat_date, parent, false);
                viewHolder = new ChatDateViewHolder(view);
                break;
            case TYPE_IN:
                view = inflater.inflate(R.layout.item_chat_in, parent, false);
                viewHolder = new ChatViewHolder(view);
                break;
            case TYPE_IN_ADMIN:
                view = inflater.inflate(R.layout.item_chat_in_admin, parent, false);
                viewHolder = new ChatViewHolder(view);
                break;
            case TYPE_OUT:
                view = inflater.inflate(R.layout.item_chat_out, parent, false);
                viewHolder = new ChatViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();

        Chat preChat = position - (type == TYPE_DATE?3:2) >= 0 ? (ChatShow) lists.get(position - (type == TYPE_DATE?3:2)): null;
        Chat chat = (ChatShow) lists.get(position - (type == TYPE_DATE?1:0));
        boolean hideName = false;
        boolean hideTime = false;
        boolean showTimeFull = false;

        if(!DateUtils.isToday(chat.time)){
            showTimeFull = true;
        }

        if(preChat != null && preChat.chatUser.mssv.equals(chat.chatUser.mssv)){
            hideName = true;
        }

        if(preChat != null && (chat.time - preChat.time) < 10 * 60 * 1000)
            hideTime = true;

        if(type == TYPE_DATE){
            ChatDateShow chatDateShow = (ChatDateShow) lists.get(position);
            ChatDateViewHolder chatDateViewHolder = (ChatDateViewHolder) holder;
            chatDateViewHolder.tvDate.setVisibility(hideTime?View.GONE:View.VISIBLE);
            chatDateViewHolder.tvDate.setText(StringUtil.getDate(chatDateShow.time, showTimeFull?"dd/MM - HH:mm":"HH:mm"));
        }

        if(type == TYPE_IN || type == TYPE_OUT){
            ChatShow chatShow = (ChatShow) lists.get(position);
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            chatViewHolder.tvName.setText(chatShow.chatUser.name);
            chatViewHolder.tvBody.setText(chatShow.body);
            if(type == TYPE_IN){
                chatViewHolder.tvName.setVisibility(hideName?View.GONE:View.VISIBLE);
                chatViewHolder.imgAvatar.setVisibility(hideName?View.INVISIBLE:View.VISIBLE);
                if(!chatShow.chatUser.showAvatar){
                    Picasso.with(mContext).load(R.drawable.user_empty).into(chatViewHolder.imgAvatar);
                }else{
                    Picasso.with(mContext).load(chatShow.chatUser.avatar).into(chatViewHolder.imgAvatar);
                }
                chatViewHolder.dotOnline.setVisibility(chatShow.online?View.VISIBLE:View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = lists.get(position);
        if(o instanceof ChatDateShow)
            return TYPE_DATE;
        if(o instanceof ChatShow){
            if(((ChatShow) o).type == 1){
                if(((ChatShow) o).chatUser.isAdmin)
                    return TYPE_IN_ADMIN;
                return TYPE_IN;
            }
            return TYPE_OUT;
        }
        return 0;
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imgAvatar;
        public TextView tvName, tvBody;
        public View dotOnline;
        public ChatViewHolder(View itemView) {
            super(itemView);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            dotOnline = (View) itemView.findViewById(R.id.dotOnline);
        }
    }

    private class ChatDateViewHolder extends RecyclerView.ViewHolder{
        public TextView tvDate;
        public ChatDateViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
