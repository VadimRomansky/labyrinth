package ru.romansky.labyrinthTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class KeyMapObject extends PortableMapObject {
    boolean isTrue;
    String color;

    public KeyMapObject(boolean isTrueValue, int colorN){
        isTrue = isTrueValue;
        if(colorN < 0 || colorN > 3){
            throw new RuntimeException("wrong key number");
        }
        switch (colorN){
            case 0:
                color = "Yellow";
                break;
            case 1:
                color = "Red";
                break;
            case 2:
                color = "Green";
                break;
            case 3:
                color = "Blue";
                break;
        }
    }

    @Override
    public void print() {
        System.out.print('K');
    }

    @Override
    public void paint(Graphics g, int cellx, int celly) {
        try {
            Image img= ImageIO.read(getClass().getResource("/key" + color + ".png"));
            g.drawImage(img, cellx - 9, celly - 9, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
