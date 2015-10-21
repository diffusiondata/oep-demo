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

public class PirSensor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PirSensor.class);

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final Publisher publisher;
    private final String[] rooms;
    private Random random = new Random();

    public PirSensor(Publisher publisher, String... rooms) {
        this.publisher = publisher;
        this.rooms = rooms;
        executor.scheduleAtFixedRate(this, 0, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    public void run() {
        try {
            for (String room : rooms) {
                if (!room.equals("Outside")) {
                    boolean v = random.nextBoolean();
                    Topic topic = publisher.getTopic("Sensors/Readings/PIR/" + room);

                    TopicMessage message = topic.createDeltaMessage();
                    message.putRecord(Boolean.toString(v));
                    publisher.publishMessage(message);
                    log.debug(message.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
