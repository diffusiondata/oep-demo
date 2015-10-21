package com.push;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HeatSensor implements Runnable {

    final double VERNIER_SCALING_FACTOR = 126.74;
    byte b[] = new byte[8];
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private List<Double> readings = new ArrayList<Double>();
    private AtomicInteger current = new AtomicInteger();
    private long delay;
    private int size;

    public HeatSensor(long delay, int size) {
        this.delay = delay;
        this.size = size;
        executor.scheduleAtFixedRate(this, 250, 250, TimeUnit.MILLISECONDS);
    }

    public int getValue() {
        return current.get();
    }

    private double readValue() throws Exception {
        FileInputStream fis = null;
        DataInputStream dis = null;
        try {
            fis = new FileInputStream("/dev/ldusb0");
            dis = new DataInputStream(fis);
            double tempavg, c;
            int temp1 = 0, temp2 = 0, temp3 = 0;

            // Read 8 bytes from Vernier Go!Temp USB probe
            // Format:
            // Byte 0: Sample Count
            // Byte 1: Sequence Index
            // Byte 2-3: First temp sample
            // Byte 4-5: Second temp sample
            // Byte 6-7: Third temp sample
            if (dis != null) {
                int available = dis.read(b, 0, 8);
                temp1 = b[2] + b[3] * 256;
                temp2 = b[4] + b[5] * 256;
                temp3 = b[6] + b[7] * 256;
            }
            tempavg = (temp1 + temp2 + temp3) / 3.0;
            c = tempavg / VERNIER_SCALING_FACTOR;
            return roundDouble(c);
        } finally {
            if (dis != null) {
                dis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static double roundDouble(double value) {
        double result = value * 100;
        result = Math.round(result);
        result = result / 100;
        return (result);
    }

    public void run() {
        try {
            readings.add(readValue());
            if (readings.size() > size) {
                readings.remove(0);
            }

            double total = 0;
            for (Double i : readings) {
                total += i;
            }
            current.set((int) (total / readings.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
