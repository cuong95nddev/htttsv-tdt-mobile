package edu.tdt.appstudent2.fragments.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.service.CheckEmailService;
import edu.tdt.appstudent2.service.CheckNewsService;
import edu.tdt.appstudent2.service.ServiceUtils;
import io.realm.Realm;


public class EditServiceDialogFragment extends DialogFragment {
    public static final String EXTRA = "TYPE";
    public static final int TYPE_TB = 1;
    public static final int TYPE_EMAIL = 2;
    RadioGroup rdgTime;
    RadioButton rbTime0;
    RadioButton rbTime1;
    RadioButton rbTime2;
    RadioButton rbTime3;
    CheckBox chbSound;
    CheckBox chbVibrate;
    Button btnSave;

    private Realm realm;
    private User user;

    public static EditServiceDialogFragment newInstance(int type) {
        EditServiceDialogFragment frag = new EditServiceDialogFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA, type);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_service_dialog, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);

        final int type = getArguments().getInt(EXTRA);
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        rdgTime = (RadioGroup) view.findViewById(R.id.rdgTime);
        rbTime0 = (RadioButton) view.findViewById(R.id.rbTime0);
        rbTime0.setChecked(true);
        rbTime1 = (RadioButton) view.findViewById(R.id.rbTime1);
        rbTime2 = (RadioButton) view.findViewById(R.id.rbTime2);
        rbTime3 = (RadioButton) view.findViewById(R.id.rbTime3);
        chbSound = (CheckBox) view.findViewById(R.id.chbSound);
        chbVibrate = (CheckBox) view.findViewById(R.id.chbVibrate);

        switch (type){
            case TYPE_EMAIL:
                chbSound.setChecked(user.getEmailServiceConfig().isSound());
                chbVibrate.setChecked(user.getEmailServiceConfig().isVibrate());
                setTimeReplay(user.getEmailServiceConfig().getTimeReplay());
                break;
            case TYPE_TB:
                chbSound.setChecked(user.getTbServiceConfig().isSound());
                chbVibrate.setChecked(user.getTbServiceConfig().isVibrate());
                setTimeReplay(user.getTbServiceConfig().getTimeReplay());
                break;
        }

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();

                switch (type){
                    case TYPE_EMAIL:
                        user.getEmailServiceConfig().setTimeReplay(getTimeReplay());
                        user.getEmailServiceConfig().setVibrate(chbVibrate.isChecked());
                        user.getEmailServiceConfig().setSound(chbSound.isChecked());
                        break;
                    case TYPE_TB:
                        user.getTbServiceConfig().setTimeReplay(getTimeReplay());
                        user.getTbServiceConfig().setVibrate(chbVibrate.isChecked());
                        user.getTbServiceConfig().setSound(chbSound.isChecked());
                        break;
                }

                switch (type){
                    case TYPE_EMAIL:
                        user.getEmailServiceConfig().setOpen(true);
                        ServiceUtils.startService(getContext()
                                , CheckEmailService.class
                                , ServiceUtils.TIME_REPLAY[(int)user.getEmailServiceConfig().getTimeReplay()]);
                        break;
                    case TYPE_TB:
                        user.getTbServiceConfig().setOpen(true);
                        ServiceUtils.startService(getContext()
                                , CheckNewsService.class
                                , ServiceUtils.TIME_REPLAY[(int)user.getTbServiceConfig().getTimeReplay()]);
                        break;
                }
                realm.insertOrUpdate(user);
                realm.commitTransaction();

                dismiss();
            }
        });

    }

    private long getTimeReplay(){
        switch (rdgTime.getCheckedRadioButtonId()){
            case R.id.rbTime0:
                return 0;
            case R.id.rbTime1:
                return 1;
            case R.id.rbTime2:
                return 2;
            case R.id.rbTime3:
                return 3;
        }
        return 0;
    }

    private void setTimeReplay(long time){
        switch ((int)time){
            case 0:
                rbTime0.setChecked(true);
                break;
            case 1:
                rbTime1.setChecked(true);
                break;
            case 2:
                rbTime2.setChecked(true);
                break;
            case 3:
                rbTime3.setChecked(true);
                break;
        }
    }

    public interface OnDismissEvent{
        void onDismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        this.onDismissEvent.onDismiss();
    }

    private OnDismissEvent onDismissEvent;

    public void setOnDismissEvent(OnDismissEvent onDismissEvent){
        this.onDismissEvent = onDismissEvent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
