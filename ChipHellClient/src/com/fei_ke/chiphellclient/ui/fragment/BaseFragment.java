
package com.fei_ke.chiphellclient.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview != null) {
            System.out.println(this.toString() + "mView 有缓存");
            return rootview;
        } else {
            System.out.println(this.toString() + "mView 无缓存");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootview = view;
    }

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
