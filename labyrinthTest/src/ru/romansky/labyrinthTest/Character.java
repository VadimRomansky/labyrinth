package ru.romansky.labyrinthTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Vadim on 09.08.2018.
 */
public class Character extends MapObject {
    @Override
    public void print() {
        System.out.print('C');
    }

    @Override
    public void paint(Graphics g, int cellx, int celly)  {
        try {
            Image img= ImageIO.read(new File("res/character.png"));
            g.drawImage(img, cellx - 5, celly - 9, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
