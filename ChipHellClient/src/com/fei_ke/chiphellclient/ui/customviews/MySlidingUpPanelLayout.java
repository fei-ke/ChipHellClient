package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by fei-ke on 2014/12/14.
 */
public class MySlidingUpPanelLayout extends SlidingUpPanelLayout {
    public MySlidingUpPanelLayout(Context context) {
        super(context);
    }

    public MySlidingUpPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HookDispatchTouchEvent getHookDispatchTouchEvent() {
        return hookDispatchTouchEvent;
    }

    public void setHookDispatchTouchEvent(HookDispatchTouchEvent hookDispatchTouchEvent) {
        this.hookDispatchTouchEvent = hookDispatchTouchEvent;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean hookRet = false;
        if (hookDispatchTouchEvent != null) {
            hookRet = hookDispatchTouchEvent.dispatchTouchEvent(ev);
        }
        try {
            return hookRet || super.dispatchTouchEvent(ev);
        } catch (ArrayIndexOutOfBoundsException e) {}
        return false;
    }

    public boolean callSuperDispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    HookDispatchTouchEvent hookDispatchTouchEvent;

    public static interface HookDispatchTouchEvent {
        boolean dispatchTouchEvent(MotionEvent ev);
    }

}
