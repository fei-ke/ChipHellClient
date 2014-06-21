
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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.utils.LogMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.ls.LSInput;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import java.io.IOException;
import java.util.List;

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
        String content = post.getContent();
        if (post.getImgList() != null) {
            content += post.getImgList();
        }
        textViewContent.setText(Html.fromHtml(content, new ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                if (!source.startsWith("http:")) {
                    source = Constants.BASE_URL + source;
                }
                LogMessage.d("PostItemView", source);
                LogMessage.d("PostItemView", PostItemView.this.hashCode());
                return new UrlDrawable(source, textViewContent);
            }
        }, null));
    }

    public static class UrlDrawable extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        @SuppressWarnings("deprecation")
        public UrlDrawable(final String url, final View container) {
            setBounds(0, 0, 100, 100);
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String arg0, View arg1) {
                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                }

                @Override
                public void onLoadingComplete(String url, View arg1, Bitmap bitmap) {
                    Drawable drawable = new BitmapDrawable(container.getResources(), bitmap);
                    int width = bitmap.getWidth() * 2;
                    int height = bitmap.getHeight() * 2;
                    drawable.setBounds(0, 0, width, height);
                    UrlDrawable.this.drawable = drawable;
                    setBounds(0, 0, width, height);

                    container.invalidate();
                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {

                }
            });
        }
    }
}
