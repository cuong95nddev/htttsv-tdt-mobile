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

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.email.EmailActivity;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailAttachment;
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
                        emailItem = new EmailItem();
                        emailItem.setmId(message.getMessageNumber());
                        Address[] froms = message.getFrom();
                        String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                        String personal = froms == null ? null : ((InternetAddress) froms[0]).getPersonal();
                        emailItem.setmFrom(email);
                        emailItem.setmPersonal(personal);
                        emailItem.setmSubject(message.getSubject());
                        emailItem.setmSentDate(message.getSentDate().getTime());

                        if(message.isSet(Flags.Flag.SEEN)){
                            emailItem.setNew(false);
                        }else{
                            emailItem.setNew(true);
                        }

                        dumpPart(message, emailItem, 0, 1);
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
                createNotification(b.size());
            }
        }
    }

    public void dumpPart(Part p, EmailItem emailItem, int level, int attnum) throws Exception {

        if (p.isMimeType("text/plain")) {
            emailItem.setmBody(p.getContent().toString());
        } else if(p.isMimeType("text/html")){
            emailItem.setmBody(Jsoup.parse(p.getContent().toString()).toString());
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            level++;
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
                dumpPart(mp.getBodyPart(i), emailItem, level, attnum);
            level--;
        } else if (p.isMimeType("message/rfc822")) {
            level++;
            dumpPart((Part)p.getContent(), emailItem, level, attnum);
            level--;
        }


        if (level != 0 && p instanceof MimeBodyPart && !p.isMimeType("multipart/*")) {
            String disp = p.getDisposition();
            if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {

                String filename = p.getFileName();
                if (filename != null) {

                    EmailAttachment emailAttachment = new EmailAttachment();
                    emailAttachment.setId(emailItem.getmId() + "-" + filename);
                    emailAttachment.setName(filename);
                    emailAttachment.setType(p.getContentType().split("; ")[0].toLowerCase());
                    emailItem.getEmailAttachments().add(emailAttachment);

//                    try {
//
//                        File file = new File(getApplicationContext().getFilesDir() + "/attachment/" + emailItem.getmId());
//                        if(!file.exists()){
//                            file.mkdirs();
//                        }
//
//                        file = new File(getApplicationContext().getFilesDir() + "/attachment/" + emailItem.getmId(), filename);
//                        if (file.exists())
//                            throw new IOException("file exists");
//                        ((MimeBodyPart)p).saveFile(file);
//
//                    } catch (IOException ex) {
//                        Log.d("", "Failed to save attachment: " + ex);
//                    }
                }
            }
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
