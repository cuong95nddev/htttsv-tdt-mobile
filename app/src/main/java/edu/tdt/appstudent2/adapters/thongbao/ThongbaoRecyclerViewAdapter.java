package edu.tdt.appstudent2.adapters.thongbao;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler;

import java.util.ArrayList;
import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.thongbao.ThongBaoDateItem;
import edu.tdt.appstudent2.models.thongbao.ThongbaoItem;

/**
 * Created by Bichan on 7/15/2016.
 */
public class ThongbaoRecyclerViewAdapter extends RecyclerView.Adapter implements StickyHeaderHandler {
    private final static int VIEW_TYPE_THONGBAO_DATE = 0;
    private final static int VIEW_TYPE_THONGBAO = 1;
    private final static int VIEW_TYPE_THONGBAO_NEW = 2;

    private boolean isNewDate;
    private String date;

    Context mContext;
    private ArrayList<Object> list;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;

    public ThongbaoRecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.list = new ArrayList<>();
        isNewDate = true;
        date = "";
    }

    public void clear(){
        isNewDate = true;
        this.list.clear();
        notifyDataSetChanged();
    }

    public void addItem(ThongbaoItem thongbaoItem){
        if(isNewDate){
            list.add(new ThongBaoDateItem(thongbaoItem.getDate()));
            isNewDate = false;
        }
        if(!date.equals(thongbaoItem.getDate())){
            date = thongbaoItem.getDate();
            isNewDate = true;

        }
        list.add(thongbaoItem);
        notifyDataSetChanged();
    }

    public ThongbaoItem getItem(int pos) throws Exception {
        Object o = list.get(pos);
        if(o instanceof ThongbaoItem){
            return (ThongbaoItem) o;
        }
        throw new Exception("Error get type");
    }

    @Override
    public List<?> getAdapterData() {
        return this.list;
    }

    public class ThongBaoDateViewHolder extends RecyclerView.ViewHolder{
        public TextView tvDate;
        public ThongBaoDateViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

    public class ThongBaoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
        MaterialRippleLayout layout;
        TextView title;
        public ThongBaoViewHolder(View itemView) {
            super(itemView);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.thongbao_layout);
            title = (TextView) itemView.findViewById(R.id.title_text);
            layout.setOnClickListener(this);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType){
            case VIEW_TYPE_THONGBAO_DATE:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thongbao_date, parent, false);
                viewHolder = new ThongBaoDateViewHolder(view);
                break;
            case VIEW_TYPE_THONGBAO_NEW:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thongbao_new, parent, false);
                viewHolder = new ThongBaoViewHolder(view);
                break;
            case VIEW_TYPE_THONGBAO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thongbao, parent, false);
                viewHolder = new ThongBaoViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object o = list.get(position);
        if(o instanceof ThongBaoDateItem){
            ThongBaoDateViewHolder thongBaoDateViewHolder = (ThongBaoDateViewHolder)holder;
            thongBaoDateViewHolder.tvDate.setText(((ThongBaoDateItem) o).getTitle());
        }
        if(o instanceof ThongbaoItem){
            ThongBaoViewHolder thongBaoViewHolder = (ThongBaoViewHolder)holder;
            thongBaoViewHolder.title.setText(((ThongbaoItem) o).getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object o = list.get(position);
        if(o instanceof ThongBaoDateItem){
            return VIEW_TYPE_THONGBAO_DATE;
        }
        if(o instanceof ThongbaoItem){
            if(((ThongbaoItem) o).isNew()){
                return VIEW_TYPE_THONGBAO_NEW;
            }else{
                return VIEW_TYPE_THONGBAO;
            }
        }
        return -1;
    }
}
