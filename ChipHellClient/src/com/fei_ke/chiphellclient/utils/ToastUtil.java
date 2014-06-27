
package com.fei_ke.chiphellclient.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by YJY on 2014/6/27.
 */
public class ToastUtil {
    public static void show(Context context, String text) {
        if (context != null) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public static void show(Context context, int stringResId) {
        if (context != null) {
            Toast.makeText(context, stringResId, Toast.LENGTH_SHORT).show();
        }
    }
}
