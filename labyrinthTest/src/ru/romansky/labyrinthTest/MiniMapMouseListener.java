package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MiniMapMouseListener implements MouseListener {
    private ClassicGamePanel myMapPanel;
    private LabyrinthMap myMap;


    public MiniMapMouseListener(ClassicGamePanel mapPanel, LabyrinthMap map) {
        myMapPanel = mapPanel;
        myMap = map;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        myMapPanel.setDragMiniMap(myMap);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        myMapPanel.stopDragMiniMap();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
