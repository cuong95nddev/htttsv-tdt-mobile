package edu.tdt.appstudent2.adapters.lichthi;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.lichthi.LichThiDateShowItem;
import edu.tdt.appstudent2.models.lichthi.LichThiLichItem;
import edu.tdt.appstudent2.utils.GradientGenerator;

/**
 * Created by cuong on 5/3/2017.
 */

public class LichThiRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderHandler {
    private static final int VIEW_DATE = 0;
    private static final int VIEW_LICHTHI = 1;

    GradientGenerator generator = GradientGenerator.COLOR;
    private List<Object> lists;

    public LichThiRecyclerViewAdapter(List<Object> lists){
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case VIEW_DATE:
                view = inflater.inflate(R.layout.item_lichthi_date, parent, false);
                viewHolder = new DateViewHolder(view);
                break;
            case VIEW_LICHTHI:
                view = inflater.inflate(R.layout.item_lichthi, parent, false);
                viewHolder = new LichThiViewHolder(view);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_DATE:
                DateViewHolder thuViewHolder = (DateViewHolder) holder;
                LichThiDateShowItem lichThiDateShowItem = (LichThiDateShowItem) lists.get(position);
                thuViewHolder.day.setText(lichThiDateShowItem.getDay());
                thuViewHolder.date.setText(lichThiDateShowItem.getDate());
                break;
            case VIEW_LICHTHI:
                final LichThiViewHolder lichThiViewHolder = (LichThiViewHolder) holder;
                LichThiLichItem lichThiLichItem = (LichThiLichItem) lists.get(position);
                lichThiViewHolder.timeStart.setText(lichThiLichItem.getGioThi());
                lichThiViewHolder.timeFinish.setText(lichThiLichItem.getThoiLuong());
                lichThiViewHolder.tenMH.setText(lichThiLichItem.getTenMH());
                lichThiViewHolder.maMH.setText(lichThiLichItem.getMaMH());
                lichThiViewHolder.nhom.setText(lichThiLichItem.getNhom());
                lichThiViewHolder.to.setText(lichThiLichItem.getTo());
                lichThiViewHolder.phong.setText(lichThiLichItem.getPhong());

                String[] colors = generator.getColor(lichThiLichItem.getTenMH());

                GradientDrawable backgroundGradient = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{Color.parseColor(colors[0]), Color.parseColor(colors[1])});
                backgroundGradient.setCornerRadius(10);
                lichThiViewHolder.layout.setBackground(backgroundGradient);


                lichThiViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lichThiViewHolder.expandableLayout.toggle();
                    }
                });

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(lists.get(position) instanceof LichThiDateShowItem){
            return VIEW_DATE;
        }else if(lists.get(position) instanceof LichThiLichItem){
            return VIEW_LICHTHI;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public List<?> getAdapterData() {
        return this.lists;
    }

    public class DateViewHolder extends RecyclerView.ViewHolder{
        public TextView day, date;
        public DateViewHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.tvDay);
            date = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

    public class LichThiViewHolder extends RecyclerView.ViewHolder{
        public TextView timeStart, timeFinish, tenMH, maMH, nhom, to, phong;
        public MaterialRippleLayout layout;
        public ExpandableLinearLayout expandableLayout;
        public LichThiViewHolder(View itemView) {
            super(itemView);
            timeStart = (TextView) itemView.findViewById(R.id.time_start_text);
            timeFinish = (TextView) itemView.findViewById(R.id.time_finish_text);
            tenMH = (TextView) itemView.findViewById(R.id.tenMH_text);
            maMH = (TextView) itemView.findViewById(R.id.maMH_text);
            nhom = (TextView) itemView.findViewById(R.id.nhom_text);
            to = (TextView) itemView.findViewById(R.id.to_text);
            phong = (TextView) itemView.findViewById(R.id.phong_text);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
            expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
            expandableLayout.setInRecyclerView(true);
        }
    }

}
