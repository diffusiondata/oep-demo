package com.oracle.smarthome.cep;

public class ThresholdEvent {

    private String sensorId = "Outside";
    private double ttl = 23; // temp low
    private double tth = 30; // temp high
    private int tl = 20; // light
    private String mode = "auto";

    public ThresholdEvent(String mode, double ttl, double tth, int tl) {
        this.ttl = ttl;
        this.tth = tth;
        this.tl = tl;
        this.mode = mode;
    }

    public double getTtl() {
        return ttl;
    }

    public void setTtl(double ttl) {
        this.ttl = ttl;
    }

    public double getTth() {
        return tth;
    }

    public void setTth(double tth) {
        this.tth = tth;
    }

    public int getTl() {
        return tl;
    }

    public void setTl(int tl) {
        this.tl = tl;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public String toString() {
        return "ThresholdConfig-" + this.hashCode() + " {" + ", ttl=" + ttl + ", tth=" + tth + ", tl=" + tl + ", mode=" + mode + '}';
    }

}
