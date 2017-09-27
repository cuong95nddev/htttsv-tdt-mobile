package edu.tdt.appstudent2.fragments.tkb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.OnChildSwipeRefreshListener;
import edu.tdt.appstudent2.adapters.tkb.TkbNgayRecyclerViewAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.tkb.TkbItem;
import edu.tdt.appstudent2.models.tkb.TkbLichItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocShowItem;
import edu.tdt.appstudent2.models.tkb.TkbThuItem;
import edu.tdt.appstudent2.utils.Tag;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;


public class TkbNgayFragment extends Fragment implements View.OnClickListener ,
        DatePickerDialog.OnDateSetListener{
    private View inflatedView = null;
    private String idHocky;
    private RecyclerView recyclerView;
    private TkbNgayRecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<TkbMonhocShowItem> list;

    private Realm realm;
    private User user;
    private String userText, passText;

    private TkbItem tkbItem;

    private Calendar calendarToDay;
    private Calendar calendarStart;
    private ArrayList<TkbThuItem> tkbThuItems;

    private ImageButton btnDayBefore, btnDayNext;
    private TextView tvDateSelected, tvDaySelected;

    private MultiStateView mMultiStateView;

    private SwipeRefreshLayout swipeContainer;
    private OnChildSwipeRefreshListener onChildSwipeRefreshListener;

    private void khoiTao(){
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            idHocky = bundle.getString(Tag.idHocKy);
        }
        list = new ArrayList<TkbMonhocShowItem>();

        calendarToDay = Calendar.getInstance();
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new TkbNgayRecyclerViewAdapter(list);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        mMultiStateView = (MultiStateView) inflatedView.findViewById(R.id.multiStateView);

        btnDayBefore = (ImageButton) inflatedView.findViewById(R.id.btnDayBefore);
        btnDayBefore.setOnClickListener(this);
        btnDayNext = (ImageButton) inflatedView.findViewById(R.id.btnDayNext);
        btnDayNext.setOnClickListener(this);

        tvDateSelected = (TextView) inflatedView.findViewById(R.id.tvDateSelected);
        tvDateSelected.setOnClickListener(this);
        tvDaySelected = (TextView) inflatedView.findViewById(R.id.tvDaySelected);
        tvDaySelected.setOnClickListener(this);

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
    public TkbNgayFragment() {
        // Required empty public constructor
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
        this.inflatedView = inflater.inflate(R.layout.fragment_tkb_ngay, container, false);
        anhXa();
        checkOffline();
        return inflatedView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnDayNext:
                calendarToDay.add(Calendar.DAY_OF_YEAR, 1);
                getTkb();
                break;
            case R.id.btnDayBefore:
                calendarToDay.add(Calendar.DAY_OF_YEAR, -1);
                getTkb();
                break;
            case R.id.tvDateSelected:
                chonNgay();
                break;
            case R.id.tvDaySelected:
                toNow();
                break;
        }
    }

    private void toNow() {
        calendarToDay = Calendar.getInstance();
        getTkb();
    }

    private void chonNgay(){
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                TkbNgayFragment.this,
                calendarToDay.get(Calendar.YEAR),
                calendarToDay.get(Calendar.MONTH),
                calendarToDay.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
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
        tvDateSelected.setText(Util.showCalendar(calendarToDay));
        tvDaySelected.setText(Util.getThuCalendar(calendarToDay));
        list.clear();
        adapter.notifyDataSetChanged();
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
                        list.add(e);
                    }
                }
            }
        }
        if(list.size() == 0){
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        }else {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendarToDay.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendarToDay.set(Calendar.MONTH, monthOfYear);
        calendarToDay.set(Calendar.YEAR, year);
        getTkb();
    }
}
