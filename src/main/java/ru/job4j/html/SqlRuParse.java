package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

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
            Element crtd = doc.select(".msgFooter").first();
            post.setCreated(dateTimeParser.parse(crtd.text().trim().split(" \\[")[0]));

            //Post description + update time(if exist)
            Element desc = doc.select(".msgBody").get(1);
            String description = desc.text();
            if (description.contains("Сообщение было отредактировано:")) {
                String[] temp = description.trim().split("Сообщение было отредактировано: ");
                post.setDescription(temp[0]);
                post.setUpdated(dateTimeParser.parse(temp[1]));
            } else {
                post.setDescription(description);
                post.setUpdated(post.getCreated());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(post);
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

    public static void main(String[] args) {
        SqlRuParse as = new SqlRuParse(new SqlRuDateTimeParser());
        System.out.println(as.list("https://www.sql.ru/forum/job-offers"));
    }
}