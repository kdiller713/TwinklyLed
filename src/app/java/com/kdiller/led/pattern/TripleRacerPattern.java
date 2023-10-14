package com.kdiller.led.pattern;

import com.kdiller.led.controller.ILedController;

import java.awt.Color;

import java.util.LinkedList;

public class TripleRacerPattern extends Pattern {
    private final int DIMENSION, LENGTH;
    private int[] directions;
    
    private LinkedList<Integer> redRacer, greenRacer, blueRacer;
    
    public TripleRacerPattern(ILedController controller, int dimension, int length){
        super(controller);
        
        DIMENSION = dimension;
        LENGTH = length;
        
        redRacer = new LinkedList<Integer>();
        greenRacer = new LinkedList<Integer>();
        blueRacer = new LinkedList<Integer>();
        
        redRacer.addLast((int)(Math.random() * DIMENSION * DIMENSION));
        greenRacer.addLast((int)(Math.random() * DIMENSION * DIMENSION));
        blueRacer.addLast((int)(Math.random() * DIMENSION * DIMENSION));
        
        for(int i = 0; i < LENGTH; i++){
            updateRacer(redRacer);
            updateRacer(greenRacer);
            updateRacer(blueRacer);
        }
    }
    
    @Override
    public void update() {
        updateRacer(redRacer);
        updateRacer(greenRacer);
        updateRacer(blueRacer);
        
        ledController.updateFrame(getFrame());
    }
    
    private void updateRacer(LinkedList<Integer> racer){
        int prev = racer.getLast();
        int next = prev;
        
        LinkedList<Integer> options = new LinkedList<Integer>();
        
        // Previous Column
        next = prev - DIMENSION;
        if(next < 0){
            next = DIMENSION * DIMENSION + next;
        }
        if(!racer.contains(next)){
            options.add(next);
        }
        
        // Next Column
        next = prev + DIMENSION;
        if(next >= DIMENSION * DIMENSION){
            next = next - DIMENSION * DIMENSION;
        }
        if(!racer.contains(next)){
            options.add(next);
        }
        
        // Previous Row
        next = prev - 1;
        if(next % DIMENSION > prev % DIMENSION || next < 0){
            next = next + DIMENSION;
        }
        if(!racer.contains(next)){
            options.add(next);
        }
        
        // Next Row
        next = prev + 1;
        if(next % DIMENSION < prev % DIMENSION){
            next = next - DIMENSION;
        }
        if(!racer.contains(next)){
            options.add(next);
        }
        
        racer.addLast(options.get((int)(Math.random() * options.size())));
        
        while(racer.size() > LENGTH) {
            racer.removeFirst();
        }
    }
    
    private Color[] getFrame() {
        int[] rgb = new int[DIMENSION * DIMENSION];
        Color[] frame = new Color[DIMENSION * DIMENSION];
        
        for(int i = 0; i < LENGTH; i++){
            int loc = redRacer.get(i);
            
            if((loc / DIMENSION) % 2 == 0){ // Reverse every other column
                loc = (loc / DIMENSION) * DIMENSION + DIMENSION - 1 - (loc % DIMENSION);
            }
            
            rgb[loc] = rgb[loc] + 0xFF0000;
            
            loc = greenRacer.get(i);
            
            if((loc / DIMENSION) % 2 == 0){ // Reverse every other column
                loc = (loc / DIMENSION) * DIMENSION + DIMENSION - 1 - (loc % DIMENSION);
            }
            
            rgb[loc] = rgb[loc] + 0xFF00;
            
            loc = blueRacer.get(i);
            
            if((loc / DIMENSION) % 2 == 0){ // Reverse every other column
                loc = (loc / DIMENSION) * DIMENSION + DIMENSION - 1 - (loc % DIMENSION);
            }
            
            rgb[loc] = rgb[loc] + 0xFF;
        }
        
        for(int i = 0; i < frame.length; i++){
            frame[i] = new Color(rgb[i]);
        }
        
        return frame;
    }
}
