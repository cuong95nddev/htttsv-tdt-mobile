package edu.tdt.appstudent2.adapters.sakai;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.sakai.ItemSakaiAttachment;

/**
 * Created by bichan on 9/25/17.
 */

public class SakaiAttachmentAdapter extends RecyclerView.Adapter {

    public List<ItemSakaiAttachment> lists;

    public SakaiAttachmentAdapter(){
        lists = new ArrayList<>();
    }

    public void setLists(ArrayList<ItemSakaiAttachment> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_email_attachment, parent, false);
        viewHolder = new SakaiAttachmentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemSakaiAttachment itemSakaiAttachment = lists.get(position);
        SakaiAttachmentViewHolder emailAttachmentViewHolder = (SakaiAttachmentViewHolder) holder;
        emailAttachmentViewHolder.tvName.setText(itemSakaiAttachment.getName());
        emailAttachmentViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClick != null)
                    onItemClick.onClick(itemSakaiAttachment, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class SakaiAttachmentViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public MaterialRippleLayout layout;

        public SakaiAttachmentViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
        }
    }

    public interface OnItemClick{
        void onClick(ItemSakaiAttachment itemSakaiAttachment, int position);
    }

    public OnItemClick onItemClick;
}
