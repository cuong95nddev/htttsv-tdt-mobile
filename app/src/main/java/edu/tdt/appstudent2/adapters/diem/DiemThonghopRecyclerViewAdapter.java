package edu.tdt.appstudent2.adapters.diem;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.diem.DiemItem;
import edu.tdt.appstudent2.utils.Util;

/**
 * Created by Bichan on 7/17/2016.
 */
public class DiemThonghopRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiemItem> list;

    public DiemThonghopRecyclerViewAdapter(List<DiemItem> list){
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_diem_tonghop, parent, false);
        RecyclerView.ViewHolder viewHolder = new DiemTonghopViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiemTonghopViewHolder diemTonghopViewHolder = (DiemTonghopViewHolder) holder;
        DiemItem diemItem = list.get(position);
        diemTonghopViewHolder.sTT.setText(Integer.toString(position));
        diemTonghopViewHolder.maMonHoc.setText(diemItem.getMonHocID());
        diemTonghopViewHolder.soTC.setText(diemItem.getSoTC());
        diemTonghopViewHolder.dTB.setText(diemItem.getdTB());
        diemTonghopViewHolder.monHoc.setText(diemItem.getTenMH());
        diemTonghopViewHolder.dTB.setTextColor(Color.parseColor(Util.xeLoaiDiemColor(diemItem.getdTB())));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DiemTonghopViewHolder extends RecyclerView.ViewHolder{
        public TextView monHoc, sTT, maMonHoc, soTC, dTB;
        public DiemTonghopViewHolder(View itemView) {
            super(itemView);
            monHoc = (TextView) itemView.findViewById(R.id.monhoc_text);
            sTT = (TextView) itemView.findViewById(R.id.stt_text);
            maMonHoc = (TextView) itemView.findViewById(R.id.mamon_text);
            soTC = (TextView) itemView.findViewById(R.id.tc_text);
            dTB = (TextView) itemView.findViewById(R.id.dtb_text);
        }
    }
}
