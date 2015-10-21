package com.oracle.smarthome.cep;

public class HeatControlEvent {
    private String status;
    private double temperature = -1;
    private boolean on;

    public HeatControlEvent() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public String toString() {
        return "ControlEvent [status=" + status + ", temperature=" + temperature + ", on=" + on + "]";
    }

}
