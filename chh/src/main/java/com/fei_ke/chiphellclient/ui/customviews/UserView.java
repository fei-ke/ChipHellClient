package com.fei_ke.chiphellclient.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.bean.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * 用户视图
 *
 * @author fei-ke
 * @2014-6-16
 */
@EViewGroup(R.layout.layout_user)
public class UserView extends FrameLayout {

    @ViewById(R.id.imageView_avatar)
    protected ImageView imageViewAvatar;

    @ViewById(R.id.textView_name)
    protected TextView textViewName;

    @ViewById(R.id.textView_info)
    protected TextView textViewInfo;

    @ViewById(R.id.button_favorite)
    protected TextView buttonFavorite;

    @ViewById(R.id.button_my_post)
    protected TextView buttonMyPost;

    @ViewById(R.id.main_frame)
    protected View mainFrame;

    public static UserView newInstance(Context context) {
        return UserView_.build(context);
    }

    public UserView(Context context) {
        super(context);
    }

    public void bindValue(User user) {

        DisplayImageOptions avatarOption = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .showImageForEmptyUri(R.drawable.noavatar)
                .showImageOnFail(R.drawable.noavatar)
                .showImageOnLoading(R.drawable.noavatar)
                .build();

        ImageLoader.getInstance().displayImage(user.getAvatarUrl(), imageViewAvatar, avatarOption,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        /*if (loadedImage != null) {
                            Bitmap bg = BitmapUtil.fastblur(loadedImage, 30);
                            mainFrame.setBackgroundDrawable(new BitmapDrawable(bg));
                        } else {
                            mainFrame.setBackgroundResource(R.drawable.card_bg_normal);
                        }*/
                    }
                });
        textViewName.setText(user.getName());
        textViewInfo.setText(Html.fromHtml(user.getInfo()));
    }

    public TextView getButtonFavorite() {
        return buttonFavorite;
    }

    public TextView getButtonMyPost() {
        return buttonMyPost;
    }
}
