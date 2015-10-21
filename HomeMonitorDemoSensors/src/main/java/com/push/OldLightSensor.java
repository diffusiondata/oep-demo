package com.push;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.client.ExternalClientConnection;
import com.pushtechnology.diffusion.api.message.TopicMessage;

public class OldLightSensor implements Runnable {

    private static File lightFile = null;
    private static Random random = new Random(System.currentTimeMillis());

    private static int lastLux;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ExternalClientConnection clientConnection;
    private final CountDownLatch countDownLatch;

    private final String name;

    static {
        String str = DevicePathFinder.find("08f7", "0003");
        if (str != null) {
            lightFile = new File(str);
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            System.out.printf("%d%n", OldLightSensor.readValue());
            Thread.sleep(1000);
        }
    }

    public OldLightSensor(ExternalClientConnection clientConnection, CountDownLatch countDownLatch, String name) {
        this.clientConnection = clientConnection;
        this.countDownLatch = countDownLatch;
        this.name = name;
        executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
        countDownLatch.countDown();
    }

    public void run() {
        try {
            Integer v = readValue();
            TopicMessage message = clientConnection.createDeltaMessage("Sensors/Readings/Light/" + name);
            message.putRecord(v.toString());
            clientConnection.send(message);
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    public static int readValue() {
        if (lightFile == null) {
            lastLux = (int) (lastLux + (2 - random.nextInt(40) / 10d));
            if (lastLux < 1) {
                lastLux = 1;
            }
            if (lastLux > 15) {
                lastLux = 15;
            }
            return lastLux;
        }
        int result = 0;

        FileInputStream fis = null;
        DataInputStream dis = null;

        byte b[] = new byte[8];

        try {
            fis = new FileInputStream(lightFile);
            dis = new DataInputStream(fis);

            dis.read(b, 0, 8);
            int measuredLight = b[3];

            if (measuredLight < -101) {
                result = 0;
            } else if (measuredLight > -101 && measuredLight < -75) {
                result = 1;
            } else if (measuredLight > -75 && measuredLight < -50) {
                result = 2;
            } else if (measuredLight > -50 && measuredLight < -25) {
                result = 3;
            } else if (measuredLight > -25 && measuredLight < 0) {
                result = 4;
            } else if (measuredLight > 0 && measuredLight < 25) {
                result = 5;
            } else if (measuredLight > 25 && measuredLight < 50) {
                result = 6;
            } else if (measuredLight > 50 && measuredLight < 75) {
                result = 7;
            } else if (measuredLight > 75 && measuredLight < 100) {
                result = 8;
            } else if (measuredLight > 100 && measuredLight < 125) {
                result = 9;
            } else if (measuredLight > 125 && measuredLight < 128) {
                result = 10;
            }
            dis.close();
            dis = null;
            fis.close();
            fis = null;
        } catch (IOException ex) {
            ex.printStackTrace();
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
        }

        return result;
    }
}

