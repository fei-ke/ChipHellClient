
package com.fei_ke.chiphellclient.constant;

import com.fei_ke.chiphellclient.R;
import com.fei_ke.chiphellclient.utils.GlobalSetting;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 一些常量
 *
 * @author fei-ke
 * @2014-6-14
 */
public class Constants {
    /**
     * 论坛地址，注意结尾的斜杠
     */
    public static String BASE_URL = GlobalSetting.getForumAddress();
    //显示头像
    public static DisplayImageOptions avatarDisplayOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisc(true)
            .showImageForEmptyUri(R.drawable.noavatar)
            .showImageOnFail(R.drawable.noavatar)
            .showImageOnLoading(R.drawable.noavatar)
            .build();
}
