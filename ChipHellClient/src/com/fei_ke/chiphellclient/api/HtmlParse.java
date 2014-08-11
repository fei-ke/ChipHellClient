package com.fei_ke.chiphellclient.api;

import android.graphics.Color;
import android.text.TextUtils;

import com.fei_ke.chiphellclient.bean.AlbumWrap;
import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateClass;
import com.fei_ke.chiphellclient.bean.PlateGroup;
import com.fei_ke.chiphellclient.bean.Post;
import com.fei_ke.chiphellclient.bean.PrepareQuoteReply;
import com.fei_ke.chiphellclient.bean.Thread;
import com.fei_ke.chiphellclient.bean.ThreadListWrap;
import com.fei_ke.chiphellclient.bean.User;
import com.fei_ke.chiphellclient.constant.Constants;
import com.fei_ke.chiphellclient.utils.LogMessage;
import com.fei_ke.chiphellclient.utils.UrlParamsMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

class HtmlParse {
    private static final String TAG = "HtmlParse";

    /**
     * 解析板块列表
     *
     * @param content
     * @return
     */
    public static List<PlateGroup> parsePlateGroupList(String content) {
        List<PlateGroup> groups = new ArrayList<PlateGroup>();
        Document document = Jsoup.parse(content);
        document.setBaseUri(Constants.BASE_URL);
        Elements elementsGroup = document.getElementsByClass("bm");
        for (Element bm : elementsGroup) {
            PlateGroup plateGroup = new PlateGroup();

            Element bm_h = bm.getElementsByClass("bm_h").first();
            String title = bm_h.text();
            plateGroup.setTitle(title);
            List<Plate> plates = new ArrayList<Plate>();
            Elements plateElements = bm.getElementsByClass("bm_c");

            for (Element bm_c : plateElements) {
                Plate plate = new Plate();
                //链接，第一个是版块链接，如果有第二个则是删除收藏连接
                Elements as = bm_c.getElementsByTag("a");
                Element a1 = as.first();
                String plateTitle = a1.text();
                String url = a1.absUrl("href");
                Elements count = bm_c.getElementsByClass("xg1");
                String xg1 = null;
                if (count.size() != 0) {
                    xg1 = count.first().text();
                } else {
                    xg1 = "(0)";
                }

                //判断是否收藏
                String favoriteId = null;
                if (as.size() > 1) {
                    String urlDelete = as.get(1).absUrl("href");
                    favoriteId = new UrlParamsMap(urlDelete).get("favid");
                }

                plate.setTitle(plateTitle);
                plate.setUrl(url);
                plate.setXg1(xg1);
                plate.setFavoriteId(favoriteId);
                plates.add(plate);

            }

            plateGroup.setPlates(plates);
            groups.add(plateGroup);
        }

        return groups;
    }

    /**
     * 解析帖子列表
     *
     * @param content
     * @param parseClass 是否解析主题分类
     */
    public static ThreadListWrap parseThreadList(String content, boolean parseClass) {
        ThreadListWrap threadWrap = new ThreadListWrap();
        List<Thread> threads = new ArrayList<Thread>();
        List<Plate> plates = null;

        Document document = Jsoup.parse(content);
        document.setBaseUri(Constants.BASE_URL);
        Elements elementsGroup = document.getElementsByClass("bm_c");
        for (Element bmc : elementsGroup) {
            try {
                Thread thread = new Thread();
                Elements xg1 = bmc.getElementsByClass("xg1");
                String timeAndCount = xg1.first().ownText();
                Elements as = bmc.getElementsByTag("a");
                Element a1 = as.first();
                String url = a1.absUrl("href");
                String title = a1.text();
                String style = a1.attr("style");
                if (!TextUtils.isEmpty(style)) {
                    int s = style.indexOf("color");
                    if (s != -1) {
                        s += 5;
                        s = style.indexOf(":", s);
                        int e = style.indexOf(";", s);
                        String color = style.substring(s + 1, e);
                        try {
                            thread.setTitleColor(Color.parseColor(color.trim()));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }

                Elements imgElements = bmc.getElementsByTag("img");
                if (imgElements != null && imgElements.size() != 0) {
                    String src = imgElements.first().absUrl("src");
                    thread.setImgSrc(src);
                }

                if (as.size() > 1) {//有作者
                    Element a2 = as.get(1);
                    String by = a2.text();
                    thread.setBy(by);
                }
                thread.setTitle(title);
                thread.setUrl(url);
                thread.setTimeAndCount(timeAndCount);

                threads.add(thread);
            } catch (Exception e) {// 当有子版块时
                e.printStackTrace();
                if (plates == null) {
                    plates = new ArrayList<Plate>();
                }
                if (bmc.children().size() > 0) {
                    Element child = bmc.child(0);
                    Plate plate = new Plate();
                    String title = child.ownText();
                    String url = child.absUrl("href");
                    plate.setTitle(title);
                    plate.setUrl(url);
                    plate.setSubPlate(true);
                    plates.add(plate);

                    LogMessage.i(TAG, plate);
                }
            }

        }
        if (parseClass) {
            Elements elements = document.getElementsByClass("box");
            for (Element box : elements) {
                if ("box ttp".equals(box.className())) {// 主题分类
                    Elements as = box.getElementsByTag("a");
                    List<PlateClass> plateClasses = new ArrayList<PlateClass>();
                    for (Element a : as) {
                        PlateClass plateClass = new PlateClass();
                        plateClass.setTitle(a.ownText());
                        plateClass.setUrl(a.absUrl("href"));
                        plateClasses.add(plateClass);
                    }
                    threadWrap.setPlateClasses(plateClasses);
                }
            }
        }
        threadWrap.setThreads(threads);
        threadWrap.setPlates(plates);
        if (elementsGroup.size() == 0) {
            String message = parseMessageText(content);
            threadWrap.setError(message);
        }
        return threadWrap;
    }

    /**
     * 解析回帖列表
     *
     * @param content
     * @return
     */
    public static List<Post> parsePostList(String content) {
        long s = System.currentTimeMillis();

        List<Post> posts = new ArrayList<Post>();
        Document document = Jsoup.parse(content);
        document.setBaseUri(Constants.BASE_URL);
        Elements elements = document.getElementsByClass("plc");
        for (Element plc : elements) {
            try {
                Post post = new Post();
                // 解析头像
                Element avatar = plc.getElementsByClass("avatar").first();
                post.setAvatarUrl(avatar.child(0).absUrl("src"));

                String authi = plc.getElementsByClass("authi").first().html();
                Element message = plc.getElementsByClass("message").first();
                post.setContent(message.html().trim());
                // // 解析头像
                // // Element avatar = plc.getElementsByClass("avatar").first();
                // Element avatar = plc.child(0);
                // post.setAvatarUrl(avatar.child(0).absUrl("src"));
                //
                // // Element message = plc.getElementsByClass("message").first();
                // Element display = plc.child(1);
                // String authi = display.child(0).html();
                // Element message = display.child(1);
                // post.setContent(message.html().trim());

                try {// 主贴没有replyUrl
                    String replyUrl = plc.getElementsByClass("replybtn").first().child(0).absUrl("href");
                    post.setReplyUrl(replyUrl);
                } catch (Exception e) {
                }

                Elements img_list = plc.getElementsByClass("img_list");
                if (img_list != null && !img_list.isEmpty()) {
                    String imgList = img_list.first().html();
                    post.setImgList(imgList);
                } else {// 单张图片附件时
                    Elements img_one = plc.getElementsByClass("img_one");
                    if (img_one != null && !img_one.isEmpty()) {
                        String imgOne = img_one.first().html();
                        post.setImgList(imgOne);
                    }
                }
                post.setAuthi(authi);

                posts.add(post);
            } catch (Exception e) {
            }
            LogMessage.i("parsePostList", "解析时间:" + (System.currentTimeMillis() - s));
        }
        return posts;
    }

    /**
     * 解析用户信息
     *
     * @param responseBody
     * @return
     */
    public static User parseUserInfo(String responseBody) {
        User user = new User();
        try {
            Document document = Jsoup.parse(responseBody);
            document.setBaseUri(Constants.BASE_URL);
            Element elementUser = document.getElementsByClass("userinfo").first();
            Element elementAvatar = elementUser.getElementsByTag("img").first();
            user.setAvatarUrl(elementAvatar.attr("src"));
            user.setName(elementUser.getElementsByClass("name").first().text());
            user.setInfo(elementUser.getElementsByClass("user_box").html());

            Element btn_exit = document.getElementsByClass("btn_exit").first();

            String url = btn_exit.child(0).attr("href");
            UrlParamsMap map = new UrlParamsMap(url);
            String formHash = map.get("formhash");

            user.setFormHash(formHash);
            LogMessage.i("formHash", formHash);
        } catch (Exception e) {
            LogMessage.w(TAG + "#parseUserInfo", e);
        }
        return user;
    }

    /**
     * 解析引用回复的准备数据
     *
     * @param responseBody
     * @return
     */
    public static PrepareQuoteReply parsePrepareQuoteReply(String responseBody) {
        PrepareQuoteReply quoteReply = new PrepareQuoteReply();
        try {

            Document document = Jsoup.parse(responseBody);
            document.setBaseUri(Constants.BASE_URL);

            Element postform = document.getElementById("postform");
            String url = postform.absUrl("action");

            String formhash = postform.getElementsByAttributeValue("name", "formhash").first().attr("value");
            String posttime = postform.getElementsByAttributeValue("name", "posttime").first().attr("value");
            String noticeauthor = postform.getElementsByAttributeValue("name", "noticeauthor").first().attr("value");
            String noticetrimstr = postform.getElementsByAttributeValue("name", "noticetrimstr").first().attr("value");
            String noticeauthormsg = postform.getElementsByAttributeValue("name", "noticeauthormsg").first().attr("value");
            String reppid = postform.getElementsByAttributeValue("name", "reppid").first().attr("value");
            String reppost = postform.getElementsByAttributeValue("name", "reppost").first().attr("value");
            String quoteBody = postform.getElementsByTag("blockquote").first().toString();

            quoteReply.setNoticeauthor(noticeauthor);
            quoteReply.setNoticeauthormsg(noticeauthormsg);
            quoteReply.setNoticetrimstr(noticetrimstr);
            quoteReply.setPosttime(posttime);
            quoteReply.setQuoteBody(quoteBody);
            quoteReply.setReppid(reppid);
            quoteReply.setUrl(url);
            quoteReply.setFormhash(formhash);
            quoteReply.setReppost(reppost);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return quoteReply;
    }

    /**
     * 解析相册
     *
     * @param responseBody
     * @return
     */
    public static AlbumWrap parseAubum(String responseBody) {
        AlbumWrap albumWrap = new AlbumWrap();
        List<String> albums = new ArrayList<String>();

        Document document = Jsoup.parse(responseBody);
        document.setBaseUri(Constants.BASE_URL);
        Elements elements = document.getElementsByClass("postalbum_i");
        for (Element album : elements) {
            String url = album.absUrl("orig");
            albums.add(url);
        }
        albumWrap.setUrls(albums);

        String strCurpic = document.getElementById("curpic").text();
        int curpic = Integer.valueOf(strCurpic) - 1;
        albumWrap.setCurPosition(curpic);
        return albumWrap;
    }

    /**
     * 解析提示消息
     *
     * @param responseString
     * @return
     */
    public static String parseMessageText(String responseString) {
        Document document = Jsoup.parse(responseString);
        Element messagetext = document.getElementById("messagetext");
        if (messagetext != null) {
            return messagetext.child(0).text();
        }

        Element jump_c = document.getElementsByClass("jump_c").first();

        if (jump_c != null) {
            return jump_c.text();
        }

        Element message = document.getElementById("message");
        if (message != null) {
            return message.text();
        }
        return null;
    }
}
