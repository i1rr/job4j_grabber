package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.sql.*;

import java.util.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit implements AutoCloseable {

    private Connection connection;
    private final Map<String, String> rabbitProps = new HashMap<>();

    private void initConnection() {
        try (InputStream in = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {

            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private int getIntervalProperties() {
        rabbitProperties();
        String rsl = rabbitProps.get("rabbit.interval");
        return isInt(rsl) ? Integer.parseInt(rsl) : -1;
    }

    private void rabbitProperties() {
        try (BufferedReader bReader = new BufferedReader(
                new FileReader("src/main/resources/rabbit.properties"))) {
            bReader.lines().forEach(nextLine -> {
                String[] kv = nextLine.trim().split("=");
                rabbitProps.put(kv[0], kv[1]);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isInt(String str) {
        try {
            @SuppressWarnings("unused")
            int x = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        try (AlertRabbit ar = new AlertRabbit()) {
            ar.initConnection();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", ar.connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(ar.getIntervalProperties())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");

            try  {
                Connection connection =
                        (Connection) context.getJobDetail().getJobDataMap().get("connection");
                Statement statement = connection.createStatement();
                String sql = String.format(
                        "insert into rabbit (created_date) values ('%s')",
                        new Timestamp(System.currentTimeMillis())
                );
                statement.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}