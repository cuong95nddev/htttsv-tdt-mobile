package edu.tdt.appstudent2.actitities.thongbao;

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
import edu.tdt.appstudent2.fragments.thongbao.ThongbaoFragment;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.thongbao.DonviItem;
import edu.tdt.appstudent2.models.thongbao.ThongbaoCache;
import edu.tdt.appstudent2.models.thongbao.ThongbaoItem;
import edu.tdt.appstudent2.utils.Tag;
import io.realm.Realm;
import io.realm.RealmResults;

public class ThongbaoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;
    private ArrayList<DonviItem> donViArrayList;

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

        donViArrayList = new ArrayList<DonviItem>();
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
        setContentView(R.layout.activity_thongbao);
        anhXa();
        checkHaveOffline();
    }


    private void checkHaveOffline(){
        if(realm.where(DonviItem.class).count() > 0){
            RealmResults<DonviItem> realmResults = realm.where(DonviItem.class).findAll();
            donViArrayList.addAll(realmResults);
            addDonVi();
        }else {
            getDonVi();
        }
    }

    public void getDonVi(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetDonViAsync().execute("");
            }
        });
    }

    public class GetDonViAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                DonviItem donViNew = null;
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "tb")
                        .timeout(30000)
                        .get();
                JSONObject root = new JSONObject(doc.text());

                if(root.has("status")){
                    if(root.getBoolean("status")){
                        JSONObject data = root.getJSONObject("data");
                        JSONArray thongBaoArray = data.getJSONArray("donvi");
                        for(int i = 0; i < thongBaoArray.length(); i++){
                            JSONObject thongBaoItem = thongBaoArray.getJSONObject(i);
                            donViNew = new DonviItem();
                            donViNew.setTitle(thongBaoItem.getString("title"));
                            donViNew.setId(thongBaoItem.getString("id"));
                            donViArrayList.add(donViNew);
                        }
                    }
                    return "success";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if(s != null){
                addDonVi();
                updateDonVi();
            }else{
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        }
    }

    private void updateDonVi(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DonviItem donviItem = null;
                for(DonviItem e: donViArrayList){
                    realm.copyToRealmOrUpdate(e);
                }
            }
        });
    }

    private void reload(){
        donViArrayList.clear();
        fragmentArrayList.clear();
        fragmentAdapter.clearTitle();
        fragmentAdapter.notifyDataSetChanged();

        // remove data offline
        realm.beginTransaction();
        realm.delete(ThongbaoCache.class);
        realm.delete(ThongbaoItem.class);
        realm.delete(DonviItem.class);
        realm.commitTransaction();

        tabs.setVisibility(View.GONE);
        getDonVi();
    }


    private void addDonVi(){
        tabs.setVisibility(View.VISIBLE);
        ThongbaoFragment thongbaoFragment = null;
        Bundle bundle = null;

        thongbaoFragment = new ThongbaoFragment();
        bundle = new Bundle();
        bundle.putString(Tag.idDonVi, "");
        thongbaoFragment.setArguments(bundle);
        fragmentArrayList.add(thongbaoFragment);
        fragmentAdapter.addTitle("Tổng hợp");

        for(DonviItem e: donViArrayList){
            thongbaoFragment = new ThongbaoFragment();
            bundle = new Bundle();
            bundle.putString(Tag.idDonVi, e.getId());
            thongbaoFragment.setArguments(bundle);
            fragmentArrayList.add(thongbaoFragment);
            fragmentAdapter.addTitle(e.getTitle());
        }

        fragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_thongbao, menu);
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