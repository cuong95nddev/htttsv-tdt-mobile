package edu.tdt.appstudent2.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.email.EmailActivity;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailPageSave;
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

    private EmailPageSave emailPageSave;

    private boolean sound;
    private boolean vibrate;

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

                    EmailPageSave emailPageSaveGet = realm.where(EmailPageSave.class)
                            .findFirst();
                    emailPageSave = new EmailPageSave();
                    emailPageSave.setIdLoadedTop(emailPageSaveGet.getIdLoadedTop());
                    emailPageSave.setIdLoadedBottom(emailPageSaveGet.getIdLoadedBottom());
                    realm.close();
//                    new readListMail().execute();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
