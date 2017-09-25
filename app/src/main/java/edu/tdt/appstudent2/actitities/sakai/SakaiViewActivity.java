package edu.tdt.appstudent2.actitities.sakai;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.io.File;
import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.adapters.sakai.SakaiAttachmentAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.sakai.ItemSakaiAnnouncement;
import edu.tdt.appstudent2.models.sakai.ItemSakaiAttachment;
import io.realm.Realm;

public class SakaiViewActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "EXTRA_ID";

    private Realm realm;
    private User user;
    private String userText, passText;
    private ItemSakaiAnnouncement sakaiAnnouncement;

    private String id;


    TextView tvTile, tvCreatedByDisplayName;
    RelativeTimeTextView tvCreatedOn;
    WebView webView;
    AppCompatImageButton btnBack;

    private RecyclerView attRv;
    private SakaiAttachmentAdapter attAdapter;
    ProgressDialog progressDialog;

    private String name;
    private String url;

    private void khoiTao(){
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            finish();

        String id = bundle.getString(EXTRA_ID);
        if(id == null)
            finish();

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();

        sakaiAnnouncement = realm.where(ItemSakaiAnnouncement.class).equalTo("id", id).findFirst();
        if(sakaiAnnouncement == null)
            return;

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

        attRv = (RecyclerView) findViewById(R.id.recyclerview);
        attAdapter = new SakaiAttachmentAdapter();
        attRv.setLayoutManager(new LinearLayoutManager(this));
        attRv.setAdapter(attAdapter);
        attRv.setNestedScrollingEnabled(false);

        final ArrayList<ItemSakaiAttachment> itemSakaiAttachments = new ArrayList<>();
        ItemSakaiAttachment attachment = null;
        for(ItemSakaiAttachment itemSakaiAttachment : sakaiAnnouncement.getAttachments()){
            attachment = new ItemSakaiAttachment();
            attachment.setName(itemSakaiAttachment.getName());
            attachment.setUrl(itemSakaiAttachment.getUrl());
            itemSakaiAttachments.add(attachment);
        }
        attAdapter.setLists(itemSakaiAttachments);

        attAdapter.onItemClick = new SakaiAttachmentAdapter.OnItemClick() {
            @Override
            public void onClick(ItemSakaiAttachment itemSakaiAttachment, int position) {
                checkAttachment(itemSakaiAttachment);
            }
        };

        tvTile = (TextView) findViewById(R.id.tvTile);
        tvCreatedByDisplayName = (TextView) findViewById(R.id.tvCreatedByDisplayName);
        tvCreatedOn = (RelativeTimeTextView) findViewById(R.id.tvCreatedOn);
        webView = (WebView) findViewById(R.id.webview);

        tvTile.setText(sakaiAnnouncement.getTitle());
        tvCreatedByDisplayName.setText(sakaiAnnouncement.getCreatedByDisplayName());
        tvCreatedOn.setReferenceTime(sakaiAnnouncement.getCreatedOn());
        webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        webView.loadData(sakaiAnnouncement.getBody(), "text/html; charset=utf-8","UTF-8");
        webView.setBackgroundColor(Color.TRANSPARENT);

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
        setContentView(R.layout.activity_sakai_view);

        anhXa();
    }


    private void checkAttachment(ItemSakaiAttachment itemSakaiAttachment){
        name = itemSakaiAttachment.getName();
        url = itemSakaiAttachment.getUrl();
        String fileUrl = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + "/" + name;
        File fileAttachment = new File(fileUrl);
        // check attachment file is exist ?

        if(fileAttachment.exists()){
            showAttachment(fileAttachment);
            return;
        }
        // get file

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemSakaiAttachment.getUrl()));
        startActivity(browserIntent);

    }


    private void showAttachment(File fileAttachment){
        String fileUrl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+ "/" + fileAttachment.getName();

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileAttachment.getName());
        Uri uri = Uri.fromFile(file).normalizeScheme();

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getMimeType(uri));
        startActivity(intent);
    }


    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType.toLowerCase();
    }
}
