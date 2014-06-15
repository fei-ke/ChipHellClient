
package com.fei_ke.chiphellclient.api;

import android.graphics.Color;
import android.text.TextUtils;

import com.fei_ke.chiphellclient.bean.Plate;
import com.fei_ke.chiphellclient.bean.PlateGroup;

import com.fei_ke.chiphellclient.bean.Thread;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HtmlParse {
    /**
     * 解析板块列表
     * 
     * @param conten
     * @return
     */
    public static List<PlateGroup> parsePlateGroupList(String content) {
        List<PlateGroup> groups = new ArrayList<PlateGroup>();
        Document document = Jsoup.parse(content);
        Elements elementsGroup = document.getElementsByClass("bm");
        for (Element bm : elementsGroup) {
            PlateGroup plateGroup = new PlateGroup();

            Element bm_h = bm.getElementsByClass("bm_h").get(0);
            String title = bm_h.text();
            plateGroup.setTitle(title);
            System.out.println(plateGroup.getTitle());
            List<Plate> plates = new ArrayList<Plate>();
            Elements plateElements = bm.getElementsByClass("bm_c");

            for (Element bm_c : plateElements) {
                Plate plate = new Plate();
                Element a = bm_c.getElementsByTag("a").get(0);
                String plateTitle = a.text();
                String url = a.attr("href");
                Elements count = bm_c.getElementsByClass("xg1");
                String xg1 = null;
                if (count.size() != 0) {
                    xg1 = count.get(0).text();
                } else {
                    xg1 = "(0)";
                }
                plate.setTitle(plateTitle);
                plate.setUrl(url);
                plate.setXg1(xg1);

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
     * @param Content
     */
    public static List<Thread> parsePostsList(String content) {
        List<Thread> threads = new ArrayList<Thread>();
        Document document = Jsoup.parse(content);
        Elements elementsGroup = document.getElementsByClass("bm_c");
        for (Element bmc : elementsGroup) {
            try {
                Thread thread = new Thread();
                Elements xg1 = bmc.getElementsByClass("xg1");
                String timeAndCount = xg1.get(0).text();

                Elements as = bmc.getElementsByTag("a");
                Element a1 = as.get(0);
                String url = a1.attr("href");
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
                    String src = imgElements.get(0).attr("src");
                    thread.setImgSrc(src);
                }

                Element a2 = as.get(1);
                String by = a2.text();

                thread.setBy(by);
                thread.setTitle(title);
                thread.setUrl(url);
                thread.setTimeAndCount(timeAndCount);

                threads.add(thread);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return threads;
    }
}
