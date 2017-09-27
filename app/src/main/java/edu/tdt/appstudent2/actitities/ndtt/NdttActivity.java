package edu.tdt.appstudent2.actitities.ndtt;

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
import edu.tdt.appstudent2.adapters.ndtt.NdttResultAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.ndtt.ItemNdttResult;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;
import io.realm.RealmResults;

public class NdttActivity extends AppCompatActivity {

    private Realm realm;
    private User user;
    private String userText, passText;

    private RecyclerView recyclerView;
    private NdttResultAdapter adapter;
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
        adapter = new NdttResultAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearAndGetNewNdttResult();
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
        setContentView(R.layout.activity_ndtt);
        anhXa();
        showOffline();
    }


    private void showOffline(){
        RealmResults<ItemNdttResult> itemNdttResults = realm.where(ItemNdttResult.class).findAll();
        if(itemNdttResults != null){
            for(ItemNdttResult itemNdttResult : itemNdttResults){
                adapter.addItem(itemNdttResult);
            }
        }

        if(Util.isNetworkAvailable(this)){
            getNewNdttResult();
        }

    }

    private void clearAndGetNewNdttResult(){
        realm.beginTransaction();
        realm.delete(ItemNdttResult.class);
        realm.commitTransaction();
        getNewNdttResult();
    }

    private void getNewNdttResult(){
        swipeContainer.setRefreshing(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetNewNdttResult().execute();
            }
        });
    }

    private class GetNewNdttResult extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "gdtt")
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
                            ItemNdttResult itemNdttResult = null;

                            if(dataJsonArray.length() > 0)
                                realm.delete(ItemNdttResult.class);

                            for(int i = 0 ; i < dataJsonArray.length(); i++){
                                dataJsonObject = dataJsonArray.getJSONObject(i);
                                itemNdttResult = new ItemNdttResult();
                                itemNdttResult.setId(dataJsonObject.getString("id").trim());
                                itemNdttResult.setType(dataJsonObject.getString("type").trim());
                                itemNdttResult.setHk(dataJsonObject.getString("hk").trim());
                                itemNdttResult.setDateRequest(dataJsonObject.getString("date_request").trim());
                                itemNdttResult.setDateResponse(dataJsonObject.getString("date_respone").trim());
                                itemNdttResult.setStatus(dataJsonObject.getString("status").toLowerCase());
                                itemNdttResult.setNote(dataJsonObject.getString("note").trim());
                                adapter.addItem(itemNdttResult);
                                realm.copyToRealmOrUpdate(itemNdttResult);
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
