
package com.fei_ke.chiphellclient.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 表情对照表
 * 
 * @author fei-ke
 * @2014-6-21
 */
public class SmileTable {
    public static final Map<String, String> smilis = new HashMap<String, String>();
    static {
        smilis.put("[睡觉]", "sleep.gif");
        smilis.put("[晕倒]", "ft.gif");
        smilis.put("[谩骂]", "abuse.gif");
        smilis.put("[愤怒]", "angry.gif");
        smilis.put("[生病]", "6.gif");
        smilis.put("[再见]", "j.gif");
        smilis.put("[吐槽]", "2.gif");
        smilis.put("[失望]", "5.gif");

        smilis.put("[困惑]", "confused.gif");
        smilis.put("[震惊]", "98.gif");
        smilis.put("[可爱]", "lovely.gif");
        smilis.put("[狂笑]", "96.gif");
        smilis.put("[无奈]", "10.gif");
        smilis.put("[吃惊]", "eek.gif");
        smilis.put("[流汗]", "97.gif");
        smilis.put("[流泪]", "99.gif");

        smilis.put("[雷人]", "cattle.gif");
        smilis.put("[偷笑]", "fd.gif");
        smilis.put("[傻笑]", "e.gif");
        smilis.put("[高傲]", "shame.gif");
        smilis.put("[怪脸]", "tong.gif");
        smilis.put("[音乐]", "music.gif");
        smilis.put("[喜欢]", "heart.gif");
        smilis.put("[恶魔]", "belial.gif");

    }

    public static String get(String key) {
        return smilis.get(key);
    }
}
