package ru.romansky.labyrinthTest;

/**
 * Created by Vadim on 24.07.2018.
 */
public abstract class Border {
    public boolean myExists;
    public Border(boolean ex){
        myExists = ex;
    }
    public abstract void print();
    public abstract boolean exists();
}
