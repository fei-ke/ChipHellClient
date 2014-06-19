
package com.fei_ke.chiphellclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class SmileyPickerUtility {
    public static void hideSoftInput(View paramEditText) {
        ((InputMethodManager) paramEditText.getContext().getSystemService("input_method"))
                .hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
    }

    public static void showKeyBoard(final View paramEditText) {
        paramEditText.requestFocus();
        paramEditText.post(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) paramEditText.getContext().getSystemService("input_method"))
                        .showSoftInput(paramEditText, 0);
            }
        });
    }

    public static int getScreenHeight(Activity paramActivity) {
        Display display = paramActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getStatusBarHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.top;

    }

    public static int getActionBarHeight(Activity paramActivity) {
        // test on samsung 9300 android 4.1.2, this value is 96px
        // but on galaxy nexus android 4.2, this value is 146px
        // statusbar height is 50px
        // I guess 4.1 Window.ID_ANDROID_CONTENT contain statusbar
        int contentViewTop =
                paramActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

        // return contentViewTop - getStatusBarHeight(paramActivity);

        return getDimensionPixelSize(paramActivity, android.R.attr.actionBarSize,
                dip2px(paramActivity, 48));
    }

    // below status bar,include actionbar, above softkeyboard
    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }

    // below actionbar, above softkeyboard
    public static int getAppContentHeight(Activity paramActivity) {
        return SmileyPickerUtility.getScreenHeight(paramActivity)
                - SmileyPickerUtility.getStatusBarHeight(paramActivity)
                - SmileyPickerUtility.getActionBarHeight(paramActivity)
                - SmileyPickerUtility.getKeyboardHeight(paramActivity);
    }

    public static int getKeyboardHeight(Activity paramActivity) {

        int height = SmileyPickerUtility.getScreenHeight(paramActivity)
                - SmileyPickerUtility.getStatusBarHeight(paramActivity)
                - SmileyPickerUtility.getAppHeight(paramActivity);
        if (height == 0) {
            height = 400;
        }
        // TODO 保存软键盘高度

        return height;
    }

    public static boolean isKeyBoardShow(Activity paramActivity) {
        int height = SmileyPickerUtility.getScreenHeight(paramActivity)
                - SmileyPickerUtility.getStatusBarHeight(paramActivity)
                - SmileyPickerUtility.getAppHeight(paramActivity);
        return height != 0;
    }

    public static int dip2px(Context context, int dipValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    public static int px2dip(Context context, int pxValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((pxValue / reSize) + 0.5);
    }

    public static float sp2px(Context context, int spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
                context.getResources().getDisplayMetrics());
    }

    public static int getDimensionPixelSize(Activity activity, int attr, int defaultValue) {
        int[] attrs = new int[] {
                attr
        };
        TypedArray ta = activity.obtainStyledAttributes(attrs);
        int value = ta.getDimensionPixelSize(0, defaultValue);
        ta.recycle();
        return value;
    }
}
