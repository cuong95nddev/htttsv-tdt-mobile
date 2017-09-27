package edu.tdt.appstudent2.actitities.cnsv;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.adapters.cnsv.CnsvResultAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.cnsv.ItemCnsvResult;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;
import io.realm.RealmResults;

public class CnsvActivity extends AppCompatActivity {

    private Realm realm;
    private User user;
    private String userText, passText;

    private RecyclerView recyclerView;
    private CnsvResultAdapter adapter;
    private SwipeRefreshLayout swipeContainer;


    AppCompatImageButton btnBack;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
    }

    private void anhXa(){
        khoiTao();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new CnsvResultAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearAndGetNewCnsvResult();
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
        setContentView(R.layout.activity_cnsv);
        anhXa();
        showOffline();
    }

    private void showOffline(){
        RealmResults<ItemCnsvResult> itemCnsvResults = realm.where(ItemCnsvResult.class).findAll();
        if(itemCnsvResults != null){
            for(ItemCnsvResult itemCnsvResult : itemCnsvResults){
                adapter.addItem(itemCnsvResult);
            }
        }

        if(Util.isNetworkAvailable(this)){
            getNewCnsvResult();
        }

    }

    private void clearAndGetNewCnsvResult(){
        realm.beginTransaction();
        realm.delete(ItemCnsvResult.class);
        realm.commitTransaction();
        getNewCnsvResult();
    }

    private void getNewCnsvResult(){
        swipeContainer.setRefreshing(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetNewCnsvResult().execute();
            }
        });
    }

    private class GetNewCnsvResult extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "cnsv")
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
            if(s != null && !"".equals(s)){
                try {
                    JSONObject root = new JSONObject(s);
                    if(root.has("status")) {
                        if (root.getBoolean("status")) {
                            adapter.clear();
                            realm.beginTransaction();
                            JSONArray dataJsonArray = root.getJSONArray("data");
                            JSONObject dataJsonObject = null;
                            ItemCnsvResult itemCnsvResult = null;

                            if(dataJsonArray.length() > 0)
                                realm.delete(ItemCnsvResult.class);

                            for(int i = 0 ; i < dataJsonArray.length(); i++){
                                dataJsonObject = dataJsonArray.getJSONObject(i);
                                itemCnsvResult = new ItemCnsvResult();
                                itemCnsvResult.setId(dataJsonObject.getString("id").trim());
                                itemCnsvResult.setType(dataJsonObject.getString("type").trim());
                                itemCnsvResult.setHk(dataJsonObject.getString("hk").trim());
                                itemCnsvResult.setDateRequest(dataJsonObject.getString("date_request").trim());
                                itemCnsvResult.setDateResponse(dataJsonObject.getString("date_respone").trim());
                                itemCnsvResult.setStatus(dataJsonObject.getString("status").toLowerCase());
                                itemCnsvResult.setNote(dataJsonObject.getString("note").trim());
                                adapter.addItem(itemCnsvResult);
                                realm.copyToRealmOrUpdate(itemCnsvResult);
                            }

                            realm.commitTransaction();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {
                    realm.close();
                }
            }

            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
