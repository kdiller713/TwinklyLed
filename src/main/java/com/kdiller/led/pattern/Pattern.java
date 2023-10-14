package com.kdiller.led.pattern;

import com.kdiller.led.controller.ILedController;

public abstract class Pattern {
    protected ILedController ledController;
    
    public Pattern(ILedController controller){
        ledController = controller;
    }
    
    public abstract void update();
}
