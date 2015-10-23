package com.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.message.TopicMessage;
import com.pushtechnology.diffusion.api.publisher.Client;
import com.pushtechnology.diffusion.api.publisher.Publisher;
import com.pushtechnology.diffusion.api.topic.Topic;

public class SensorsPublisher extends Publisher {

    private static final Logger log = LoggerFactory.getLogger(SensorsPublisher.class);

    public static final String SENSORS_TOPIC = "Sensors";
    public static final String READINGS_TOPIC = "Readings";
    public static final String CONTROL_TOPIC = "Control";
    public static final String TEMP_TOPIC = "";
    public static final String LIGHT_TOPIC = "";
    public static final String PRESSURE_TOPIC = "";
    public static final String WEATHER_TOPIC = "";

    private Topic readingsTopic;
    private Topic controlTopic;

    private Topic sensorsTopic;

    private String[] sensors = { "Temp", "Light", "PIR" };
    private String[] rooms = { "Bedroom 1", "Bedroom 2", "Bedroom 3", "Kitchen", "Living Room", "Outside" };

    private HeatSensor heatSensor;
    private LightSensor lightSensor;
    private PirSensor pirSensor;

    @Override
    protected void initialLoad() throws APIException {
        sensorsTopic = addTopic(SENSORS_TOPIC);
        controlTopic = sensorsTopic.addTopic(CONTROL_TOPIC);
        controlTopic.addTopic("Heater");
        controlTopic.addTopic("Light");

        readingsTopic = sensorsTopic.addTopic(READINGS_TOPIC);

        for (String sensor : sensors) {
            Topic topic = readingsTopic.addTopic(sensor);
            for (String room : rooms) {
                topic.addTopic(room);
            }
        }

        heatSensor = new HeatSensor(this, rooms);
        pirSensor = new PirSensor(this, rooms);
        lightSensor = new LightSensor(this, rooms);
    }

    protected boolean isStoppable() {
        return true;
    }

    @Override
    protected void messageFromClient(TopicMessage message, Client client) {
        try {
            Topic topic = getTopic(message.getTopicName());
            topic.publishMessage(message);
            if (message.getTopicName().equals("Sensors/Control/Heater")) {
                boolean on = true;
                if (message.asFields().get(0).equals("off")) {
                    on = false;
                }
                heatSensor.heaterOn(on);
            }

            if (message.getTopicName().equals("sesnsor/Control")) {
                final List<String> fields = message.asFields();
                
                final Integer min = Integer.valueOf(fields.get(0));
                final Integer max = Integer.valueOf(fields.get(1));

                heatSensor.setMinMax(min, max);
            }
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void publisherStopping() throws APIException {
        heatSensor.stop();
        pirSensor.stop();
        lightSensor.stop();
    }

}
