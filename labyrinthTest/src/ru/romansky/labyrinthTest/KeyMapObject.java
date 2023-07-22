package ru.romansky.labyrinthTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class KeyMapObject extends PortableMapObject {
    boolean isTrue;

    public KeyMapObject(boolean isTrueValue){
        isTrue = isTrueValue;
    }

    @Override
    public void print() {
        System.out.print('K');
    }

    @Override
    public void paint(Graphics g, int cellx, int celly) {
        try {
            Image img= ImageIO.read(getClass().getResource("/key.png"));
            g.drawImage(img, cellx - 9, celly - 9, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
