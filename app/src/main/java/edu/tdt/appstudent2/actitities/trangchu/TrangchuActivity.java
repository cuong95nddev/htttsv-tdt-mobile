package edu.tdt.appstudent2.actitities.trangchu;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.EncryptUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import edu.tdt.appstudent2.BuildConfig;
import edu.tdt.appstudent2.MainActivity;
import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.chat.ChatActivity;
import edu.tdt.appstudent2.fragments.trangchu.TrangchuMenuFragment;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.firebase.News;
import edu.tdt.appstudent2.models.firebase.UpdateApp;
import edu.tdt.appstudent2.utils.StringUtil;
import edu.tdt.appstudent2.views.widget.CircleImageView;
import io.realm.Realm;

public class TrangchuActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private Realm realm;
    private User user;
    private String userText, passText, avatarText, nameText;
    private TextView massv, name;
    private CircleImageView avatar;

    private DatabaseReference mDatabase;
    private DatabaseReference updateReference;
    private DatabaseReference newsReference;
    private DatabaseReference userReference;

    private LinearLayout layoutUpdate;
    private TextView tvTileUpdate;
    private WebView logUpdate;
    private Button btnUpdate;

    private LinearLayout layoutNews;
    private TextView tvTileNews;
    private WebView logNews;

    private UpdateApp updateApp;
    private News news;

    private AppCompatImageButton openChat;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
        nameText = user.getName();
        avatarText = user.getLinkAvatar();

        layoutUpdate = (LinearLayout) findViewById(R.id.layoutUpdate);
        tvTileUpdate = (TextView) findViewById(R.id.tvTitleUpdate);
        logUpdate = (WebView) findViewById(R.id.logUpdate);
        btnUpdate = (Button) findViewById(R.id.btnDownloadUpdate);

        layoutNews = (LinearLayout) findViewById(R.id.layoutNews);
        tvTileNews = (TextView) findViewById(R.id.tvTitleNews);
        logNews = (WebView) findViewById(R.id.logNews);

        openChat = (AppCompatImageButton) findViewById(R.id.btnMess);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    private void anhXa(){
        khoiTao();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hệ thống thông tin Sinh viên");

        name = (TextView) findViewById(R.id.name_text);
        massv = (TextView) findViewById(R.id.mssv_text);
        avatar = (CircleImageView) findViewById(R.id.avatar_img);

        name.setText(nameText);
        massv.setText(userText);
        Picasso.with(getApplicationContext()).load(avatarText).into(avatar);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu);
        anhXa();
        addPaper();

        userReference = mDatabase.child("User");
        userReference.child(EncryptUtils.encryptMD5ToString(user.getUserName())).setValue(System.currentTimeMillis());

        updateReference = mDatabase.child("Update");
        updateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateApp = dataSnapshot.getValue(UpdateApp.class);
                checkUpdate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateApp.downloadUrl));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(TrangchuActivity.this, "No application can handle this request."
                            + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        newsReference = mDatabase.child("News");
        newsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                news = dataSnapshot.getValue(News.class);
                news();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        openChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void news(){
        if(news == null)
            return;

        if(news.show){
            String title = news.title + " - " + StringUtil.getDate(news.time, "dd/MM/yyyy");
            tvTileNews.setText(title);
            logNews.getSettings().setJavaScriptEnabled(true);
            logNews.setBackgroundColor(Color.TRANSPARENT);
            logNews.loadDataWithBaseURL("", news.body, "text/html", "UTF-8", "");
            layoutNews.setVisibility(View.VISIBLE);
        }

        if(!news.show){
            layoutNews.setVisibility(View.GONE);
        }
    }

    private void checkUpdate(){
        if(updateApp == null)
            return;
        if(BuildConfig.VERSION_NAME.equals(updateApp.ver)){
            layoutUpdate.setVisibility(View.GONE);
            return;
        }
        String title = "Phiên bản mới: " + updateApp.ver + " - " + StringUtil.getDate(updateApp.time, "dd/MM/yyyy");
        tvTileUpdate.setText(title);
        logUpdate.getSettings().setJavaScriptEnabled(true);
        logUpdate.setBackgroundColor(Color.TRANSPARENT);
        logUpdate.loadDataWithBaseURL("", updateApp.changelog, "text/html", "UTF-8", "");
        layoutUpdate.setVisibility(View.VISIBLE);
    }

    private void addPaper(){
        TrangchuMenuFragment trangchuMenuFragment = new TrangchuMenuFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, trangchuMenuFragment);
        ft.commit();
    }

    private void logOut(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.mess_logout_tile))
                .setMessage(getResources().getString(R.string.mess_logout_info))
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                        Intent mainActicity = new Intent(TrangchuActivity.this, MainActivity.class);
                        startActivity(mainActicity);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trangchu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_logout:
                logOut();
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
