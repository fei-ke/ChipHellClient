
package com.fei_ke.chiphellclient.ui.activity;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.fei_ke.chiphellclient.R;

public class SoftwareNoticesActivity extends BaseActivity {
    @Override
    protected void onAfterViews() {

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.open_source_notices);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.root_layout, new SoftwareNoticesFragment())
                .commit();

        onPrivateAfterViews();
    }


    public static class SoftwareNoticesFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.software_notices);
        }
    }
}
