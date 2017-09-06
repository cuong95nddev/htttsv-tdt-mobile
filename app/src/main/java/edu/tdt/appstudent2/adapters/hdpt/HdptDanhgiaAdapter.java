package edu.tdt.appstudent2.adapters.hdpt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.hdpt.HdptDanhgiaDiemItem;
import edu.tdt.appstudent2.models.hdpt.HdptDanhgiaItem;
import edu.tdt.appstudent2.models.hdpt.HdptDanhgiaTitleItem;
import edu.tdt.appstudent2.utils.ColorGenerator;
import edu.tdt.appstudent2.views.widget.RoundedLetterView;

/**
 * Created by Bichan on 7/28/2016.
 */
public class HdptDanhgiaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TITLE = 0;
    private static final int VIEW_DANHGIA = 1;
    private static final int VIEW_DIEM = 2;
    private List<Object> lists;
    ColorGenerator generator = ColorGenerator.TKB;
    public HdptDanhgiaAdapter(List<Object> lists){
        this.lists = lists;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case VIEW_TITLE:
                view = inflater.inflate(R.layout.item_hdpt_danhgia_title, parent, false);
                viewHolder = new HdptDanhgiaTitleViewHolder(view);
                break;
            case VIEW_DANHGIA:
                view = inflater.inflate(R.layout.item_hdpt_danhgia, parent, false);
                viewHolder = new HdptDanhgiaViewHolder(view);
                break;
            case VIEW_DIEM:
                view = inflater.inflate(R.layout.item_hdpt_danhgia_diem, parent, false);
                viewHolder = new HdptDanhgiaDiemViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TITLE:
                HdptDanhgiaTitleItem get1 = (HdptDanhgiaTitleItem) lists.get(position);
                HdptDanhgiaTitleViewHolder mHolder = (HdptDanhgiaTitleViewHolder) holder;
                mHolder.title.setText(get1.getTitle());
                break;
            case VIEW_DANHGIA:
                HdptDanhgiaItem get2 = (HdptDanhgiaItem) lists.get(position);
                HdptDanhgiaViewHolder mHolder2 = (HdptDanhgiaViewHolder) holder;
                int color = generator.getColor(get2.getDiem());
                mHolder2.diem.setTitleText(get2.getDiem());
                mHolder2.diem.setBackgroundColor(color);
                mHolder2.ketQua.setText(get2.getKetQua());
                mHolder2.noiDung.setText(get2.getNoiDung());
                break;
            case VIEW_DIEM:
                HdptDanhgiaDiemItem get3 = (HdptDanhgiaDiemItem) lists.get(position);
                HdptDanhgiaDiemViewHolder mHolder3 = (HdptDanhgiaDiemViewHolder) holder;
                mHolder3.diem.setText(get3.getDiem());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object o = lists.get(position);
        if(o instanceof HdptDanhgiaTitleItem){
            return VIEW_TITLE;
        }else if(o instanceof HdptDanhgiaItem){
            return VIEW_DANHGIA;
        }else if(o instanceof HdptDanhgiaDiemItem){
            return VIEW_DIEM;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class HdptDanhgiaTitleViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public HdptDanhgiaTitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_text);
        }
    }
    public class HdptDanhgiaViewHolder extends RecyclerView.ViewHolder{
        public TextView noiDung, ketQua;
        public RoundedLetterView diem;
        public HdptDanhgiaViewHolder(View itemView) {
            super(itemView);
            diem = (RoundedLetterView) itemView.findViewById(R.id.diem_text);
            noiDung = (TextView) itemView.findViewById(R.id.noidung_text);
            ketQua = (TextView) itemView.findViewById(R.id.ketqua_text);
        }
    }

    public class HdptDanhgiaDiemViewHolder extends RecyclerView.ViewHolder{
        public TextView diem;
        public HdptDanhgiaDiemViewHolder(View itemView) {
            super(itemView);
            diem = (TextView) itemView.findViewById(R.id.diem_text);
        }
    }
}
