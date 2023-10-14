package com.kdiller.led.controller.wireless;

import java.util.Map;

import java.awt.Color;

public class CachingWirelessLedController implements IWirelessLedController {
    private IWirelessLedController wirelessController;
    
    private String cachedDeviceName;
    private String cachedProductCode;
    private String cachedUUID;
    private String cachedHardwareID;
    private LedMode cachedLedMode;
    private int cachedLedCount;
    
    public CachingWirelessLedController(IWirelessLedController controller) {
        wirelessController = controller;
        cachedLedCount = -1;
    }

    // LED Strand Details
    public String getDeviceName() {
        if(cachedDeviceName == null){
            cachedDeviceName = wirelessController.getDeviceName();
        }
        
        return cachedDeviceName;
    }
    
    public String getProductCode() {
        if(cachedProductCode == null){
            cachedProductCode = wirelessController.getProductCode();
        }
        
        return cachedProductCode;
    }
    
    public String getUUID() {
        if(cachedUUID == null){
            cachedUUID = wirelessController.getUUID();
        }
        
        return cachedUUID;
    }
    
    public String getHardwareID() {
        if(cachedHardwareID == null){
            cachedHardwareID = wirelessController.getHardwareID();
        }
        
        return cachedHardwareID;
    }
    
    public Map<String, String> getFullInfo() {
        // This is not cached
        return wirelessController.getFullInfo();
    }
    
    public LedMode getMode() {
        if(cachedLedMode == null){
            cachedLedMode = wirelessController.getMode();
        }
        
        return cachedLedMode;
    }
    
    public int numberOfLeds() {
        if(cachedLedCount == -1){
            cachedLedCount = wirelessController.numberOfLeds();
        }
        
        return cachedLedCount;
    }
    
    // LED Operations
    public void setMode(LedMode mode) {
        cachedLedMode = null;
        wirelessController.setMode(mode);
    }
    
    public void updateFrame(Color[] frame) {
        wirelessController.updateFrame(frame);
    }
    
    public void setColor(Color color) {
        wirelessController.setColor(color);
    }
    
    public void turnOff() {
        wirelessController.turnOff();
    }
}
