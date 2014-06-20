
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.constant.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * 帖子内的一个item
 * 
 * @author 杨金阳
 * @2014-6-16
 */
@EViewGroup(R.layout.layout_post_item)
public class PostItemView extends FrameLayout {
    @ViewById(R.id.imageView_avatar)
    ImageView imageViewAvatar;

    @ViewById(R.id.textView_content)
    TextView textViewContent;

    @ViewById(R.id.textView_authi)
    TextView textViewAuthi;

    public static PostItemView newInstance(Context context) {
        return PostItemView_.build(context);
    }

    public PostItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PostItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostItemView(Context context) {
        super(context);
    }

    public void bindValue(Post post, boolean isFirst) {
        textViewContent.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(post.getAvatarUrl(), imageViewAvatar);
        textViewAuthi.setText(Html.fromHtml(post.getAuthi()));
        if (isFirst) {
            textViewContent.setText("");
            return;
        }
        textViewContent.setText(Html.fromHtml(post.getContent(), new ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                if (!source.startsWith("http:")) {
                    source = Constants.BASE_URL + source;
                }
                System.out.println(source);
                return getContext().getResources().getDrawable(R.drawable.ic_action_collections_sort_by_size);
                // return new UrlDrawable(source, textViewContent);
            }
        }, null));
    }

    public static class UrlDrawable extends BitmapDrawable {
        protected Drawable drawable;
        View container;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public UrlDrawable(String url, View container) {
            this.container = container;
            setBounds(0, 0, 30, 30);
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String arg0, View arg1) {
                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                }

                @Override
                public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
                    BitmapDrawable drawable = new BitmapDrawable(bitmap);
                    setBounds(0, 0, 0 + bitmap.getWidth(), 0
                            + bitmap.getHeight());
                    UrlDrawable.this.drawable = drawable;

                    // redraw the image by invalidating the container
                    UrlDrawable.this.container.invalidate();
                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {

                }
            });
        }
    }
}
