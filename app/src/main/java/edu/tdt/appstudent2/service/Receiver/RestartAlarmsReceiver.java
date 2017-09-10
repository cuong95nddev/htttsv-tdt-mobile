package edu.tdt.appstudent2.service.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailPageSave;
import edu.tdt.appstudent2.models.thongbao.DonviItem;
import edu.tdt.appstudent2.service.CheckEmailService;
import edu.tdt.appstudent2.service.CheckNewsService;
import edu.tdt.appstudent2.service.ServiceUtils;
import io.realm.Realm;

/**
 * Created by bichan on 9/8/17.
 */

public class RestartAlarmsReceiver extends BroadcastReceiver {
    private Realm realm;
    private User user;

    @Override
    public void onReceive(Context context, Intent intent) {
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if(user != null){

                // check config
                if(user.getEmailServiceConfig().isOpen()
                        && realm.where(EmailPageSave.class).findFirst() != null){
                    ServiceUtils.startService(context
                            , CheckEmailService.class
                            , user.getEmailServiceConfig().getTimeReplay());
                }

                if(user.getTbServiceConfig().isOpen()
                        && realm.where(DonviItem.class).count() > 0){
                    ServiceUtils.startService(context
                            , CheckNewsService.class
                            , user.getTbServiceConfig().getTimeReplay());
                }

            }
        }

        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            boolean check = false;

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                check = true;
            }

            if(user != null && check){
                if(user.isCheckNetworkState()){

                    // disable check when network change again
                    realm.beginTransaction();
                    user.setCheckNetworkState(false);
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();

                    if(user.getEmailServiceConfig().isOpen()
                            && realm.where(EmailPageSave.class).findFirst() != null){
                        ServiceUtils.startService(context
                                , CheckEmailService.class);
                    }

                    if(user.getTbServiceConfig().isOpen()
                            && realm.where(DonviItem.class).count() > 0){
                        ServiceUtils.startService(context
                                , CheckNewsService.class);
                    }
                }
            }
        }

        realm.close();
    }
}
