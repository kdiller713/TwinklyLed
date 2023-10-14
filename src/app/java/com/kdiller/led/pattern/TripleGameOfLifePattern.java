package com.kdiller.led.pattern;

import com.kdiller.led.controller.ILedController;

import java.awt.Color;

import java.util.Arrays;
import java.util.Random;

public class TripleGameOfLifePattern extends Pattern {
    private static final int RED_MASK = 0x1;
    private static final int GREEN_MASK = 0x2;
    private static final int BLUE_MASK = 0x4;

    private byte[] state;
    private int rowCount;
    private int[] diffs;
    
    public TripleGameOfLifePattern(ILedController controller) {
        this(controller, System.currentTimeMillis());
    }
    
    public TripleGameOfLifePattern(ILedController controller, long seed){
        super(controller);
        
        int numberOfLeds = ledController.numberOfLeds();
        state = new byte[numberOfLeds];
        
        rowCount = (int)Math.sqrt(numberOfLeds);
        while(numberOfLeds % rowCount != 0) rowCount--;
        
        // Defines the rules for counting neighbors
        diffs = new int[]{
            -rowCount - 1, -rowCount, -rowCount + 1,
            -1,                       1,
            rowCount - 1,  rowCount,  rowCount + 1
        };
        
        Random rand = new Random(seed);
        for(int i = 0; i < state.length; i++){
            state[i] = (byte)(rand.nextInt() & 0x7);
        }
    }
    
    private int countAlive(int ind, byte[] frame, int mask){
        int count = 0;
        
        for(int i = 0; i < diffs.length; i++){
            int newInd = diffs[i] + ind;
            
            if(0 <= newInd && newInd < frame.length && (frame[newInd] & mask) == mask){
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public void update() {
        byte[] previous = Arrays.copyOf(state, state.length);
        Color[] frame = new Color[state.length];
        
        for(int i = 0; i < state.length; i++){
            int rAlive = countAlive(i, previous, RED_MASK);
            int gAlive = countAlive(i, previous, GREEN_MASK);
            int bAlive = countAlive(i, previous, BLUE_MASK);
            // Defines rules for updating based on neighbors alive
            state[i] = (byte)((rAlive == 3 || rAlive == 2 ? RED_MASK : 0) | (gAlive == 3 || gAlive == 2 ? GREEN_MASK : 0) | (bAlive == 3 || bAlive == 2 ? BLUE_MASK : 0));
            frame[i] = new Color(rAlive == 3 || rAlive == 2 ? 255 : 0, gAlive == 3 || gAlive == 2 ? 255 : 0, bAlive == 3 || bAlive == 2 ? 255 : 0);
        }
        
        ledController.updateFrame(frame);
    }
}
