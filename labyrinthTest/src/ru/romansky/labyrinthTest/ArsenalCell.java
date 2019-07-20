package ru.romansky.labyrinthTest;

import java.awt.*;

public class ArsenalCell extends Cell {
    public ArsenalCell(int xv, int yv, int id) {
        super(xv, yv, id);
        type = CellType.ARSENAL;
    }

    public ArsenalCell(Cell cell){
        super(cell.x, cell.y, cell.setId);
        this.connectedCells.addAll(cell.connectedCells);
        this.characters.addAll(cell.characters);
        this.minotaur = cell.minotaur;
        type = CellType.ARSENAL;
    }

    public void paint(Graphics g, int cellx, int celly) {
        String text = "A";
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.setColor(Color.BLUE);
        g2d.drawString(text, cellx - textWidth/2, celly -  textHeight/2 + fm.getAscent());
        g2d.setColor(Color.BLACK);
        paintObjects(g, cellx, celly);
    }
}
