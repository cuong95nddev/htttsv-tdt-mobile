package edu.tdt.appstudent2.adapters.hdpt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.hdpt.HdptHoatdongItem;
import edu.tdt.appstudent2.utils.ColorGenerator;
import edu.tdt.appstudent2.views.widget.RoundedLetterView;

/**
 * Created by Bichan on 7/27/2016.
 */
public class HdptHoatdongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<HdptHoatdongItem> lists;
    ColorGenerator generator = ColorGenerator.MATERIAL;
    public HdptHoatdongAdapter(List<HdptHoatdongItem> lists){
        this.lists = lists;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_hdpt_hoatdong, parent, false);
        RecyclerView.ViewHolder viewHolder = new HdptHoatdongViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HdptHoatdongViewHolder hdptHoatdongViewHolder = (HdptHoatdongViewHolder) holder;
        HdptHoatdongItem hdptHoatdongItem = lists.get(position);
        hdptHoatdongViewHolder.noiDung.setText(hdptHoatdongItem.getTenSuKien());
        hdptHoatdongViewHolder.thoiGian.setText(hdptHoatdongItem.getThoiGian());
        int color = generator.getColor(hdptHoatdongItem.getDiemRL());
        hdptHoatdongViewHolder.diem.setTitleText(hdptHoatdongItem.getDiemRL());
        hdptHoatdongViewHolder.diem.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class HdptHoatdongViewHolder extends RecyclerView.ViewHolder{
        public TextView  noiDung, thoiGian;
        public RoundedLetterView diem;
        public HdptHoatdongViewHolder(View itemView) {
            super(itemView);
            noiDung = (TextView) itemView.findViewById(R.id.noidung_text);
            thoiGian = (TextView) itemView.findViewById(R.id.thoigian_text);
            diem = (RoundedLetterView) itemView.findViewById(R.id.diem_text);
        }
    }
}
