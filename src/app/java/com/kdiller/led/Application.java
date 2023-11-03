package com.kdiller.led;

import com.kdiller.led.controller.ILedController;
import com.kdiller.led.controller.MultiLedController;
import com.kdiller.led.controller.jframe.SquareJFrameLedController;
import com.kdiller.led.controller.wireless.IWirelessLedController;
import com.kdiller.led.controller.wireless.http.HttpWirelessLedController;

import com.kdiller.led.pattern.*;

import java.util.Map;

public class Application {
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.java2d.xrender", "f"); // Need this property to not do buffered rendering
        
        ILedController jframeLedController = new SquareJFrameLedController(10);
        
        MultiLedController multi = new MultiLedController();
        multi.addLedController(jframeLedController);

        Map<String, String> devices = HttpWirelessLedController.findDevices();
        for(Map.Entry<String, String> d : devices.entrySet()) {
            IWirelessLedController wirelessLedController = new HttpWirelessLedController(d.getKey());
            multi.addLedController(wirelessLedController);
            
            System.out.println("IP Address: " + d.getKey());
            System.out.println("Device ID: " + d.getValue());
            System.out.println("Device Name: " + wirelessLedController.getDeviceName());
            System.out.println("Product Code: " + wirelessLedController.getProductCode());
            System.out.println("UUID: " + wirelessLedController.getUUID());
            System.out.println("Hardware ID: " + wirelessLedController.getHardwareID());
            System.out.println("Mode: " + wirelessLedController.getMode());
        }
        
        Pattern pattern = new TripleGradientPattern(multi);
        
        while(true){
            pattern.update();
            Thread.sleep(20);
        }
    }
}
