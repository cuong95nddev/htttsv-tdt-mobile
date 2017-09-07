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
import edu.tdt.appstudent2.actitities.email.EmailActivity;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailItem;
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
                    new readListMail().execute();
                }
            };
            thread.start();
        }
    }


    public class readListMail extends AsyncTask<Void, Integer, ArrayList<EmailItem>> {

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

                    loadFrom = messages.length - 1;
                    loadTo = emailPageSave.getIdLoadedTop();
                    emailPageSave.setIdLoadedTop(messages.length);

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
            if(b != null && b.size() > 0){
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(emailPageSave);
                for(int i = b.size() - 1; i >= 0; i--){
                    realm.copyToRealmOrUpdate(b.get(i));
                }
                realm.commitTransaction();
                realm.close();
                createNotification();
            }
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

    private void createNotification(){
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
                .setContentTitle("CÓ EMAIL MỚI")
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
