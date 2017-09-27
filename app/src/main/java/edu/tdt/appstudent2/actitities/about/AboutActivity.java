package edu.tdt.appstudent2.actitities.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import edu.tdt.appstudent2.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final FrameLayout flHolder = (FrameLayout) findViewById(R.id.about);

        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.drawable.avatar_fb)
                .setCover(R.drawable.banner)
                .setName("Lý Kim Phát")
                .setSubTitle("FA chính hiệu")
                .setBrief("Mình là người đơn giản, mình thích thì mình làm thôi :))")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .addFiveStarsAction()
                .addUpdateAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setWrapScrollView(false)
                .setLinksAnimated(true)
                .setShowAsCard(false)
                .build();

        flHolder.addView(view);

    }
}
