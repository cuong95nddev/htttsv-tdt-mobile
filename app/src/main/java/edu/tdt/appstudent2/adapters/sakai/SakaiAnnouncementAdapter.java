package edu.tdt.appstudent2.adapters.sakai;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.sakai.ItemSakaiAnnouncement;

/**
 * Created by bichan on 9/25/17.
 */

public class SakaiAnnouncementAdapter extends RecyclerView.Adapter {
    public ArrayList<ItemSakaiAnnouncement> lists;

    public SakaiAnnouncementAdapter(){
        lists = new ArrayList<>();
    }

    public void addItem(ItemSakaiAnnouncement itemSakaiAnnouncement){
        if(itemSakaiAnnouncement == null)
            return;

        lists.add(itemSakaiAnnouncement);
        notifyDataSetChanged();
    }

    public void clear(){
        lists.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_sakai_announcement, parent, false);
        viewHolder = new SakaiAnnouncementViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemSakaiAnnouncement item = lists.get(position);
        SakaiAnnouncementViewHolder viewHolder  = (SakaiAnnouncementViewHolder) holder;
        viewHolder.tvTile.setText(item.getTitle());
        viewHolder.tvCreatedByDisplayName.setText(item.getCreatedByDisplayName());
        viewHolder.tvCreatedOn.setReferenceTime(item.getCreatedOn());
        if(item.getAttachments().size() > 0){
            viewHolder.imgAttachment.setVisibility(View.VISIBLE);
        }else{
            viewHolder.imgAttachment.setVisibility(View.GONE);
        }

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClick != null)
                    onItemClick.onClick(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class SakaiAnnouncementViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTile, tvCreatedByDisplayName;
        public ImageView imgAttachment;
        public RelativeTimeTextView tvCreatedOn;
        public MaterialRippleLayout layout;

        public SakaiAnnouncementViewHolder(View itemView) {
            super(itemView);
            tvCreatedByDisplayName = (TextView) itemView.findViewById(R.id.tvCreatedByDisplayName);
            tvTile = (TextView) itemView.findViewById(R.id.tvTile);
            imgAttachment = (ImageView) itemView.findViewById(R.id.imgAttachment);
            tvCreatedOn = (RelativeTimeTextView) itemView.findViewById(R.id.tvCreatedOn);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
        }
    }

    public interface OnItemClick{
        void onClick(String id);
    }

    public OnItemClick onItemClick;
}
