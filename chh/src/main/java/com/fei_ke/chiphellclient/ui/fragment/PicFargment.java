
package com.fei_ke.chiphellclient.ui.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ProgressBar;

import com.fei_ke.chiphellclient.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
            .cacheInMemory(true).cacheOnDisk(true)
            .showImageForEmptyUri(R.drawable.default_img)
            .showImageOnFail(R.drawable.default_img)
            .build();

    private OnViewTapListener mViewTapListener;
    PhotoViewAttacher mPhotoViewAttacher;
    @FragmentArg
    protected String mUrl;

    @ViewById(R.id.imageView_pic)
    GifImageView mImageView;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @ViewById
    View mainFrame;

    public static PicFargment getInstance(String url) {
        return PicFargment_.builder().mUrl(url).build();
    }

    public void setOnViewTapListener(OnViewTapListener mViewTapListener) {
        this.mViewTapListener = mViewTapListener;
    }

    @Override
    protected void onAfterViews() {
        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
        ImageLoader.getInstance().displayImage(mUrl, mImageView, imageOptions, new SimpleImageLoadingListener() {
            final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

            @Override
            public void onLoadingComplete(String url, View view, Bitmap bitmap) {
                if (bitmap == null) return;

                if (url.endsWith(".gif") || url.endsWith(".GIF")) {
                    File file = ImageLoader.getInstance().getDiscCache().get(url);
                    try {
                        GifDrawable drawable = new GifDrawable(file);
                        mImageView.setImageDrawable(drawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                boolean firstDisplay = !displayedImages.contains(url);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(mImageView, 500);
                    displayedImages.add(url);
                }

                mPhotoViewAttacher.update();
                mProgressBar.setVisibility(View.GONE);

                Palette p = Palette.from(bitmap).generate();
                //mainFrame.setBackgroundColor(p.getLightMutedColor(Color.BLACK));

                ColorDrawable background = (ColorDrawable) mainFrame.getBackground();
                ObjectAnimator animator = ObjectAnimator.ofInt(mainFrame, "backgroundColor", background.getColor(), p.getLightMutedColor(Color.BLACK));
                animator.setEvaluator(new ArgbEvaluator());
                animator.setDuration(500);
                animator.start();
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
