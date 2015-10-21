package com.oracle.smarthome.cep;

import com.bea.wlevs.ede.api.EventRejectedException;
import com.bea.wlevs.ede.api.StreamSink;

public class LightControlAdapter implements StreamSink {

    private DiffusionAdapter diffusionAdapter;

    public LightControlAdapter() {
    }
    
    public void setDiffusionAdapter(DiffusionAdapter da) {
    	diffusionAdapter = da;
    }
    
    public DiffusionAdapter getDiffusionAdapter() {
    	return diffusionAdapter;
    }

    @Override
    public void onInsertEvent(Object event) throws EventRejectedException {
        diffusionAdapter.sendControlEvent((LightControlEvent) event);
    }

}
