package ru.romansky.labyrinthTest;

import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class LabEngine implements Runnable {
    Character character;
    LabyrinthMap map;

    int characterx;
    int charactery;

    LinkedBlockingQueue<GameEvent> fromClientToServer;
    LinkedBlockingQueue<ServerEvent> fromServerToClient;
    ClassicGamePanel myGamePanel;

    LabEngine(LabyrinthMap m, LinkedBlockingQueue<GameEvent> queue1, LinkedBlockingQueue<ServerEvent> queue2, ClassicGamePanel panel){
        map = m;
        myGamePanel = panel;

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
                    //ServerEvent serverEvent = handleGameEvent(gameEvent);
                    //System.out.println("not null event");
                    //fromServerToClient.add(serverEvent);
                    //notifyAll();
                    if(myGamePanel.gameState == GameState.NORMAL) {
                        if (gameEvent instanceof KeyGameEvent) {
                            int key = ((KeyGameEvent) gameEvent).keyCode;
                            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
                                myGamePanel.shootBullet(key);
                                //return;
                            } else if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
                                myGamePanel.moveCharacter(key);
                            }
                        }
                    } else if(myGamePanel.gameState == GameState.DIALOG_ONE_KEY){
                        if (gameEvent instanceof DialogGameEvent) {
                            int number = ((DialogGameEvent) gameEvent).chosenNumber;
                            myGamePanel.handleDialog(number);
                        }
                    }
                }
            }
        }
    }
}
