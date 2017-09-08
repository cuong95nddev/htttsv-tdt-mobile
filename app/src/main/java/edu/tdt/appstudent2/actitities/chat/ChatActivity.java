package edu.tdt.appstudent2.actitities.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.adapters.chat.ChatAdapter;
import edu.tdt.appstudent2.adapters.chat.UserOnlineAdapter;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.firebase.Admin;
import edu.tdt.appstudent2.models.firebase.Chat;
import edu.tdt.appstudent2.models.firebase.ChatShow;
import edu.tdt.appstudent2.models.firebase.ChatUser;
import edu.tdt.appstudent2.models.firebase.UserOnline;
import io.realm.Realm;

public class ChatActivity extends AppCompatActivity {
    private Realm realm;
    private User user;

    private RecyclerView mMessageRecycler;
    private ChatAdapter mMessageAdapter;

    private RecyclerView rvUserOnline;
    private UserOnlineAdapter userOnlineAdapter;
    private StaggeredGridLayoutManager userOnlineManager;

    private EditText edtChat;
    private AppCompatImageButton btnChat;
    private AppCompatImageButton btnBack;

    private DatabaseReference mDatabase;
    private DatabaseReference chatReference;
    private DatabaseReference adminReference;
    private DatabaseReference userReference;
    private Query updateStatusAvatarQuery;

    private ValueEventListener valueChatEventListener;
    private ValueEventListener valueAdminEventListener;
    private ValueEventListener valueUserEventListener;
    private ValueEventListener valueUpdateStatusAvatarEventListener;

    private String username;
    private String name;
    private String avatar;

    private boolean autoScroll = true;
    private boolean isAdmin = false;
    private Admin admin = null;

    private ArrayList<String> mssvOnline = new ArrayList<>();

    private Switch swAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        username = user.getUserName();
        name = user.getName();
        avatar = user.getLinkAvatar();


        swAvatar = (Switch) findViewById(R.id.swAvatar);
        swAvatar.setChecked(user.isShowAvatar());

        rvUserOnline = (RecyclerView) findViewById(R.id.rvUserOnline);
        userOnlineAdapter = new UserOnlineAdapter();
        userOnlineManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        userOnlineManager.setSpanCount(1);
        rvUserOnline.setLayoutManager(userOnlineManager);
        rvUserOnline.setHasFixedSize(true);
        rvUserOnline.setAdapter(userOnlineAdapter);


        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new ChatAdapter(this);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        edtChat = (EditText) findViewById(R.id.edittext_chatbox);
        btnChat = (AppCompatImageButton) findViewById(R.id.button_chatbox_send);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = edtChat.getText().toString().trim();
                if(!"".equals(text)){
                    Chat chat = new Chat();
                    chat.time = System.currentTimeMillis();
                    chat.body = text;
                    chat.chatUser = new ChatUser();
                    chat.chatUser.mssv = username;
                    chat.chatUser.name = isAdmin?admin.nickname:name;
                    chat.chatUser.avatar = avatar;
                    chat.chatUser.isAdmin = isAdmin;
                    chatReference.push().setValue(chat, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            edtChat.setText("");
                            mMessageRecycler.scrollToPosition((int)mMessageAdapter.getItemCount() - 1);
                            autoScroll = true;
                        }
                    });
                    updateOnlineState();
                }
            }
        });

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        chatReference = mDatabase.child("Chat2");
        chatReference.keepSynced(true);
        valueChatEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessageAdapter.clear();
                ChatShow chatShow = null;
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    chatShow = postSnapshot.getValue(ChatShow.class);
                    if(chatShow.chatUser.mssv.equals(username)){
                        chatShow.type = 2;
                    }else {
                        chatShow.type = 1;
                    }
                    if(mssvOnline.contains(chatShow.chatUser.mssv)){
                        chatShow.online = true;
                    }
                    mMessageAdapter.addItem(chatShow);
                }
                if(autoScroll){
                    mMessageRecycler.scrollToPosition((int)mMessageAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        chatReference.addValueEventListener(valueChatEventListener);

        adminReference = mDatabase.child("Admin");
        valueAdminEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isAdmin = false;
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    admin = postSnapshot.getValue(Admin.class);
                    if(admin.mssv.equals(username)){
                        isAdmin = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        adminReference.addValueEventListener(valueAdminEventListener);


        userReference = mDatabase.child("UserOnline");
        userReference.keepSynced(false);
        valueUserEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //userOnlineAdapter.lists.clear();
                mssvOnline.clear();
                UserOnline userOnline = null;
                long timeNow = System.currentTimeMillis();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    userOnline = postSnapshot.getValue(UserOnline.class);
                    if((timeNow - userOnline.time) <= 2 * 60 * 1000){
                        //userOnlineAdapter.addItem(userOnline);
                        mssvOnline.add(userOnline.mssv);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userReference.addValueEventListener(valueUserEventListener);

        updateOnlineState();

        mMessageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {

                } else {
                    autoScroll = false;
                }
            }
        });

        updateStatusAvatarQuery = chatReference.orderByChild("chatUser/mssv").equalTo(username);


        swAvatar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showOrHideAvatar(b);
            }
        });

    }

    private void showOrHideAvatar(final boolean b) {
        realm.beginTransaction();
        user.setShowAvatar(b);
        realm.commitTransaction();
        valueUpdateStatusAvatarEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().child("chatUser").child("showAvatar").setValue(b);
                }
                updateStatusAvatarQuery.removeEventListener(valueUpdateStatusAvatarEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        updateStatusAvatarQuery.addValueEventListener(valueUpdateStatusAvatarEventListener);
    }

    private void updateOnlineState(){
        UserOnline userOnline = new UserOnline();
        userOnline.mssv = username;
        userOnline.time = System.currentTimeMillis();
        userReference.child(userOnline.mssv).setValue(userOnline);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        chatReference.removeEventListener(valueChatEventListener);
        adminReference.removeEventListener(valueAdminEventListener);
        userReference.removeEventListener(valueUserEventListener);
    }
}
