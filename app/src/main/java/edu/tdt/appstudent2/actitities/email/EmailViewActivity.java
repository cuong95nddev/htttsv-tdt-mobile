package edu.tdt.appstudent2.actitities.email;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.adapters.email.EmailAttachmentAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailAttachment;
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

    private RecyclerView attRv;
    private EmailAttachmentAdapter attAdapter;

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


        attRv = (RecyclerView) findViewById(R.id.rvAttachment);
        attAdapter = new EmailAttachmentAdapter();
        attRv.setLayoutManager(new LinearLayoutManager(this));
        attRv.setAdapter(attAdapter);
        attRv.setNestedScrollingEnabled(false);

        final ArrayList<EmailAttachment> emailAttachments = new ArrayList<>();
        EmailAttachment attachment = null;
        for(EmailAttachment emailAttachment : emailItem.getEmailAttachments()){
            attachment = new EmailAttachment();
            attachment.setId(emailAttachment.getId());
            attachment.setName(emailAttachment.getName());
            attachment.setType(emailAttachment.getType());
            emailAttachments.add(attachment);
        }

        attAdapter.setLists(emailAttachments);

        attAdapter.onItemClick = new EmailAttachmentAdapter.OnItemClick() {
            @Override
            public void onClick(EmailAttachment emailAttachment) {

                File file = new File(getApplicationContext().getFilesDir() + "/attachment/" + emailItem.getmId(), emailAttachment.getName());

                String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + emailAttachment.getName();
                File destination = new File(destinationPath);

                FileUtils.copyFile(file, destination);


                DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(getApplicationContext().DOWNLOAD_SERVICE);

                downloadManager.addCompletedDownload(
                        file.getName(),
                        file.getName(),
                        true,
                        emailAttachment.getType(),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                        file.length(),
                        true);


            }
        };
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
