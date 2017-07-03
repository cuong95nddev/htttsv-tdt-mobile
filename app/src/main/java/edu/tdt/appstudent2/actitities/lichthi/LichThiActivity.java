package edu.tdt.appstudent2.actitities.lichthi;

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
import java.util.Collections;
import java.util.Comparator;

import de.mrapp.android.bottomsheet.BottomSheet;
import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.adapters.thongbao.FragmentAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.fragments.lichthi.LichThiFragment;
import edu.tdt.appstudent2.models.Config;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.lichthi.LichThiHocKyItem;
import edu.tdt.appstudent2.models.lichthi.LichThiItem;
import edu.tdt.appstudent2.models.lichthi.LichThiLichItem;
import edu.tdt.appstudent2.utils.Tag;
import io.realm.Realm;
import io.realm.RealmResults;

public class LichThiActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;

    private Realm realm;
    private User user;
    private String userText, passText;

    private String idHocKy = null;
    private ArrayList<LichThiHocKyItem> lichThiHocKyItems;
    private LichThiItem lichThiItem;

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

        lichThiHocKyItems = new ArrayList<LichThiHocKyItem>();
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
        setContentView(R.layout.activity_lich_thi);
        anhXa();
        checkOffline();
    }

    private void reload(){
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();
        lichThiHocKyItems.clear();

        realm.beginTransaction();
        realm.delete(LichThiHocKyItem.class);
        realm.delete(LichThiItem.class);
        realm.delete(LichThiLichItem.class);
        realm.commitTransaction();
        getHocky();
    }

    private void setIdHocKyMacDinh(){
        realm.beginTransaction();
        if(user.getConfig() == null){
            Config config = realm.createObject(Config.class);
            user.setConfig(config);
        }
        user.getConfig().setLichThiIdHocKyMacDinh(idHocKy);
        idHocKyMacDinh = idHocKy;
        realm.commitTransaction();
        invalidateOptionsMenu();
    }

    private void checkOffline(){
        if(realm.where(LichThiHocKyItem.class).count() > 0){
            RealmResults<LichThiHocKyItem> realmResults = realm.where(LichThiHocKyItem.class).findAll();
            lichThiHocKyItems.addAll(realmResults);
            showHocky();
        }else {
            getHocky();
        }
    }

    private void checkLichThiOffline(){
        lichThiItem = realm.where(LichThiItem.class)
                .equalTo("idHocKy", idHocKy)
                .findFirst();
        if(lichThiItem == null){
            getLichThi();
        }else {
            showLichThi();
        }
    }

    private void getLichThi(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetLichThi().execute("");
            }
        });
    }

    public class GetLichThi extends AsyncTask<String , Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "lt")
                        .data("id", idHocKy)
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
                            lichThiItem = new LichThiItem();
                            JSONObject data = root.getJSONObject("data");
                            lichThiItem.setIdHocKy(idHocKy);

                            JSONArray lichThiGiaKyArray = data.getJSONArray("giuaky");
                            LichThiLichItem lichThiLichItem = null;

                            JSONObject licThiObject = null;
                            for(int i = 0 ; i < lichThiGiaKyArray.length(); i++){
                                licThiObject = lichThiGiaKyArray.getJSONObject(i);
                                lichThiLichItem = new LichThiLichItem();
                                lichThiLichItem.setTenMH(licThiObject.getString("TenMH"));
                                lichThiLichItem.setGioThi(licThiObject.getString("GioThi"));
                                lichThiLichItem.setMaMH(licThiObject.getString("MaMH"));
                                lichThiLichItem.setNgayThi(licThiObject.getString("NgayThi"));
                                lichThiLichItem.setPhong(licThiObject.getString("Phong"));
                                lichThiLichItem.setThoiLuong(licThiObject.getString("ThoiLuong"));
                                lichThiLichItem.setTo(licThiObject.getString("To"));
                                lichThiLichItem.setNhom(licThiObject.getString("Nhom"));
                                lichThiItem.getGiuaKy().add(lichThiLichItem);
                            }

                            Collections.sort(lichThiItem.getGiuaKy(), new Comparator<LichThiLichItem>() {
                                @Override
                                public int compare(LichThiLichItem o1, LichThiLichItem o2) {
                                    return o1.getNgayThi().compareTo(o2.getNgayThi());
                                }
                            });


                            JSONArray lichThiCuoiKyArray = data.getJSONArray("cuoiky");
                            lichThiLichItem = null;

                            licThiObject = null;
                            for(int i = 0 ; i < lichThiCuoiKyArray.length(); i++){
                                licThiObject = lichThiCuoiKyArray.getJSONObject(i);
                                lichThiLichItem = new LichThiLichItem();
                                lichThiLichItem.setTenMH(licThiObject.getString("TenMH"));
                                lichThiLichItem.setGioThi(licThiObject.getString("GioThi"));
                                lichThiLichItem.setMaMH(licThiObject.getString("MaMH"));
                                lichThiLichItem.setNgayThi(licThiObject.getString("NgayThi"));
                                lichThiLichItem.setPhong(licThiObject.getString("Phong"));
                                lichThiLichItem.setThoiLuong(licThiObject.getString("ThoiLuong"));
                                lichThiLichItem.setTo(licThiObject.getString("To"));
                                lichThiLichItem.setNhom(licThiObject.getString("Nhom"));
                                lichThiItem.getCuoiKy().add(lichThiLichItem);
                            }

                            Collections.sort(lichThiItem.getCuoiKy(), new Comparator<LichThiLichItem>() {
                                @Override
                                public int compare(LichThiLichItem o1, LichThiLichItem o2) {
                                    return o1.getNgayThi().compareTo(o2.getNgayThi());
                                }
                            });

                            realm.copyToRealmOrUpdate(lichThiItem);
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
            showLichThi();
        }
    }

    private void showLichThi(){
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
        bundle.putInt(LichThiFragment.ARG_TYPE, 1);
        LichThiFragment lichThiFragment = new LichThiFragment();
        lichThiFragment.setArguments(bundle);
        fragmentArrayList.add(lichThiFragment);
        fragmentAdapter.addTitle("Giữa kỳ");

        bundle = new Bundle();
        bundle.putString(Tag.idHocKy, idHocKy);
        bundle.putInt(LichThiFragment.ARG_TYPE, 2);
        lichThiFragment = new LichThiFragment();
        lichThiFragment.setArguments(bundle);
        fragmentArrayList.add(lichThiFragment);
        fragmentAdapter.addTitle("Cuối kỳ");

        fragmentAdapter.notifyDataSetChanged();
    }

    private void getHocky(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetHocky().execute("");
            }
        });
    }

    public class GetHocky extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "lt")
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
                            LichThiHocKyItem lichThiHocKyItem = null;
                            realm.beginTransaction();
                            for(int i = 0 ; i < data.length(); i++){
                                get = data.getJSONObject(i);
                                lichThiHocKyItem = new LichThiHocKyItem();
                                lichThiHocKyItem.setId(get.getString("id"));
                                lichThiHocKyItem.setTenHocKy(get.getString("TenHocKy"));
                                lichThiHocKyItems.add(lichThiHocKyItem);
                                realm.copyToRealmOrUpdate(lichThiHocKyItem);
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
            showHocky();
        }
    }

    private void showHocky(){
        builder = new BottomSheet.Builder(this);
        builder.setTitle(R.string.bottom_sheet_title_hocky);
        int id = 0;
        for(LichThiHocKyItem e: lichThiHocKyItems){
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
            if(user.getConfig().getLichThiIdHocKyMacDinh() != null) {
                idHocKyMacDinh = user.getConfig().getLichThiIdHocKyMacDinh();
                for (int i = 0; i < lichThiHocKyItems.size(); i++) {
                    if (lichThiHocKyItems.get(i).getId().equals(idHocKyMacDinh)) {
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
            if(lichThiHocKyItems.size() > 0){
                chonHocKy(0);
            }
        }
    }

    private void chonHocKy(int postition){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        tvTiteHocKy.setText(lichThiHocKyItems.get(postition).getTenHocKy());
        idHocKy = lichThiHocKyItems.get(postition).getId();
        invalidateOptionsMenu();
        checkLichThiOffline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lichthi, menu);
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
