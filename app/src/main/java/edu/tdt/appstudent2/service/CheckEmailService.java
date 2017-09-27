package edu.tdt.appstudent2.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sun.mail.imap.IMAPFolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.email.EmailActivity;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailItem;
import edu.tdt.appstudent2.models.email.EmailPageSave;
import edu.tdt.appstudent2.models.thongbao.ThongbaoItem;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;

public class CheckEmailService extends IntentService {
    private Realm realm;
    private User user;
    private String userText, passText;
    private String linkHostMail;

    private Folder emailFolder;
    private Store store;

    private Properties properties;
    private Session emailSession;


    private boolean sound;
    private boolean vibrate;

    private EmailPageSave emailPageSave;

    public CheckEmailService() {
        super("CheckEmailService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(Util.isNetworkAvailable(getApplicationContext())){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    realm = Realm.getDefaultInstance();

                    user = realm.where(User.class).findFirst();
                    userText = user.getUserName();
                    passText = user.getPassWord();

                    sound = user.getEmailServiceConfig().isSound();
                    vibrate = user.getEmailServiceConfig().isVibrate();

                    linkHostMail = user.getLinkHostMail();

                    properties = System.getProperties();
                    properties.setProperty("mail.store.protocol", "imaps");
                    emailSession = Session.getDefaultInstance(properties);

                    emailPageSave = realm.where(EmailPageSave.class)
                            .findFirst();
                    if(emailPageSave != null){
                        long idLoadedTop = emailPageSave.getIdLoadedTop();
                        new ReadNewMail().execute(idLoadedTop);
                    }
                    realm.close();
                }
            };
            thread.start();
        }else{
            realm = Realm.getDefaultInstance();
            user = realm.where(User.class).findFirst();
            realm.beginTransaction();
            user.setCheckNetworkState(true);
            realm.copyToRealmOrUpdate(user);
            realm.commitTransaction();
            realm.close();
        }
    }



    private void createNotification(int nNews){
        Intent notificationIntent = new Intent(this, EmailActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(this, "university-tdt");
        }else {
            mBuilder = new NotificationCompat.Builder(this);
        }

        mBuilder.setSmallIcon(R.drawable.ic_email_black_24dp)
                .setLargeIcon(largeIcon)
                .setContentTitle("Có " + nNews + " email mới !!!")
                .setContentText("Vui lòng nhấn vào đây để chuyển đến màn hình Email.")
                .setContentIntent(intent)
                .setAutoCancel(true);

        if(sound){
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        if(vibrate){
            long[] vibrate = { 0, 100, 200, 300 };
            mBuilder.setVibrate(vibrate);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }


    private class ReadNewMail extends AsyncTask<Long, Integer, List<EmailItem>> {

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

                realm = Realm.getDefaultInstance();

                realm.beginTransaction();

                emailPageSave = realm.where(EmailPageSave.class)
                        .findFirst();

                if(emailPageSave == null)
                    emailPageSave = new EmailPageSave();

                emailItem = emailItems.get(emailItems.size() - 1);

                emailPageSave.setIdLoadedTop(emailItem.getmId());

                realm.copyToRealmOrUpdate(emailPageSave);

                //Log.d("Ahihi", "top " + emailPageSave.getIdLoadedTop() + ", bottom " + emailPageSave.getIdLoadedBottom());
                int nNews = 0;
                for(int i = 0; i < emailItems.size(); i++){
                    emailItem = emailItems.get(i);
                    if(realm.where(EmailItem.class).equalTo("mId", emailItem.getmId()).findFirst() == null){
                        nNews++;
                    }
                    realm.copyToRealmOrUpdate(emailItem);
                }

                if(nNews > 0){
                    createNotification(nNews);
                }

                realm.commitTransaction();
                realm.close();

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
