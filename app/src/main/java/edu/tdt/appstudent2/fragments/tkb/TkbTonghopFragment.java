package edu.tdt.appstudent2.fragments.tkb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brandongogetap.stickyheaders.StickyLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.OnChildSwipeRefreshListener;
import edu.tdt.appstudent2.adapters.tkb.TkbTonghopRecyclerViewAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.tkb.TkbItem;
import edu.tdt.appstudent2.models.tkb.TkbLichItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocShowItem;
import edu.tdt.appstudent2.models.tkb.TkbThuItem;
import edu.tdt.appstudent2.models.tkb.TkbThuShowItem;
import edu.tdt.appstudent2.utils.Tag;
import edu.tdt.appstudent2.utils.Util;
import edu.tdt.appstudent2.views.custom.TopSnappedStickyLayoutManager;
import io.realm.Realm;


public class TkbTonghopFragment extends Fragment {
    private View inflatedView = null;
    private String idHocky;
    private RecyclerView recyclerView;
    private TkbTonghopRecyclerViewAdapter adapter;
    private StickyLayoutManager manager;

    private ArrayList<Object> list;

    private Realm realm;
    private User user;
    private String userText, passText;

    private TkbItem tkbItem;
    private ArrayList<TkbMonhocShowItem> tkbMonhocShowItems;

    private SwipeRefreshLayout swipeContainer;
    private OnChildSwipeRefreshListener onChildSwipeRefreshListener;

    private void khoiTao(){
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            idHocky = bundle.getString(Tag.idHocKy);
        }
        list = new ArrayList<Object>();
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new TkbTonghopRecyclerViewAdapter(list);
        manager = new TopSnappedStickyLayoutManager(inflatedView.getContext(), adapter);
        manager.elevateHeaders(1);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(onChildSwipeRefreshListener != null){
                    onChildSwipeRefreshListener.onChildSwipeRefreshListener();
                }
                swipeContainer.setRefreshing(false);
            }
        });
    }
    public TkbTonghopFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnChildSwipeRefreshListener){
            onChildSwipeRefreshListener = (OnChildSwipeRefreshListener)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_tkb_tonghop, container, false);
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
        TkbMonhocShowItem tkbMonhocShowItem = null;
        ArrayList<TkbThuItem> tkbThuItems = new ArrayList<TkbThuItem>();
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
                list.add(e);
            }
        }
        list.add(new TkbThuShowItem("Chủ nhật", "#303F9F"));
        for(TkbMonhocShowItem e: tkbThuItems.get(0).getTkbMonhocShowItems()){
            list.add(e);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
