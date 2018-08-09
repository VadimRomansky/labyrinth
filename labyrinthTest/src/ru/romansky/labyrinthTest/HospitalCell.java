package ru.romansky.labyrinthTest;

import java.awt.*;

public class HospitalCell extends Cell {
    public HospitalCell(int xv, int yv, int id) {
        super(xv, yv, id);
        type = CellType.HOSPITAL;
    }

    public HospitalCell(Cell cell){
        super(cell.x, cell.y, cell.setId);
        this.connectedCells.addAll(cell.connectedCells);
        this.myObjects.addAll(cell.myObjects);
        type = CellType.HOSPITAL;
    }

    public void paint(Graphics g, int cellx, int celly) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        int length = MapPanel.cellWidth*3/4;
        int width = MapPanel.cellWidth*1/4;
        g2d.fillRect(cellx - length/2, celly - width/2, length, width);
        g2d.fillRect(cellx - width/2, celly - length/2, width, length);
        g2d.setColor(Color.BLACK);
        paintObjects(g, cellx, celly);
    }
}
