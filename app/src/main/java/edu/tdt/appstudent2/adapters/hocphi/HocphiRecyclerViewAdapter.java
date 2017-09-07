package edu.tdt.appstudent2.adapters.hocphi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.hocphi.HocphiChitiet;
import edu.tdt.appstudent2.models.hocphi.HocphiHeaderItem;
import edu.tdt.appstudent2.models.hocphi.HocphiMucItem;
import edu.tdt.appstudent2.models.hocphi.HocphiThanhtoanItem;
import edu.tdt.appstudent2.models.hocphi.HocphiTitleItem;
import edu.tdt.appstudent2.viewholder.hocphi.HocphiChitietViewHolder;
import edu.tdt.appstudent2.viewholder.hocphi.HocphiHeaderViewHolder;
import edu.tdt.appstudent2.viewholder.hocphi.HocphiMucViewHolder;
import edu.tdt.appstudent2.viewholder.hocphi.HocphiThanhtoanViewHolder;
import edu.tdt.appstudent2.viewholder.hocphi.HocphiTitleViewHolder;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TITLE = 0;
    private static final int VIEW_MUC = 1;
    private static final int VIEW_CHITIET = 2;
    private static final int VIEW_THANHTOAN = 3;
    private static final int VIEW_HEADER = 4;
    private List<Object> items;
    public HocphiRecyclerViewAdapter(List<Object> items) {
        this.items = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TITLE:
                View v1 = inflater.inflate(R.layout.item_hocphi_title, parent, false);
                viewHolder = new HocphiTitleViewHolder(v1);
                break;
            case VIEW_MUC:
                View v2 = inflater.inflate(R.layout.item_hocphi_muc, parent, false);
                viewHolder = new HocphiMucViewHolder(v2);
                break;
            case VIEW_CHITIET:
                View v3 = inflater.inflate(R.layout.item_hocphi_chitiet, parent, false);
                viewHolder = new HocphiChitietViewHolder(v3);
                break;
            case VIEW_THANHTOAN:
                View v4 = inflater.inflate(R.layout.item_hocphi_thanhtoan, parent, false);
                viewHolder = new HocphiThanhtoanViewHolder(v4);
                break;
            case  VIEW_HEADER:
                View v5 = inflater.inflate(R.layout.item_hocphi_header, parent, false);
                viewHolder = new HocphiHeaderViewHolder(v5);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TITLE:
                HocphiTitleViewHolder holderGet = (HocphiTitleViewHolder)holder;
                HocphiTitleItem hocphiTitleItem = (HocphiTitleItem)items.get(position);
                holderGet.title.setText(hocphiTitleItem.getName());
                break;
            case VIEW_MUC:
                HocphiMucViewHolder hocphiMucViewHolder = (HocphiMucViewHolder)holder;
                HocphiMucItem hocphiMucItem = (HocphiMucItem)items.get(position);
                hocphiMucViewHolder.money.setText(hocphiMucItem.getMoney());
                hocphiMucViewHolder.title.setText(hocphiMucItem.getTitle());
                break;
            case VIEW_CHITIET:
                HocphiChitietViewHolder hocphiChitietViewHolder = (HocphiChitietViewHolder) holder;
                HocphiChitiet hocphiChitiet = (HocphiChitiet)items.get(position);
                hocphiChitietViewHolder.id.setText(hocphiChitiet.getMaMonHoc());
                hocphiChitietViewHolder.money.setText(hocphiChitiet.getSoTien());
                hocphiChitietViewHolder.title.setText(hocphiChitiet.getTenMonHoc());
                break;
            case VIEW_THANHTOAN:
                HocphiThanhtoanViewHolder hocphiThanhtoanViewHolder = (HocphiThanhtoanViewHolder) holder;
                HocphiThanhtoanItem hocphiThanhtoanItem = (HocphiThanhtoanItem)items.get(position);
                hocphiThanhtoanViewHolder.title.setText(hocphiThanhtoanItem.getNgayThanhToan());
                hocphiThanhtoanViewHolder.id.setText(hocphiThanhtoanItem.getHinhThucThanhToan());
                hocphiThanhtoanViewHolder.money.setText(hocphiThanhtoanItem.getSoTienThanhToan());
                break;
            case VIEW_HEADER:
                HocphiHeaderViewHolder hocphiHeaderViewHolder = (HocphiHeaderViewHolder) holder;
                HocphiHeaderItem hocphiHeaderItem = (HocphiHeaderItem) items.get(position);
                hocphiHeaderViewHolder.title.setText(hocphiHeaderItem.getTitle());
                hocphiHeaderViewHolder.date.setText(hocphiHeaderItem.getDate());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof HocphiTitleItem) {
            return VIEW_TITLE;
        }else if (items.get(position) instanceof HocphiChitiet) {
            return VIEW_CHITIET;
        }else if (items.get(position) instanceof HocphiMucItem) {
            return VIEW_MUC;
        }else if (items.get(position) instanceof HocphiThanhtoanItem) {
            return VIEW_THANHTOAN;
        }else if (items.get(position) instanceof HocphiHeaderItem) {
            return VIEW_HEADER;
        }
        return -1;
    }
}