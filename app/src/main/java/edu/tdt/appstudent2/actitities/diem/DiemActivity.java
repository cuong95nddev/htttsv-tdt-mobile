package edu.tdt.appstudent2.actitities.diem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
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
import edu.tdt.appstudent2.adapters.thongbao.FragmentAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.fragments.diem.DiemFragment;
import edu.tdt.appstudent2.fragments.diem.DiemTonghopFragment;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.diem.Diem;
import edu.tdt.appstudent2.models.diem.DiemHockyItem;
import edu.tdt.appstudent2.models.diem.DiemItem;
import io.realm.Realm;
import io.realm.RealmResults;

import static edu.tdt.appstudent2.utils.Tag.idHocKy;

public class DiemActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private ArrayList<DiemHockyItem> diemHockyItems;

    private Realm realm;
    private User user;
    private String userText, passText;

    private MultiStateView mMultiStateView;
    AppCompatImageButton btnBack;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();

        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);

        diemHockyItems = new ArrayList<DiemHockyItem>();
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
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diem);
        anhXa();
        checkOffline();
    }

    private void checkOffline(){
        if(realm.where(DiemHockyItem.class).count() > 0){
            RealmResults<DiemHockyItem> realmResults = realm.where(DiemHockyItem.class).findAll();
            diemHockyItems.addAll(realmResults);
            addPaper();
        }else {
            getHocKy();
        }
    }


    private void addPaper(){
        tabs.setVisibility(View.VISIBLE);
        DiemTonghopFragment diemTonghopFragment = new DiemTonghopFragment();
        fragmentArrayList.add(diemTonghopFragment);
        fragmentAdapter.addTitle("Điểm tổng hợp");


        DiemFragment diemFragment = null;
        Bundle bundle = null;
        for(int i = 0 ; i < diemHockyItems.size(); i++){
            diemFragment = new DiemFragment();
            bundle = new Bundle();
            bundle.putString(idHocKy, diemHockyItems.get(i).getNameTable());
            diemFragment.setArguments(bundle);
            fragmentArrayList.add(diemFragment);
            fragmentAdapter.addTitle(diemHockyItems.get(i).getTenHocKy());
        }
        fragmentAdapter.notifyDataSetChanged();
    }

    private void getHocKy(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHocKy().execute("");
            }
        });
    }

    public class getHocKy extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "kqht")
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
            if (s != null) {
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            realm.beginTransaction();
                            JSONArray data = root.getJSONArray("data");
                            JSONObject rootObject = null;
                            DiemHockyItem diemHockyItem = null;
                            if (data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    rootObject = data.getJSONObject(i);
                                    diemHockyItem = new DiemHockyItem();
                                    diemHockyItem.setId(rootObject.getString("id"));
                                    diemHockyItem.setNameTable(rootObject.getString("NameTable"));
                                    diemHockyItem.setTenHocKy(rootObject.getString("TenHocKy"));
                                    diemHockyItems.add(diemHockyItem);
                                    realm.copyToRealmOrUpdate(diemHockyItem);
                                }
                            }
                            realm.commitTransaction();
                        }else{
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {
                    realm.close();
                }
                addPaper();
            }
        }
    }

    private void reload(){
        tabs.setVisibility(View.GONE);
        diemHockyItems.clear();
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();

        // remove data offline
        realm.beginTransaction();
        realm.delete(DiemHockyItem.class);
        realm.delete(DiemItem.class);
        realm.delete(Diem.class);
        realm.commitTransaction();

        tabs.setVisibility(View.GONE);
        getHocKy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_diem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_reload:
                reload();
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
