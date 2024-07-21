package ru.romansky.labyrinthTest;

public class MobTurnGameEvent extends GameEvent {
    int direction;
    MobTurnGameEvent(int number) {direction = number;}
}
