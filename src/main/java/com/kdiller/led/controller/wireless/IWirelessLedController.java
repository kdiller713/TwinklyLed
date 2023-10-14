package com.kdiller.led.controller.wireless;

import com.kdiller.led.controller.ILedController;

import java.util.Map;

public interface IWirelessLedController extends ILedController {
    // LED Strand Details
    public String getDeviceName();
    public String getProductCode();
    public String getUUID();
    public String getHardwareID();
    public Map<String, String> getFullInfo();
    
    // LED Operations
    public void turnOff();
}
