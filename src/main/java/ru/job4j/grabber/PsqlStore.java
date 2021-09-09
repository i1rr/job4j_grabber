package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));

            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Properties getConnectionProperties() {
        Properties props = new Properties();
        try {
            InputStream in = PsqlStore.class.getClassLoader()
                    .getResourceAsStream("app.properties");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    @Override
    public void save(Post post) {
            try (PreparedStatement ps = cnn.prepareStatement(
                        "insert into post (name, text, link, created) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, post.getTitle());
                ps.setString(2, post.getDescription());
                ps.setString(3, post.getLink());
                ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
                ps.execute();

                try (ResultSet genKeys = ps.getGeneratedKeys()) {
                    if (genKeys.next()) {
                        post.setId(genKeys.getInt(1));
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
            try (PreparedStatement ps = cnn.prepareStatement(
                    "SELECT * FROM post")) {
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        Post post = new Post();
                        post.setId(resultSet.getInt("id"));
                        post.setLink(resultSet.getString("link"));
                        post.setDescription(resultSet.getString("text"));
                        post.setTitle(resultSet.getString("name"));
                        post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                        rsl.add(post);
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        Post rsl = null;
            try (PreparedStatement ps = cnn.prepareStatement(
                    "select * from post where id = ?")) {
                ps.setInt(1, id);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        rsl = new Post();
                        rsl.setId(resultSet.getInt("id"));
                        rsl.setLink(resultSet.getString("link"));
                        rsl.setTitle(resultSet.getString("name"));
                        rsl.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}