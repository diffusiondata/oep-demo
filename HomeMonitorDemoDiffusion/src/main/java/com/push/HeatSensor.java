package com.push;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pushtechnology.diffusion.api.message.TopicMessage;
import com.pushtechnology.diffusion.api.publisher.Publisher;
import com.pushtechnology.diffusion.api.topic.Topic;

public class HeatSensor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HeatSensor.class);
    private static Random random = new Random(System.currentTimeMillis());
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private double lastTemp = 15;
    private double trend = -1;

    private final Publisher publisher;
    private final String[] rooms;

    public HeatSensor(Publisher publisher, String... rooms) {
        this.publisher = publisher;
        this.rooms = rooms;
        executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    public void run() {
        try {
            for (String room : rooms) {
                if (!room.equals("Outside")) {
                    Double v = readValue();
                    Topic topic = publisher.getTopic("Sensors/Readings/Temp/" + room);

                    TopicMessage message = topic.createDeltaMessage();
                    message.putRecord(v.toString());
                    publisher.publishMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void heaterOn(boolean on) {
        if (on) {
            log.debug("Heating on");
            trend = 1;
        } else {
            log.debug("Heating off");
            trend = -1;
        }
    }

    public double readValue() {
        double variation = random.nextInt(50) / 10d;

        double newTemp = lastTemp + (trend * variation);

        if (newTemp < 10) {
            newTemp = 10;
        } else if (newTemp > 30) {
            newTemp = 30;
        }

        if (random.nextInt(30) == 1) {
            lastTemp = newTemp;
        }
        return newTemp;
    }

}
