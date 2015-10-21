package com.oracle.smarthome.cep;

import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSource;

public class LightAdapter implements StreamSource {

    private StreamSender sender;

    public LightAdapter() {
    }

    @Override
    public void setEventSender(StreamSender sender) {
        this.sender = sender;
    }

    public void sendEvent(String sensorId, int value) {
        sender.sendInsertEvent(new LightReadingEvent(sensorId, value));
    }
}
