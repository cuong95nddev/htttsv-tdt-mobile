package edu.tdt.appstudent2.viewholder.hocphi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import edu.tdt.appstudent2.R;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiTitleViewHolder extends RecyclerView.ViewHolder  {
    public TextView title;
    public HocphiTitleViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title_text);
    }
}
