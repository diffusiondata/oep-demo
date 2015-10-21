package com.push;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pushtechnology.diffusion.api.ServerConnection;
import com.pushtechnology.diffusion.api.client.ExternalClientConnection;
import com.pushtechnology.diffusion.api.message.MessageException;
import com.pushtechnology.diffusion.api.message.TopicMessage;

public class Sensors extends ServerConnectionAdapter {

    public static void main(String[] args) throws Exception {
        new Sensors();
    }

    private final String serverUri = "dpt://192.168.53.111:8080";

    private final ExternalClientConnection clientConnection;
    private final GpioController gpio;
    private final GpioPinDigitalOutput pin;

    public Sensors() throws Exception {
        clientConnection = new ExternalClientConnection(this, serverUri);
        clientConnection.connect("Sensors/Control/Light");

        gpio = GpioFactory.getInstance();
        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
        pin.low();

        HeatSensor heatSensor = new HeatSensor(250, 8);
        LightSensor lightSensor = new LightSensor(250, 8);
        while (true) {
            int temp = heatSensor.getValue();
            System.out.println("Real temp = " + temp);
            TopicMessage message = clientConnection.createDeltaMessage("Sensors/Readings/Temp/Outside");
            message.put(Integer.toString(temp));
            clientConnection.send(message);

            int lux = lightSensor.getValue();
            System.out.println("Light lux = " + lux);
            message = clientConnection.createDeltaMessage("Sensors/Readings/Light/Outside");
            message.put(Integer.toString(lux));
            clientConnection.send(message);
            Thread.sleep(250);
        }
    }

    @Override
    public void serverConnected(ServerConnection serverConnection) {
        try {
            clientConnection.subscribe("Sensors/Readings/Temp/Outside", "Sensors/Readings/Light/Outside");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageFromServer(ServerConnection serverConnection, TopicMessage topicMessage) {
        try {
            String topicName = topicMessage.getTopicName();
            String command = topicMessage.asFields().get(0);
	    System.out.println(topicName);
	    System.out.println(command);
            if (topicName.equals("Sensors/Control/Light")) {
                if (command.equals("on")) {
                    pin.high();
                } else {
                    pin.low();
		}
            }
        } catch (MessageException e) {
            e.printStackTrace();
        }
    }
}
