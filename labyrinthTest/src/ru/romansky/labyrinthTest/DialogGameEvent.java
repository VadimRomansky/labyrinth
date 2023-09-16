package ru.romansky.labyrinthTest;

public class DialogGameEvent extends GameEvent {
    int chosenNumber;
    DialogGameEvent(int number){
        chosenNumber = number;
    }
}
