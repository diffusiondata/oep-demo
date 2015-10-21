package com.oracle.smarthome.cep;

import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSource;

public class ThresholdAdapter implements StreamSource {

    private StreamSender sender;

    public ThresholdAdapter() {
    }

    @Override
    public void setEventSender(StreamSender sender) {
        this.sender = sender;
    }

    public void sendEvent(String mode, double ttl, double tth, int tl) {
    	//System.out.println("ThresholdEvent(" + mode + ", " + ttl + ", " + tth + ")");
        sender.sendInsertEvent(new ThresholdEvent(mode, ttl, tth, tl));
    }

    public void sendEvent(String mode, String sensorId, double ttl, double tth, int tl) {
    	//System.out.println("ThresholdEvent(" + mode + ", " + sensorId + ", " + ttl + ", " + tth + ")");
    	ThresholdEvent te = new ThresholdEvent(mode, ttl, tth, tl);
    	te.setSensorId(sensorId);
        sender.sendInsertEvent(te);
    }
}
