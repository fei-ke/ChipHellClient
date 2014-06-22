
package com.fei_ke.chiphellclient.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.fei_ke.chiphellclient.R;

public class SoftwareNoticesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.software_notices);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.open_source_notices);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
