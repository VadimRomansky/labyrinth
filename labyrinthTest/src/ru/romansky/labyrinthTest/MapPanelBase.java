package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.event.KeyEvent;

public abstract class MapPanelBase extends JPanel {
    JFrame myFrame;
    JPanel myParent;
    LabyrinthMap myMap;
    final static int cellWidth = 30;
    final static int borderWidth = 1;

    public void resetMap(LabyrinthMap map){
        myMap = map;
    }
    public abstract void keyPressed(KeyEvent e);
}
