
package com.fei_ke.chiphellclient.ui.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.ui.commen.GlideApp;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

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
        Log.i(TAG, "onAfterViews: url " + mUrl);
        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
        mPhotoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        GlideApp.with(this)
                .load(mUrl)
                .placeholder(R.drawable.default_img)
                .error(R.drawable.default_img)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mImageView.post(new Runnable() {
                            @Override
                            public void run() {
                                mPhotoViewAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                mPhotoViewAttacher.update();
                            }
                        });

                        mProgressBar.setVisibility(View.GONE);

                        if (resource instanceof BitmapDrawable) {
                            Palette p = Palette.from(((BitmapDrawable) resource).getBitmap()).generate();

                            ColorDrawable background = (ColorDrawable) mainFrame.getBackground();
                            ObjectAnimator animator = ObjectAnimator.ofInt(mainFrame, "backgroundColor", background.getColor(), p.getLightMutedColor(Color.BLACK));
                            animator.setEvaluator(new ArgbEvaluator());
                            animator.setDuration(500);
                            animator.start();
                        }
                        return false;
                    }
                })
                .into(mImageView);


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
