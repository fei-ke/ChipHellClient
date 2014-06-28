
package com.fei_ke.chiphellclient.ui.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.fei_ke.chiphellclient.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

import java.io.File;
import java.io.IOException;

/**
 * 图片
 * 
 * @author fei-ke
 * @2014-1-26
 */
@EFragment(R.layout.fragment_pic)
public class PicFargment extends BaseFragment {
    private static final String TAG = "PicFargment";

    private static DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisc(true)
            // .imageScaleType(ImageScaleType.NONE)
            .showImageForEmptyUri(R.drawable.logo)
            .showImageOnFail(R.drawable.logo)
            .build();

    private OnViewTapListener mViewTapListener;
    PhotoViewAttacher mPhotoViewAttacher;
    @FragmentArg
    protected String mUrl;

    @ViewById(R.id.imageView_pic)
    GifImageView mImageView;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    public static PicFargment getInstance(String url) {
        return PicFargment_.builder().mUrl(url).build();
    }

    public void setOnViewTapListener(OnViewTapListener mViewTapListener) {
        this.mViewTapListener = mViewTapListener;
    }

    @Override
    protected void onAfterViews() {
        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
        ImageLoader.getInstance().displayImage(mUrl, mImageView, imageOptions, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {

            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

            }

            @Override
            public void onLoadingComplete(String url, View view, Bitmap bitmap) {
                if (url.endsWith(".gif") || url.endsWith(".GIF")) {
                    File file = ImageLoader.getInstance().getDiscCache().get(url);
                    try {
                        GifDrawable drawable = new GifDrawable(file);
                        mImageView.setImageDrawable(drawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mPhotoViewAttacher.update();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {

            }
        });

        mPhotoViewAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                toggleHideyBar();
            }
        });
    }

    @Override
    public void onDestroyView() {
        mPhotoViewAttacher.cleanup();
        System.gc();
        super.onDestroyView();
    }

    public static interface OnViewTapListener {
        void OnViewTap();
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    public void toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding: Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN. For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getActivity().getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        // END_INCLUDE (set_ui_flags)
        if (getActivity().getActionBar().isShowing()) {
            getActivity().getActionBar().hide();
        } else {
            getActivity().getActionBar().show();
        }
    }
}
