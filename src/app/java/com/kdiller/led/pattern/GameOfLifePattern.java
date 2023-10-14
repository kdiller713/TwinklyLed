package com.kdiller.led.pattern;

import com.kdiller.led.controller.ILedController;

import java.awt.Color;

import java.util.Arrays;
import java.util.Random;

public class GameOfLifePattern extends Pattern {
    private static final Color OFF_COLOR = Color.BLACK;
    
    private boolean[] state;
    private Color color;
    private int rowCount;
    private int[] diffs;
    
    public GameOfLifePattern(ILedController controller) {
        this(controller, Color.BLUE);
    }
    
    public GameOfLifePattern(ILedController controller, Color color) {
        this(controller, color, System.currentTimeMillis());
    }
    
    public GameOfLifePattern(ILedController controller, Color color, long seed){
        super(controller);
        
        this.color = color;
        
        int numberOfLeds = ledController.numberOfLeds();
        state = new boolean[numberOfLeds];
        
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
            state[i] = rand.nextBoolean();
        }
    }
    
    private int countAlive(int ind, boolean[] frame){
        int count = 0;
        
        for(int i = 0; i < diffs.length; i++){
            int newInd = diffs[i] + ind;
            
            if(0 <= newInd && newInd < frame.length && frame[newInd]){
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public void update() {
        boolean[] previous = Arrays.copyOf(state, state.length);
        Color[] frame = new Color[state.length];
        
        for(int i = 0; i < state.length; i++){
            int alive = countAlive(i, previous);
            // Defines rules for updating based on neighbors alive
            state[i] = alive == 3 || alive == 2;
            frame[i] = state[i] ? color : OFF_COLOR;
        }
        
        ledController.updateFrame(frame);
    }
}
