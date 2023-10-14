package com.kdiller.led.controller;

import java.awt.Color;

import java.util.LinkedList;

public class MultiLedController implements ILedController {
    private LinkedList<ILedController> ledControllers;
    
    public MultiLedController() {
        ledControllers = new LinkedList<ILedController>();
    }
    
    public void addLedController(ILedController controller){
        ledControllers.add(controller);
    }
    
    @Override
    public LedMode getMode() {
        LedMode mode = null;
        
        for(ILedController controller : ledControllers){
            if(mode == null){
                mode = controller.getMode();
            }else if(mode != controller.getMode()){
                mode = null;
                break;
            }
        }
        
        return mode;
    }
    
    @Override
    public int numberOfLeds(){
        int min = -1;
        
        for(ILedController controller : ledControllers){
            if(min == -1){
                min = controller.numberOfLeds();
            }else{
                min = Math.min(min, controller.numberOfLeds());
            }
        }
        
        return Math.max(0, min);
    }
    
    @Override
    public void setMode(LedMode mode) {
        for(ILedController controller : ledControllers){
            controller.setMode(mode);
        }
    }
    
    @Override
    public void updateFrame(Color[] frame) {
        for(ILedController controller : ledControllers){
            controller.updateFrame(frame);
        }
    }
    
    @Override
    public void setColor(Color color) {
        for(ILedController controller : ledControllers){
            controller.setColor(color);
        }
    }
}
