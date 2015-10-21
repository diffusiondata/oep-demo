package com.oracle.smarthome.cep;

public class LightReadingEvent {

    private String sensorId;
    private int intensity;
    private Double temperature;

    public LightReadingEvent() {
    }

    public LightReadingEvent(String sensorId, int intensity) {
        this.sensorId = sensorId;
        this.intensity = intensity;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "LightEvent{" + "sensorId=" + getSensorId() + ", intensity=" + intensity + '}';
    }

}
