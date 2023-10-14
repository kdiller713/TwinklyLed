package com.kdiller.led.controller;

import java.awt.Color;

public interface ILedController {
    public enum LedMode {
        // Not implementing effect, demo, movie, or playlist since those aren't interesting
        // from a programming perspective right now
        // Use OFF for those right now
        OFF("off"),
        COLOR("color"),
        REAL_TIME("rt");
        
        private String modeStr;
        
        private LedMode(String str){
            modeStr = str;
        }
        
        public String getModeStr() {
            return modeStr;
        }
        
        public static LedMode parseModeStr(String str){
            for(LedMode mode : LedMode.values()) {
                if(str.equals(mode.getModeStr())) {
                    return mode;
                }
            }
            
            return null;
        }
    }

    // LED Strand Details
    public LedMode getMode();
    public int numberOfLeds();
    
    // LED Operations
    public void setMode(LedMode mode);
    public void updateFrame(Color[] frame); // TODO Determine what to do if the wrong mode is set for this
    public void setColor(Color color);
}
