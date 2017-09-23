package edu.tdt.appstudent2.adapters.email;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.utils.Util;

/**
 * Created by bichan on 9/23/17.
 */

public class EmailAttchmentNewAdapter extends RecyclerView.Adapter {
    public List<File> lists;

    public EmailAttchmentNewAdapter(){
        lists = new ArrayList<>();
    }

    public void setLists(ArrayList<File> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    public void addItem(File file){
        if(file == null)
            return;
        lists.add(file);
        notifyDataSetChanged();
    }

    public void removeFile(File file){
        lists.remove(file);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_email_attachment_new, parent, false);
        viewHolder = new EmailAttachmentViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final File file = lists.get(position);
        EmailAttachmentViewHolder emailAttachmentViewHolder = (EmailAttachmentViewHolder) holder;
        emailAttachmentViewHolder.tvName.setText(file.getName());
        emailAttachmentViewHolder.tvSize.setText(Util.formatFileSize(file.length()));
        emailAttachmentViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFile(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    private class EmailAttachmentViewHolder extends RecyclerView.ViewHolder{

        public TextView tvName, tvSize;
        public AppCompatImageButton btnRemove;


        public EmailAttachmentViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSize = (TextView) itemView.findViewById(R.id.tvSize);
            btnRemove = (AppCompatImageButton) itemView.findViewById(R.id.btnRemove);
        }
    }
}
