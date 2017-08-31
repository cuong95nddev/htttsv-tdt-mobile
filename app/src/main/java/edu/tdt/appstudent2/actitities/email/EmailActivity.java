package edu.tdt.appstudent2.actitities.email;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.ybq.endless.Endless;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.adapters.email.EmailRecyclerViewAdapter;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailItem;
import edu.tdt.appstudent2.models.email.EmailPageSave;
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

    private Folder emailFolder;
    private Store store;

    private EmailPageSave emailPageSave;
    private static final int MAX_NUM_LOAD = 20;

    private boolean isRefresh = false;
    private Properties properties;
    private Session emailSession;

    AppCompatImageButton btnBack;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
        linkHostMail = user.getLinkHostMail();
        lists = new ArrayList<EmailItem>();

        properties = System.getProperties();
        properties.setProperty("mail.store.protocol", "imaps");
        emailSession = Session.getDefaultInstance(properties);
    }
    private void anhXa(){
        khoiTao();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new EmailRecyclerViewAdapter(getApplicationContext(), lists);
        manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        manager.setSpanCount(1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        View loadingView = View.inflate(this, R.layout.recyclerview_loading, null);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        endless = Endless.applyTo(recyclerView, loadingView);
        endless.setAdapter(adapter);

        endless.setLoadMoreListener(new Endless.LoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                if(Util.isNetworkAvailable(EmailActivity.this)) {
                    isRefresh = false;
                    readListMail();
                }else{

                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

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
        setContentView(R.layout.activity_email);
        anhXa();
        checkLinkHostMail();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                readListMail();
            }
        });

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
            getHostMail();
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
       EmailPageSave emailPageSaveGet = realm.where(EmailPageSave.class)
                .findFirst();
        if(emailPageSaveGet == null){
            swipeContainer.setRefreshing(true);
            readListMailFirst();
        }else {
            emailPageSave = new EmailPageSave();
            emailPageSave.setIdLoadedTop(emailPageSaveGet.getIdLoadedTop());
            emailPageSave.setIdLoadedBottom(emailPageSaveGet.getIdLoadedBottom());
            showOffline();
            isRefresh = true;
            readListMail();
        }
    }

    private void showOffline(){
        RealmResults<EmailItem> realmResults = realm.where(EmailItem.class)
                .findAllSorted("mId", Sort.DESCENDING);
        lists.addAll(realmResults);
        adapter.notifyDataSetChanged();
    }

    private void readListMailFirst(){
        isRefresh = false;
        readListMail();
    }




    private void thaoTacEmail(final int key, final int pos){
        if(Util.isNetworkAvailable(this)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new thaoTacEmail().execute(key, pos);
                }
            });
        }
    }

    public class thaoTacEmail extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                store = emailSession.getStore("imaps");
                store.connect(linkHostMail, userText + "@student.tdt.edu.vn", passText);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_WRITE);
                int key = integers[0];
                int pos = integers[1];

                Message message = emailFolder.getMessage(pos);

                switch (key){
                    case MAIL_SEEN:
                        message.setFlag(Flags.Flag.SEEN, true);
                        break;

                }

                emailFolder.close(false);
                store.close();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    private void readListMail(){
        swipeContainer.setRefreshing(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new readListMail().execute();
            }
        });
    }
    public class readListMail extends AsyncTask<Void, Integer, ArrayList<EmailItem>>{

        @Override
        protected ArrayList<EmailItem> doInBackground(Void... voids) {
            try {
                store = emailSession.getStore("imaps");
                store.connect(linkHostMail, userText + "@student.tdt.edu.vn", passText);
                emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);
                Message[] messages = emailFolder.getMessages();
                if(messages.length > 0) {
                    //Kiểm tra đã được tải lần nào chưa
                    int numNotGet; //Số lượng item chưa được lấy0
                    int numLoad; // số lượng item sẽ lấy
                    int loadFrom; // lấy từ vị trí
                    int loadTo; // lấy đến vị trí
                    if(isRefresh && emailPageSave != null){
                        loadFrom = messages.length - 1;
                        loadTo = emailPageSave.getIdLoadedTop();
                        emailPageSave.setIdLoadedTop(messages.length);
                    }else {
                        isRefresh = false;
                        if (emailPageSave == null) {
                            emailPageSave = new EmailPageSave();
                            numNotGet = messages.length;
                            emailPageSave.setIdLoadedTop(numNotGet); //đặt vị trí item đầu tiên
                        } else {
                            numNotGet = emailPageSave.getIdLoadedBottom();
                        }

                        loadFrom = numNotGet - 1; // tổng số chưa lấy -1
                        if (numNotGet < MAX_NUM_LOAD) {
                            numLoad = numNotGet;
                        } else {
                            numLoad = MAX_NUM_LOAD;
                        }
                        loadTo = loadFrom - numLoad;
                        emailPageSave.setIdLoadedBottom(loadTo);
                    }
                    EmailItem emailItem = null;
                    Message message = null;
                    ArrayList<EmailItem> emailGetNew = new ArrayList<EmailItem>();
                    for (int i = loadFrom; i >= loadTo; i--) {
                        message = messages[i];
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
                        String mSentDate = dateFormat.format(message.getSentDate());
                        dateFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                        String mSentDateShort = dateFormat.format(message.getSentDate());
                        emailItem = new EmailItem();
                        emailItem.setmId(message.getMessageNumber());
                        Address[] froms = message.getFrom();
                        String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                        String personal = froms == null ? null : ((InternetAddress) froms[0]).getPersonal();
                        emailItem.setmFrom(email);
                        emailItem.setmPersonal(personal);
                        emailItem.setmSubject(message.getSubject());
                        emailItem.setmSentDate(mSentDate);
                        emailItem.setmSentDateShort(mSentDateShort);

                        if(message.isSet(Flags.Flag.SEEN)){
                            emailItem.setNew(false);
                        }else{
                            emailItem.setNew(true);
                        }

                        emailItem.setmBody(getTextFromMessage(message));


                        emailGetNew.add(emailItem);
                    }
                    return emailGetNew;
                }
                emailFolder.close(false);
                store.close();
                return null;
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<EmailItem> b) {
            super.onPostExecute(b);
            if(b != null){
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(emailPageSave);
                if(isRefresh){
                    for(int i = b.size() - 1; i >= 0; i--){
                        lists.add(0, b.get(i));
                        realm.copyToRealmOrUpdate(b.get(i));
                    }
                }else {
                    for (EmailItem emailItem : b) {
                        lists.add(emailItem);
                        adapter.notifyItemInserted(lists.size() - 1);
                        realm.copyToRealmOrUpdate(emailItem);
                    }
                }
                realm.commitTransaction();
            }
            endless.loadMoreComplete();
            swipeContainer.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }


    private String getTextFromMessage(Message message) throws Exception {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws Exception{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {

            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).toString();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_email, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_open_web:
                getEmailWeb();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
