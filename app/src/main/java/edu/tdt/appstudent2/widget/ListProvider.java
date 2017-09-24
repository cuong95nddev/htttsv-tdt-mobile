package edu.tdt.appstudent2.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.tkb.TkbItem;
import edu.tdt.appstudent2.models.tkb.TkbLichItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocShowItem;
import edu.tdt.appstudent2.models.tkb.TkbThuItem;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;

/**
 * Created by bichan on 9/23/17.
 */

public class ListProvider implements RemoteViewsFactory {
    private ArrayList<TkbMonhocShowItem> listItemList = new ArrayList<TkbMonhocShowItem>();
    private Context context = null;
    private int appWidgetId;


    private String idHocky;
    private Realm realm;
    private TkbItem tkbItem;
    private User user;

    private Calendar calendarToDay;
    private Calendar calendarStart;
    private ArrayList<TkbThuItem> tkbThuItems;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    private void showTkb(){
        calendarToDay = Calendar.getInstance();
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        if(user != null && user.getConfig() != null){
            idHocky = user.getConfig().getIdHocKyMacDinh();

            if(!"".equals(idHocky)){
                tkbItem = realm.where(TkbItem.class).equalTo("idHocKy", idHocky).findFirst();
                if(tkbItem != null){
                    loadTkbOffline();
                }
            }

        }
        realm.close();
    }


    private void loadTkbOffline(){
        TkbMonhocShowItem tkbMonhocShowItem = null;
        tkbThuItems = new ArrayList<TkbThuItem>();
        for(int i = 0 ; i < 7; i++)
            tkbThuItems.add(new TkbThuItem());
        for (TkbMonhocItem e: tkbItem.getTkbMonhocItems()){
            for(TkbLichItem m: e.getTkbLichItems()){
                tkbMonhocShowItem = new TkbMonhocShowItem();
                tkbMonhocShowItem.setTenMH(e.getTenMH());
                tkbMonhocShowItem.setMaMH(e.getMaMH());
                tkbMonhocShowItem.setTo(e.getTo());
                tkbMonhocShowItem.setNhom(e.getNhom());
                tkbMonhocShowItem.setPos(Integer.toString(1));
                tkbMonhocShowItem.setPhong(m.getPhong());
                tkbMonhocShowItem.setTuan(m.getTuan());
                tkbMonhocShowItem.setTimeStart(Util.tinhTGBatDau(m.getTiet()));
                tkbMonhocShowItem.setTimeFinish(Util.tinhTGKetThuc(m.getTiet()));
                tkbMonhocShowItem.setTiet(m.getTiet());
                tkbMonhocShowItem.setPos(Util.tinhCaHoc(m.getTiet()));
                tkbThuItems.get(Integer.parseInt(m.getThu()) - 1).getTkbMonhocShowItems().add(tkbMonhocShowItem);
            }
        }

        String[] ngayBatDau = tkbItem.getDateStart().split("[/]");

        calendarStart = Calendar.getInstance();
        calendarStart.clear();
        calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ngayBatDau[0].trim()));
        calendarStart.set(Calendar.MONTH, Integer.parseInt(ngayBatDau[1].trim()) - 1);
        calendarStart.set(Calendar.YEAR, Integer.parseInt(ngayBatDau[2].trim()));

        getTkb();
    }

    private void getTkb(){
        listItemList.clear();
        if(calendarToDay.compareTo(calendarStart) >= 0){
            int weekOfToDay = Weeks.weeksBetween(new DateTime(calendarStart), new DateTime(calendarToDay)).getWeeks();
            weekOfToDay++;
            int thu = calendarToDay.get(Calendar.DAY_OF_WEEK);
            thu--;
            Collections.sort(tkbThuItems.get(thu).getTkbMonhocShowItems(), new Comparator<TkbMonhocShowItem>() {
                @Override
                public int compare(TkbMonhocShowItem tkbMonhocShowItem, TkbMonhocShowItem t1) {
                    return tkbMonhocShowItem.getTimeStart().compareTo(t1.getTimeStart());
                }
            });
            for(TkbMonhocShowItem e: tkbThuItems.get(thu).getTkbMonhocShowItems()){
                String[] tuanHocArray = e.getTuan().split("");
                if(tuanHocArray.length > weekOfToDay){
                    if(!tuanHocArray[weekOfToDay].equals("-")){
                        listItemList.add(e);
                    }
                }
            }
        }
    }


    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(),
                R.layout.item_tkb_monhoc_widget);

        TkbMonhocShowItem item = listItemList.get(position);
        remoteView.setTextViewText(R.id.time_start_text, item.getTimeStart());
        remoteView.setTextViewText(R.id.time_finish_text, item.getTimeFinish());
        remoteView.setTextViewText(R.id.pos_text, item.getPos());
        remoteView.setTextViewText(R.id.tenMH_text, item.getTenMH());
        remoteView.setTextViewText(R.id.maMH_text, item.getMaMH());
        remoteView.setTextViewText(R.id.nhom_text, item.getNhom());
        remoteView.setTextViewText(R.id.to_text, item.getTo());
        remoteView.setTextViewText(R.id.phong_text, item.getPhong());

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        showTkb();
    }

    @Override
    public void onDestroy() {
    }

}
