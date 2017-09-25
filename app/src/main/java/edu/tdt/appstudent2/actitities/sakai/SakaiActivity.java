package edu.tdt.appstudent2.actitities.sakai;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.adapters.sakai.SakaiAnnouncementAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.sakai.ItemSakaiAnnouncement;
import edu.tdt.appstudent2.models.sakai.ItemSakaiAttachment;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;
import io.realm.RealmResults;

public class SakaiActivity extends AppCompatActivity {

    private Realm realm;
    private User user;
    private String userText, passText;

    private RecyclerView recyclerView;
    private SakaiAnnouncementAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    AppCompatImageButton btnBack;
    AppCompatImageButton btnSetPass;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
    }

    private void anhXa(){
        khoiTao();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new SakaiAnnouncementAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.onItemClick = new SakaiAnnouncementAdapter.OnItemClick() {
            @Override
            public void onClick(String id) {
                Intent sakaiViewIntent = new Intent(SakaiActivity.this, SakaiViewActivity.class);
                sakaiViewIntent.putExtra(SakaiViewActivity.EXTRA_ID, id);
                startActivity(sakaiViewIntent);
            }
        };


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewSakai();
            }
        });

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSetPass = (AppCompatImageButton) findViewById(R.id.btnSetPass);
        btnSetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasswordSakai();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sakai);
        anhXa();
        showOffline();
    }


    private void showOffline(){
        RealmResults<ItemSakaiAnnouncement> itemSakaiAnnouncements = realm.where(ItemSakaiAnnouncement.class).findAll();
        if(itemSakaiAnnouncements != null){
            for(ItemSakaiAnnouncement itemSakaiAnnouncement : itemSakaiAnnouncements){
                adapter.addItem(itemSakaiAnnouncement);
            }
        }

        if(Util.isNetworkAvailable(this)){
            getNewSakai();
        }

    }

    private void setPasswordSakai(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mật khẩu");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pass = input.getText().toString().trim();
                if("".equals(pass)){
                    return;
                }


                realm.beginTransaction();
                user.setPassWordSakai(pass);
                realm.commitTransaction();

                getNewSakai();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void getNewSakai(){

        Log.d("ahihi", passText);

        if(passText == null){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập mật khẩu cho Sakai", Toast.LENGTH_SHORT).show();
            return;
        }

        swipeContainer.setRefreshing(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetNewSakai().execute();
            }
        });
    }

    private class GetNewSakai extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {
            Connection.Response res = null;
            BufferedInputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                res = Jsoup.connect("http://sakai.it.tdt.edu.vn/portal/xlogin")
                        .method(Connection.Method.POST)
                        .data("eid", userText)
                        .data("pw", passText)
                        .timeout(30000)
                        .execute();

                String sessionId = res.cookie("JSESSIONID");

                Log.d("ahihi", sessionId);

                if(sessionId != null){
                    URL url = new URL("http://sakai.it.tdt.edu.vn/direct/announcement/user.json?n=100&d=100");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Host", "sakai.it.tdt.edu.vn");
                    conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
                    conn.connect();
                    inputStream = new BufferedInputStream(conn.getInputStream());
                    bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                    String line = "";
                    String result = "";
                    while((line = bufferedReader.readLine()) != null){
                        result += line;
                    }

                    conn.disconnect();

                    return result;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    inputStream.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                try {

                    JSONObject root = new JSONObject(s);
                    if(root.has("announcement_collection")){
                        JSONArray announcementJsonArray = root.getJSONArray("announcement_collection");
                        JSONObject announcementJsonObject = null;
                        JSONArray attachmentJsonArray = null;
                        JSONObject attachmentJsonObject = null;
                        ItemSakaiAnnouncement itemSakaiAnnouncement = null;
                        ItemSakaiAttachment itemSakaiAttachment = null;
                        realm.beginTransaction();
                        adapter.clear();
                        for(int i = 0 ; i < announcementJsonArray.length(); i++){
                            announcementJsonObject = announcementJsonArray.getJSONObject(i);
                            itemSakaiAnnouncement = new ItemSakaiAnnouncement();
                            itemSakaiAnnouncement.setId(announcementJsonObject.getString("id"));
                            itemSakaiAnnouncement.setBody(announcementJsonObject.getString("body"));
                            itemSakaiAnnouncement.setCreatedByDisplayName(announcementJsonObject.getString("createdByDisplayName"));
                            itemSakaiAnnouncement.setCreatedOn(announcementJsonObject.getLong("createdOn"));
                            itemSakaiAnnouncement.setTitle(announcementJsonObject.getString("title"));

                            if(announcementJsonObject.has("attachments")){
                                attachmentJsonArray = announcementJsonObject.getJSONArray("attachments");
                                for(int j = 0; j < attachmentJsonArray.length(); j++){
                                    attachmentJsonObject = attachmentJsonArray.getJSONObject(j);
                                    itemSakaiAttachment = new ItemSakaiAttachment();
                                    itemSakaiAttachment.setName(attachmentJsonObject.getString("name"));
                                    itemSakaiAttachment.setUrl(attachmentJsonObject.getString("url"));
                                    itemSakaiAnnouncement.getAttachments().add(itemSakaiAttachment);
                                }
                            }

                            adapter.addItem(itemSakaiAnnouncement);
                            realm.copyToRealmOrUpdate(itemSakaiAnnouncement);
                        }
                        realm.commitTransaction();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            swipeContainer.setRefreshing(false);
        }
    }
}
