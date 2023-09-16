package ru.romansky.labyrinthTest;

public class KeyGameEvent extends GameEvent {
    int keyCode;

    KeyGameEvent(int key){
        keyCode = key;
    }
}
