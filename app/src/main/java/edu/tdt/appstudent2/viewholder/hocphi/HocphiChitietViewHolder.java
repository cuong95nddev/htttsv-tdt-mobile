package edu.tdt.appstudent2.viewholder.hocphi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.views.widget.RoundedLetterView;

/**
 * Created by Bichan on 7/16/2016.
 */
public class HocphiChitietViewHolder extends RecyclerView.ViewHolder {
    public TextView line, title, id, money;
    public RoundedLetterView nameRLV;
    public HocphiChitietViewHolder(View itemView) {
        super(itemView);
        id = (TextView) itemView.findViewById(R.id.id_lable);
        title = (TextView) itemView.findViewById(R.id.ten_text);
        money = (TextView) itemView.findViewById(R.id.money_text);
        nameRLV = (RoundedLetterView) itemView.findViewById(R.id.name_rlv);
    }
}
