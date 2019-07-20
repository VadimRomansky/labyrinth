package ru.romansky.labyrinthTest;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class PortalCell extends Cell {
    int number;
    int portalx;
    int portaly;
    PortalCell next;
    PortalCell prev;
    public PortalCell(int n, int xv, int yv, int id) {
        super(xv, yv, id);
        number = n;
        type = CellType.PORTAL;
    }

    public PortalCell(int n, Cell cell){
        super(cell.x, cell.y, cell.setId);
        this.connectedCells.addAll(cell.connectedCells);
        this.characters.addAll(cell.characters);
        this.minotaur = cell.minotaur;
        number = n;
        type = CellType.PORTAL;
    }

    public void setPortalCoordinates(int px, int py){
        portalx = px;
        portaly = py;
    }

    public void setNumber(int n){
        number = n;
    }

    public void paint(Graphics g, int cellx, int celly) {
        int radius = (MapPanel.cellWidth*2)/6;
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawOval(cellx - radius, celly - radius, 2*radius, 2*radius);
        g2d.fill(new Ellipse2D.Double(cellx - radius, celly - radius, 2*radius, 2*radius));
        g2d.setColor(Color.WHITE);
        String text = Integer.toString(number);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.drawString(text, cellx - textWidth/2, celly -  textHeight/2 + fm.getAscent());
        g2d.setColor(Color.BLACK);
        paintObjects(g, cellx, celly);
    }
}
