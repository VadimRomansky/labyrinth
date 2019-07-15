package ru.romansky.labyrinthTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Vadim on 09.08.2018.
 */
public class Minotaur extends MapObject {
    private boolean alive = true;

    public boolean isAlive(){
        return alive;
    }

    @Override
    public void print() {
        System.out.print('M');
    }

    @Override
    public void paint(Graphics g, int cellx, int celly) {
        try {
            File file = new File("res/minotaur.png");
            Image img= ImageIO.read(file);
            g.drawImage(img, cellx - 9, celly - 9, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
