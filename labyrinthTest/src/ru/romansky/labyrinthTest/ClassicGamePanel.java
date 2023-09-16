package ru.romansky.labyrinthTest;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import static java.lang.Thread.sleep;

public class ClassicGamePanel extends MapPanelBase {
    private boolean isShowingDialog = true;
    private String dialogText = "You found a key. Pick it up?";
    boolean dialogResult;
    private boolean dragMiniMap = false;
    private LabyrinthMap draggedMap;
    private List<LabyrinthMap> draggedList;
    private JTextArea myTextArea;
    private List<MiniMapPanel> myMiniMapList;
    private int textPosition = 0;
    int stepNumber = 0;
    boolean gameOver = false;
    Character character;
    LabyrinthMap visibleMap;
    List<LabyrinthMap> additionalMapList;
    int realCharacterx = -1;
    int realCharactery = -1;
    int visibleCharacterx = -1;
    int visibleCharactery = -1;
    int portalsPassing = 0;
    int mapShiftX;
    int mapShiftY;
    public static final Font smallFont = new Font("TimesRoman", Font.PLAIN, 14);;
    public static final Font microFont = new Font("TimesRoman", Font.PLAIN, 10);;
    public static final Font hugeFont = new Font("TimesRoman", Font.PLAIN, 48);;
    public static final Font middleFont = new Font("TimesRoman", Font.PLAIN, 24);;
    private JPanel myMiniMapPanel;
    LinkedBlockingQueue<GameEvent> fromClientToServer;
    LinkedBlockingQueue<ServerEvent> fromServerToClient;

    public ClassicGamePanel(JFrame frame, JPanel parent, LinkedBlockingQueue<GameEvent> queue1, LinkedBlockingQueue<ServerEvent> queue2) {
        this.myFrame = frame;
        this.myParent = parent;
        fromClientToServer = queue1;
        fromServerToClient = queue2;
        myMap = null;
        visibleMap = null;
        setBackground(Color.WHITE);
        additionalMapList = new LinkedList<>();
        setMouseDialogListener();
    }
    public ClassicGamePanel(JFrame frame, JPanel parent, LabyrinthMap map, LinkedBlockingQueue<GameEvent> queue1, LinkedBlockingQueue<ServerEvent> queue2) {
        this.myFrame = frame;
        this.myParent = parent;
        fromClientToServer = queue1;
        fromServerToClient = queue2;
        myMap = map;
        visibleMap = new LabyrinthMap(2*myMap.width-1, 2*myMap.height-1);
        additionalMapList = new LinkedList<>();

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height; ++j){
                Vector<MapObject> objects = myMap.cells[i][j].characters;
                for (MapObject object :
                        objects) {
                    if (object instanceof Character){
                        realCharacterx = i;
                        realCharactery = j;
                        character = (Character) object;
                        objects.removeElement(object);
                        break;
                    }
                }

            }
        }
        visibleCharacterx = myMap.width - 1;
        visibleCharactery = myMap.height - 1;
        visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
        visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
        mapShiftX = visibleCharacterx - realCharacterx;
        mapShiftY = visibleCharactery - realCharactery;

        if(realCharacterx >=0 && realCharacterx < myMap.width && realCharactery > 0 && realCharactery < myMap.height){
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
        }

        for(int i = 0; i < visibleMap.width+1; ++i){
            for(int j = 0; j < visibleMap.height; ++j){
                visibleMap.verticalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }

        for(int i = 0; i < visibleMap.width; ++i){
            for(int j = 0; j < visibleMap.height+1; ++j){
                visibleMap.horizontalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }


        setMouseDialogListener();
    }

    public void resetMap(LabyrinthMap map){
        myMap = map;
        visibleMap = new LabyrinthMap(2*myMap.width-1, 2*myMap.height-1);

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height; ++j){
                Vector<MapObject> objects = myMap.cells[i][j].characters;
                for (MapObject object :
                        objects) {
                    if (object instanceof Character){
                        realCharacterx = i;
                        realCharactery = j;
                        character = (Character) object;
                        objects.removeElement(object);
                        break;
                    }
                }

            }
        }
        visibleCharacterx = myMap.width - 1;
        visibleCharactery = myMap.height - 1;
        visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
        visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
        mapShiftX = visibleCharacterx - realCharacterx;
        mapShiftY = visibleCharactery - realCharactery;

        if(realCharacterx >=0 && realCharacterx < myMap.width && realCharactery > 0 && realCharactery < myMap.height){
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
        }

        for(int i = 0; i < visibleMap.width+1; ++i){
            for(int j = 0; j < visibleMap.height; ++j){
                visibleMap.verticalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }

        for(int i = 0; i < visibleMap.width; ++i){
            for(int j = 0; j < visibleMap.height+1; ++j){
                visibleMap.horizontalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }
    }

    public void resetVisibleMap(){
        additionalMapList.clear();
        visibleMap = new LabyrinthMap(2*myMap.width-1, 2*myMap.height-1);


        visibleCharacterx = myMap.width - 1;
        visibleCharactery = myMap.height - 1;
        visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
        visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
        mapShiftX = visibleCharacterx - realCharacterx;
        mapShiftY = visibleCharactery - realCharactery;

        if(realCharacterx >=0 && realCharacterx < myMap.width && realCharactery > 0 && realCharactery < myMap.height){
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
        }

        for(int i = 0; i < visibleMap.width+1; ++i){
            for(int j = 0; j < visibleMap.height; ++j){
                visibleMap.verticalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }

        for(int i = 0; i < visibleMap.width; ++i){
            for(int j = 0; j < visibleMap.height+1; ++j){
                visibleMap.horizontalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(!gameOver) {
            if(!isShowingDialog) {
                int key = e.getKeyCode();

                fromClientToServer.add(new KeyGameEvent(key));
                System.out.println("put");
                //notifyAll();

                /*if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
                    shootBullet(key);
                    return;
                }
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
                    moveCharacter(key);
                    return;
                }*/

            }
        }
    }



    private void shootBullet(int key) {
        if(character.bulletCount <= 0){
            return;
        }
        String text = "You shot";
        character.bulletCount--;
        int bulletx = realCharacterx;
        int bullety = realCharactery;
        Direction direction = Direction.DOWN;//todo
        if(key == KeyEvent.VK_DOWN){
            direction = Direction.DOWN;
            text = text + " down.";
        } else if (key == KeyEvent.VK_UP){
            direction = Direction.UP;
            text = text + " up.";
        } else if (key == KeyEvent.VK_LEFT){
            direction = Direction.LEFT;
            text = text + " left.";
        } else if (key == KeyEvent.VK_RIGHT){
            direction = Direction.RIGHT;
            text = text + " right.";
        }
        boolean bulletHit = false;
        while(isMovePossible(direction, bulletx,bullety)){
            if(direction == Direction.DOWN) {
                bullety ++;
            }
            if(direction == Direction.UP) {
                bullety --;
            }
            if(direction == Direction.LEFT) {
                bulletx --;
            }
            if(direction == Direction.RIGHT) {
                bulletx ++;
            }
            if(myMap.cells[bulletx][bullety].minotaur != null){
                if(myMap.cells[bulletx][bullety].minotaur.isAlive()){
                    text = text + " You killed the minotaur.";
                    myMap.aliveMinotaursCount--;
                } else {
                    text = text + " The bullet hit the minotaur's corpse.";
                }
                myMap.cells[bulletx][bullety].minotaur.kill();
                bulletHit = true;
                break;
            }
            //todo characters

        }
        if(!bulletHit){
            text = text + " The bullet hit the wall.";
        }
        writeTextMessage(text);
        //repaint();
    }

    private void writeTextMessage(String text) {
        myTextArea.insert("\n", textPosition);
        textPosition++;
        myTextArea.insert(text, textPosition);
        textPosition += text.length();
        myTextArea.setCaretPosition(textPosition);
    }

    private void moveCharacter(int key) {
        if(visibleMap == null){
            return;
        }
        Direction direction = Direction.DOWN;//todo;
        stepNumber++;
        String text = stepNumber + ". You moved";
        if(key == KeyEvent.VK_S){
            direction = Direction.DOWN;
            text = text + " down";
        } else if (key == KeyEvent.VK_W){
            direction = Direction.UP;
            text = text + " up";
        } else if (key == KeyEvent.VK_A){
            direction = Direction.LEFT;
            text = text + " left";
        } else if (key == KeyEvent.VK_D){
            direction = Direction.RIGHT;
            text = text + " right";
        }
        if (isMovePossible(direction, realCharacterx, realCharactery)){
            setBoundaryStateVisible(direction);
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.removeElement(character);
            if(direction == Direction.DOWN) {
                realCharactery ++;
                visibleCharactery++;
            }
            if(direction == Direction.UP) {
                realCharactery --;
                visibleCharactery--;
            }
            if(direction == Direction.LEFT) {
                realCharacterx --;
                visibleCharacterx--;
            }
            if(direction == Direction.RIGHT) {
                realCharacterx ++;
                visibleCharacterx++;
            }
            Vector<MapObject> objects = myMap.cells[realCharacterx][realCharactery].characters;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.clear();
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
            visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(objects);
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
            for(PortableMapObject object : myMap.cells[realCharacterx][realCharactery].mapObjects){
                if(! visibleMap.cells[visibleCharacterx][visibleCharactery].mapObjects.contains(object)) {
                    visibleMap.cells[visibleCharacterx][visibleCharactery].mapObjects.add(object);
                }
            }
            checkCellConflict(visibleCharacterx, visibleCharactery, myMap.cells[realCharacterx][realCharactery]);
            revalidate();
            //repaint();
            myFrame.revalidate();
            eventAfterMove(text);
            //repaint();
        } else if(checkVictory(direction, realCharacterx, realCharactery, text)) {
            //text = text + " and went out of the labyrinth.";
            //writeTextMessage(text);
            setBoundaryStateVisible(direction);
            celebrateVictory();
        } else {
            //text = text + " and bumped into the wall.";
            //writeTextMessage(text);
            setBoundaryStateVisible(direction);
            //repaint();
        }
    }

    private void checkCellConflict(int i, int j,Cell cell) {
        for (LabyrinthMap map :
                additionalMapList) {
            if(map.cells[i][j].state != CellState.UNDEFINED){
                if(map.cells[i][j].type != cell.type){
                    additionalMapList.remove(map);
                } else {
                    if(map.cells[i][j].minotaur == null){
                        if(cell.minotaur != null){
                            additionalMapList.remove(map);
                        }
                    } else {
                        if(cell.minotaur == null){
                            additionalMapList.remove(map);
                        } else {
                            if(!map.cells[i][j].minotaur.isAlive()){
                                if(cell.minotaur.isAlive()){
                                    additionalMapList.remove(map);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void celebrateVictory() {
        gameOver = true;
        //repaint();
    }

    private boolean checkVictory(Direction direction, int currentx, int currenty, String text) {
        if(direction == Direction.DOWN) {
            if(myMap.horizontalBorders[currentx][currenty + 1].state() != BorderState.DOOR){
                text = text.concat(" and bumped into the wall");
                writeTextMessage(text);
                return false;
            }
        }
        if(direction == Direction.UP) {
            if(myMap.horizontalBorders[currentx][currenty].state() != BorderState.DOOR){
                text = text.concat(" and bumped into the wall");
                writeTextMessage(text);
                return false;
            }
        }
        if(direction == Direction.LEFT) {
            if(myMap.verticalBorders[currentx][currenty].state() != BorderState.DOOR) {
                text = text.concat(" and bumped into the wall");
                writeTextMessage(text);
                return false;
            }
        }
        if(direction == Direction.RIGHT) {
            if(myMap.verticalBorders[currentx+1][currenty].state() != BorderState.DOOR) {
                text = text.concat(" and bumped into the wall");
                writeTextMessage(text);
                return false;
            }
        }

        text = text.concat(" and found the door");
        if(character.myKey == null){
            text = text.concat(" but it is locked");
            writeTextMessage(text);
            //repaint();
            return false;
        }

        if(!character.myKey.isTrue){
                text = text.concat(" but you have a wrong key");
                writeTextMessage(text);
                //repaint();
                return false;
        }

        text = text.concat(" and went out of the labyrinth, congrats!");
        writeTextMessage(text);
        //repaint();


        //todo
        return true;
    }

    private void setBoundaryStateVisible(Direction direction) {
        if(direction == Direction.DOWN) {
            visibleMap.horizontalBorders[visibleCharacterx][visibleCharactery + 1].setState(myMap.horizontalBorders[realCharacterx][realCharactery + 1].state());
            checkHorizontalBorderConflictWithAdditional(visibleCharacterx, visibleCharactery+1);
        }
        if(direction == Direction.UP) {
            visibleMap.horizontalBorders[visibleCharacterx][visibleCharactery].setState(myMap.horizontalBorders[realCharacterx][realCharactery].state());
            checkHorizontalBorderConflictWithAdditional(visibleCharacterx, visibleCharactery);
        }
        if(direction == Direction.LEFT) {
            visibleMap.verticalBorders[visibleCharacterx][visibleCharactery].setState(myMap.verticalBorders[realCharacterx][realCharactery].state());
            checkVerticalBorderConflictWithAdditional(visibleCharacterx, visibleCharactery);
        }
        if(direction == Direction.RIGHT) {
            visibleMap.verticalBorders[visibleCharacterx+1][visibleCharactery].setState(myMap.verticalBorders[realCharacterx+1][realCharactery].state());
            checkVerticalBorderConflictWithAdditional(visibleCharacterx+1, visibleCharactery);
        }
    }

    private void checkHorizontalBorderConflictWithAdditional(int i, int j) {
        for (LabyrinthMap map :
                additionalMapList) {
            if(map.horizontalBorders[i][j].state() != BorderState.UNDEFINED && visibleMap.horizontalBorders[i][j].state() != BorderState.UNDEFINED){
                if(map.horizontalBorders[i][j].state() != visibleMap.horizontalBorders[i][j].state()){
                    additionalMapList.remove(map);
                }
            }
        }
    }

    private void checkVerticalBorderConflictWithAdditional(int i, int j) {
        for (LabyrinthMap map :
                additionalMapList) {
            if(map.verticalBorders[i][j].state() != BorderState.UNDEFINED && visibleMap.verticalBorders[i][j].state() != BorderState.UNDEFINED){
                if(map.verticalBorders[i][j].state() != visibleMap.verticalBorders[i][j].state()){
                    additionalMapList.remove(map);
                }
            }
        }
    }

    private boolean isMovePossible(Direction direction, int currentx, int currenty) {
        if(direction == Direction.DOWN) {
            if(myMap.horizontalBorders[currentx][currenty + 1].state() == BorderState.DOOR){
                return false;//todo
            }
            if(myMap.horizontalBorders[currentx][currenty + 1].state() == BorderState.EXISTS){
                return false;
            }
            return true;
        }
        if(direction == Direction.UP) {
            if(myMap.horizontalBorders[currentx][currenty].state() == BorderState.DOOR){
                return false;
            }
            if(myMap.horizontalBorders[currentx][currenty].state() == BorderState.EXISTS){
                return false;
            }
            return true;
        }
        if(direction == Direction.LEFT) {
            if(myMap.verticalBorders[currentx][currenty].state() == BorderState.DOOR) {
                return false;
            }
            if(myMap.verticalBorders[currentx][currenty].state() == BorderState.EXISTS) {
                return false;
            }
            return true;
        }
        if(direction == Direction.RIGHT) {
            if(myMap.verticalBorders[currentx+1][currenty].state() == BorderState.DOOR) {
                return false;
            }
            if(myMap.verticalBorders[currentx+1][currenty].state() == BorderState.EXISTS) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void eventAfterMove(String text) {
        //todo !!!!
        if(myMap.cells[realCharacterx][realCharactery].minotaur != null){
            //visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
            if(myMap.cells[realCharacterx][realCharactery].minotaur.isAlive()){
                /*try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                /*try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                if(character.myKey != null){
                    PortableMapObject key = character.myKey;
                    myMap.cells[realCharacterx][realCharactery].mapObjects.add(key);
                    character.myKey = null;
                }
                visibleMap.cells[visibleCharacterx][visibleCharactery].characters.removeElement(character);
                realCharacterx = myMap.hospitalx;
                realCharactery = myMap.hospitaly;
                putMapToPanel();
                resetVisibleMap();
                visibleCharacterx = realCharacterx + mapShiftX;
                visibleCharactery = realCharactery + mapShiftY;
                character.bulletCount = 0;
                visibleMap.cells[visibleCharacterx][visibleCharactery] = new HospitalCell(visibleCharacterx,visibleCharactery,0);
                visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
                visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
                visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(myMap.cells[realCharacterx][realCharactery].characters);
                text = text + " ,stepped on the minotaur, and he killed you. You woke up in the hospital.";
                writeTextMessage(text);
                //repaint();
                return;
            } else {
                myMap.cells[realCharacterx][realCharactery].minotaur.confirmKill();
                text = text + " and stepped on the minotaur's corpse.";
                writeTextMessage(text);
                //repaint();
                return;
            }

        }
        if(myMap.cells[realCharacterx][realCharactery].type == CellType.ARSENAL){
            visibleMap.cells[visibleCharacterx][visibleCharactery] = new ArsenalCell(visibleCharacterx,visibleCharactery,0);
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
            visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(myMap.cells[realCharacterx][realCharactery].characters);
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
            character.bulletCount = Character.maxBulletCount;
            text = text + " and got into the arsenal.";
            writeTextMessage(text);
            return;
        }
        if(myMap.cells[realCharacterx][realCharactery].type == CellType.HOSPITAL){
            visibleMap.cells[visibleCharacterx][visibleCharactery] = new HospitalCell(visibleCharacterx,visibleCharactery,0);
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
            visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(myMap.cells[realCharacterx][realCharactery].characters);
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
            text = text + " and got into the hospital.";
            writeTextMessage(text);
            return;
        }
        if(myMap.cells[realCharacterx][realCharactery].type == CellType.PORTAL) {
            visibleMap.cells[visibleCharacterx][visibleCharactery] = new PortalCell(((PortalCell)myMap.cells[realCharacterx][realCharactery]).number, visibleCharacterx,visibleCharactery, 0);
            if(portalsPassing == 0){
                ((PortalCell)visibleMap.cells[visibleCharacterx][visibleCharactery]).visibleNumber = ((PortalCell)visibleMap.cells[visibleCharacterx][visibleCharactery]).number;
            }
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
            visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(myMap.cells[realCharacterx][realCharactery].characters);
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
            //repaint();
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.removeElement(character);
            PortalCell next = ((PortalCell)myMap.cells[realCharacterx][realCharactery]).next;
            realCharacterx = next.x;
            realCharactery = next.y;
            putMapToPanel();
            resetVisibleMap();
            visibleCharacterx = realCharacterx + mapShiftX;
            visibleCharactery = realCharactery + mapShiftY;
            visibleMap.cells[visibleCharacterx][visibleCharactery] = new PortalCell(((PortalCell)myMap.cells[realCharacterx][realCharactery]).number, visibleCharacterx,visibleCharactery, 0);
            if(portalsPassing == 0){
                ((PortalCell)visibleMap.cells[visibleCharacterx][visibleCharactery]).visibleNumber = ((PortalCell)visibleMap.cells[visibleCharacterx][visibleCharactery]).number;
            }
            portalsPassing++;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
            visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(myMap.cells[realCharacterx][realCharactery].characters);
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
            text = text + ", fall into the potal and got out from another.";
            writeTextMessage(text);
            //repaint();
            return;
        }
        text = text + " and went through.";
        writeTextMessage(text);
    }

    public void putMapToPanel() {
        int minXindex = 0;
        int minYindex = 0;
        int maxXindex = 0;
        int maxYindex = 0;
        IndexContainer index = defineMinMaxIndex(visibleMap);
        minXindex = index.minXindex;
        maxXindex = index.maxXindex;
        minYindex = index.minYindex;
        maxYindex = index.maxYindex;

        if(additionalMapList.size() > 0){
            int tempMinXindex = 0;
            int tempMinYindex = 0;
            int tempMaxXindex = 0;
            int tempMaxYindex = 0;
            for (LabyrinthMap map :
                    additionalMapList) {
                index = defineMinMaxIndex(map);
                tempMinXindex = index.minXindex;
                tempMaxXindex = index.maxXindex;
                tempMinYindex = index.minYindex;
                tempMaxYindex = index.maxYindex;

                if(tempMinXindex < minXindex){
                    minXindex = tempMinXindex;
                }
                if(tempMaxXindex > maxXindex){
                    maxXindex = tempMaxXindex;
                }
                if(tempMinYindex < minYindex){
                    minYindex = tempMinYindex;
                }
                if(tempMaxYindex > maxYindex){
                    maxYindex = tempMaxYindex;
                }
            }
        }

        int xsize = maxXindex - minXindex + 1;
        int ysize = maxYindex - minYindex + 1;

        List<LabyrinthMap> miniMapList = copyAllMapsLargeToSmall(visibleMap, additionalMapList, minXindex, minYindex, xsize, ysize);


        MiniMapPanel miniMapPanel = new MiniMapPanel(miniMapList);
        int height = (miniMapPanel.getMap().height + 2)*(MiniMapPanel.cellWidth + MiniMapPanel.borderWidth);
        miniMapPanel.setPreferredSize(new Dimension(myMiniMapPanel.getWidth() - 30,height));
        miniMapPanel.setBackground(Color.WHITE);
        setMouseListener(miniMapPanel);
        myMiniMapList.add(miniMapPanel);
        myMiniMapPanel.add(miniMapPanel);
    }

    private void setMouseDialogListener(){
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isShowingDialog){
                    if(SwingUtilities.isLeftMouseButton(e)) {
                        Point point = e.getPoint();
                        int x = point.x;
                        int y = point.y;
                        if(onLeftDialogButton(x,y)){
                            isShowingDialog = false;
                            dialogResult = true;
                            //repaint();
                        } else if (onRightDialogButton(x,y)){
                            isShowingDialog = false;
                            dialogResult = false;
                            //repaint();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private boolean onLeftDialogButton(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        int dialogWidth = width/4;
        int dialogHeight = height/4;
        int buttonWidth = dialogWidth/4;
        int buttonHeight = dialogHeight/4;
        int centerx = width/2;
        int centery = height/2;
        int buttonx = centerx - dialogWidth/4;
        int buttony = centery + dialogHeight/6;

        return ((x > buttonx - buttonWidth/2)&&(x < buttonx + buttonWidth/2)&&(y > buttony - buttonHeight/2)&&(y < buttony + buttonHeight/2));
    }

    private boolean onRightDialogButton(int x, int y) {
        int width = getWidth();
        int height = getHeight();
        int dialogWidth = width/4;
        int dialogHeight = height/4;
        int buttonWidth = dialogWidth/4;
        int buttonHeight = dialogHeight/4;
        int centerx = width/2;
        int centery = height/2;
        int buttonx = centerx + dialogWidth/4;
        int buttony = centery + dialogHeight/6;

        return ((x > buttonx - buttonWidth/2)&&(x < buttonx + buttonWidth/2)&&(y > buttony - buttonHeight/2)&&(y < buttony + buttonHeight/2));
    }

    private void setMouseListener(MiniMapPanel miniMapPanel) {
        miniMapPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    Point point = e.getPoint();
                    int x = point.x;
                    int y = point.y;
                    if (miniMapPanel.mouseOnSplitButton(point)) {
                        stopDragMiniMap();
                        if (miniMapPanel.mapsNumber() > 1) {
                            splitMiniMap(miniMapPanel);
                        }
                    } else if (miniMapPanel.mouseOnDeleteButton(point)) {
                        stopDragMiniMap();
                        myMiniMapList.remove(miniMapPanel);
                        myMiniMapPanel.remove(miniMapPanel);
                    } else {
                        setDragMiniMap(miniMapPanel.getMap(), miniMapPanel.getMaps());
                    }
                } else if(SwingUtilities.isRightMouseButton(e)){
                    Point point = e.getPoint();
                    if(miniMapPanel.mouseOnPortal(point)){
                        Pair<Integer, Integer> coordinates = miniMapPanel.getSelectedCellCoordinates(point);
                        String string = JOptionPane.showInputDialog(ClassicGamePanel.this,
                                "Input number", null);
                        try {
                            int number = Integer.parseInt(string);
                            miniMapPanel.setPortalNumber(coordinates.getKey(), coordinates.getValue(), number);
                        } catch (NumberFormatException exception){

                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseClicked(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private void splitMiniMap(MiniMapPanel miniMapPanel) {
        myMiniMapList.remove(miniMapPanel);
        myMiniMapPanel.remove(miniMapPanel);
        List<LabyrinthMap> maps = miniMapPanel.getMaps();
        for (LabyrinthMap map :
                maps) {
            IndexContainer index = defineMinMaxIndex(map);
            int xsize = index.maxXindex - index.minXindex + 1;
            int ysize = index.maxYindex - index.minYindex + 1;
            LabyrinthMap newMap = copyMapLargeToSmall(map, index.minXindex, index.minYindex, xsize, ysize);
            MiniMapPanel tempMapPanel = new MiniMapPanel(newMap);
            int height = (newMap.height + 2)*(MiniMapPanel.cellWidth + MiniMapPanel.borderWidth);
            tempMapPanel.setPreferredSize(new Dimension(myMiniMapPanel.getWidth() - 30,height));
            tempMapPanel.setBackground(Color.WHITE);
            setMouseListener(tempMapPanel);
            myMiniMapList.add(tempMapPanel);
            myMiniMapPanel.add(tempMapPanel);
        }
        myMiniMapPanel.revalidate();
    }

    private IndexContainer defineMinMaxIndex(LabyrinthMap map){
        int minXindex = 0;
        int maxXindex = 0;
        int minYindex = 0;
        int maxYindex = 0;
        for(int i = 0; i < map.width; ++i){
            boolean minIndexSet = false;
            for(int j = 0; j < map.height; ++j){
                if ((map.verticalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (map.horizontalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (map.cells[i][j].state != CellState.UNDEFINED)) {
                    minIndexSet = true;
                    break;
                }
            }
            if(minIndexSet){
                minXindex = i;
                break;
            }
        }

        for(int j = 0; j < map.height; ++j){
            boolean minIndexSet = false;
            for(int i = 0; i < map.width; ++i){
                if ((map.verticalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (map.horizontalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (map.cells[i][j].state != CellState.UNDEFINED)) {
                    minIndexSet = true;
                    break;
                }
            }
            if(minIndexSet){
                minYindex = j;
                break;
            }
        }

        for(int i = map.width; i > 0; --i){
            boolean maxIndexSet = false;
            for(int j = 0; j < map.height; ++j){
                if ((map.verticalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (map.horizontalBorders[i-1][j].state() != BorderState.UNDEFINED) ||
                        (map.cells[i-1][j].state != CellState.UNDEFINED)) {
                    maxIndexSet = true;
                    break;
                }
            }
            if(maxIndexSet){
                maxXindex = i-1;
                break;
            }
        }

        for(int j = map.height; j > 0; --j){
            boolean maxIndexSet = false;
            for(int i = 0; i < map.width; ++i){
                if ((map.verticalBorders[i][j-1].state() != BorderState.UNDEFINED) ||
                        (map.horizontalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (map.cells[i][j-1].state != CellState.UNDEFINED)) {
                    maxIndexSet = true;
                    break;
                }
            }
            if(maxIndexSet){
                maxYindex = j-1;
                break;
            }
        }

        return new IndexContainer(minXindex, maxXindex, minYindex, maxYindex);
    }

    private LabyrinthMap copyMapLargeToSmall(LabyrinthMap map, int minXindex, int minYindex, int xsize, int ysize) {
        LabyrinthMap newMap = new LabyrinthMap(xsize, ysize);

        for(int i = 0; i < newMap.width; ++i){
            for(int j = 0; j < newMap.height + 1; ++j){
                newMap.horizontalBorders[i][j].setState(map.horizontalBorders[i+minXindex][j + minYindex].state());
            }
        }

        for(int i = 0; i < newMap.width + 1; ++i){
            for(int j = 0; j < newMap.height; ++j){
                newMap.verticalBorders[i][j].setState(map.verticalBorders[i+minXindex][j+minYindex].state());
            }
        }

        for(int i = 0; i < newMap.width; ++i){
            for(int j = 0; j < newMap.height; ++j){
                if(map.cells[i+minXindex][j+minYindex].type == CellType.PORTAL){
                    newMap.cells[i][j] = new PortalCell(((PortalCell)map.cells[i+minXindex][j+minYindex]).number, ((PortalCell)map.cells[i+minXindex][j+minYindex]).visibleNumber, i, j, 0);
                }
                if(map.cells[i+minXindex][j+minYindex].type == CellType.ARSENAL){
                    newMap.cells[i][j] = new ArsenalCell(i,j,0);
                }
                if(map.cells[i+minXindex][j+minYindex].type == CellType.HOSPITAL){
                    newMap.cells[i][j] = new HospitalCell(i, j, 0);
                }
                if(map.cells[i+minXindex][j+minYindex].minotaur != null){
                    newMap.cells[i][j].minotaur = new Minotaur(map.cells[i+minXindex][j+minYindex].minotaur);
                }
                newMap.cells[i][j].state = map.cells[i+minXindex][j+minYindex].state;
            }
        }

        return newMap;
    }

    private List<LabyrinthMap> copyAllMapsLargeToSmall(LabyrinthMap map, List<LabyrinthMap> mapList, int minXindex, int minYindex, int xsize, int ysize) {
        List<LabyrinthMap> result = new LinkedList<>();
        if(!mapCoveredByList(map, mapList, minXindex, minYindex, xsize, ysize)) {
            result.add(copyMapLargeToSmall(map, minXindex, minYindex, xsize, ysize));
        }
        for (LabyrinthMap tempMap :
                mapList) {
            result.add(copyMapLargeToSmall(tempMap, minXindex, minYindex, xsize, ysize));
        }
        return result;
    }

    private boolean mapCoveredByList(LabyrinthMap map, List<LabyrinthMap> mapList, int minXindex, int minYindex, int xsize, int ysize) {
        for(int i = 0; i < xsize; ++i){
            for(int j = 0; j <= ysize; ++j){
                if(map.horizontalBorders[i+minXindex][j+minYindex].state() != BorderState.UNDEFINED){
                    boolean covered = false;
                    for (LabyrinthMap coveringMap :
                        mapList) {
                        if(coveringMap.horizontalBorders[i+minXindex][j+minYindex].state() != BorderState.UNDEFINED){
                            covered = true;
                        }
                    }
                    if(!covered){
                        return false;
                    }
                }
            }
        }

        for(int i = 0; i <= xsize; ++i){
            for(int j = 0; j < ysize; ++j){
                if(map.verticalBorders[i+minXindex][j+minYindex].state() != BorderState.UNDEFINED){
                    boolean covered = false;
                    for (LabyrinthMap coveringMap :
                            mapList) {
                        if(coveringMap.verticalBorders[i+minXindex][j+minYindex].state() != BorderState.UNDEFINED){
                            covered = true;
                        }
                    }
                    if(!covered){
                        return false;
                    }
                }
            }
        }

        for(int i = 0; i < xsize; ++i){
            for(int j = 0; j < ysize; ++j){
                if(map.cells[i+minXindex][j+minYindex].state != CellState.UNDEFINED){
                    boolean covered = false;
                    for (LabyrinthMap coveringMap :
                            mapList) {
                        if(coveringMap.cells[i+minXindex][j+minYindex].state != CellState.UNDEFINED){
                            covered = true;
                        }
                    }
                    if(!covered){
                        return false;
                    }
                }
            }
        }


        return true;
    }

    private boolean mapCoveredByMap(LabyrinthMap map, LabyrinthMap coveringMap, int minXindex, int minYindex, int xsize, int ysize) {
        for(int i = 0; i < xsize; ++i){
            for(int j = 0; j <= ysize; ++j){
                if(coveringMap.horizontalBorders[i+minXindex][j+minYindex].state() == BorderState.UNDEFINED){
                    if(map.horizontalBorders[i+minXindex][j+minYindex].state() != BorderState.UNDEFINED){
                        return false;
                    }
                }
            }
        }

        for(int i = 0; i <= xsize; ++i){
            for(int j = 0; j < ysize; ++j){
                if(coveringMap.verticalBorders[i+minXindex][j+minYindex].state() == BorderState.UNDEFINED){
                    if(map.verticalBorders[i+minXindex][j+minYindex].state() != BorderState.UNDEFINED){
                        return false;
                    }
                }
            }
        }

        for(int i = 0; i < xsize; ++i){
            for(int j = 0; j < ysize; ++j){
                if(coveringMap.cells[i+minXindex][j+minYindex].state == CellState.UNDEFINED){
                    if(map.cells[i+minXindex][j+minYindex].state == CellState.VISITED){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private LabyrinthMap copyMapSmallToLarge(LabyrinthMap map, int minXindex, int minYindex, int xsize, int ysize) {
        LabyrinthMap newMap = new LabyrinthMap(xsize, ysize, BorderState.UNDEFINED);

        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height + 1; ++j){
                newMap.horizontalBorders[i + minXindex][j + minYindex].setState(map.horizontalBorders[i][j].state());
            }
        }

        for(int i = 0; i < map.width + 1; ++i){
            for(int j = 0; j < map.height; ++j){
                newMap.verticalBorders[i + minXindex][j + minYindex].setState(map.verticalBorders[i][j].state());
            }
        }

        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                if(map.cells[i][j].type == CellType.PORTAL){
                    newMap.cells[i + minXindex][j + minYindex] = new PortalCell(((PortalCell)map.cells[i][j]).number, ((PortalCell)map.cells[i][j]).visibleNumber, i + minXindex, j + minYindex, 0);
                }
                if(map.cells[i][j].type == CellType.ARSENAL){
                    newMap.cells[i + minXindex][j + minYindex] = new ArsenalCell(i + minXindex,j + minYindex ,0);
                }
                if(map.cells[i][j].type == CellType.HOSPITAL){
                    newMap.cells[i + minXindex][j + minYindex] = new HospitalCell(i + minXindex, j + minYindex, 0);
                }
                if(map.cells[i][j].minotaur != null){
                    newMap.cells[i + minXindex][j + minYindex].minotaur = new Minotaur(map.cells[i][j].minotaur);
                }
                newMap.cells[i + minXindex][j + minYindex].state = map.cells[i][j].state;
            }
        }

        return newMap;
    }

    public void paint(Graphics g) {
        super.paint(g);
        //myMiniMapScrollPane.repaint();
        //g.setColor(Color.WHITE);
        g.setFont(smallFont);
        if((myMap != null) && (visibleMap != null)){
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;
            paintInfoText(g);
            int leftX = centerx - visibleMap.width*cellWidth/2 - (visibleMap.width + 1)*borderWidth/2;
            int topY = centery - visibleMap.height*cellWidth/2 - (visibleMap.height + 1)*borderWidth/2;
            int rightX = centerx + visibleMap.width*cellWidth/2 + (visibleMap.width + 1)*borderWidth/2;
            int bottomY = centery + visibleMap.height*cellWidth/2 + (visibleMap.height + 1)*borderWidth/2;

            ///draw inner cell borders
            /*g.setColor(Color.WHITE);
            for(int i = 1; i < myMap.width; ++i){
                int tempX = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                g.drawLine(tempX, topY, tempX, bottomY);
            }
            for(int j = 1; j < myMap.width; ++j) {
                int tempY = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                g.drawLine(leftX, tempY, rightX, tempY);
            }*/
            g.setColor(Color.GRAY);

            ///draw outer borders
            /*g.drawLine(leftX, topY, rightX, topY);
            g.drawLine(rightX, topY, rightX, bottomY);
            g.drawLine(rightX, bottomY, leftX, bottomY);
            g.drawLine(leftX, bottomY, leftX, topY);*/

            g.setColor(Color.BLACK);

            ///draw walls
            for(int i = 0; i < visibleMap.width+1; ++i) {
                for (int j = 0; j < visibleMap.height; ++j) {
                    if (visibleMap.verticalBorders[i][j].state() != BorderState.NOTEXISTS) {
                        int x = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                        int topy = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                        int downy = topY + (j + 1) * cellWidth + (2 * j + 3) * borderWidth / 2;
                        boolean wallExists = true;
                        if (visibleMap.verticalBorders[i][j].state() == BorderState.EXISTS) {
                            g.setColor(Color.BLACK);
                        }
                        if (visibleMap.verticalBorders[i][j].state() == BorderState.DOOR) {
                            g.setColor(Color.RED);
                        }
                        if (visibleMap.verticalBorders[i][j].state() == BorderState.UNDEFINED) {
                            g.setColor(Color.LIGHT_GRAY);
                            for (LabyrinthMap map :
                                    additionalMapList) {
                                if(map.verticalBorders[i][j].state() == BorderState.EXISTS){
                                    g.setColor(Color.BLUE);
                                }
                                if(map.verticalBorders[i][j].state() == BorderState.NOTEXISTS){
                                    g.setColor(Color.BLUE);
                                    wallExists = false;
                                }
                            }
                        }
                        if(wallExists) {
                            g.drawLine(x, topy, x, downy);
                        }
                    }
                }
            }
            for(int i = 0; i < visibleMap.width; ++i) {
                for (int j = 0; j < visibleMap.height + 1; ++j) {
                    if (visibleMap.horizontalBorders[i][j].state() != BorderState.NOTEXISTS) {
                        int y = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                        int leftx = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                        int rightx = leftX + (i + 1) * cellWidth + (2 * i + 3) * borderWidth / 2;
                        boolean wallExists = true;
                        if (visibleMap.horizontalBorders[i][j].state() == BorderState.EXISTS) {
                            g.setColor(Color.BLACK);
                        }
                        if (visibleMap.horizontalBorders[i][j].state() == BorderState.DOOR) {
                            g.setColor(Color.RED);
                        }
                        if (visibleMap.horizontalBorders[i][j].state() == BorderState.UNDEFINED) {
                            g.setColor(Color.LIGHT_GRAY);
                            for (LabyrinthMap map :
                                    additionalMapList) {
                                if(map.horizontalBorders[i][j].state() == BorderState.EXISTS){
                                    g.setColor(Color.BLUE);
                                }
                                if(map.horizontalBorders[i][j].state() == BorderState.NOTEXISTS){
                                    g.setColor(Color.BLUE);
                                    wallExists = false;
                                }
                            }
                        }
                        if(wallExists) {
                            g.drawLine(leftx, y, rightx, y);
                        }
                    }
                }
            }
            g.setColor(Color.BLACK);
            for(int i = 0; i < visibleMap.width; ++i) {
                for (int j = 0; j < visibleMap.height; ++j) {
                    int cellx = leftX + (2*i+1)*cellWidth/2 + i*borderWidth;
                    int celly = topY + (2*j+1)*cellWidth/2 + j*borderWidth;
                    if(visibleMap.cells[i][j].state == CellState.VISITED) {
                        paintCell(g, cellx, celly, visibleMap.cells[i][j]);
                    } else {
                        for (LabyrinthMap map :
                                additionalMapList) {
                            if(map.cells[i][j].state == CellState.VISITED){
                                paintCell(g, cellx, celly, map.cells[i][j]);
                                break;
                            }
                        }
                    }
                }
            }
            g.setColor(Color.BLACK);

            if(gameOver){
                String text = "VICTORY!";
                g.setFont(hugeFont);
                Graphics2D g2d = (Graphics2D) g;
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g.setColor(Color.RED);
                g2d.drawString(text, centerx - textWidth/2, centery + textHeight/2);
                g.setFont(smallFont);
                g.setColor(Color.BLACK);
            }

            if(dragMiniMap){
                drawMiniMap(g);
            }
        }
        if(isShowingDialog){
            showDialog(dialogText, g);
        }
        //super.paint(g);
    }

    private void showDialog(String dialogText, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int dialogWidth = width/4;
        int dialogHeight = height/4;
        int buttonWidth = dialogWidth/4;
        int buttonHeight = dialogHeight/4;
        int centerx = width/2;
        int centery = height/2;
        int buttonx1 = centerx - dialogWidth/4;
        int buttonx2 = centerx + dialogWidth/4;
        int buttony = centery + dialogHeight/6;
        g.setColor(Color.BLACK);
        g.setFont(middleFont);
        g2d.fillRect(centerx - dialogWidth/2 - 2, centery - dialogHeight/2 - 2, dialogWidth + 4, dialogHeight + 4);
        g.setColor(Color.WHITE);
        g2d.fillRect(centerx - dialogWidth/2, centery - dialogHeight/2, dialogWidth, dialogHeight);
        g.setColor(Color.BLACK);
        g2d.fillRect(buttonx1 - buttonWidth/2 - 2, buttony - buttonHeight/2 - 2, buttonWidth+4, buttonHeight+4);
        g.setColor(Color.WHITE);
        g2d.fillRect(buttonx1 - buttonWidth/2, buttony - buttonHeight/2, buttonWidth, buttonHeight);
        g.setColor(Color.BLACK);
        g2d.fillRect(buttonx2 - buttonWidth/2 - 2, buttony - buttonHeight/2 - 2, buttonWidth+4, buttonHeight+4);
        g.setColor(Color.WHITE);
        g2d.fillRect(buttonx2 - buttonWidth/2, buttony - buttonHeight/2, buttonWidth, buttonHeight);
        g.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(dialogText);
        int textHeight = fm.getHeight();
        g2d.drawString(dialogText, centerx - textWidth/2, centery - dialogHeight/3 + textHeight/2);

        String leftText = "Yes";
        String rightText = "No";
        textWidth = fm.stringWidth(leftText);
        textHeight = fm.getHeight();
        g2d.drawString(leftText, buttonx1 - textWidth/2, buttony + textHeight/2 - 10);

        textWidth = fm.stringWidth(rightText);
        textHeight = fm.getHeight();
        g2d.drawString(rightText, buttonx2 - textWidth/2, buttony + textHeight/2 - 10);


        g.setFont(smallFont);
        g.setColor(Color.BLACK);
    }

    private void drawMiniMap(Graphics g) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        int mousex = point.x;
        int mousey = point.y;

        int width = getWidth();
        int height = getHeight();
        int centerx = width/2;
        int centery = height/2;
        int mainLeftX = centerx - visibleMap.width*cellWidth/2 - (visibleMap.width + 1)*borderWidth/2;
        int mainTopY = centery - visibleMap.height*cellWidth/2 - (visibleMap.height + 1)*borderWidth/2;
        int mainRightX = centerx + visibleMap.width*cellWidth/2 + (visibleMap.width + 1)*borderWidth/2;
        int mainBottomY = centery + visibleMap.height*cellWidth/2 + (visibleMap.height + 1)*borderWidth/2;

        int leftX = mousex - draggedMap.width*cellWidth/2 - (draggedMap.width + 1)*borderWidth/2;
        int topY = mousey - draggedMap.height*cellWidth/2 - (draggedMap.height + 1)*borderWidth/2;
        int rightX = mousex + draggedMap.width*cellWidth/2 + (draggedMap.width + 1)*borderWidth/2;
        int bottomY = mousey + draggedMap.height*cellWidth/2 + (draggedMap.height + 1)*borderWidth/2;

        boolean isPinned = false;
        int shiftX = 0;
        int shiftY = 0;
        if(leftX >= mainLeftX && rightX <= mainRightX && topY >= mainTopY && bottomY <= mainBottomY){

            int xn = (leftX - mainLeftX)/(cellWidth + borderWidth);
            int modx = (leftX - mainLeftX)%(cellWidth + borderWidth);
            int yn = (topY - mainTopY)/(cellWidth + borderWidth);
            int mody = (topY - mainTopY)%(cellWidth + borderWidth);

            if((modx < cellWidth/5 || modx > (cellWidth*4)/5) && (mody < cellWidth/5 || mody > (cellWidth*4)/5)) {
                isPinned = true;
                if (modx < cellWidth / 5) {
                    leftX = mainLeftX + xn * cellWidth + (2 * xn + 1) * borderWidth / 2;
                    shiftX = xn;
                } else if (modx > (cellWidth * 4) / 5) {
                    leftX = mainLeftX + (xn + 1) * cellWidth + (2 * xn + 3) * borderWidth / 2;
                    shiftX = xn + 1;
                }

                if (mody < cellWidth / 5) {
                    topY = mainTopY + yn * cellWidth + (2 * yn + 1) * borderWidth / 2;
                    shiftY = yn;
                } else if (mody > (cellWidth * 4) / 5) {
                    topY = mainTopY + (yn + 1) * cellWidth + (2 * yn + 3) * borderWidth / 2;
                    shiftY = yn + 1;
                }
            }
        }



        ///draw inner cell borders
            /*g.setColor(Color.WHITE);
            for(int i = 1; i < myMap.width; ++i){
                int tempX = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                g.drawLine(tempX, topY, tempX, bottomY);
            }
            for(int j = 1; j < myMap.width; ++j) {
                int tempY = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                g.drawLine(leftX, tempY, rightX, tempY);
            }*/
        g.setColor(Color.GRAY);

        ///draw outer borders
            /*g.drawLine(leftX, topY, rightX, topY);
            g.drawLine(rightX, topY, rightX, bottomY);
            g.drawLine(rightX, bottomY, leftX, bottomY);
            g.drawLine(leftX, bottomY, leftX, topY);*/

        g.setColor(Color.BLACK);

        boolean match = false;
        if(isPinned){
            match = Util.mapFitingToAll(visibleMap, additionalMapList, draggedMap, shiftX, shiftY);
        }

        ///draw walls
        for(int i = 0; i < draggedMap.width+1; ++i) {
            for (int j = 0; j < draggedMap.height; ++j) {
                if (draggedMap.verticalBorders[i][j].state() != BorderState.NOTEXISTS) {
                    int x = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                    int topy = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                    int downy = topY + (j + 1) * cellWidth + (2 * j + 3) * borderWidth / 2;
                    if (draggedMap.verticalBorders[i][j].state() == BorderState.EXISTS) {
                        g.setColor(Color.BLACK);
                        if(match){
                            g.setColor(Color.GREEN);
                        }
                    }
                    if (draggedMap.verticalBorders[i][j].state() == BorderState.UNDEFINED) {
                        g.setColor(Color.LIGHT_GRAY);
                    }
                    if (draggedMap.verticalBorders[i][j].state() == BorderState.DOOR) {
                        g.setColor(Color.RED);
                    }
                    g.drawLine(x, topy, x, downy);
                }
            }
        }
        for(int i = 0; i < draggedMap.width; ++i) {
            for (int j = 0; j < draggedMap.height + 1; ++j) {
                if (draggedMap.horizontalBorders[i][j].state() != BorderState.NOTEXISTS) {
                    int y = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                    int leftx = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                    int rightx = leftX + (i + 1) * cellWidth + (2 * i + 3) * borderWidth / 2;
                    if (draggedMap.horizontalBorders[i][j].state() == BorderState.EXISTS) {
                        g.setColor(Color.BLACK);
                        if(match){
                            g.setColor(Color.GREEN);
                        }
                    }
                    if (draggedMap.horizontalBorders[i][j].state() == BorderState.UNDEFINED) {
                        g.setColor(Color.LIGHT_GRAY);
                    }
                    if (draggedMap.horizontalBorders[i][j].state() == BorderState.DOOR) {
                        g.setColor(Color.RED);
                    }
                    g.drawLine(leftx, y, rightx, y);
                }
            }
        }
    }

    private void paintInfoText(Graphics g) {
        String text = "Minotaurs number: " + myMap.minotaursCount + ", alive: " + myMap.aliveMinotaursCount;
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics();
        int textHeight = fm.getHeight();
        g2d.drawString(text, 10, 10 + fm.getAscent());
        text = "Portals number: " + myMap.portalsCount;
        g2d.drawString(text, 10, 10 + textHeight + fm.getAscent());
        text = "Bullets: " + character.bulletCount;
        g2d.drawString(text, 10, 10 + 2*textHeight + fm.getAscent());
        if(character.myKey == null){
            text = "Key: not found";
            g2d.drawString(text, 10, 10 + 3*textHeight + fm.getAscent());
        } else {
            text = "Key: ";
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, 10, 10 + 3*textHeight + fm.getAscent());
            character.myKey.paint(g, 10 + textWidth + cellWidth/2, 10 + 3*textHeight + cellWidth/2);

        }
    }

    private void paintCell(Graphics g, int cellx, int celly, Cell cell) {
        cell.paint(g, cellx, celly);
    }

    public void restart() {
        gameOver = false;
        stepNumber = 0;
        myTextArea.setText("");
        textPosition = 0;
        myMiniMapList.clear();
        myMiniMapPanel.removeAll();
        dragMiniMap = false;
        draggedMap = null;
        additionalMapList.clear();
        portalsPassing = 0;
    }

    public void setTextArea(JTextArea simpleGameTextArea) {
        myTextArea = simpleGameTextArea;
        textPosition = 0;
    }


    public void setMiniMapPanel(JPanel panel){
        myMiniMapPanel = panel;
        myMiniMapList = new LinkedList<>();

    }

    public void setDragMiniMap(LabyrinthMap map, List<LabyrinthMap> list) {
        dragMiniMap = true;
        draggedMap = map;
        draggedList = list;
    }

    public void stopDragMiniMap() {
        //todo
        //addMiniMap
        dragMiniMap = false;
        draggedMap = null;
    }

    public boolean isDragMiniMap(){
        return dragMiniMap;
    }

    public void tryAddMiniMap(Point point) {
        //todo why does not work?
        Point point2 = MouseInfo.getPointerInfo().getLocation();
        int mousex = point2.x;
        int mousey = point2.y;

        int width = getWidth();
        int height = getHeight();
        int centerx = width/2;
        int centery = height/2;
        int mainLeftX = centerx - visibleMap.width*cellWidth/2 - (visibleMap.width + 1)*borderWidth/2;
        int mainTopY = centery - visibleMap.height*cellWidth/2 - (visibleMap.height + 1)*borderWidth/2;
        int mainRightX = centerx + visibleMap.width*cellWidth/2 + (visibleMap.width + 1)*borderWidth/2;
        int mainBottomY = centery + visibleMap.height*cellWidth/2 + (visibleMap.height + 1)*borderWidth/2;

        int leftX = mousex - draggedMap.width*cellWidth/2 - (draggedMap.width + 1)*borderWidth/2;
        int topY = mousey - draggedMap.height*cellWidth/2 - (draggedMap.height + 1)*borderWidth/2;
        int rightX = mousex + draggedMap.width*cellWidth/2 + (draggedMap.width + 1)*borderWidth/2;
        int bottomY = mousey + draggedMap.height*cellWidth/2 + (draggedMap.height + 1)*borderWidth/2;

        boolean isPinned = false;
        int shiftx = 0;
        int shifty = 0;

        if(leftX >= mainLeftX && rightX <= mainRightX && topY >= mainTopY && bottomY <= mainBottomY){
            int xn = (leftX - mainLeftX)/(cellWidth + borderWidth);
            int modx = (leftX - mainLeftX)%(cellWidth + borderWidth);
            int yn = (topY - mainTopY)/(cellWidth + borderWidth);
            int mody = (topY - mainTopY)%(cellWidth + borderWidth);
            if((modx < cellWidth/5 || modx > (cellWidth*4)/5) && (mody < cellWidth/5 || mody > (cellWidth*4)/5)) {
                isPinned = true;
                if (modx < cellWidth / 5) {
                    shiftx = xn;
                } else if (modx > (cellWidth * 4) / 5) {
                    shiftx = xn + 1;
                }

                if (mody < cellWidth / 5) {
                    shifty = yn;
                } else if (mody > (cellWidth * 4) / 5) {
                    shifty = yn + 1;
                }
            }
            if(!isPinned){
                return;
            }
            //todo maybe check all separately
            if(!Util.mapFitingToAll(visibleMap, additionalMapList, draggedMap, shiftx, shifty)){
                return;
            }
            addMiniMap(draggedList,shiftx,shifty);
        }
    }

    private void addMiniMap(LabyrinthMap map, int shiftx, int shifty) {
        LabyrinthMap newMap = copyMapSmallToLarge(map, shiftx, shifty, visibleMap.width, visibleMap.height);
        additionalMapList.add(newMap);
        stopDragMiniMap();
    }

    private void addMiniMap(List<LabyrinthMap> list, int shiftx, int shifty) {
        for (LabyrinthMap map :
                list) {
            LabyrinthMap newMap = copyMapSmallToLarge(map, shiftx, shifty, visibleMap.width, visibleMap.height);
            //todo unify portal numbers
            additionalMapList.add(newMap);
        }
        stopDragMiniMap();
    }

    public void removeMiniMap(MiniMapPanel map) {
        myMiniMapList.remove(map);
    }

    private class IndexContainer{
        public int minXindex;
        public int maxXindex;
        public int minYindex;
        public int maxYindex;

        public IndexContainer(int minx, int maxx, int miny, int maxy) {
            minXindex = minx;
            maxXindex = maxx;
            minYindex = miny;
            maxYindex = maxy;
        }
    }
}
