package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class MiniMapPanel extends JPanel {
    public static final int cellWidth = 15;
    public static final int borderWidth = 1;
    private LabyrinthMap myMap;
    public MiniMapPanel(LabyrinthMap map){
        myMap = map;
    }
    private static final Color lightLightGray = new Color(240,240,240);

    public void paint(Graphics g) {
        super.paint(g);

        if((myMap != null)) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width / 2;
            int centery = height / 2;
            int leftX = centerx - myMap.width * cellWidth / 2 - (myMap.width + 1) * borderWidth / 2;
            int topY = centery - myMap.height * cellWidth / 2 - (myMap.height + 1) * borderWidth / 2;
            int rightX = centerx + myMap.width * cellWidth / 2 + (myMap.width + 1) * borderWidth / 2;
            int bottomY = centery + myMap.height * cellWidth / 2 + (myMap.height + 1) * borderWidth / 2;

            ///draw inner cell borders
            /*g.setColor(Color.WHITE);
            for(int i = 1; i < myMap.width; ++i){
                int tempX = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                g.drawLine(tempX, topY, tempX, bottomY);
            }
            for(int j = 1; j < myMap.width; ++j) {
                int tempY = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                g.drawLine(leftX, tempY, rightX, tempY);
            }*/
            g.setColor(Color.GRAY);

            ///draw outer borders
            /*g.drawLine(leftX, topY, rightX, topY);
            g.drawLine(rightX, topY, rightX, bottomY);
            g.drawLine(rightX, bottomY, leftX, bottomY);
            g.drawLine(leftX, bottomY, leftX, topY);*/

            g.setColor(Color.BLACK);

            ///draw walls


            for(int i = 0; i < myMap.width; ++i){
                for(int j = 0; j < myMap.height; ++j){
                    if(myMap.cells[i][j].state == CellState.VISITED){
                        g.setColor(lightLightGray);
                        int topy = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                        int downy = topY + (j + 1) * cellWidth + (2 * j + 3) * borderWidth / 2;
                        int leftx = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                        int rightx = leftX + (i + 1) * cellWidth + (2 * i + 3) * borderWidth / 2;

                        g.fillRect(leftx, topy, rightx - leftx, downy - topy);
                    }
                }
            }

            for (int i = 0; i < myMap.width + 1; ++i) {
                for (int j = 0; j < myMap.height; ++j) {
                    if (myMap.verticalBorders[i][j].state() != BorderState.NOTEXISTS) {
                        int x = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                        int topy = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                        int downy = topY + (j + 1) * cellWidth + (2 * j + 3) * borderWidth / 2;
                        if (myMap.verticalBorders[i][j].state() == BorderState.EXISTS) {
                            g.setColor(Color.BLACK);
                            g.drawLine(x, topy, x, downy);
                        }
                        //if (myMap.verticalBorders[i][j].state() == BorderState.UNDEFINED) {
                        //    g.setColor(Color.LIGHT_GRAY);
                        //}
                        if (myMap.verticalBorders[i][j].state() == BorderState.DOOR) {
                            g.setColor(Color.RED);
                            g.drawLine(x, topy, x, downy);
                        }
                        //g.drawLine(x, topy, x, downy);
                    }
                }
            }
            for (int i = 0; i < myMap.width; ++i) {
                for (int j = 0; j < myMap.height + 1; ++j) {
                    if (myMap.horizontalBorders[i][j].state() != BorderState.NOTEXISTS) {
                        int y = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                        int leftx = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                        int rightx = leftX + (i + 1) * cellWidth + (2 * i + 3) * borderWidth / 2;
                        if (myMap.horizontalBorders[i][j].state() == BorderState.EXISTS) {
                            g.setColor(Color.BLACK);
                            g.drawLine(leftx, y, rightx, y);
                        }
                        //if (myMap.horizontalBorders[i][j].state() == BorderState.UNDEFINED) {
                        //    g.setColor(Color.LIGHT_GRAY);
                        //}
                        if (myMap.horizontalBorders[i][j].state() == BorderState.DOOR) {
                            g.setColor(Color.RED);
                            g.drawLine(leftx, y, rightx, y);
                        }
                        //g.drawLine(leftx, y, rightx, y);
                    }
                }
            }

            for(int i = 0; i < myMap.width; ++i){
                for(int j = 0; j < myMap.height; ++j){
                    if(myMap.cells[i][j].state == CellState.VISITED){
                        g.setColor(lightLightGray);
                        int topy = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                        int downy = topY + (j + 1) * cellWidth + (2 * j + 3) * borderWidth / 2;
                        int leftx = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                        int rightx = leftX + (i + 1) * cellWidth + (2 * i + 3) * borderWidth / 2;

                        int cellx = leftX + (2*i+1)*cellWidth/2 + i*borderWidth;
                        int celly = topY + (2*j+1)*cellWidth/2 + j*borderWidth;

                        if(myMap.cells[i][j].type == CellType.PORTAL){
                            int radius = (cellWidth*2)/6;
                            Graphics2D g2d = (Graphics2D) g;
                            g2d.setColor(Color.BLACK);
                            g2d.drawOval(cellx - radius, celly - radius, 2*radius, 2*radius);
                            g2d.fill(new Ellipse2D.Double(cellx - radius, celly - radius, 2*radius, 2*radius));
                        }
                        if(myMap.cells[i][j].type == CellType.ARSENAL){
                            String text = "A";
                            Graphics2D g2d = (Graphics2D) g;
                            g.setFont(ClassicGamePanel.microFont);
                            FontMetrics fm = g2d.getFontMetrics();
                            int textWidth = fm.stringWidth(text);
                            int textHeight = fm.getHeight();
                            g2d.setColor(Color.BLUE);
                            g2d.drawString(text, cellx - textWidth/2, celly -  textHeight/2 + fm.getAscent());
                        }
                        if(myMap.cells[i][j].type == CellType.HOSPITAL){
                            Graphics2D g2d = (Graphics2D) g;
                            g2d.setColor(Color.RED);
                            int length = cellWidth*3/4;
                            int crosswidth = cellWidth*1/4;
                            g2d.fillRect(cellx - length/2, celly - crosswidth/2, length, crosswidth);
                            g2d.fillRect(cellx - crosswidth/2, celly - length/2, crosswidth, length);
                        }
                        if(myMap.cells[i][j].minotaur != null){
                            String text = "M";
                            Graphics2D g2d = (Graphics2D) g;
                            g.setFont(ClassicGamePanel.microFont);
                            FontMetrics fm = g2d.getFontMetrics();
                            int textWidth = fm.stringWidth(text);
                            int textHeight = fm.getHeight();
                            g2d.setColor(Color.BLACK);
                            g2d.drawString(text, cellx - textWidth/2, celly -  textHeight/2 + fm.getAscent());
                            if(!myMap.cells[i][j].minotaur.isSupposedAlive()){
                                g2d.setColor(Color.RED);
                                textWidth = fm.stringWidth("X");
                                g2d.drawString("X", cellx - textWidth/2, celly -  textHeight/2 + fm.getAscent());
                            }
                        }
                    }
                }
            }
        }
    }
}
