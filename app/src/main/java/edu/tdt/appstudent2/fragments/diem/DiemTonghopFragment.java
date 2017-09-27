package edu.tdt.appstudent2.fragments.diem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.adapters.diem.DiemThonghopRecyclerViewAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.diem.Diem;
import edu.tdt.appstudent2.models.diem.DiemItem;
import io.realm.Realm;
import io.realm.RealmList;


public class DiemTonghopFragment extends Fragment {
    private View inflatedView = null;
    private RecyclerView recyclerView;
    private DiemThonghopRecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<DiemItem> list;
    private SwipeRefreshLayout swipeContainer;

    private Realm realm;
    private User user;
    private String userText, passText;

    private Diem diem;

    private void khoiTao(){
        list = new ArrayList<DiemItem>();
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        adapter = new DiemThonghopRecyclerViewAdapter(list);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
    }
    public DiemTonghopFragment() {

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
        this.inflatedView = inflater.inflate(R.layout.fragment_diem_tonghop, container, false);
        anhXa();
        checkOffline();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                adapter.notifyDataSetChanged();
                getDiemTongHop();
            }
        });

        return inflatedView;
    }

    private void checkOffline(){
        diem = realm.where(Diem.class)
                .equalTo("id", "TH")
                .findFirst();
        if(diem == null){
            getDiemTongHop();
        }else {
            showDiemTongHop();
        }
    }

    private void showDiemTongHop(){
        if(diem != null){
            RealmList<DiemItem> diemItems = diem.getDiemItems();
            for(int i = 0 ; i < diemItems.size(); i++){
                list.add(diemItems.get(i));
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void getDiemTongHop(){
        if(!swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(true);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getDiemTongHop().execute("");
            }
        });
    }

    public class getDiemTongHop extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "kqht")
                        .data("option", "lth")
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
            if(s != null) {
                try {

                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            realm.beginTransaction();
                            DiemItem diemItem = null;
                            JSONArray data = root.getJSONArray("data");
                            JSONObject rootObject = null;
                            if (data.length() > 0) {
                                diem = new Diem();
                                diem.setId("TH");
                                for (int i = 0; i < data.length(); i++) {
                                    rootObject = data.getJSONObject(i);
                                    diemItem = new DiemItem();
                                    diemItem.setMonHocID(rootObject.getString("MonHocID"));
                                    diemItem.setTenMH(rootObject.getString("TenMH"));
                                    diemItem.setDiem1(rootObject.getString("Diem1"));
                                    diemItem.setDiem2(rootObject.getString("Diem2"));
                                    diemItem.setDiemThi1(rootObject.getString("DiemThi1"));
                                    diemItem.setDiemThi2(rootObject.getString("DiemThi2"));
                                    diemItem.setdTB(rootObject.getString("DTB"));
                                    diemItem.setSoTC(rootObject.getString("SoTC"));
                                    diemItem.setDiem1_1(rootObject.getString("Diem1_1"));
                                    diemItem.setGhiChu(rootObject.getString("GhiChu"));
                                    diem.getDiemItems().add(diemItem);
                                }
                                realm.copyToRealmOrUpdate(diem);
                            }
                            realm.commitTransaction();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {
                    realm.close();
                }
                swipeContainer.setRefreshing(false);
                showDiemTongHop();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
