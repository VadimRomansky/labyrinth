package ru.romansky.labyrinthTest;

import java.util.Vector;
import java.util.concurrent.SynchronousQueue;

enum GameState {NORMAL, GAME_OVER, DIALOG_ONE_KEY, DIALOG_MULTI_KEY}

public class LabEngine implements Runnable {
    GameState gameState;
    Character character;
    LabyrinthMap map;

    int characterx;
    int charactery;

    SynchronousQueue<GameEvent> fromClientToServer;
    SynchronousQueue<ServerEvent> fromServerToClient;

    LabEngine(LabyrinthMap m){
        map = m;
        gameState = GameState.NORMAL;


        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                Vector<MapObject> objects = map.cells[i][j].characters;
                for (MapObject object :
                        objects) {
                    if (object instanceof Character){
                        character = (Character) object;
                        //objects.removeElement(object);
                        characterx = i;
                        charactery = j;
                        break;
                    }
                }

            }
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                GameEvent gameEvent = fromClientToServer.poll();

                ServerEvent serverEvet = handleGameEvent(gameEvent);

                try {
                    fromServerToClient.put(serverEvet);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                notify();
            }
        }
    }

    ServerEvent handleGameEvent(GameEvent gameEvent){
        if(gameEvent instanceof KeyGameEvent){
            if(gameState == GameState.NORMAL){
                return handleKeyGameEvent((KeyGameEvent) gameEvent);
            }
        } else if(gameEvent instanceof  DialogGameEvent){
            if(gameState == GameState.DIALOG_ONE_KEY || gameState == GameState.DIALOG_MULTI_KEY){
                return handleDialogGameEvent(gameEvent);
            }
        }

        return new EmptyServerEvent();
    }

    private ServerEvent handleDialogGameEvent(GameEvent gameEvent) {
        return null;
    }

    private ServerEvent handleKeyGameEvent(KeyGameEvent gameEvent) {
        return null;
    }

}
