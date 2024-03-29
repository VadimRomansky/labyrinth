package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Vadim on 24.07.2018.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        //MapGenerator mapGenerator = new MapGenerator(10,10);
        //LabyrinthMap map = mapGenerator.generateMap();
        //map.print();
        //System.in.read();
        //FileWriter fileWriter = new FileWriter("a.txt");
        //PrintWriter printWriter = new PrintWriter(fileWriter);
        //printWriter.print("Some String");
        //printWriter.printf("Product name is %s and its price is %d $", "iPhone", 1000);
        //printWriter.close();
        //MainWindow window = new MainWindow();
        NewMainWindow window = new NewMainWindow();
        window.show();
        Thread UIthread = new Thread(new Runnable() {
            @Override
            public void run() {
                int fps = 60;
                synchronized (this) {
                    try{
                        while(true) {
                            wait(1000 / fps);
                            window.repaint();
                        }
                        } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        UIthread.start();
    }
}
