package com.push;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LightSensor implements Runnable {

    final double VERNIER_SCALING_FACTOR = 1;
    byte b[] = new byte[8];
    private final long delay;
    private final int size;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private List<Integer> readings = new ArrayList<Integer>();
    private AtomicInteger current = new AtomicInteger();

    public LightSensor(long delay, int size) {
        this.delay = delay;
        this.size = size;
        executor.scheduleAtFixedRate(this, 250, 250, TimeUnit.MILLISECONDS);
    }

    public int getValue() {
        return current.get();
    }

    private int readValue() throws Exception {
        FileInputStream fis = null;
        DataInputStream dis = null;
        try {
            fis = new FileInputStream("/dev/ldusb1");
            dis = new DataInputStream(fis);
            int lux = 0;
            
            // Read 8 bytes from Vernier Go!Temp USB probe
            // Format:
            // Byte 0: Sample Count
            // Byte 1: Sequence Index
            // Byte 2-3: First temp sample
            // Byte 4-5: Second temp sample
            // Byte 6-7: Third temp sample
            if (dis != null) {
                int available = dis.read(b, 0, 8);
                lux = 0xff & b[2];
                System.out.printf("lux=%d%n", lux);
            }
            return lux;
        } finally {
            if (dis != null) {
                dis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    public void run() {
        try {
            readings.add(readValue());
            if (readings.size() > size) {
                readings.remove(0);
            }

            int total = 0;
            for (Integer i : readings) {
                total += i;
            }
            current.set(total / readings.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
