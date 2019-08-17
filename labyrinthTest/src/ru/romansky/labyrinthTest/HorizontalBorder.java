package ru.romansky.labyrinthTest;

/**
 * Created by Vadim on 24.07.2018.
 */
public class HorizontalBorder extends Border {

    public HorizontalBorder(BorderState state) {
        super(state);
    }

    public HorizontalBorder(HorizontalBorder horizontalBorder) {
        super(horizontalBorder.state());
    }

    @Override
    public void print() {
        if(myState == BorderState.EXISTS){
            System.out.print('-');
        } else if(myState == BorderState.NOTEXISTS){
            System.out.print(' ');
        } else {
            System.out.print('~');
        }
    }

    @Override
    public BorderState state() {
        return myState;
    }

}
