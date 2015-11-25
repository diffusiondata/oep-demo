package com.oracle.smarthome.cep;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bea.wlevs.ede.api.ActivatableBean;
import com.bea.wlevs.ede.api.InitializingBean;
import com.pushtechnology.diffusion.api.ServerConnection;
import com.pushtechnology.diffusion.api.client.ExternalClientConnection;
import com.pushtechnology.diffusion.api.connection.ConnectionDetails;
import com.pushtechnology.diffusion.api.connection.ConnectionFactory;
import com.pushtechnology.diffusion.api.message.TopicMessage;

public class DiffusionAdapter extends ServerConnectionAdapter implements ActivatableBean {

    private static byte[] lightSetColour = { 0x40, 0x40, 0x55 };
    private static byte[] lightOn = { 0x42, 0x00, 0x55 };
    private static byte[] lightOff = { 0x41, 0x40, 0x55 };

    private static DiffusionAdapter instance;

    private final String milightAddress = "192.168.53.110";
    private final int milightPort = 8899;
    private String serverUri = "dpt://localhost:8080";
    private ExternalClientConnection clientConnection;
    private HeatAdapter heatAdapter;
    private LightAdapter lightAdapter;
    private ThresholdAdapter thresholdAdapter;
    private LightControlAdapter lightControlAdapter;
    private HeatControlAdapter heatControlAdapter;
    private boolean startup = true;
    private volatile boolean isConnected = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public enum Device {
        Heater, Light
    }

    public enum Rooms {
        Bed1("Bedroom 1"), Bed2("Bedroom 2"), Bed3("Bedroom 3"), Kitchen("Kitchen"), Living("Living Room"), Outside("Outside");

        private final String name;

        private Rooms(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Rooms get(String name) {
            for (Rooms room : Rooms.values()) {
                if (room.getName().equals(name)) {
                    return room;
                }
            }
            return null;
        }
    }

    public DiffusionAdapter() {
    }
    
	@Override
	public void afterConfigurationActive() throws Exception {
		lightControlAdapter.setDiffusionAdapter(this);
		heatControlAdapter.setDiffusionAdapter(this);
        connect();
	}

    @Override
    public void serverConnected(ServerConnection serverConnection) {
        System.out.printf("Connected to Diffusion%n");
        isConnected = true;
        System.out.println(isConnected + this.toString());
        if (startup) {
            thresholdAdapter.sendEvent("auto", "Bed1", 15, 25, 50);
            thresholdAdapter.sendEvent("auto", 15, 25, 50);
            startup = false;
        }
    }

    @Override
    public void serverDisconnected(ServerConnection serverConnection) {
        System.out.printf("Lost connection to Diffusion server%n");
        isConnected = false;
        connect();
    }

    private void connect() {
        executorService.execute(new DiffusionConnector());
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public void setHeatAdapter(HeatAdapter heatAdapter) {
        this.heatAdapter = heatAdapter;
    }

    public void setLightAdapter(LightAdapter lightAdapter) {
        this.lightAdapter = lightAdapter;
    }

    public void setLightControlAdapter(LightControlAdapter lightControlAdapter) {
        this.lightControlAdapter = lightControlAdapter;
    }

    public void setHeatControlAdapter(HeatControlAdapter heatControlAdapter) {
        this.heatControlAdapter = heatControlAdapter;
    }

    public void setThresholdAdapter(ThresholdAdapter thresholdAdapter) {
        this.thresholdAdapter = thresholdAdapter;
    }

    public void sendControlMessage(Device device, String control) {
		while (!isConnected) {
			try {
				System.out.println(isConnected + this.toString());
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}   		
        try {
            System.out.printf("Sending %s for device %s%n", control, device.name());

            TopicMessage message = clientConnection.createDeltaMessage("Sensors/Control/" + device.name());
            message.putFields(control);
            clientConnection.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageFromServer(ServerConnection serverConnection, TopicMessage topicMessage) {
        String topicName = topicMessage.getTopicName();
        String[] split = topicName.split("/");
        try {
            List<String> fields = topicMessage.asFields();
            if (split[1].equals("Readings")) {
                String type = split[2];
                String sensor = split[3];

                Rooms room = Rooms.get(sensor);
                if (room == null) {
                    System.out.println("Unknown room " + sensor);
                    return;
                }
                String sensorId = room.name();
                String valString = fields.get(0);

                if (type.equals("Temp")) {
                    if (heatAdapter != null) {
                        double value = Double.valueOf(valString);
                        System.out.printf("type[%s] id[%s] value[%.1f]%n", type, sensorId, value);
                        heatAdapter.sendEvent(sensorId, value);
                    }
                } else if (type.equals("Light")) {
                    if (lightAdapter != null) {
                        int value = Integer.valueOf(valString);
                        System.out.printf("type[%s] id[%s] value[%d]%n", type, sensorId, value);
                        lightAdapter.sendEvent(sensorId, value);
                    }
                } else if (type.equals("PIR")) {
                    boolean occupied = Boolean.parseBoolean(valString);
                } else {
                    System.err.println("Unknown sensor type:" + type);
                }
            } else if (topicName.equals("Sensors/Control")) {
                if (thresholdAdapter == null) {
                    return;
                }
                System.out.printf("Control mode [%s]%n", topicMessage.toString());
                String mode = fields.get(0);
                Double ttl = fields.get(1) == null || fields.get(1).trim().equals("") ? null : Double.parseDouble(fields.get(1));
                Double tth = fields.get(2) == null || fields.get(2).trim().equals("") ? null : Double.parseDouble(fields.get(2));
                Integer tl = fields.get(4) == null || fields.get(4).trim().equals("") ? null : Integer.parseInt(fields.get(4));
                for (Rooms room : Rooms.values()) {
                	thresholdAdapter.sendEvent(mode, room.toString(), ttl, tth, tl);
                }
            } else if (topicName.equals("Sensors/Control/Light")) {
                String command = fields.get(0);
                System.out.printf("Received Light control event [%s]%n", command);
                if (command.equals("on")) {
                    // sendMilight(lightSetColour);
                    sendMilight(lightOn);
                } else {
                    sendMilight(lightOff);
                }

            }
        } catch (Exception e) {
            System.err.printf("ERROR: %s%n", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendControlEvent(LightControlEvent event) {
        sendControlMessage(Device.Light, event.getStatus());
    }

    public void sendControlEvent(HeatControlEvent event) {
        sendControlMessage(Device.Heater, event.getStatus());
    }

    private void sendMilight(byte[] cmd) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(cmd, cmd.length, InetAddress.getByName(milightAddress), milightPort);
            DatagramSocket socket = new DatagramSocket();
            socket.send(datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DiffusionConnector implements Runnable {

        @Override
        public void run() {
            try {
          		while (!isConnected) {
          			ConnectionDetails details = ConnectionFactory.createConnectionDetails(serverUri);
           			details.setAutoFailover(false);
           			clientConnection = new ExternalClientConnection(DiffusionAdapter.this, details);
           			System.out.printf("Trying to connect to Diffusion [%s] ...%n", serverUri);
           			clientConnection.connect("Sensors//");
            	}
            } catch (Throwable e) {
                System.err.printf("%s%n", e.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                }
                executorService.execute(new DiffusionConnector());
            }
        }

    }

}
