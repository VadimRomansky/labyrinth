package ru.romansky.labyrinthTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Vadim on 09.08.2018.
 */
public class Minotaur extends MapObject {
    private boolean alive = true;
    private boolean supposedAlive = true;

    public Minotaur(){

    }

    public Minotaur(Minotaur minotaur) {
        alive = minotaur.alive;
        supposedAlive = minotaur.supposedAlive;
    }

    public boolean isAlive(){
        return alive;
    }

    public boolean isSupposedAlive(){
        return alive;
    }

    @Override
    public void print() {
        System.out.print('M');
    }

    @Override
    public void paint(Graphics g, int cellx, int celly) {
        try {
            URL file;
            if(supposedAlive) {
                file = getClass().getResource("/minotaur.png");
            } else {
                file = getClass().getResource("/dead_minotaur.png");
            }
            Image img= ImageIO.read(file);
            g.drawImage(img, cellx - 9, celly - 9, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        alive = false;
    }

    public void confirmKill(){
        supposedAlive = alive;
    }
}
