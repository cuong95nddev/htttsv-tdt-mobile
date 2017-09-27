package edu.tdt.appstudent2.viewholder.hocphi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import edu.tdt.appstudent2.R;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiHeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView title, date;
    public HocphiHeaderViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.money_text);
        date = (TextView) itemView.findViewById(R.id.date_text);
    }
}
