package com.oracle.smarthome.cep;

public class LightControlEvent {
    private String status;
    private int intensity = -1;
    private boolean on;

    public LightControlEvent() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public String toString() {
        return "ControlEvent [status=" + status + ", intensity=" + intensity + ", on=" + on + "]";
    }

}
