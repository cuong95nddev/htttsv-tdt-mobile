package edu.tdt.appstudent2.fragments.lichthi;

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
import java.util.Calendar;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.OnChildSwipeRefreshListener;
import edu.tdt.appstudent2.adapters.lichthi.LichThiRecyclerViewAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.lichthi.LichThiDateShowItem;
import edu.tdt.appstudent2.models.lichthi.LichThiItem;
import edu.tdt.appstudent2.models.lichthi.LichThiLichItem;
import edu.tdt.appstudent2.utils.Tag;
import edu.tdt.appstudent2.views.custom.TopSnappedStickyLayoutManager;
import io.realm.Realm;
import io.realm.RealmList;

public class LichThiFragment extends Fragment {
    public static final String ARG_TYPE = "ARG_TYPE";
    private int type;

    private View inflatedView = null;
    private String idHocky;

    private RecyclerView recyclerView;
    private LichThiRecyclerViewAdapter adapter;
    private StickyLayoutManager manager;

    private ArrayList<Object> list;

    private Realm realm;
    private User user;
    private String userText, passText;

    private LichThiItem lichThiItem;
    private RealmList<LichThiLichItem> lichThiLichItems;

    private Calendar calendar;

    private SwipeRefreshLayout swipeContainer;
    private OnChildSwipeRefreshListener onChildSwipeRefreshListener;

    public LichThiFragment() {

    }

    private void anhXa(){
        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new LichThiRecyclerViewAdapter(list);
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
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
            idHocky = getArguments().getString(Tag.idHocKy);
        }
        list = new ArrayList<Object>();

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_lich_thi, container, false);
        anhXa();
        checkOffline();
        return inflatedView;
    }

    private void checkOffline(){
        lichThiItem = realm.where(LichThiItem.class)
                .equalTo("idHocKy", idHocky)
                .findFirst();
        if(lichThiItem == null){

        }else {
            loadOffline();
        }
    }

    private void loadOffline(){
        if(type == 1){
            lichThiLichItems = lichThiItem.getGiuaKy();
        }else{
            lichThiLichItems = lichThiItem.getCuoiKy();
        }

        String[] date = null;
        LichThiDateShowItem lichThiDateShowItem = null;
        LichThiLichItem lichThiLichItem = null;
        for(int i = 0 ; i < lichThiLichItems.size(); i++){
            lichThiLichItem = lichThiLichItems.get(i);
            date = lichThiLichItem.getNgayThi().split("[/]");
            calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(date[2]));

            if(i == 0 || !lichThiLichItem.getNgayThi().equals(lichThiLichItems.get(i - 1).getNgayThi())){
                int thu = calendar.get(Calendar.DAY_OF_WEEK);
                lichThiDateShowItem = new LichThiDateShowItem();
                lichThiDateShowItem.setDate(lichThiLichItem.getNgayThi());
                lichThiDateShowItem.setDay("Thứ " + thu);
                list.add(lichThiDateShowItem);
            }

            list.add(lichThiLichItem);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
