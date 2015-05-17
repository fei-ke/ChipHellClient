package com.fei_ke.chiphellclient.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.fei_ke.chiphellclient.R;

/**
 * Created by 杨金阳 on 2015/5/17.
 */
public class ThemeUtil {
    public static void brandGlowEffect(Context context) {
        try {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            brandGlowEffect(context, color);
        } catch (Exception e) { }
    }

    private static void brandGlowEffect(Context context, int brandColor) {
        // glow
        int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
        Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
        androidGlow.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
        // edge
        int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
        Drawable androidEdge = context.getResources().getDrawable(edgeDrawableId);
        androidEdge.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
    }
}
