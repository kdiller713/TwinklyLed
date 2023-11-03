package com.kdiller.led.controller.jframe;

import com.kdiller.led.controller.ILedController;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Graphics;

public abstract class JFrameLedController implements ILedController {
    protected static final int LED_DIMENSION = 20;

    protected LedMode mode;
    private JFrame frame;

    public JFrameLedController(final int width, final int height) {
        frame = new JFrame();
        
        SwingUtilities.invokeLater(() -> {
            initialize(width, height + 30);
        });
    }
    
    private void initialize(final int width, final int height) {
        frame.add(new JPanel() {
            @Override
            public void paintComponent(Graphics g){
                drawLeds(g);
            }
        });
    
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setVisible(true);
    }
    
    public final void triggerRepaint(){
        SwingUtilities.invokeLater(() -> {
            frame.repaint();
        });
    }
    
    public abstract void drawLeds(Graphics g);
    
    @Override
    public LedMode getMode() {
        return mode;
    }
    
    @Override
    public void setMode(LedMode mode) {
        this.mode = mode;
    }
}
