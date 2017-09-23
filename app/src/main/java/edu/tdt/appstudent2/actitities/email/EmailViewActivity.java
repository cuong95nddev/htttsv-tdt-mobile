package edu.tdt.appstudent2.actitities.email;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

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

    private TextView from, personal, subject;
    public RelativeTimeTextView tvDate;
    AppCompatImageButton btnBack;
    AppCompatImageButton btnReply;

    private RecyclerView attRv;
    private EmailAttachmentAdapter attAdapter;
    ProgressDialog progressDialog;

    private Folder emailFolder;
    private Store store;
    private Properties properties;
    private Session emailSession;
    private String linkHostMail;

    private int positionAttachment;
    private String typeAttachment;
    private File fileAttachment;
    private String nameAttachment;
    static int level = 0;
    static int attnum = 1;

    private void khoiTao(){
        Bundle bundle = getIntent().getExtras();
        idEmail = bundle.getInt(Tag.idEmail);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        emailItem = realm.where(EmailItem.class)
                .equalTo("mId", idEmail).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
        linkHostMail = user.getLinkHostMail();

        properties = System.getProperties();
        properties.setProperty("mail.store.protocol", "imaps");
        emailSession = Session.getDefaultInstance(properties);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang tải các tập tin đính kèm");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setCancelable(false);
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
        tvDate = (RelativeTimeTextView) findViewById(R.id.tvDate);

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnReply = (AppCompatImageButton) findViewById(R.id.btnReply);
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendMailIntent = new Intent(EmailViewActivity.this, EmailNewActivity.class);
                sendMailIntent.putExtra(EmailNewActivity.EXTRA_TO, emailItem.getmFrom());
                sendMailIntent.putExtra(EmailNewActivity.EXTRA_SUBJECT, "Re: " + emailItem.getmSubject());
                sendMailIntent.putExtra(EmailNewActivity.EXTRA_ID_REPLY, emailItem.getmId());

                startActivity(sendMailIntent);
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
            public void onClick(EmailAttachment emailAttachment, int position) {
                checkAttachment(emailAttachment, position);
            }
        };
    }


    private void checkAttachment(EmailAttachment emailAttachment, int position){
        nameAttachment = emailAttachment.getName();
        positionAttachment = position;
        typeAttachment = emailAttachment.getType();
        String fileUrl = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + "/" + nameAttachment;
        fileAttachment = new File(fileUrl);
        // check attachment file is exist ?

        Toast.makeText(this, "Tập tin sẽ được lưu tại: " + fileUrl, Toast.LENGTH_SHORT).show();

        if(fileAttachment.exists()){
            showAttachment();
            return;
        }
        // get file
        getAttachment();

    }

    private void getAttachment(){
        progressDialog.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetAttachment().execute();
            }
        });
    }

    private class GetAttachment extends AsyncTask<Void, Integer, File>{

        @Override
        protected File doInBackground(Void... voids) {

            try {
                store = emailSession.getStore("imaps");
                store.connect(linkHostMail, userText + "@student.tdt.edu.vn", passText);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);
                Message message = emailFolder.getMessage(idEmail);
                if(message != null){
                    level = 0;
                    attnum = 1;
                    dumpPart(message);
                    showAttachment();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    emailFolder.close(true);
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            progressDialog.dismiss();
        }
    }


    public void dumpPart(Part p) throws Exception {
        if (p.isMimeType("text/plain")) {

        } else if(p.isMimeType("text/html")){

        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            level++;
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
                dumpPart(mp.getBodyPart(i));
            level--;
        } else if (p.isMimeType("message/rfc822")) {
            level++;
            dumpPart((Part)p.getContent());
            level--;
        }

        if (level != 0 && p instanceof MimeBodyPart && !p.isMimeType("multipart/*")) {
            String disp = p.getDisposition();
            if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
                String filename = p.getFileName();
                if (filename != null) {
                    try {
                        File file = new File(Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .getAbsolutePath());

                        if (!file.exists()){
                            file.mkdirs();
                        }

                        file = new File(Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getAbsolutePath() + "/" + filename);
                        if (file.exists())
                            throw new IOException("file exists");
                        ((MimeBodyPart)p).saveFile(file);
                    } catch (IOException ex) {
                        Log.d("", "Failed to save attachment: " + ex);
                    }
                }
            }
        }
    }

    private void showAttachment(){
        String fileUrl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+ "/" + nameAttachment;
        DownloadManager downloadManager = (DownloadManager) getApplicationContext()
                .getSystemService(getApplicationContext().DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(
                fileAttachment.getName(),
                fileAttachment.getName(),
                true,
                typeAttachment,
                fileUrl,
                fileAttachment.length(),
                true);
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
            tvDate.setReferenceTime(emailItem.getmSentDate());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
