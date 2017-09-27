package edu.tdt.appstudent2.adapters.thongbao;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Bichan on 7/15/2016.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> list;
    private ArrayList<String> title;
    private Context mContext;
    public FragmentAdapter(Context mContext, FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
        title = new ArrayList<String>();
        this.mContext = mContext;
    }
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }

    public void addTitle(String e){
        title.add(e);
    }
    public void clearTitle(){
        title.clear();
    }

    @Override
    public int getItemPosition(Object object) {
        int index = list.indexOf (object);

        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }
}
