
package com.fei_ke.chiphellclient.ui.fragment;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

/**
 * Fragment基类
 *
 * @author fei-ke
 * @2014-6-14
 */
@EFragment
public abstract class BaseFragment extends Fragment {
    /**
     * 切勿调用和复写此方法
     */
    @AfterViews
    final protected void onPrivateAfterViews() {
        onAfterViews();
    }

    /**
     * 此方法在onCreateView之后调用
     */
    protected abstract void onAfterViews();

}
