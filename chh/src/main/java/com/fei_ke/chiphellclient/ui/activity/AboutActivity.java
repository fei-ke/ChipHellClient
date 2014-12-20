
package com.fei_ke.chiphellclient.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Plate;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 关于页面
 * 
 * @author fei-ke
 * @2014-6-22
 */
@EActivity(R.layout.activity_about)
public class AboutActivity extends BaseActivity {
    @ViewById(R.id.textView_version)
    TextView textViewVersion;

    @ViewById(R.id.textView_about)
    TextView textViewAbout;

    public static Intent getStartIntent(Context context) {
        return AboutActivity_.intent(context).get();
    }

    protected void setVersion() {
        PackageManager pm = getPackageManager();
        try {
            String versionName = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
            textViewVersion.setText(getString(R.string.about_version, versionName));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setAboutText() {
        textViewAbout.setClickable(false);
        textViewAbout.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("目前版本问题较多，反馈问题请到:\n");
        ClickableSpan chhSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Plate plate = new Plate();
                plate.setTitle("自由水世界");
                plate.setUrl("http://www.chiphell.com/forum.php?mod=forumdisplay&fid=201&mobile=2");
                Thread thread = new Thread();
                thread.setTitle("ChipHell非官方客户端发布页");
                thread.setUrl("http://www.chiphell.com/forum.php?mod=viewthread&tid=1058176&extra=page%3D1&mobile=2");
                Intent intent = ThreadDetailActivity.getStartIntent(AboutActivity.this, plate, thread);
                // Intent intent = new Intent(Intent.ACTION_VIEW);
                // intent.setData(Uri.parse("http://www.chiphell.com/forum.php?mod=viewthread&tid=1058176&extra=page%3D1&mobile=2"));
                startActivity(intent);
            }
        };
        SpannableString chh = new SpannableString("ChhHell非官方客户端发布页");
        chh.setSpan(chhSpan, 0, chh.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(chh);
        builder.append(" 或者我的新浪微博 ");
        ClickableSpan weiboSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://weibo.com/jinyang656"));
                startActivity(intent);
            }
        };
        SpannableString weibo = new SpannableString("@与非");
        weibo.setSpan(weiboSpan, 0, weibo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(weibo);
        builder.append("进行反馈..\n源代码稍后开放。");

        textViewAbout.setText(builder);
    }

    @Override
    protected void onAfterViews() {
        setTitle(R.string.about);
        setAboutText();
        setVersion();
    }

}
