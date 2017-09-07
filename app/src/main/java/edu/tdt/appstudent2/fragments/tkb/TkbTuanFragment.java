package edu.tdt.appstudent2.fragments.tkb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brandongogetap.stickyheaders.StickyLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.adapters.tkb.TkbTonghopRecyclerViewAdapter;
import edu.tdt.appstudent2.adapters.tkb.TkbTuanTileAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.tkb.TkbItem;
import edu.tdt.appstudent2.models.tkb.TkbLichItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocShowItem;
import edu.tdt.appstudent2.models.tkb.TkbThuItem;
import edu.tdt.appstudent2.models.tkb.TkbThuShowItem;
import edu.tdt.appstudent2.models.tkb.TkbTuanItem;
import edu.tdt.appstudent2.utils.Tag;
import edu.tdt.appstudent2.utils.Util;
import edu.tdt.appstudent2.views.custom.TopSnappedStickyLayoutManager;
import io.realm.Realm;


public class TkbTuanFragment extends Fragment {

    private View inflatedView = null;
    private String idHocky;

    private Realm realm;
    private TkbItem tkbItem;
    private User user;


    private RecyclerView rvTuan;
    private TkbTuanTileAdapter tkbTuanTileAdapter;
    private StaggeredGridLayoutManager tkbTuanManager;

    private RecyclerView recyclerView;
    private TkbTonghopRecyclerViewAdapter adapter;
    private StickyLayoutManager manager;
    private ArrayList<Object> list;


    private ArrayList<TkbThuItem> tkbThuItems;


    public TkbTuanFragment() {

    }


    private void khoiTao(){
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            idHocky = bundle.getString(Tag.idHocKy);
        }

        list = new ArrayList<Object>();
    }

    private void anhXa(){
        khoiTao();
        rvTuan = (RecyclerView) inflatedView.findViewById(R.id.rvTuan);
        tkbTuanTileAdapter = new TkbTuanTileAdapter(getActivity());
        tkbTuanManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        tkbTuanManager.setSpanCount(1);
        rvTuan.setLayoutManager(tkbTuanManager);
        rvTuan.setHasFixedSize(true);
        rvTuan.setAdapter(tkbTuanTileAdapter);

        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new TkbTonghopRecyclerViewAdapter(list);
        manager = new TopSnappedStickyLayoutManager(inflatedView.getContext(), adapter);
        manager.elevateHeaders(1);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        tkbTuanTileAdapter.setOnItemClickListener(new TkbTuanTileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectTuan(position);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_tkb_tuan, container, false);
        anhXa();
        checkOffline();
        return inflatedView;
    }

    private void checkOffline(){
        tkbItem = realm.where(TkbItem.class)
                .equalTo("idHocKy", idHocky)
                .findFirst();
        if(tkbItem == null){

        }else {
            loadTkbOffline();
        }
    }

    private void loadTkbOffline(){
        int nTuan = tkbItem.getnTuan();
        String[] ngayBatDau = tkbItem.getDateStart().split("[/]");

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.clear();
        calendarStart.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ngayBatDau[0].trim()));
        calendarStart.set(Calendar.MONTH, Integer.parseInt(ngayBatDau[1].trim()) - 1);
        calendarStart.set(Calendar.YEAR, Integer.parseInt(ngayBatDau[2].trim()));

        Calendar calendarToDay = Calendar.getInstance();
        int weekToDay = calendarToDay.get(Calendar.WEEK_OF_YEAR) - calendarStart.get(Calendar.WEEK_OF_YEAR);

        TkbTuanItem tkbTuanItem = null;

        for(int i = 0 ; i < nTuan; i++){
            tkbTuanItem = new TkbTuanItem();
            tkbTuanItem.setNgayBatDau(calendarStart);
            tkbTuanItem.setTuan(i+1);
            tkbTuanTileAdapter.addItem(tkbTuanItem);
            calendarStart.add(Calendar.DAY_OF_YEAR, 7);
        }

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

        if(weekToDay >= 0 && weekToDay < nTuan){
            selectTuan(weekToDay);
        }
    }

    private void selectTuan(int tuan){
        tuan++;
        tkbTuanTileAdapter.selected(tuan);
        rvTuan.smoothScrollToPosition(tuan);

        list.clear();
        adapter.notifyDataSetChanged();


        for(int i = 0 ; i < 7; i ++) {
            Collections.sort(tkbThuItems.get(i).getTkbMonhocShowItems(), new Comparator<TkbMonhocShowItem>() {
                @Override
                public int compare(TkbMonhocShowItem tkbMonhocShowItem, TkbMonhocShowItem t1) {
                    return tkbMonhocShowItem.getTimeStart().compareTo(t1.getTimeStart());
                }
            });
        }

        for(int i = 1 ; i < 7; i++){
            list.add(new TkbThuShowItem("Thứ " + Integer.toString(i + 1), "#303F9F"));
            for(TkbMonhocShowItem e: tkbThuItems.get(i).getTkbMonhocShowItems()){
                String[] tuanHocArray = e.getTuan().split("");
                if(!tuanHocArray[tuan].equals("-")){
                    list.add(e);
                }
            }
        }
        list.add(new TkbThuShowItem("Chủ nhật", "#303F9F"));
        for(TkbMonhocShowItem e: tkbThuItems.get(0).getTkbMonhocShowItems()){
            String[] tuanHocArray = e.getTuan().split("");
            if(!tuanHocArray[tuan].equals("-")){
                list.add(e);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
