package edu.tdt.appstudent2.actitities.hdpt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import de.mrapp.android.bottomsheet.BottomSheet;
import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.adapters.thongbao.FragmentAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.fragments.hdpt.HdptDanhgiaFragment;
import edu.tdt.appstudent2.fragments.hdpt.HdptHoatdongFragment;
import edu.tdt.appstudent2.models.Config;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.hdpt.HdptDanhgiaItem;
import edu.tdt.appstudent2.models.hdpt.HdptHoatdongItem;
import edu.tdt.appstudent2.models.hdpt.HdptHockyItem;
import edu.tdt.appstudent2.models.hdpt.HdptItem;
import edu.tdt.appstudent2.models.hdpt.HdptTagDanhgiaItem;
import io.realm.Realm;
import io.realm.RealmResults;

public class HdptActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;

    private Realm realm;
    private User user;
    private String userText, passText;

    private String idHocKy = null;
    private ArrayList<HdptHockyItem> hdptHockyItems;
    private HdptItem hdptItem;


    private MultiStateView mMultiStateView;
    private AppCompatImageButton btnBack;
    private MaterialRippleLayout btnChonHocKy;

    private BottomSheet.Builder builder;
    private BottomSheet bottomSheet;
    private TextView tvTiteHocKy;

    private String idHocKyMacDinh = "";

    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();


        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);

        hdptHockyItems = new ArrayList<HdptHockyItem>();

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
                reload();
            }
        });

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnChonHocKy = (MaterialRippleLayout) findViewById(R.id.btnChonHocKy);
        btnChonHocKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.show();
            }
        });

        tvTiteHocKy = (TextView)findViewById(R.id.tvHocKy);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdpt);
        anhXa();
        checkOffline();
    }
    private void reload(){
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();
        hdptHockyItems.clear();

        realm.beginTransaction();
        realm.delete(HdptHockyItem.class);
        realm.delete(HdptItem.class);
        realm.commitTransaction();
        getHdptHocky();
    }

    private void setIdHocKyMacDinh(){
        realm.beginTransaction();
        if(user.getConfig() == null){
            Config config = realm.createObject(Config.class);
            user.setConfig(config);
        }
        user.getConfig().setHdptIdHocKyMacDinh(idHocKy);
        realm.commitTransaction();
        idHocKyMacDinh = idHocKy;
        invalidateOptionsMenu();
    }

    private void checkOffline(){
        if(realm.where(HdptHockyItem.class).count() > 0){
            RealmResults<HdptHockyItem> realmResults = realm.where(HdptHockyItem.class).findAll();
            hdptHockyItems.addAll(realmResults);
            showHdptHocky();
        }else {
            getHdptHocky();
        }
    }

    
    private void checkHdptOffline(){
        hdptItem = realm.where(HdptItem.class)
                .equalTo("idHocKy", idHocKy)
                .findFirst();
        if(hdptItem == null){
            getHdpt();
        }else {
            showHdpt();
        }
    }

    private void getHdpt(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHdpt().execute();
            }
        });
    }

    public class getHdpt extends AsyncTask<Void , Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("id", idHocKy)
                        .data("act", "hdpt")
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
                            hdptItem = new HdptItem();
                            hdptItem.setIdHocKy(idHocKy);
                            JSONObject dataRootObject = root.getJSONObject("data");
                            JSONArray hdptArray = dataRootObject.getJSONArray("hdpt");
                            JSONObject hdptObject = null;
                            HdptHoatdongItem hdptHoatdongItem = null;
                            for(int i = 0 ; i < hdptArray.length(); i++){
                                hdptObject = hdptArray.getJSONObject(i);
                                hdptHoatdongItem = new HdptHoatdongItem();
                                hdptHoatdongItem.setsTT(hdptObject.getString("STT"));
                                hdptHoatdongItem.setTenSuKien(hdptObject.getString("TenSuKien"));
                                hdptHoatdongItem.setThoiGian(hdptObject.getString("ThoiGian").replace("\n", ""));
                                hdptHoatdongItem.setDiemRL(hdptObject.getString("DiemRL"));
                                hdptItem.getHdptHoatdongItems().add(hdptHoatdongItem);
                            }

                            JSONArray drlArray = dataRootObject.getJSONArray("drl");
                            JSONObject drlObject = null;
                            HdptTagDanhgiaItem hdptTagDanhgiaItem = null;
                            HdptDanhgiaItem hdptDanhgiaItem = null;
                            for(int i = 0 ; i < drlArray.length(); i++){
                                drlObject = drlArray.getJSONObject(i);
                                hdptTagDanhgiaItem = new HdptTagDanhgiaItem();
                                hdptTagDanhgiaItem.setTitle(drlObject.getString("title"));
                                if(drlObject.has("data")) {
                                    JSONArray dataArray = drlObject.getJSONArray("data");
                                    JSONObject dataObject = null;
                                    for (int j = 0; j < dataArray.length(); j++) {
                                        dataObject = dataArray.getJSONObject(j);
                                        hdptDanhgiaItem = new HdptDanhgiaItem();
                                        hdptDanhgiaItem.setsTT(dataObject.getString("STT"));
                                        hdptDanhgiaItem.setNoiDung(dataObject.getString("NoiDung"));
                                        hdptDanhgiaItem.setKetQua(dataObject.getString("KetQua"));
                                        hdptDanhgiaItem.setDiem(dataObject.getString("Diem"));
                                        hdptTagDanhgiaItem.getHdptDanhgiaItems().add(hdptDanhgiaItem);
                                    }
                                }
                                hdptItem.getHdptTagDanhgiaItems().add(hdptTagDanhgiaItem);
                            }
                            hdptItem.setDiem(dataRootObject.getString("diem"));
                            realm.copyToRealmOrUpdate(hdptItem);
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
            showHdpt();
        }
    }


    private void showHdpt(){
        addPaper();
    }

    private void addPaper(){
        tabs.setVisibility(View.VISIBLE);

        Bundle bundle = null;

        viewPager.removeAllViews();
        fragmentAdapter.clearTitle();
        fragmentArrayList.clear();
        fragmentAdapter.notifyDataSetChanged();

        HdptHoatdongFragment hdptHoatdongFragment = new HdptHoatdongFragment();
        bundle = new Bundle();
        bundle.putString(HdptHoatdongFragment.EXTRA_IDHOCKY, idHocKy);
        hdptHoatdongFragment.setArguments(bundle);
        fragmentArrayList.add(hdptHoatdongFragment);
        fragmentAdapter.addTitle("Hoạt động phong trào");

        HdptDanhgiaFragment hdptDanhgiaFragment = new HdptDanhgiaFragment();
        bundle = new Bundle();
        bundle.putString(HdptDanhgiaFragment.EXTRA_IDHOCKY, idHocKy);
        hdptDanhgiaFragment.setArguments(bundle);
        fragmentArrayList.add(hdptDanhgiaFragment);
        fragmentAdapter.addTitle("Kết quả đánh giá rèn luyện");

        fragmentAdapter.notifyDataSetChanged();

    }

    private void getHdptHocky(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHdptHocky().execute();
            }
        });
    }

    public class getHdptHocky extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "hdpt")
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
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            realm.beginTransaction();
                            JSONArray data = root.getJSONArray("data");
                            JSONObject get = null;
                            HdptHockyItem hdptHockyItem = null;
                            for(int i = 0 ; i < data.length(); i++){
                                get = data.getJSONObject(i);
                                hdptHockyItem = new HdptHockyItem();
                                hdptHockyItem.setId(get.getString("id"));
                                hdptHockyItem.setTenHocKy(get.getString("TenHocKy"));
                                hdptHockyItems.add(hdptHockyItem);
                                realm.copyToRealmOrUpdate(hdptHockyItem);
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
            showHdptHocky();
        }
    }

    private void showHdptHocky(){
        builder = new BottomSheet.Builder(this);
        builder.setTitle(R.string.bottom_sheet_title_hocky);
        int id = 0;
        for(HdptHockyItem e: hdptHockyItems){
            builder.addItem(id, e.getTenHocKy(), ContextCompat.getDrawable(this, R.drawable.ic_radio_button_unchecked_black_24dp));
            id++;
        }
        bottomSheet = builder.create();
        bottomSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chonHocKy(position);
            }
        });

        int pos = -1;
        //Chuyển đến thời khóa biểu mặc định
        if(user.getConfig() != null){
            if(user.getConfig().getHdptIdHocKyMacDinh() != null) {
                idHocKyMacDinh = user.getConfig().getHdptIdHocKyMacDinh();
                for (int i = 0; i < hdptHockyItems.size(); i++) {
                    if (hdptHockyItems.get(i).getId().equals(idHocKyMacDinh)) {
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
            chonHocKy(hdptHockyItems.size() - 1);
        }
    }

    private void chonHocKy(int postition){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        tvTiteHocKy.setText(hdptHockyItems.get(postition).getTenHocKy());
        idHocKy = hdptHockyItems.get(postition).getId();
        invalidateOptionsMenu();
        checkHdptOffline();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_hdpt, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem settingsItem = menu.findItem(R.id.action_set_default);

        if(settingsItem != null){
            if(idHocKyMacDinh.equals(idHocKy)){
                settingsItem.setIcon(getResources().getDrawable(R.drawable.ic_star_black_24dp));
            }else{
                settingsItem.setIcon(getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_reload:
                reload();
                break;
            case R.id.action_set_default:
                setIdHocKyMacDinh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
