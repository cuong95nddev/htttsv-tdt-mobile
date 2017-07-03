package edu.tdt.appstudent2.adapters.tkb;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.tkb.TkbMonhocShowItem;
import edu.tdt.appstudent2.utils.ColorGenerator;

/**
 * Created by Bichan on 7/19/2016.
 */
public class TkbNgayRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<TkbMonhocShowItem> lists;

    ColorGenerator generator = ColorGenerator.TKB;


    public TkbNgayRecyclerViewAdapter(List<TkbMonhocShowItem> lists){
        this.lists = lists;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_tkb_monhoc, parent, false);
        RecyclerView.ViewHolder viewHolder = new MonhocViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TkbMonhocShowItem tkbMonhocShowItem = lists.get(position);
        MonhocViewHolder monhocViewHolder = (MonhocViewHolder) holder;
        monhocViewHolder.pos.setText(tkbMonhocShowItem.getPos());
        monhocViewHolder.timeStart.setText(tkbMonhocShowItem.getTimeStart());
        monhocViewHolder.timeFinish.setText(tkbMonhocShowItem.getTimeFinish());
        monhocViewHolder.tenMH.setText(tkbMonhocShowItem.getTenMH());
        monhocViewHolder.maMH.setText(tkbMonhocShowItem.getMaMH());
        monhocViewHolder.nhom.setText(tkbMonhocShowItem.getNhom());
        monhocViewHolder.to.setText(tkbMonhocShowItem.getTo());
        monhocViewHolder.phong.setText(tkbMonhocShowItem.getPhong());
        if(position + 1 == lists.size()){
            monhocViewHolder.line.setVisibility(View.GONE);
        }else {
            monhocViewHolder.line.setVisibility(View.VISIBLE);
        }

        int color = generator.getColor(tkbMonhocShowItem.getPos());
        GradientDrawable backgroundGradient = (GradientDrawable)monhocViewHolder.pos.getBackground();
        backgroundGradient.setStroke(5, color);

        color = generator.getColor(tkbMonhocShowItem.getTenMH());
        backgroundGradient = (GradientDrawable)monhocViewHolder.layout.getBackground();
        backgroundGradient.setColor(color);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class MonhocViewHolder extends RecyclerView.ViewHolder{
        public TextView pos, timeStart, timeFinish, tenMH, maMH, nhom, to, phong, line;
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
            line =(TextView) itemView.findViewById(R.id.line_text);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
        }
    }
}
