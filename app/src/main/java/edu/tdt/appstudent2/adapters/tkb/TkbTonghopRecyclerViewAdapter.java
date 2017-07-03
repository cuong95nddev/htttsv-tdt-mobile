package edu.tdt.appstudent2.adapters.tkb;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
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
import edu.tdt.appstudent2.models.tkb.TkbMonhocShowItem;
import edu.tdt.appstudent2.models.tkb.TkbThuShowItem;
import edu.tdt.appstudent2.utils.ColorGenerator;

/**
 * Created by Bichan on 7/19/2016.
 */
public class TkbTonghopRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderHandler {
    private static final int VIEW_THU = 0;
    private static final int VIEW_MONHOC_FULL = 1;

    ColorGenerator generator = ColorGenerator.TKB;


    private List<Object> lists;

    public TkbTonghopRecyclerViewAdapter(List<Object> lists){
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case VIEW_THU:
                view = inflater.inflate(R.layout.item_tkb_day_title, parent, false);
                viewHolder = new ThuViewHolder(view);
                break;
            case VIEW_MONHOC_FULL:
                view = inflater.inflate(R.layout.item_tkb_full, parent, false);
                viewHolder = new MonhocViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_THU:
                ThuViewHolder thuViewHolder = (ThuViewHolder) holder;
                TkbThuShowItem tkbThuShowItem = (TkbThuShowItem) lists.get(position);
                thuViewHolder.thu.setText(tkbThuShowItem.getTen());
                //thuViewHolder.bg.setCardBackgroundColor(Color.parseColor(tkbThuShowItem.getColor()));
                break;
            case VIEW_MONHOC_FULL:
                final MonhocViewHolder monhocViewHolder = (MonhocViewHolder) holder;
                TkbMonhocShowItem tkbMonhocShowItem = (TkbMonhocShowItem) lists.get(position);
                monhocViewHolder.pos.setText(tkbMonhocShowItem.getPos());
                monhocViewHolder.timeStart.setText(tkbMonhocShowItem.getTimeStart());
                monhocViewHolder.timeFinish.setText(tkbMonhocShowItem.getTimeFinish());
                monhocViewHolder.tenMH.setText(tkbMonhocShowItem.getTenMH());
                monhocViewHolder.maMH.setText(tkbMonhocShowItem.getMaMH());
                monhocViewHolder.nhom.setText(tkbMonhocShowItem.getNhom());
                monhocViewHolder.to.setText(tkbMonhocShowItem.getTo());
                monhocViewHolder.phong.setText(tkbMonhocShowItem.getPhong());
                monhocViewHolder.tuan.setText(tkbMonhocShowItem.getTuan());

                if(position + 1 < lists.size()){
                    if(lists.get(position + 1) instanceof TkbThuShowItem){
                        monhocViewHolder.line.setVisibility(View.GONE);
                    }else {
                        monhocViewHolder.line.setVisibility(View.VISIBLE);
                    }
                }

                monhocViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        monhocViewHolder.expandableLayout.toggle();
                    }
                });

                int color = generator.getColor(tkbMonhocShowItem.getPos());
                GradientDrawable backgroundGradient = (GradientDrawable)monhocViewHolder.pos.getBackground();
                backgroundGradient.setStroke(5, color);

                color = generator.getColor(tkbMonhocShowItem.getTenMH());
                backgroundGradient = (GradientDrawable)monhocViewHolder.layout.getBackground();
                backgroundGradient.setColor(color);

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(lists.get(position) instanceof TkbThuShowItem){
            return VIEW_THU;
        }else if(lists.get(position) instanceof TkbMonhocShowItem){
            return VIEW_MONHOC_FULL;
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

    public class ThuViewHolder extends RecyclerView.ViewHolder{
        public TextView thu;
        public CardView bg;
        public ThuViewHolder(View itemView) {
            super(itemView);
            thu = (TextView) itemView.findViewById(R.id.thu_text);
            bg = (CardView) itemView.findViewById(R.id.bg_cardview);
        }
    }

    public class MonhocViewHolder extends RecyclerView.ViewHolder{
        public TextView pos, timeStart, timeFinish, tenMH, maMH, nhom, to, phong, tuan, line;
        public ExpandableLinearLayout expandableLayout;
        public MaterialRippleLayout layout;
        public MonhocViewHolder(View itemView) {
            super(itemView);
            pos = (TextView) itemView.findViewById(R.id.pos_text);
            timeStart = (TextView) itemView.findViewById(R.id.time_start_text);
            timeFinish = (TextView) itemView.findViewById(R.id.time_finish_text);
            tenMH = (TextView) itemView.findViewById(R.id.tenMH_text);
            maMH = (TextView) itemView.findViewById(R.id.maMH_text);
            nhom = (TextView) itemView.findViewById(R.id.nhom_text);
            to = (TextView) itemView.findViewById(R.id.to_text);
            phong = (TextView) itemView.findViewById(R.id.phong_text);
            tuan = (TextView) itemView.findViewById(R.id.tuan_text);
            line =(TextView) itemView.findViewById(R.id.line_text);
            expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
            expandableLayout.setInRecyclerView(true);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
        }
    }
}
