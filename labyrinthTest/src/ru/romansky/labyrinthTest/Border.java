package ru.romansky.labyrinthTest;

/**
 * Created by Vadim on 24.07.2018.
 */
enum BorderState {EXISTS, NOTEXISTS, UNDEFINED, DOOR}

public abstract class Border {
    protected BorderState myState;

    public Border(BorderState state) {
        myState = state;
    }

    public void setState(BorderState state){
        myState = state;
    }

    public boolean exists(){
        return myState == BorderState.EXISTS;
    }

    public boolean undefined(){
        return myState == BorderState.UNDEFINED;
    }

    public boolean notExists(){
        return myState == BorderState.NOTEXISTS;
    }

    public abstract void print();
    public abstract BorderState state();
}
