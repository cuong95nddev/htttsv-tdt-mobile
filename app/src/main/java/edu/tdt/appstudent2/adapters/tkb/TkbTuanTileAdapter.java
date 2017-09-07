package edu.tdt.appstudent2.adapters.tkb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.tkb.TkbTuanItem;

/**
 * Created by bichan on 9/7/17.
 */

public class TkbTuanTileAdapter extends RecyclerView.Adapter {
    private static final int VIEW = 1;
    private static final int VIEW_SELECTED = 2;
    private int tuan;
    private Context mContext;
    public List<TkbTuanItem> lists;

    OnItemClickListener mItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public TkbTuanTileAdapter(Context mContext){
        this.mContext = mContext;
        lists = new ArrayList<>();
        tuan = -1;
    }

    public void addItem(TkbTuanItem tkbTuanItem){
        if(tkbTuanItem == null)
            return;
        lists.add(tkbTuanItem);
        notifyDataSetChanged();
    }

    public void selected(int tuan){
        if(this.tuan == tuan)
            return;
        this.tuan = tuan;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType){
            case VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tkb_tuan_tile, parent, false);
                break;
            case VIEW_SELECTED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tkb_tuan_tile_selected, parent, false);
                break;
        }
        viewHolder = new TkbTuanTileViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TkbTuanItem tkbTuanItem = lists.get(position);
        TkbTuanTileViewHolder tkbTuanTileViewHolder = (TkbTuanTileViewHolder) holder;
        tkbTuanTileViewHolder.tvTitle.setText(tkbTuanItem.getTile());
        tkbTuanTileViewHolder.tvDate.setText(tkbTuanItem.getDate());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        TkbTuanItem tkbTuanItem  = lists.get(position);
        if(tkbTuanItem.getTuan() == tuan)
            return VIEW_SELECTED;
        return VIEW;
    }

    private class TkbTuanTileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvTitle, tvDate;
        public MaterialRippleLayout layout;
        public TkbTuanTileViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTile);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}
