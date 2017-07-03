package edu.tdt.appstudent2.adapters.thongtin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.thongtin.ThongtinItem;
import edu.tdt.appstudent2.models.thongtin.ThongtinTitleItem;

/**
 * Created by Bichan on 7/17/2016.
 */
public class ThongtinRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TITLE = 0;
    private static final int VIEW_THONGTIN = 1;
    List<Object> lists;

    public ThongtinRecyclerViewAdapter(List<Object> lists){
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case VIEW_TITLE:
                view = inflater.inflate(R.layout.item_thongtin_title, parent, false);
                viewHolder = new ThongtinTitleViewHolder(view);
                break;
            case VIEW_THONGTIN:
                view = inflater.inflate(R.layout.item_thongtin, parent, false);
                viewHolder = new ThongtinViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TITLE:
                ThongtinTitleViewHolder thongtinTitleViewHolder = (ThongtinTitleViewHolder) holder;
                ThongtinTitleItem thongtinTitleItem = (ThongtinTitleItem)lists.get(position);
                thongtinTitleViewHolder.title.setText(thongtinTitleItem.getTitle());
                break;
            case VIEW_THONGTIN:
                ThongtinViewHolder thongtinViewHolder = (ThongtinViewHolder) holder;
                ThongtinItem thongtinItem = (ThongtinItem)lists.get(position);
                thongtinViewHolder.title.setText(thongtinItem.getTitle());
                if(thongtinItem.getContent().equals(""))
                    thongtinViewHolder.content.setText("Chưa có thông tin");
                else
                    thongtinViewHolder.content.setText(thongtinItem.getContent());

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (lists.get(position) instanceof ThongtinTitleItem) {
            return VIEW_TITLE;
        }else if (lists.get(position) instanceof ThongtinItem) {
            return VIEW_THONGTIN;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ThongtinViewHolder extends RecyclerView.ViewHolder{
        public TextView title, content;
        public ThongtinViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_text);
            content = (TextView) itemView.findViewById(R.id.content_text);
        }
    }

    public class ThongtinTitleViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public ThongtinTitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_text);
        }
    }
}
