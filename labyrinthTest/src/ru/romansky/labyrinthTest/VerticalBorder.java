package ru.romansky.labyrinthTest;

/**
 * Created by Vadim on 24.07.2018.
 */
public class VerticalBorder extends  Border {

    public VerticalBorder(BorderState state) {
        super(state);
    }

    public VerticalBorder(VerticalBorder verticalBorder) {
        super(verticalBorder.state());
    }

    @Override
    public void print(){
        if(myState == BorderState.EXISTS){
            System.out.print('|');
        } else if(myState == BorderState.NOTEXISTS){
            System.out.print(' ');
        } else {
            System.out.print(';');
        }
    }

    @Override
    public BorderState state() {
        return myState;
    }
}
