package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public Post details(String link) {
        Post post = new Post();
        try {
            Document doc = Jsoup.connect(link).get();

            //Post title
            post.setTitle(doc.select(".messageHeader").first().text());

            //Post link
            post.setLink(link);

            //Post created
            Element crtd = doc.select("div#content-wrapper-forum").first();
            String comment = String.valueOf(crtd.childNode(3));
            post.setCreated(dateTimeParser.parse(comment.trim().split("Последнее сообщение: ")[1]
                    .split("</div>")[0]));

            //Post description
            Element desc = doc.select(".msgBody").get(1);
            post.setDescription(desc.text());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> list = new ArrayList<>();
        try {
            int limiter = 0; //to ignore first 3 topics as they are not the part of the task
            for (int i = 1; i < 6; i++) { //to go through the pages (i < 6 means up to 5th page)
                Document doc = Jsoup.connect(link + "/" + i).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    limiter++;
                    if (limiter > 3) {
                        Element href = td.child(0);
                        list.add(details(href.attr("href")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}