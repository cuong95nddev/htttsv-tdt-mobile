package edu.tdt.appstudent2.actitities.email;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jkcarino.rtexteditorview.RTextEditorButton;
import com.jkcarino.rtexteditorview.RTextEditorToolbar;
import com.jkcarino.rtexteditorview.RTextEditorView;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.sun.mail.imap.IMAPFolder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.adapters.email.EmailAttchmentNewAdapter;
import edu.tdt.appstudent2.fragments.dialog.InsertLinkDialogFragment;
import edu.tdt.appstudent2.fragments.dialog.InsertTableDialogFragment;
import edu.tdt.appstudent2.models.User;
import edu.tdt.appstudent2.models.email.EmailItem;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class EmailNewActivity extends AppCompatActivity implements ColorPickerDialogListener {
    public static final String EXTRA_TO = "EXTRA_TO";
    public static final String EXTRA_SUBJECT = "EXTRA_SUBJECT";
    public static final String EXTRA_ID_REPLY = "EXTRA_ID_REPLY";
    public static final String EXTRA_BUG = "EXTRA_BUG";


    private static final int DIALOG_TEXT_FORE_COLOR_ID = 0;
    private static final int DIALOG_TEXT_BACK_COLOR_ID = 1;

    private RTextEditorView editor;

    AppCompatImageButton btnBack;
    AppCompatImageButton btnSend;
    AppCompatImageButton btnAttchment;

    private RecyclerView attRv;
    private EmailAttchmentNewAdapter attAdapter;

    AutoCompleteTextView edtTo;
    EditText edtSubject;

    String to, from, subject, body, mailhost, linkHostMail;

    private Properties properties;
    private Session emailSession;

    private Realm realm;
    private User user;
    private String username, password, name;

    ProgressDialog progressDialog;

    private long idMailReply = 0;

    private String emailAddress [];
    private ArrayList<String> emailAddressList;

    private boolean bug;


    private void khoiTao(){
        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
        from = user.getUserName() + "@student.tdt.edu.vn";
        mailhost = "smtp.student.tdt.edu.vn";
        username = user.getUserName();
        password = user.getPassWord();
        name = user.getName();
        linkHostMail = user.getLinkHostMail();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            to = bundle.getString(EXTRA_TO);
            subject = bundle.getString(EXTRA_SUBJECT);
            idMailReply = bundle.getLong(EXTRA_ID_REPLY);
            bug = bundle.getBoolean(EXTRA_BUG);
        }


        RealmResults<EmailItem> realmResults = realm.where(EmailItem.class)
                .findAllSorted("mId", Sort.DESCENDING);

        emailAddressList = new ArrayList<>();
        for(int i = 0 ; i < realmResults.size(); i++){
            if(!emailAddressList.contains(realmResults.get(i).getmFrom())){
                emailAddressList.add(realmResults.get(i).getmFrom());
            }
        }

        emailAddress = new String[emailAddressList.size()];
        emailAddressList.toArray(emailAddress);


        properties = System.getProperties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.host", mailhost);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.quitwait", "false");

        emailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCancelable(false);
    }

    private void anhXa(){
        khoiTao();

        edtTo = (AutoCompleteTextView) findViewById(R.id.edtTo);
        edtTo.setAdapter(new ArrayAdapter<String>(  this,
                android.R.layout.simple_list_item_1,
                emailAddress));

        if(bug){
            edtTo.setVisibility(View.GONE);
        }

        edtSubject = (EditText) findViewById(R.id.edtSubject);

        edtTo.setText(to);
        edtSubject.setText(subject);


        attRv = (RecyclerView) findViewById(R.id.rvAttachment);
        attAdapter = new EmailAttchmentNewAdapter();
        attRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        attRv.setAdapter(attAdapter);
        attRv.setNestedScrollingEnabled(false);

        btnBack = (AppCompatImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSend = (AppCompatImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBeforeSend();
            }
        });
        btnAttchment = (AppCompatImageButton) findViewById(R.id.btnAttachment);
        btnAttchment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findAttachment();
            }
        });


        editor = findViewById(R.id.editor_view);
        editor.setIncognitoModeEnabled(true);

        RTextEditorToolbar editorToolbar = findViewById(R.id.editor_toolbar);
        editorToolbar.setEditorView(editor);


        editor.setOnTextChangeListener(new RTextEditorView.OnTextChangeListener() {
            @Override
            public void onTextChanged(String content) {

            }
        });

        // Text foreground color
        RTextEditorButton textForeColorButton = findViewById(R.id.text_fore_color);
        textForeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder()
                        .setDialogId(DIALOG_TEXT_FORE_COLOR_ID)
                        .setDialogTitle(R.string.dialog_title_text_back_color)
                        .setShowAlphaSlider(false)
                        .setAllowCustom(true)
                        .show(EmailNewActivity.this);
            }
        });

        // Text background color
        RTextEditorButton textBackColorButton = findViewById(R.id.text_back_color);
        textBackColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder()
                        .setDialogId(DIALOG_TEXT_BACK_COLOR_ID)
                        .setDialogTitle(R.string.dialog_title_text_back_color)
                        .setShowAlphaSlider(false)
                        .setAllowCustom(true)
                        .show(EmailNewActivity.this);
            }
        });

        // Insert table
        RTextEditorButton insertTableButton = findViewById(R.id.insert_table);
        insertTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertTableDialogFragment dialog = InsertTableDialogFragment.newInstance();
                dialog.setOnInsertClickListener(onInsertTableClickListener);
                dialog.show(getSupportFragmentManager(), "insert-table-dialog");
            }
        });

        // Insert Link
        RTextEditorButton insertLinkButton = findViewById(R.id.insert_link);
        insertLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertLinkDialogFragment dialog = InsertLinkDialogFragment.newInstance();
                dialog.setOnInsertClickListener(onInsertLinkClickListener);
                dialog.show(getSupportFragmentManager(), "insert-link-dialog");
            }
        });
    }

    private static final int FILE_CODE = 1;

    private void findAttachment() {
        Intent i = new Intent(this, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            for (Uri uri: files) {
                File file = Utils.getFileForUri(uri);
                attAdapter.addItem(file);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = getContentResolver().query( contentUri, proj, null, null,null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_new);
        anhXa();
    }


    private void checkBeforeSend() {
        to = edtTo.getText().toString().trim();
        if("".equals(to)){
            Toast.makeText(this, "Vui lòng nhập địa chỉ email người nhận", Toast.LENGTH_SHORT).show();
            return;
        }

        subject = edtSubject.getText().toString().trim();
        if("".equals(subject)){
            Toast.makeText(this, "Vui lòng nhập chủ đề cho email", Toast.LENGTH_SHORT).show();
        }

        body = editor.getHtml();

        sendEmail();

    }


    private void sendEmail(){
        progressDialog.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SendEmail().execute();
            }
        });
    }

    private class SendEmail extends AsyncTask<Void, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                MimeMessage message = null;

                if(idMailReply != 0){
                    Store store = emailSession.getStore("imaps");
                    store.connect(linkHostMail, username + "@student.tdt.edu.vn", password);
                    Folder emailFolder = store.getFolder("INBOX");
                    emailFolder.open(Folder.READ_WRITE);
                    IMAPFolder imapFolder = (IMAPFolder) emailFolder;

                    Message emailMessage = imapFolder.getMessageByUID(idMailReply);
                    message = (MimeMessage) emailMessage.reply(false);
                    emailFolder.close(false);
                    store.close();
                }else{
                    message = new MimeMessage(emailSession);
                }
                message.setFrom(new InternetAddress(from, name));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject(subject);

                BodyPart messageBodyPart = null;
                Multipart multipart = new MimeMultipart();

                messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(body, "text/html; charset=UTF-8");
                multipart.addBodyPart(messageBodyPart);

                for(File file : attAdapter.lists){
                    messageBodyPart = new MimeBodyPart();
                    String filename = file.getName();
                    DataSource source = new FileDataSource(file.getAbsoluteFile());
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);
                }

                message.setContent(multipart);

                Transport.send(message);
            }catch (MessagingException mex) {
                mex.printStackTrace();
                return false;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            progressDialog.dismiss();
            if(b){
                Toast.makeText(EmailNewActivity.this, "Email được gửi thành công", Toast.LENGTH_SHORT).show();
                idMailReply = -1;
            }else{
                Toast.makeText(EmailNewActivity.this, "Gửi email thất bại. Vui lòng kiếm tra kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.setOnTextChangeListener(null);
        editor.removeAllViews();
        editor.destroy();
        editor = null;
        realm.close();
    }

    private final InsertTableDialogFragment.OnInsertClickListener onInsertTableClickListener =
            new InsertTableDialogFragment.OnInsertClickListener() {
                @Override
                public void onInsertClick(int colCount, int rowCount) {
                    editor.insertTable(colCount, rowCount);
                }
            };

    private final InsertLinkDialogFragment.OnInsertClickListener onInsertLinkClickListener =
            new InsertLinkDialogFragment.OnInsertClickListener() {
                @Override
                public void onInsertClick(@NonNull String title, @NonNull String url) {
                    editor.insertLink(title, url);
                }
            };

    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == DIALOG_TEXT_FORE_COLOR_ID) {
            editor.setTextColor(color);
        } else if (dialogId == DIALOG_TEXT_BACK_COLOR_ID) {
            editor.setTextBackgroundColor(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
