package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {

    private static void getDescription(String link) throws Exception {
        Document doc = Jsoup.connect(link).get();
        Element el = doc.select(".msgBody").get(1);
        System.out.println(el.text());
    }

    public static void main(String[] args) throws Exception {
        int limiter = 0; //to ignore first 3 topics as they are not the part of the task
        for (int i = 1; i < 6; i++) { //to go through the pages (i < 6 means up to 5th page)
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                limiter++;
                if (limiter > 3) {
                    Element href = td.child(0);
                    System.out.println(href.attr("href"));
                    System.out.println(href.text());
                    getDescription(href.attr("href")); //description loader
                    System.out.println(td.parent().child(5).text());
                    System.out.println();
                }
            }
        }
    }
}