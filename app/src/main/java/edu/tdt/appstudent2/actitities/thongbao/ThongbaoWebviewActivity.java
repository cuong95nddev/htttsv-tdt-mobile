package edu.tdt.appstudent2.actitities.thongbao;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.kennyc.view.MultiStateView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.thongbao.ThongbaoCache;
import edu.tdt.appstudent2.utils.Tag;
import io.realm.Realm;

public class ThongbaoWebviewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView webView;
    private String idThongBao;
    private Realm realm;
    private User user;
    private ThongbaoCache thongbaoCache;
    private String userText, passText;

    private MultiStateView mMultiStateView;
    AppCompatImageButton btnBack;

    private void khoiTao(){
        Bundle bundle = getIntent().getExtras();
        idThongBao = bundle.getString(Tag.idThongBao);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        thongbaoCache = realm.where(ThongbaoCache.class)
                .equalTo("id", idThongBao).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
    }
    private void anhXa(){
        khoiTao();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 );
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAgain();
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
        setContentView(R.layout.activity_thongbao_webview);
        anhXa();

        if(thongbaoCache == null){
            if(isNetworkAvailable()){
                getThongBao();
            }else {
                // no network

            }
        }else {
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
            // load cache
            webView.loadData(thongbaoCache.getData(), "text/html; charset=utf-8",null);
        }

    }

    private void getThongBao(){
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetThongBaoAsync().execute();
            }
        });
    }

    private class GetThongBaoAsync extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "tb")
                        .data("id", idThongBao)
                        .timeout(30000)
                        .get();
                return doc.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if(s != null){
                if(thongbaoCache == null) {
                    // save data
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            thongbaoCache = realm.createObject(ThongbaoCache.class);
                            thongbaoCache.setId(idThongBao);
                            thongbaoCache.setData(s);
                            realm.copyToRealmOrUpdate(thongbaoCache);
                        }
                    });

                    // load data to webview
                    webView.loadData(s, "text/html; charset=utf-8",null);
                }
            }else{
                // get error
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        }
    }

    private void loadAgain(){
        if(isNetworkAvailable()){
            webView.getSettings().setCacheMode( WebSettings.LOAD_NO_CACHE);
            getThongBao();
        }else {
            // error network
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_thongbao_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_reload:
                loadAgain();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
