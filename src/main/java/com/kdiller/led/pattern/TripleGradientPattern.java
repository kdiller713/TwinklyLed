package com.kdiller.led.pattern;

import com.kdiller.led.controller.ILedController;

import java.awt.Color;

public class TripleGradientPattern extends Pattern {
    private Color[] frame;
    
    public TripleGradientPattern(ILedController controller){
        super(controller);
        
        frame = new Color[ledController.numberOfLeds()];
        
        for(int i = 0; i < frame.length; i++){
            frame[i] = new Color(getColorValue(i), getColorValue((i + frame.length / 3) % frame.length), getColorValue((i + 2 * frame.length / 3) % frame.length));
        }
    }
    
    private int getColorValue(int i){
        // This function creates a graph that is 255 at 0 and frame.length, and negative slope
        // that meets at frame.length / 2 with a value of -255/2
        // The Math.min and Math.max is to limit the values between 0 and 255
        return Math.max(0, Math.min(255, Math.abs(i - frame.length / 2) * 765 / frame.length - 127));
    }
    
    @Override
    public void update() {
        Color temp = frame[0];
        for(int i = 1; i < frame.length; i++){
            frame[i - 1] = frame[i];
        }
        frame[frame.length - 1] = temp;
        
        ledController.updateFrame(frame);
    }
}
