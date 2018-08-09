package ru.romansky.labyrinthTest;

/**
 * Created by Vadim on 24.07.2018.
 */
public class HorizontalBorder extends Border {
    public HorizontalBorder(boolean ex) {
        super(ex);
    }

    @Override
    public void print() {
        if(exists()) {
            System.out.print('-');
        } else {
            System.out.print(' ');
        }
    }

    @Override
    public boolean exists() {
        return myExists;
    }
}
