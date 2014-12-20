
package com.fei_ke.chiphellclient.ui.fragment;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.fei_ke.chiphellclient.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

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
                getActivity().finish();
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


}
