package com.oracle.smarthome.cep;

import com.bea.wlevs.ede.api.EventRejectedException;
import com.bea.wlevs.ede.api.StreamSink;

public class HeatControlAdapter implements StreamSink {

    private DiffusionAdapter diffusionAdapter;

    public HeatControlAdapter() {
    }
    
    public void setDiffusionAdapter(DiffusionAdapter da) {
    	diffusionAdapter = da;
    }
    
    public DiffusionAdapter getDiffusionAdapter() {
    	return diffusionAdapter;
    }

    @Override
    public void onInsertEvent(Object event) throws EventRejectedException {
    	System.out.println("HeatControlEvent(" + event + ")");
        diffusionAdapter.sendControlEvent((HeatControlEvent) event);
    }

}
