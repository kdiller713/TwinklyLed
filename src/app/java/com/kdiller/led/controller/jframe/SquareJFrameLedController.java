package com.kdiller.led.controller.jframe;

import java.awt.Color;
import java.awt.Graphics;

public class SquareJFrameLedController extends JFrameLedController {
    private Color[][] ledBoard;
    
    public SquareJFrameLedController(int sideLength) {
        super((sideLength + 2) * LED_DIMENSION, (sideLength + 2) * LED_DIMENSION);
        
        ledBoard = new Color[sideLength][sideLength];
    }
    
    public int numberOfLeds(){
        return ledBoard.length * ledBoard.length;
    }
    
    @Override
    public void drawLeds(Graphics g) {
        for(int r = 0; r < ledBoard.length; r++) {
            for(int c = 0; c < ledBoard.length; c++) {
                g.setColor(ledBoard[r][c]);
                g.fillRect((c + 1) * LED_DIMENSION, (r + 1) * LED_DIMENSION, LED_DIMENSION, LED_DIMENSION);
            }
        }
        
        g.setColor(Color.BLACK);
        for(int i = 0; i <= ledBoard.length; i++){
            g.drawLine(LED_DIMENSION, (i + 1) * LED_DIMENSION, (ledBoard.length + 1) * LED_DIMENSION, (i + 1) * LED_DIMENSION);
            g.drawLine((i + 1) * LED_DIMENSION, LED_DIMENSION, (i + 1) * LED_DIMENSION, (ledBoard.length + 1) * LED_DIMENSION);
        }
    }
    
    public void updateFrame(Color[] frame) {
        setMode(LedMode.REAL_TIME);
        
        // This function should be overriden to handle different led configurations
        // This one is designed to be a snaking pattern with the first led being
        // in the bottom right and going up first
        
        // TODO Determine what happens when the frame length is too small or too large
        // For now just assuming it truncates for too large and repeats for too small
        int numLeds = numberOfLeds();
        
        for(int i = 0; i < numLeds; i++) {
            int col = i / ledBoard.length;
            int row = i % ledBoard.length;
            
            if(col % 2 == 0){ // Up
                row = ledBoard.length - 1 - row;
            }
            
            col = ledBoard.length - 1 - col; // Doing this after to make the row check easier
            
            ledBoard[row][col] = frame[i % frame.length];
        }
    
        triggerRepaint();
    }
    
    public void setColor(Color color) {
        setMode(LedMode.COLOR);
        
        for(int i = 0; i < ledBoard.length; i++){
            for(int j = 0; j < ledBoard.length; j++){
                ledBoard[i][j] = color;
            }
        }
        
        triggerRepaint();
    }
}
