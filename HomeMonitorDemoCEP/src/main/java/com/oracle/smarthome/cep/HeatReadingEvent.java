package com.oracle.smarthome.cep;

public class HeatReadingEvent {

    private Double temperature;
    private String sensorId;
    private int stuff = 1;

    public HeatReadingEvent() {
    }

    public HeatReadingEvent(String sensorId, Double temperature, int stuff) {
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.stuff = stuff;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public int getStuff() {
        return stuff;
    }

    public void setStuff(int stuff) {
        this.stuff = stuff;
    }

    @Override
    public String toString() {
        return "HeatEvent{" + "sensorId=" + getSensorId() + ", temperature=" + temperature + '}';
    }

}
