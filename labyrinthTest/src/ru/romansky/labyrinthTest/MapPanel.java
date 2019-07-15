package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Vadim on 01.08.2018.
 */
public class MapPanel extends MapPanelBase {

    public MapPanel(JFrame frame, JPanel parent) {
        this.myFrame = frame;
        this.myParent = parent;
        myMap = null;
    }

    public MapPanel(JFrame frame, JPanel parent, LabyrinthMap map) {
        this.myFrame = frame;
        this.myParent = parent;
        myMap = map;
    }

    public void resetMap(LabyrinthMap map){
        myMap = map;
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }

    public void paint(Graphics g) {
        super.paint(g);
        //g.setColor(Color.WHITE);
        if(myMap != null){
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;
            int leftX = centerx - myMap.width*cellWidth/2 - (myMap.width + 1)*borderWidth/2;
            int topY = centery - myMap.height*cellWidth/2 - (myMap.height + 1)*borderWidth/2;
            int rightX = centerx + myMap.width*cellWidth/2 + (myMap.width + 1)*borderWidth/2;
            int bottomY = centery + myMap.height*cellWidth/2 + (myMap.height + 1)*borderWidth/2;

            ///draw inner cell borders
            g.setColor(Color.WHITE);
            for(int i = 1; i < myMap.width; ++i){
                int tempX = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                g.drawLine(tempX, topY, tempX, bottomY);
            }
            for(int j = 1; j < myMap.width; ++j) {
                int tempY = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                g.drawLine(leftX, tempY, rightX, tempY);
            }
            g.setColor(Color.BLACK);

            ///draw outer borders
            g.drawLine(leftX, topY, rightX, topY);
            g.drawLine(rightX, topY, rightX, bottomY);
            g.drawLine(rightX, bottomY, leftX, bottomY);
            g.drawLine(leftX, bottomY, leftX, topY);

            ///draw walls
            for(int i = 0; i < myMap.width; ++i){
                for(int j = 0; j < myMap.height; ++j){
                    if(myMap.verticalBorders[i][j].exists()){
                        int x = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                        int topy = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                        int downy = topY + (j+1)*cellWidth + (2*j+3)*borderWidth/2;
                        g.drawLine(x, topy, x, downy);
                    }
                    if(myMap.horizontalBorders[i][j].exists()){
                        int y = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                        int leftx = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                        int rightx = leftX + (i+1)*cellWidth + (2*i+3)*borderWidth/2;
                        g.drawLine(leftx, y, rightx, y);
                    }
                    int cellx = leftX + (2*i+1)*cellWidth/2 + i*borderWidth;
                    int celly = topY + (2*j+1)*cellWidth/2 + j*borderWidth;
                    paintCell(g, cellx, celly, myMap.cells[i][j]);
                }
            }
        }
        //super.paint(g);
    }

    private void paintCell(Graphics g, int cellx, int celly, Cell cell) {
        cell.paint(g, cellx, celly);
    }
}
