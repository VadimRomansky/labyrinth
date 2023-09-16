package ru.romansky.labyrinthTest;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

enum GameState {NORMAL, GAME_OVER, DIALOG_ONE_KEY, DIALOG_MULTI_KEY}

public class LabEngine implements Runnable {
    GameState gameState;
    Character character;
    LabyrinthMap map;

    int characterx;
    int charactery;

    LinkedBlockingQueue<GameEvent> fromClientToServer;
    LinkedBlockingQueue<ServerEvent> fromServerToClient;

    LabEngine(LabyrinthMap m, LinkedBlockingQueue<GameEvent> queue1, LinkedBlockingQueue<ServerEvent> queue2){
        map = m;
        gameState = GameState.NORMAL;

        fromClientToServer = queue1;
        fromServerToClient = queue2;


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
                /*try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                GameEvent gameEvent = null;
                try {
                    gameEvent = fromClientToServer.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("take");
                if(gameEvent != null) {
                    ServerEvent serverEvent = handleGameEvent(gameEvent);
                    System.out.println("not null event");
                    fromServerToClient.add(serverEvent);
                    notifyAll();
                }
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
