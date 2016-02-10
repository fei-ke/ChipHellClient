
package com.fei_ke.chiphellclient.ui.activity;

import android.os.Bundle;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.ui.fragment.SettingFragment;

/**
 * 设置页面
 *
 * @author fei-ke
 * @2014年6月28日
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onAfterViews() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        setTitle(R.string.action_settings);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.root_layout, new SettingFragment())
                .commit();

        onPrivateAfterViews();

    }
}
