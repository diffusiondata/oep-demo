package com.oracle.smarthome.cep;

import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSource;

public class HeatAdapter implements StreamSource {

    private StreamSender sender;

    public HeatAdapter() {
    }

    @Override
    public void setEventSender(StreamSender sender) {
        this.sender = sender;
    }

    public void sendEvent(String sensorId, double value) {
        sender.sendInsertEvent(new HeatReadingEvent(sensorId, value, 1));
    }
}
