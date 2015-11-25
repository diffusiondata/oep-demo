package com.push;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pushtechnology.diffusion.api.message.TopicMessage;
import com.pushtechnology.diffusion.api.publisher.Publisher;
import com.pushtechnology.diffusion.api.topic.Topic;

public class HeatSensor implements Runnable {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final int MAX_TEMP = 40;
    
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentMap<String, Double> prevTemps = new ConcurrentHashMap<String, Double>();

    private final Publisher publisher;
    private final String[] rooms;

    private volatile double trend = -1;
    private volatile int min = 10;
    private volatile int max = 30;    
    
    public HeatSensor(Publisher publisher, String... rooms) {
        this.publisher = publisher;
        this.rooms = rooms;
        
        for (String room : rooms) {
        	prevTemps.put(room, (double) 15);
        }
        
        executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    public void run() {
        try {
            for (String room : rooms) {
                if (!room.equals("Outside")) {
                    final Double value = readValue(room);
                    final Topic topic = publisher.getTopic("Sensors/Readings/Temp/" + room);

                    final TopicMessage message = topic.createDeltaMessage();
                    message.putRecord(value.toString());
                    
                    publisher.publishMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void heaterOn(boolean on) {
        if (on) {
            System.out.println("Heating on");
            trend = 1;
        } else {
        	System.out.println("Heating off");
            trend = -1;
        }
    }

    public void setMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public double readValue(String room) {
        final double variation = (RANDOM.nextInt(50) / 10d) - 1;
        final double prev = prevTemps.get(room);
        
        double newTemp = prev + (trend * variation);

        // Clamp all temperatures to an absolute maximum
        if (newTemp > MAX_TEMP) {
        	newTemp = MAX_TEMP;
        }
        
        // When temperatures hit threshold min/max values, clamp prev values 
        // to ensure they hover around them
        if (newTemp < min) {
        	prevTemps.replace(room, prev, (double) min);
        	newTemp++;
        } else if (newTemp > max) {
        	prevTemps.replace(room, prev, (double) max);
        	newTemp--;
        } else {
        	prevTemps.replace(room, prev, newTemp);
        }
        
        return newTemp;
    }
}
