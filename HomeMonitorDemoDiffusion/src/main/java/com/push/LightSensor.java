package com.push;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pushtechnology.diffusion.api.message.TopicMessage;
import com.pushtechnology.diffusion.api.publisher.Publisher;
import com.pushtechnology.diffusion.api.topic.Topic;

public class LightSensor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(LightSensor.class);

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final Publisher publisher;
    private final String[] rooms;

    public LightSensor(Publisher publisher, String... rooms) {
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
                    int v = readValue();
                    Topic topic = publisher.getTopic("Sensors/Readings/Light/" + room);

                    TopicMessage message = topic.createDeltaMessage();
                    message.putRecord(Integer.toString(v));
                    publisher.publishMessage(message);
                    log.debug(message.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int readValue() {
        Calendar date = new GregorianCalendar();
        return Math.abs(30 - date.get(Calendar.SECOND)) * 4;
    }

}
