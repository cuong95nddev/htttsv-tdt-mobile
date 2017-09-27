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
public class DiemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiemItem> list;

    public DiemRecyclerViewAdapter(List<DiemItem> list){
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.item_diem, parent, false);
        RecyclerView.ViewHolder viewHolder = new DiemViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiemViewHolder diemTonghopViewHolder = (DiemViewHolder) holder;
        DiemItem diemItem = list.get(position);
        diemTonghopViewHolder.monHoc.setText(diemItem.getTenMH());
        diemTonghopViewHolder.diem1.setText(diemItem.getDiem1());
        diemTonghopViewHolder.diem2.setText(diemItem.getDiem2());
        diemTonghopViewHolder.diemthi1.setText(diemItem.getDiemThi1());
        diemTonghopViewHolder.diemthi2.setText(diemItem.getDiemThi2());
        diemTonghopViewHolder.dtb.setText(diemItem.getdTB());

        diemTonghopViewHolder.diem1.setTextColor(Color.parseColor(Util.xeLoaiDiemColor(diemItem.getDiem1())));
        diemTonghopViewHolder.diem2.setTextColor(Color.parseColor(Util.xeLoaiDiemColor(diemItem.getDiem2())));
        diemTonghopViewHolder.diemthi1.setTextColor(Color.parseColor(Util.xeLoaiDiemColor(diemItem.getDiemThi1())));
        diemTonghopViewHolder.diemthi2.setTextColor(Color.parseColor(Util.xeLoaiDiemColor(diemItem.getDiemThi2())));
        diemTonghopViewHolder.dtb.setTextColor(Color.parseColor(Util.xeLoaiDiemColor(diemItem.getdTB())));

        if(diemItem.getGhiChu().equals("")){
            diemTonghopViewHolder.ghiCHu.setVisibility(View.GONE);
        }else {
            diemTonghopViewHolder.ghiCHu.setVisibility(View.VISIBLE);
            diemTonghopViewHolder.ghiCHu.setText(diemItem.getGhiChu());
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DiemViewHolder extends RecyclerView.ViewHolder{
        public TextView monHoc, diem1, diem2, diemthi1, diemthi2, dtb, ghiCHu;
        public DiemViewHolder(View itemView) {
            super(itemView);
            monHoc = (TextView) itemView.findViewById(R.id.monhoc_text);
            diem1 = (TextView) itemView.findViewById(R.id.diem1_text);
            diem2 = (TextView) itemView.findViewById(R.id.diem2_text);
            diemthi1 = (TextView) itemView.findViewById(R.id.diemthi1_text);
            diemthi2 = (TextView) itemView.findViewById(R.id.diemthi2_text);
            ghiCHu = (TextView) itemView.findViewById(R.id.ghichu_text);
            dtb = (TextView) itemView.findViewById(R.id.dtb_text);
        }
    }
}
