package edu.tdt.appstudent2.actitities.trangchu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.tdt.appstudent2.MainActivity;
import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.adapters.thongbao.FragmentAdapter;
import edu.tdt.appstudent2.fragments.trangchu.TrangchuMenuFragment;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.views.widget.CircleImageView;
import io.realm.Realm;

public class TrangchuActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Fragment> fragmentArrayList;

    private Realm realm;
    private User user;
    private String userText, passText, avatarText, nameText;
    private TextView massv, name;
    private CircleImageView avatar;

    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        userText = user.getUserName();
        passText = user.getPassWord();
        nameText = user.getName();
        avatarText = user.getLinkAvatar();

        fragmentArrayList = new ArrayList<Fragment>();
        fragmentAdapter = new FragmentAdapter(getApplicationContext(), getSupportFragmentManager(), fragmentArrayList);

    }
    private void anhXa(){
        khoiTao();

        viewPager = (ViewPager) findViewById(R.id.viewpaper);
        viewPager.setAdapter(fragmentAdapter);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hệ thống thông tin Sinh viên");

        name = (TextView) findViewById(R.id.name_text);
        massv = (TextView) findViewById(R.id.mssv_text);
        avatar = (CircleImageView) findViewById(R.id.avatar_img);

        name.setText(nameText);
        massv.setText(userText);
        Picasso.with(getApplicationContext()).load(avatarText).into(avatar);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu);
        anhXa();
        addPaper();


    }

    private void addPaper(){
        TrangchuMenuFragment trangchuMenuFragment = new TrangchuMenuFragment();
        fragmentArrayList.add(trangchuMenuFragment);
        fragmentAdapter.addTitle("Menu");
        fragmentAdapter.notifyDataSetChanged();
    }

    private void logOut(){
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.mess_logout_tile))
                .setMessage(getResources().getString(R.string.mess_logout_info))
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                        Intent mainActicity = new Intent(TrangchuActivity.this, MainActivity.class);
                        startActivity(mainActicity);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trangchu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_logout:
                logOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
