package edu.tdt.appstudent2.adapters.trangchu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.trangchu.TrangchuMenuItem;
import edu.tdt.appstudent2.utils.ColorGenerator;

/**
 * Created by Bichan on 7/18/2016.
 */
public class TrangchuMenuRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    private ArrayList<TrangchuMenuItem> lists;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    ColorGenerator generator = ColorGenerator.TKB;
    public TrangchuMenuRecyclerViewAdapter(Context mContext,ArrayList<TrangchuMenuItem> lists){
        this.lists = lists;
        this.mContext = mContext;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_trangchu_menu, parent, false);;
        RecyclerView.ViewHolder viewHolder = new TrangchuMenuViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TrangchuMenuItem get = lists.get(position);
        TrangchuMenuViewHolder trangchuMenuViewHolder = (TrangchuMenuViewHolder) holder;
        trangchuMenuViewHolder.title.setText(get.getTitle());
        trangchuMenuViewHolder.icon.setImageDrawable(mContext.getResources().getDrawable(get.getIdImage()));

//        int color = generator.getColor(get.getTitle());
//        GradientDrawable backgroundGradient = (GradientDrawable)trangchuMenuViewHolder.bg.getBackground();
//        backgroundGradient.setStroke(4, color);
        //backgroundGradient.setColor(color);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class TrangchuMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
        public TextView title;
        //public View bg;
        public ImageView icon;
        private MaterialRippleLayout layout;
        private LinearLayout bg;
        public TrangchuMenuViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_text);
            icon = (ImageView) itemView.findViewById(R.id.icon_img);
            bg = (LinearLayout) itemView.findViewById(R.id.bg);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
            layout.setOnClickListener(this);
            layout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }


        @Override
        public boolean onLongClick(View v) {
            if(mItemLongClickListener != null){
                mItemLongClickListener.onItemLongClick(itemView, getPosition());
            }
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public void setOnLongItemClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }
}
