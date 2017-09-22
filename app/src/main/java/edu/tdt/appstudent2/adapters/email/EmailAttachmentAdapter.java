package edu.tdt.appstudent2.adapters.email;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.email.EmailAttachment;

/**
 * Created by bichan on 9/21/17.
 */

public class EmailAttachmentAdapter extends RecyclerView.Adapter {


    private boolean isLoading;
    public List<EmailAttachment> lists;

    public EmailAttachmentAdapter(){
        lists = new ArrayList<>();
        isLoading = false;
    }

    public void setLists(ArrayList<EmailAttachment> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_email_attachment, parent, false);
        viewHolder = new EmailAttachmentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final EmailAttachment emailAttachment = lists.get(position);
        EmailAttachmentViewHolder emailAttachmentViewHolder = (EmailAttachmentViewHolder) holder;
        emailAttachmentViewHolder.tvName.setText(emailAttachment.getName());
        emailAttachmentViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClick != null)
                    onItemClick.onClick(emailAttachment, position);
            }
        });
        if(isLoading){
            emailAttachmentViewHolder.progressLoader.setVisibility(View.VISIBLE);
        }else{
            emailAttachmentViewHolder.progressLoader.setVisibility(View.GONE);
        }
    }

    public void setLoading(){
        isLoading = !isLoading;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class EmailAttachmentViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        public MaterialRippleLayout layout;
        public ProgressBar progressLoader;

        public EmailAttachmentViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            layout = (MaterialRippleLayout) itemView.findViewById(R.id.layout);
            progressLoader = (ProgressBar) itemView.findViewById(R.id.progress_loader);
        }
    }

    public interface OnItemClick{
        void onClick(EmailAttachment emailAttachment, int position);
    }

    public OnItemClick onItemClick;
}
