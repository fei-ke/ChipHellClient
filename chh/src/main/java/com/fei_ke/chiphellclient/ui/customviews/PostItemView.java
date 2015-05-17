
package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.fei_ke.chiphellclient.utils.LogMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 帖子内的一个item
 *
 * @author fei-ke
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

    private static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

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

    public void bindValue(Post post) {
        ImageLoader.getInstance().displayImage(post.getAvatarUrl(), imageViewAvatar, Constants.avatarDisplayOption, animateFirstListener);
        textViewAuthi.setText(Html.fromHtml(post.getAuthi()));
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
                LogMessage.i("PostItemView", source);
                return new UrlDrawable(source, textViewContent);
            }
        }, null));
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView,600);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
