package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Vadim on 24.07.2018.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        //MapGenerator mapGenerator = new MapGenerator(10,10);
        //LabyrinthMap map = mapGenerator.generateMap();
        //map.print();
        //System.in.read();
        MainWindow window = new MainWindow();
        window.show();
    }
}
