package edu.tdt.appstudent2.actitities.email;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailItem;
import edu.tdt.appstudent2.utils.Tag;
import io.realm.Realm;

public class EmailViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView webView;
    private int idEmail;

    private Realm realm;
    private User user;
    private EmailItem emailItem;

    private String userText, passText;

    private TextView from, personal, subject, date;
    AppCompatImageButton btnBack;

    private void khoiTao(){
        Bundle bundle = getIntent().getExtras();
        idEmail = bundle.getInt(Tag.idEmail);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        emailItem = realm.where(EmailItem.class)
                .equalTo("mId", idEmail).findFirst();
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

        from = (TextView) findViewById(R.id.from_text);
        personal = (TextView) findViewById(R.id.personal_text);
        subject = (TextView) findViewById(R.id.subject_text);
        date = (TextView) findViewById(R.id.date_text);

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
        setContentView(R.layout.activity_email_view);
        anhXa();
        showEmail();

    }

    private void showEmail(){
        if(emailItem != null){
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
            webView.loadData(emailItem.getmBody(), "text/html; charset=utf-8","UTF-8");
            webView.setBackgroundColor(Color.TRANSPARENT);

            from.setText(emailItem.getmFrom());
            personal.setText(emailItem.getmPersonal());
            subject.setText(emailItem.getmSubject());
            date.setText(emailItem.getmSentDate());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
