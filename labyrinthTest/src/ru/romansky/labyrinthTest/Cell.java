package ru.romansky.labyrinthTest;


import javafx.util.Pair;

import java.awt.*;
import java.util.Vector;

/**
 * Created by Vadim on 24.07.2018.
 */
enum CellType { SIMPLE_CELL, ARSENAL, HOSPITAL, PORTAL}

public class Cell {
    int x;
    int y;
    int setId;
    CellType type;
    Vector<Pair<Integer, Integer>> connectedCells;

    Vector<MapObject> characters;
    Minotaur minotaur = null;

    public Cell(int xv, int yv, int id){
        x = xv;
        y = yv;
        setId = id;
        connectedCells = new Vector<>();
        characters = new Vector<>();
        type = CellType.SIMPLE_CELL;
    }

    public  void print(){
        System.out.print(' ');
    }

    public void paint(Graphics g, int cellx, int celly) {
        String text = Integer.toString(setId);
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.drawString(text, cellx - textWidth/2, celly -  textHeight/2 + fm.getAscent());
        paintObjects(g, cellx, celly);
    }

    public void paintObjects(Graphics g, int cellx, int celly){
        if(minotaur != null){
            minotaur.paint(g, cellx, celly);
        }
        for (MapObject object : characters) {
            object.paint(g, cellx, celly);
        }
    }

    public void addCharacter(MapObject object) {
        characters.add(object);
    }

    boolean hasMinotaurus(){
        return minotaur != null;
    }
}
