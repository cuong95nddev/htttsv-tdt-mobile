package edu.tdt.appstudent2.service.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            user = realm.where(User.class).findFirst();
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
            realm.close();
        }
    }
}
