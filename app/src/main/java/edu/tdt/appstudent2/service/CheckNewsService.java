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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.Token;
import edu.tdt.appstudent2.actitities.thongbao.ThongbaoActivity;
import edu.tdt.appstudent2.api.Api;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.thongbao.ThongbaoItem;
import edu.tdt.appstudent2.utils.StringUtil;
import edu.tdt.appstudent2.utils.Util;
import io.realm.Realm;


public class CheckNewsService extends IntentService {
    private String idDonVi;

    private int pageNow;
    private Realm realm;
    private User user;
    private String userText, passText;
    private boolean sound;
    private boolean vibrate;

    public CheckNewsService() {
        super("CheckNewsService");
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
                    sound = user.getTbServiceConfig().isSound();
                    vibrate = user.getTbServiceConfig().isVibrate();
                    idDonVi = "";
                    pageNow = 1;
                    new readThongBao().execute("");
                    realm.close();
                }
            };

            thread.start();
        }else {
            realm = Realm.getDefaultInstance();
            user = realm.where(User.class).findFirst();
            realm.beginTransaction();
            user.setCheckNetworkState(true);
            realm.copyToRealmOrUpdate(user);
            realm.commitTransaction();
            realm.close();
        }
    }

    public class readThongBao extends AsyncTask<String, Integer, ArrayList<ThongbaoItem>> {

        @Override
        protected ArrayList<ThongbaoItem> doInBackground(String... strings) {
            ArrayList<ThongbaoItem> thongbaoItems = new ArrayList<ThongbaoItem>();
            try {
                Document doc = Jsoup.connect(Api.host)
                        .data("user", userText)
                        .data("token", Token.getToken(userText, passText))
                        .data("act", "tb")
                        .data("lv", idDonVi)
                        .data("page", Integer.toString(pageNow))
                        .timeout(30000)
                        .get();
                JSONObject root = new JSONObject(doc.text());

                if(!root.getBoolean("status")){
                    return thongbaoItems;
                }

                JSONObject dataObject = root.getJSONObject("data");
                if(dataObject != null){
                    JSONArray thongBaoArray = dataObject.getJSONArray("thongbao");
                    ThongbaoItem thongBaoNew = null;
                    for(int i = 0; i < thongBaoArray.length(); i++){
                        JSONObject thongBaoItem = thongBaoArray.getJSONObject(i);
                        thongBaoNew = new ThongbaoItem();
                        thongBaoNew.setTitle(StringUtil.thongBaoFormat(thongBaoItem.getString("title")));
                        //Khóa chính là id + "-" + idDonvi nên idDonvi phải được ghi trước
                        thongBaoNew.setIdDonVi(idDonVi);
                        thongBaoNew.setId(thongBaoItem.getString("id"));
                        thongBaoNew.setDate(StringUtil.thongBaoGetDate(thongBaoItem.getString("title")));
                        thongBaoNew.setNew(thongBaoItem.getBoolean("unread"));
                        thongbaoItems.add(thongBaoNew);
                    }
                }
            } catch (IOException e) {
                //
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (java.lang.IllegalStateException e) {
                realm.close();
            }
            return thongbaoItems;
        }

        @Override
        protected void onPostExecute(ArrayList<ThongbaoItem> thongbaoItems) {
            super.onPostExecute(thongbaoItems);

            if(thongbaoItems.size() > 0){
                int nNews = 0;
                realm = Realm.getDefaultInstance();
                for (ThongbaoItem e: thongbaoItems){
                    try {
                        realm.beginTransaction();
                        if(realm.where(ThongbaoItem.class).equalTo("id", e.getId()).findFirst() == null){
                            nNews++;
                        }
                        realm.copyToRealmOrUpdate(e);
                        realm.commitTransaction();
                    }catch (java.lang.IllegalStateException a){
                        realm.close();
                    }
                }
                realm.close();
                if(nNews > 0)
                    createNotification(nNews);
            }
        }
    }

    private void createNotification(int nNews){
        Intent notificationIntent = new Intent(this, ThongbaoActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(this, "university-tdt");
        }else {
            mBuilder = new NotificationCompat.Builder(this);
        }

        mBuilder.setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setLargeIcon(largeIcon)
                .setContentTitle("Có " + nNews + " thông báo mới !!!")
                .setContentText("Vui lòng nhấn vào đây để chuyển đến màn hình thông báo.")
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
        mNotificationManager.notify(2, mBuilder.build());
    }
}
