package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private final Map<String, String> rabbitProps = new HashMap<>();

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

    private boolean isInt(String str) {
        try {
            @SuppressWarnings("unused")
            int x = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        AlertRabbit ar = new AlertRabbit();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();

            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(ar.getIntervalProperties())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}