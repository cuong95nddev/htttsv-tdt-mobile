package edu.tdt.appstudent2.actitities.tkb;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.balysv.materialripple.MaterialRippleLayout;
import com.kennyc.view.MultiStateView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.actitities.OnChildSwipeRefreshListener;
import edu.tdt.appstudent2.actitities.dangnhap.DangnhapActivity;
import edu.tdt.appstudent2.adapters.thongbao.FragmentAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.fragments.tkb.TkbNgayFragment;
import edu.tdt.appstudent2.fragments.tkb.TkbTonghopFragment;
import edu.tdt.appstudent2.fragments.tkb.TkbTuanFragment;
import edu.tdt.appstudent2.models.Config;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.tkb.TkbHockyItem;
import edu.tdt.appstudent2.models.tkb.TkbItem;
import edu.tdt.appstudent2.models.tkb.TkbLichItem;
import edu.tdt.appstudent2.models.tkb.TkbMonhocItem;
import edu.tdt.appstudent2.utils.Tag;
import edu.tdt.appstudent2.widget.TkbWidget;
import io.realm.Realm;
import io.realm.RealmResults;

public class TkbActivity extends AppCompatActivity implements OnChildSwipeRefreshListener{
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;

    private Realm realm;
    private User user;
    private String userText, passText;

    private String idHocKy = null;
    private ArrayList<TkbHockyItem> tkbHockyItems;
    private TkbItem tkbItem;

    private MultiStateView mMultiStateView;
    private AppCompatImageButton btnBack;
    private AppCompatImageButton btnReload;
    private AppCompatImageButton btnDefault;

    private MaterialRippleLayout btnChonHocKy;

    private TextView tvTiteHocKy;

    private String idHocKyMacDinh = "";

    AlertDialog.Builder dialogHocKy;

    private void checkUser(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        if(user == null){
            Intent dangNhap = new Intent(this, DangnhapActivity.class);
            startActivity(dangNhap);
            finish();
        }else{
            anhXa();
            checkOffline();
        }

    }


    private void khoiTao(){
        userText = user.getUserName();
        passText = user.getPassWord();
        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);
        tkbHockyItems = new ArrayList<TkbHockyItem>();
    }
    private void anhXa(){
        khoiTao();
        viewPager = (ViewPager) findViewById(R.id.viewpaper);
        viewPager.setAdapter(fragmentAdapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);
        tabs.setVisibility(View.GONE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTkb();
            }
        });

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnReload = (AppCompatImageButton) findViewById(R.id.btnReload);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });
        btnDefault = (AppCompatImageButton) findViewById(R.id.btnDefault);
        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIdHocKyMacDinh();
            }
        });

        btnChonHocKy = (MaterialRippleLayout) findViewById(R.id.btnChonHocKy);
        btnChonHocKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHocKy.show();
            }
        });

        tvTiteHocKy = (TextView)findViewById(R.id.tvHocKy);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tkb);
        checkUser();
    }


    private void reload(){
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();
        tkbHockyItems.clear();

        realm.beginTransaction();
        realm.delete(TkbHockyItem.class);
        realm.delete(TkbItem.class);
        realm.delete(TkbLichItem.class);
        realm.delete(TkbMonhocItem.class);
        realm.commitTransaction();
        getTkbHocky();
    }

    private void setIdHocKyMacDinh(){
        realm.beginTransaction();
        if(user.getConfig() == null){
            Config config = realm.createObject(Config.class);
            user.setConfig(config);
        }
        user.getConfig().setIdHocKyMacDinh(idHocKy);
        idHocKyMacDinh = idHocKy;
        realm.commitTransaction();

        updateWidget();

        setIconDefault();
    }

    private void updateWidget(){
        Intent intent = new Intent(this, TkbWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), TkbWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    private void setIconDefault(){
        if(idHocKyMacDinh != null){
            if(idHocKyMacDinh.equals(idHocKy)){
                btnDefault.setImageResource(R.drawable.ic_favorite_black_24dp);
            }else{
                btnDefault.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }
    }

    private void checkOffline(){
        if(realm.where(TkbHockyItem.class).count() > 0){
            RealmResults<TkbHockyItem> realmResults = realm.where(TkbHockyItem.class).findAll();
            tkbHockyItems.addAll(realmResults);
            showTkbHocky();
        }else {
            getTkbHocky();
        }
    }

    private void checkTkbOffline(){
        tkbItem = realm.where(TkbItem.class)
                .equalTo("idHocKy", idHocKy)
                .findFirst();
        if(tkbItem == null){
            getTkb();
        }else {
            showTkb();
        }
    }

    private void getTkb(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getTkb().execute("");
            }
        });
    }

    @Override
    public void onChildSwipeRefreshListener() {
        getTkb();
    }

    public class getTkb extends AsyncTask<String , Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "tkb")
                        .data("id", idHocKy)
                        .data("option", "ln")
                        .timeout(30000)
                        .get();
                return doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if(s != null) {
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {

                            realm.beginTransaction();
                            tkbItem = new TkbItem();
                            JSONObject data = root.getJSONObject("data");
                            tkbItem.setIdHocKy(idHocKy);
                            tkbItem.setDateStart(data.getString("start"));
                            JSONArray tkbArray = data.getJSONArray("tkb");
                            TkbMonhocItem tkbMonhocItem = null;
                            TkbLichItem tkbLichItem = null;
                            JSONObject tkbObject = null;
                            JSONArray lichArray = null;
                            JSONObject lichObject = null;
                            for(int i = 0 ; i < tkbArray.length() ; i++){
                                tkbObject = tkbArray.getJSONObject(i);
                                tkbMonhocItem = new TkbMonhocItem();
                                tkbMonhocItem.setMaMH(tkbObject.getString("MaMH").trim());
                                tkbMonhocItem.setTenMH(tkbObject.getString("TenMH").trim());
                                tkbMonhocItem.setNhom(tkbObject.getString("Nhom").trim());
                                tkbMonhocItem.setTo(tkbObject.getString("To").trim());

                                lichArray = tkbObject.getJSONArray("Lich");
                                for(int j = 0 ; j < lichArray.length(); j++){
                                    lichObject = lichArray.getJSONObject(j);
                                    tkbLichItem = new TkbLichItem();
                                    tkbLichItem.setPhong(lichObject.getString("phong").trim());
                                    tkbLichItem.setThu(lichObject.getString("thu").trim());
                                    tkbLichItem.setTiet(lichObject.getString("tiet").trim());
                                    tkbLichItem.setTuan(lichObject.getString("tuan").trim());
                                    if(tkbItem.getnTuan() == 0){
                                        tkbItem.setnTuan(tkbLichItem.getTuan().length());
                                    }
                                    tkbMonhocItem.getTkbLichItems().add(tkbLichItem);
                                }
                                tkbItem.getTkbMonhocItems().add(tkbMonhocItem);
                            }
                            realm.copyToRealmOrUpdate(tkbItem);
                            realm.commitTransaction();
                        }else{
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                        }
                    }else{
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    realm.commitTransaction();
                } catch (java.lang.IllegalStateException e) {
                    realm.commitTransaction();
                }
            }
            showTkb();
        }
    }


    private void showTkb(){
        addPaper();
    }

    private void addPaper(){
        tabs.setVisibility(View.VISIBLE);

        Bundle bundle = null;


        viewPager.removeAllViews();
        fragmentAdapter.clearTitle();
        fragmentArrayList.clear();
        fragmentAdapter.notifyDataSetChanged();

        bundle = new Bundle();
        bundle.putString(Tag.idHocKy, idHocKy);
        TkbNgayFragment tkbNgayFragment = new TkbNgayFragment();
        tkbNgayFragment.setArguments(bundle);
        fragmentArrayList.add(tkbNgayFragment);
        fragmentAdapter.addTitle("NGÀY");

        bundle = new Bundle();
        bundle.putString(Tag.idHocKy, idHocKy);
        TkbTuanFragment tkbTuanFragment = new TkbTuanFragment();
        tkbTuanFragment.setArguments(bundle);
        fragmentArrayList.add(tkbTuanFragment);
        fragmentAdapter.addTitle("TUẦN");

        bundle = new Bundle();
        bundle.putString(Tag.idHocKy, idHocKy);
        TkbTonghopFragment tkbTonghopFragment = new TkbTonghopFragment();
        tkbTonghopFragment.setArguments(bundle);
        fragmentArrayList.add(tkbTonghopFragment);
        fragmentAdapter.addTitle("TỔNG QUÁT");
        fragmentAdapter.notifyDataSetChanged();
    }

    private void getTkbHocky(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getTkbHocky().execute("");
            }
        });
    }

    public class getTkbHocky extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "tkb")
                        .data("option", "lhk")
                        .timeout(30000)
                        .get();
                return doc.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if(s != null) {
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")){
                        if(root.getBoolean("status")){
                            JSONArray data = root.getJSONArray("data");
                            JSONObject get = null;
                            TkbHockyItem tkbHockyItemGet = null;
                            realm.beginTransaction();
                            for(int i = 0 ; i < data.length(); i++){
                                get = data.getJSONObject(i);
                                tkbHockyItemGet = new TkbHockyItem();
                                tkbHockyItemGet.setId(get.getString("id"));
                                tkbHockyItemGet.setTenHocKy(get.getString("TenHocKy"));
                                tkbHockyItems.add(tkbHockyItemGet);
                                realm.copyToRealmOrUpdate(tkbHockyItemGet);
                            }
                            realm.commitTransaction();
                        }else{
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                        }
                    }else{
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {

                }
            }
            showDialogHocKy();
        }
    }

    private void showDialogHocKy(){
        initDialogHocKy();
        dialogHocKy.show();
    }

    private void showTkbHocky(){
        int pos = -1;
        //Chuyển đến thời khóa biểu mặc định
        if(user.getConfig() != null){
            if(user.getConfig().getIdHocKyMacDinh() != null) {
                idHocKyMacDinh = user.getConfig().getIdHocKyMacDinh();
                for (int i = 0; i < tkbHockyItems.size(); i++) {
                    if (tkbHockyItems.get(i).getId().equals(idHocKyMacDinh)) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1) {
                    chonHocKy(pos);
                }
            }
        }

        if(pos == -1){
            if(tkbHockyItems.size() > 0){
                chonHocKy(0);
            }
        }

        initDialogHocKy();
    }

    private void initDialogHocKy(){
        ArrayAdapter<String> tenHocKys = new ArrayAdapter<String>(this
                , R.layout.my_select_dialog_item);

        for(TkbHockyItem e: tkbHockyItems){
            tenHocKys.add(e.getTenHocKy());
        }

        dialogHocKy = new AlertDialog.Builder(this)
                .setTitle(R.string.bottom_sheet_title_hocky)
                .setAdapter(tenHocKys, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chonHocKy(i);
                    }
                });

        dialogHocKy.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void chonHocKy(int postition){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        tvTiteHocKy.setText(tkbHockyItems.get(postition).getTenHocKy());
        idHocKy = tkbHockyItems.get(postition).getId();
        setIconDefault();
        checkTkbOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
