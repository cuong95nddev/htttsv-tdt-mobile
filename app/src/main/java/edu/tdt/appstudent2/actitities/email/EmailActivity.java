package edu.tdt.appstudent2.actitities.email;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.endless.Endless;
import com.sun.mail.imap.IMAPFolder;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.adapters.email.EmailRecyclerViewAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.fragments.dialog.EditServiceDialogFragment;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailItem;
import edu.tdt.appstudent2.models.email.EmailPageSave;
import edu.tdt.appstudent2.service.CheckEmailService;
import edu.tdt.appstudent2.service.ServiceUtils;
import edu.tdt.appstudent2.utils.Tag;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class EmailActivity extends AppCompatActivity {
    private static final int MAIL_SEEN = 0;

    private Toolbar toolbar;
    private Realm realm;
    private User user;
    private String userText, passText;
    private String linkHostMail;

    private RecyclerView recyclerView;
    private EmailRecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private ArrayList<EmailItem> lists;

    private SwipeRefreshLayout swipeContainer;
    private Endless endless;

    private EmailPageSave emailPageSave;
    private static final int MAX_NUM_LOAD = 50;


    private Properties properties;
    private Session emailSession;

    AppCompatImageButton btnBack;
    AppCompatImageButton btnWeb;
    AppCompatImageButton btnNoti;
    TextView tvLoadMore;
    LinearLayout layoutLoadmore;

    FloatingActionButton fabNewMail;

    private boolean enableNoti = false;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
        linkHostMail = user.getLinkHostMail();

        lists = new ArrayList<EmailItem>();

        properties = System.getProperties();
        properties.setProperty("mail.store.protocol", "imaps");
        emailSession = Session.getInstance(properties);
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new EmailRecyclerViewAdapter(getApplicationContext(), lists);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        View loadingView = View.inflate(this, R.layout.recyclerview_loading, null);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        endless = Endless.applyTo(recyclerView, loadingView);
        endless.setAdapter(adapter);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(emailPageSave != null){
                    readNewMail(emailPageSave.getIdLoadedTop());
                }
            }
        });

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnWeb = (AppCompatImageButton) findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailWeb();
            }
        });
        btnNoti = (AppCompatImageButton) findViewById(R.id.btnNoti);

        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enableNoti){
                    if(user.getEmailServiceConfig().isOpen()){
                        openOrCloseService();
                    }else{
                        openDialogConfigService();
                    }
                }

            }
        });

        btnNoti.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(enableNoti){
                    openDialogConfigService();
                }
                return true;
            }
        });

        fabNewMail = (FloatingActionButton) findViewById(R.id.fabNewMail);
        fabNewMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendMailIntent = new Intent(EmailActivity.this, EmailNewActivity.class);
                startActivity(sendMailIntent);
            }
        });

        tvLoadMore = (TextView) findViewById(R.id.tvLoadMore);
        tvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailPageSave != null){
                    readMoreMail(MAX_NUM_LOAD, emailPageSave.getIdLoadedBottom());
                }

            }
        });

        layoutLoadmore = (LinearLayout) findViewById(R.id.layoutLoadmore);
        layoutLoadmore.setVisibility(View.GONE);
    }

    private void openDialogConfigService(){
        FragmentManager fm = getSupportFragmentManager();
        EditServiceDialogFragment alertDialog = EditServiceDialogFragment
                .newInstance(EditServiceDialogFragment.TYPE_EMAIL);
        alertDialog.show(fm, "fragment_alert");
        alertDialog.setOnDismissEvent(new EditServiceDialogFragment.OnDismissEvent() {
            @Override
            public void onDismiss() {
                setIconNoti();
            }
        });
    }

    private void openOrCloseService(){
        realm.beginTransaction();
        if(user.getEmailServiceConfig().isOpen()){
            user.getEmailServiceConfig().setOpen(false);
            ServiceUtils.stopService(this
                    , CheckEmailService.class);
        }else{
            user.getEmailServiceConfig().setOpen(true);
            ServiceUtils.startService(this
                    , CheckEmailService.class
                    , ServiceUtils.TIME_REPLAY[(int)user.getEmailServiceConfig().getTimeReplay()]);
        }

        realm.commitTransaction();
        setIconNoti();
    }

    private void setIconNoti(){
        btnNoti.setImageResource(user.getEmailServiceConfig().isOpen()?
                R.drawable.ic_notifications_active_black_24dp:R.drawable.ic_notifications_off_black_24dp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        anhXa();
        checkLinkHostMail();


        adapter.setOnItemClickListener(new EmailRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent emailView = new Intent(EmailActivity.this, EmailViewActivity.class);
                emailView.putExtra(Tag.idEmail, lists.get(position).getmId());
                startActivity(emailView);

                EmailItem emailItem = lists.get(position);
                if(emailItem.isNew()) {
                    realm.beginTransaction();
                    emailItem.setNew(false);
                    realm.copyToRealmOrUpdate(emailItem);
                    realm.commitTransaction();
                    adapter.notifyItemChanged(position);
                    thaoTacEmail(MAIL_SEEN, emailItem.getmId());
                }
            }
        });
    }

    private void hideWaitProgress(){
        swipeContainer.setRefreshing(false);
    }

    private void checkLinkHostMail(){
        if (linkHostMail == null) {
            if(Util.isNetworkAvailable(this)){
                getHostMail();
            }else {
                Toast.makeText(this, "Bạn đang ở chế độ Offline.", Toast.LENGTH_LONG).show();
            }
        }else {
            checkOffline();
        }
    }


    private void getHostMail(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new getHostMail().execute();
            }
        });
    }
    public class getHostMail extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {

                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "mail")
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
            if (s != null) {
                try {
                    realm.beginTransaction();
                    JSONObject root = new JSONObject(s);
                    user.setLinkHostMail(root.getString("data"));
                    linkHostMail = user.getLinkHostMail();
                    realm.commitTransaction();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (java.lang.IllegalStateException e) {
                    realm.close();
                }
                hideWaitProgress();
                checkOffline();
            }
        }
    }

    private void checkOffline(){
        enableNoti = true;
        emailPageSave = realm.where(EmailPageSave.class)
                .findFirst();
        if(emailPageSave == null){
            swipeContainer.setRefreshing(true);
            readMailFirst(MAX_NUM_LOAD);
        }else {
            showOffline();
            swipeContainer.setRefreshing(true);
            readNewMail(emailPageSave.getIdLoadedTop());
        }
    }

    private void showOffline(){
        RealmResults<EmailItem> realmResults = realm.where(EmailItem.class)
                .findAllSorted("mId", Sort.DESCENDING);
        lists.addAll(realmResults);
        adapter.notifyDataSetChanged();
    }



    private void thaoTacEmail(final long key, final long pos){
        if(Util.isNetworkAvailable(this)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new thaoTacEmail().execute(key, pos);
                }
            });
        }
    }

    public class thaoTacEmail extends AsyncTask<Long, Void, Void>{

        @Override
        protected Void doInBackground(Long... longs) {
            Store store = null;
            Folder emailFolder = null;
            IMAPFolder imapFolder = null;
            try {
                store = emailSession.getStore("imaps");
                store.connect(linkHostMail, userText + "@student.tdt.edu.vn", passText);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_WRITE);
                imapFolder = (IMAPFolder) emailFolder;
                long key = longs[0];
                long id = longs[1];

                Message message = imapFolder.getMessageByUID(id);

                switch ((int)key){
                    case MAIL_SEEN:
                        message.setFlag(Flags.Flag.SEEN, true);
                        break;

                }

                emailFolder.close(true);
                store.close();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }


    private void readMoreMail(final long limit, final long idLoadedBottom){
        tvLoadMore.setVisibility(View.GONE);
        layoutLoadmore.setVisibility(View.VISIBLE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadMoreMail().execute(limit, idLoadedBottom);
            }
        });
    }

    private class ReadMoreMail extends AsyncTask<Long, Integer, List<EmailItem>>{

        @Override
        protected List<EmailItem> doInBackground(Long... longs) {

            long limit = longs[0];
            long idLoadedBottom = longs[1];
            Store store = null;
            Folder emailFolder = null;
            IMAPFolder imapFolder = null;
            try{
                store = emailSession.getStore("imaps");
                store.connect(linkHostMail, userText + "@student.tdt.edu.vn", passText);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);
                imapFolder = (IMAPFolder)emailFolder;


                long posGetTo = idLoadedBottom - 1;
                long posGetFrom = 0;

                Message[] messages = null;

                int loop = 1;
                do {
                    posGetFrom = posGetTo - limit * loop++;
                    posGetFrom = (posGetFrom >= 1) ? posGetFrom : 1;
                    messages = imapFolder.getMessagesByUID(posGetFrom, posGetTo);

                    //Log.d("ahihi", posGetTo + "-" + posGetFrom);

                }while (posGetFrom > 1 && messages.length == 0);

                Log.d("ahihi",  posGetFrom + "-" + posGetTo);

                List<EmailItem> emailItems = new ArrayList<>();
                EmailItem emailItem = null;

                for(Message message : messages){
                    emailItem = Util.createEmailItem(message, imapFolder);

                    if(emailItem != null)
                        emailItems.add(emailItem);
                }

                return emailItems;

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } finally {
                try {
                    emailFolder.close(false);
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<EmailItem> emailItems) {
            super.onPostExecute(emailItems);

            if(emailItems != null && emailItems.size() > 0){
                EmailItem emailItem = null;

                realm.beginTransaction();

                if(emailPageSave == null)
                    emailPageSave = new EmailPageSave();

                emailItem = emailItems.get(emailItems.size() - 1);

                emailPageSave.setIdLoadedBottom(emailItem.getmId());

                realm.copyToRealmOrUpdate(emailPageSave);

                //Log.d("Ahihi", "top " + emailPageSave.getIdLoadedTop() + ", bottom " + emailPageSave.getIdLoadedBottom());

                for(int i = emailItems.size() - 1; i >=0; i--){
                    emailItem = emailItems.get(i);
                    if(!lists.contains(emailItem)){
                        lists.add(emailItem);
                    }
                    realm.copyToRealmOrUpdate(emailItem);
                }

                realm.commitTransaction();

                adapter.notifyDataSetChanged();
            }

            tvLoadMore.setVisibility(View.VISIBLE);
            layoutLoadmore.setVisibility(View.GONE);
        }
    }

    private void readNewMail(final long idLoadedTop){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadNewMail().execute(idLoadedTop);
            }
        });
    }

    private class ReadNewMail extends AsyncTask<Long, Integer, List<EmailItem>>{

        @Override
        protected List<EmailItem> doInBackground(Long... longs) {

            long idLoadedTop = longs[0];
            Store store = null;
            Folder emailFolder = null;
            try{
                store = emailSession.getStore("imaps");
                store.connect(linkHostMail, userText + "@student.tdt.edu.vn", passText);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                IMAPFolder imapFolder = (IMAPFolder)emailFolder;

                long posGetTo = Long.MAX_VALUE;
                long posGetFrom = idLoadedTop + 1;

                Message[] messages = imapFolder.getMessagesByUID(posGetFrom, posGetTo);

                //Log.d("ahihi", ""  + messages.length);

                List<EmailItem> emailItems = new ArrayList<>();
                EmailItem emailItem = null;

                for(Message message : messages){
                    emailItem = Util.createEmailItem(message, imapFolder);

                    if(emailItem != null)
                        emailItems.add(emailItem);
                }

                return emailItems;

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } finally {
                try {
                    emailFolder.close(false);
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<EmailItem> emailItems) {
            super.onPostExecute(emailItems);

            if(emailItems != null && emailItems.size() > 0){
                EmailItem emailItem = null;

                realm.beginTransaction();

                if(emailPageSave == null)
                    emailPageSave = new EmailPageSave();

                emailItem = emailItems.get(emailItems.size() - 1);

                emailPageSave.setIdLoadedTop(emailItem.getmId());

                realm.copyToRealmOrUpdate(emailPageSave);

                //Log.d("Ahihi", "top " + emailPageSave.getIdLoadedTop() + ", bottom " + emailPageSave.getIdLoadedBottom());

                for(int i = 0; i < emailItems.size(); i++){
                    lists.add(0, emailItems.get(i));
                    realm.copyToRealmOrUpdate(emailItems.get(i));
                }

                realm.commitTransaction();

                adapter.notifyDataSetChanged();
            }

            swipeContainer.setRefreshing(false);
        }
    }

    private void readMailFirst(final int limit){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadMailFirst().execute(limit);
            }
        });
    }

    private class ReadMailFirst extends AsyncTask<Integer, Integer, List<EmailItem>>{

        @Override
        protected List<EmailItem> doInBackground(Integer... integers) {
            int limit = integers[0];
            Store store = null;
            Folder emailFolder = null;
            try{
                store = emailSession.getStore("imaps");
                store.connect(linkHostMail, userText + "@student.tdt.edu.vn", passText);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                IMAPFolder imapFolder = (IMAPFolder)emailFolder;

                int messageCount = imapFolder.getMessageCount();
                int posGetTo = messageCount;
                int posGetFrom = posGetTo - limit;
                posGetFrom = posGetFrom >= 1 ? posGetFrom : 1;


                Message[] messages = imapFolder.getMessages(posGetFrom, posGetTo);

                //Log.d("ahihi", ""  + messages.length);

                List<EmailItem> emailItems = new ArrayList<>();
                EmailItem emailItem = null;

                for(Message message : messages){
                    emailItem = Util.createEmailItem(message, imapFolder);

                    if(emailItem != null)
                        emailItems.add(emailItem);
                }

                return emailItems;

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } finally {
                try {
                    emailFolder.close(false);
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<EmailItem> emailItems) {
            super.onPostExecute(emailItems);

            if(emailItems != null && emailItems.size() > 0){
                EmailItem emailItem = null;
                emailItem = emailItems.get(0);
                realm.beginTransaction();

                if(emailPageSave == null)
                    emailPageSave = new EmailPageSave();

                emailPageSave.setIdLoadedBottom(emailItem.getmId());

                emailItem = emailItems.get(emailItems.size() - 1);

                emailPageSave.setIdLoadedTop(emailItem.getmId());

                realm.copyToRealmOrUpdate(emailPageSave);

                //Log.d("Ahihi", "top " + emailPageSave.getIdLoadedTop() + ", bottom " + emailPageSave.getIdLoadedBottom());

                for(int i = 0; i < emailItems.size(); i++){
                    lists.add(0, emailItems.get(i));
                    realm.copyToRealmOrUpdate(emailItems.get(i));
                }

                realm.commitTransaction();

                adapter.notifyDataSetChanged();
            }

            swipeContainer.setRefreshing(false);
        }
    }


    private void getEmailWeb(){
        swipeContainer.setRefreshing(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetEmailWebAsync().execute();
            }
        });
    }

    private class GetEmailWebAsync extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "mail")
                        .data("option", "web")
                        .timeout(30000)
                        .get();
                JSONObject root = new JSONObject(doc.text());
                if(root.getBoolean("status")){
                    return root.getString("data");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeContainer.setRefreshing(false);
            if(s != null){
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(EmailActivity.this, "No application can handle this request."
                            + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }else{

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
