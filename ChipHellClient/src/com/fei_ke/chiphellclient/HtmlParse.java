
package com.fei_ke.chiphellclient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParse {
    public static void parseMainList(String conten) {
        Document document = Jsoup.parse(conten);
        Elements elements = document.getElementsByClass("bm_h");
        for (Element element : elements) {
            System.out.println(element.toString());
        }
    }
}
