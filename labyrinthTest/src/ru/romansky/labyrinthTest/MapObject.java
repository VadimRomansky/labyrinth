package ru.romansky.labyrinthTest;

import java.awt.*;

/**
 * Created by Vadim on 24.07.2018.
 */
public abstract class MapObject {
    public abstract void print();
    public abstract void paint(Graphics g, int cellx, int celly);
}
