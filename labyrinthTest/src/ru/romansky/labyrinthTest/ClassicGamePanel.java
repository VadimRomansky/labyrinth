package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class ClassicGamePanel extends MapPanelBase {
    private JTextArea myTextArea;
    private JList<LabyrinthMap> myMiniMapList;
    private DefaultListModel<LabyrinthMap> myMiniMapListModel;
    private int textPosition = 0;
    int stepNumber = 0;
    boolean gameOver = false;
    Character character;
    LabyrinthMap visibleMap;
    int realCharacterx = -1;
    int realCharactery = -1;
    int visibleCharacterx = -1;
    int visibleCharactery = -1;
    int mapShiftX;
    int mapShiftY;
    public static final Font smallFont = new Font("TimesRoman", Font.PLAIN, 14);;
    public static final Font microFont = new Font("TimesRoman", Font.PLAIN, 10);;
    public static final Font hugeFont = new Font("TimesRoman", Font.PLAIN, 48);;
    private JScrollPane myMiniMapScrollPane;
    private int miniMapPosition;

    public ClassicGamePanel(JFrame frame, JPanel parent) {
        this.myFrame = frame;
        this.myParent = parent;
        myMap = null;
        visibleMap = null;
        setBackground(Color.WHITE);
    }
    public ClassicGamePanel(JFrame frame, JPanel parent, LabyrinthMap map) {
        this.myFrame = frame;
        this.myParent = parent;
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
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
                moveCharacter(key);
                return;
            }
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
                shootBullet(key);
                return;
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
        if(key == KeyEvent.VK_S){
            direction = Direction.DOWN;
            text = text + " down.";
        } else if (key == KeyEvent.VK_W){
            direction = Direction.UP;
            text = text + " up.";
        } else if (key == KeyEvent.VK_A){
            direction = Direction.LEFT;
            text = text + " left.";
        } else if (key == KeyEvent.VK_D){
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
        repaint();
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
        if(key == KeyEvent.VK_DOWN){
            direction = Direction.DOWN;
            text = text + " down";
        } else if (key == KeyEvent.VK_UP){
            direction = Direction.UP;
            text = text + " up";
        } else if (key == KeyEvent.VK_LEFT){
            direction = Direction.LEFT;
            text = text + " left";
        } else if (key == KeyEvent.VK_RIGHT){
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
            repaint();
            eventAfterMove(text);
            repaint();
        } else if(checkVictory(direction, realCharacterx, realCharactery)) {
            text = text + " and went out of the labyrinth.";
            writeTextMessage(text);
            setBoundaryStateVisible(direction);
            celebrateVictory();
        } else {
            text = text + " and bumped into the wall.";
            writeTextMessage(text);
            setBoundaryStateVisible(direction);
            repaint();
        }
    }

    private void celebrateVictory() {
        gameOver = true;
        repaint();
    }

    private boolean checkVictory(Direction direction, int currentx, int currenty) {
        if(direction == Direction.DOWN) {
            if(myMap.horizontalBorders[currentx][currenty + 1].state() != BorderState.DOOR){
                return false;
            }
        }
        if(direction == Direction.UP) {
            if(myMap.horizontalBorders[currentx][currenty].state() != BorderState.DOOR){
                return false;
            }
        }
        if(direction == Direction.LEFT) {
            if(myMap.verticalBorders[currentx][currenty].state() != BorderState.DOOR) {
                return false;
            }
        }
        if(direction == Direction.RIGHT) {
            if(myMap.verticalBorders[currentx+1][currenty].state() != BorderState.DOOR) {
                return false;
            }
        }

        //todo
        return true;
    }

    private void setBoundaryStateVisible(Direction direction) {
        if(direction == Direction.DOWN) {
            visibleMap.horizontalBorders[visibleCharacterx][visibleCharactery + 1].setState(myMap.horizontalBorders[realCharacterx][realCharactery + 1].state());
        }
        if(direction == Direction.UP) {
            visibleMap.horizontalBorders[visibleCharacterx][visibleCharactery].setState(myMap.horizontalBorders[realCharacterx][realCharactery].state());
        }
        if(direction == Direction.LEFT) {
            visibleMap.verticalBorders[visibleCharacterx][visibleCharactery].setState(myMap.verticalBorders[realCharacterx][realCharactery].state());
        }
        if(direction == Direction.RIGHT) {
            visibleMap.verticalBorders[visibleCharacterx+1][visibleCharactery].setState(myMap.verticalBorders[realCharacterx+1][realCharactery].state());
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
                repaint();
                return;
            } else {
                myMap.cells[realCharacterx][realCharactery].minotaur.confirmKill();
                text = text + " and stepped on the minotaur's corpse.";
                writeTextMessage(text);
                repaint();
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
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
            visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(myMap.cells[realCharacterx][realCharactery].characters);
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
            repaint();
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.removeElement(character);
            PortalCell next = ((PortalCell)myMap.cells[realCharacterx][realCharactery]).next;
            realCharacterx = next.x;
            realCharactery = next.y;
            putMapToPanel();
            resetVisibleMap();
            visibleCharacterx = realCharacterx + mapShiftX;
            visibleCharactery = realCharactery + mapShiftY;
            visibleMap.cells[visibleCharacterx][visibleCharactery] = new PortalCell(((PortalCell)myMap.cells[realCharacterx][realCharactery]).number, visibleCharacterx,visibleCharactery, 0);
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.add(character);
            visibleMap.cells[visibleCharacterx][visibleCharactery].state = CellState.VISITED;
            visibleMap.cells[visibleCharacterx][visibleCharactery].characters.addAll(myMap.cells[realCharacterx][realCharactery].characters);
            visibleMap.cells[visibleCharacterx][visibleCharactery].minotaur = myMap.cells[realCharacterx][realCharactery].minotaur;
            text = text + ", fall into the potal and got out from another.";
            writeTextMessage(text);
            repaint();
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
        for(int i = 0; i < visibleMap.width; ++i){
            boolean minIndexSet = false;
            for(int j = 0; j < visibleMap.height; ++j){
                if ((visibleMap.verticalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.horizontalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.cells[i][j].state != CellState.UNDEFINED)) {
                    minIndexSet = true;
                    break;
                }
            }
            if(minIndexSet){
                minXindex = i;
                break;
            }
        }

        for(int j = 0; j < visibleMap.height; ++j){
            boolean minIndexSet = false;
            for(int i = 0; i < visibleMap.width; ++i){
                if ((visibleMap.verticalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.horizontalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.cells[i][j].state != CellState.UNDEFINED)) {
                    minIndexSet = true;
                    break;
                }
            }
            if(minIndexSet){
                minYindex = j;
                break;
            }
        }

        for(int i = visibleMap.width - 1; i > 0; --i){
            boolean maxIndexSet = false;
            for(int j = 0; j < visibleMap.height; ++j){
                if ((visibleMap.verticalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.horizontalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.cells[i][j].state != CellState.UNDEFINED)) {
                    maxIndexSet = true;
                    break;
                }
            }
            if(maxIndexSet){
                maxXindex = i;
                break;
            }
        }

        for(int j = visibleMap.height - 1; j > 0; --j){
            boolean maxIndexSet = false;
            for(int i = 0; i < visibleMap.width; ++i){
                if ((visibleMap.verticalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.horizontalBorders[i][j].state() != BorderState.UNDEFINED) ||
                        (visibleMap.cells[i][j].state != CellState.UNDEFINED)) {
                    maxIndexSet = true;
                    break;
                }
            }
            if(maxIndexSet){
                maxYindex = j;
                break;
            }
        }

        int xsize = maxXindex - minXindex + 1;
        int ysize = maxYindex - minYindex + 1;

        LabyrinthMap miniMap = copyMap(visibleMap, minXindex, minYindex, xsize, ysize);

        myMiniMapListModel.addElement(miniMap);
    }

    private LabyrinthMap copyMap(LabyrinthMap visibleMap, int minXindex, int minYindex, int xsize, int ysize) {
        LabyrinthMap map = new LabyrinthMap(xsize, ysize);

        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height + 1; ++j){
                map.horizontalBorders[i][j].setState(visibleMap.horizontalBorders[i+minXindex][j + minYindex].state());
            }
        }

        for(int i = 0; i < map.width + 1; ++i){
            for(int j = 0; j < map.height; ++j){
                map.verticalBorders[i][j].setState(visibleMap.verticalBorders[i+minXindex][j+minYindex].state());
            }
        }

        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                if(visibleMap.cells[i+minXindex][j+minYindex].type == CellType.PORTAL){
                    map.cells[i][j] = new PortalCell(((PortalCell)visibleMap.cells[i+minXindex][j+minYindex]).number, i, j, 0);
                }
                if(visibleMap.cells[i+minXindex][j+minYindex].type == CellType.ARSENAL){
                    map.cells[i][j] = new ArsenalCell(i,j,0);
                }
                if(visibleMap.cells[i+minXindex][j+minYindex].type == CellType.HOSPITAL){
                    map.cells[i][j] = new HospitalCell(i, j, 0);
                }
                if(visibleMap.cells[i+minXindex][j+minYindex].minotaur != null){
                    map.cells[i][j].minotaur = new Minotaur(visibleMap.cells[i+minXindex][j+minYindex].minotaur);
                }
                map.cells[i][j].state = visibleMap.cells[i+minXindex][j+minYindex].state;
            }
        }

        return map;
    }

    public void paint(Graphics g) {
        super.paint(g);
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
                        if (visibleMap.verticalBorders[i][j].state() == BorderState.EXISTS) {
                            g.setColor(Color.BLACK);
                        }
                        if (visibleMap.verticalBorders[i][j].state() == BorderState.UNDEFINED) {
                            g.setColor(Color.LIGHT_GRAY);
                        }
                        if (visibleMap.verticalBorders[i][j].state() == BorderState.DOOR) {
                            g.setColor(Color.RED);
                        }
                        g.drawLine(x, topy, x, downy);
                    }
                }
            }
            for(int i = 0; i < visibleMap.width; ++i) {
                for (int j = 0; j < visibleMap.height + 1; ++j) {
                    if (visibleMap.horizontalBorders[i][j].state() != BorderState.NOTEXISTS) {
                        int y = topY + j * cellWidth + (2 * j + 1) * borderWidth / 2;
                        int leftx = leftX + i * cellWidth + (2 * i + 1) * borderWidth / 2;
                        int rightx = leftX + (i + 1) * cellWidth + (2 * i + 3) * borderWidth / 2;
                        if (visibleMap.horizontalBorders[i][j].state() == BorderState.EXISTS) {
                            g.setColor(Color.BLACK);
                        }
                        if (visibleMap.horizontalBorders[i][j].state() == BorderState.UNDEFINED) {
                            g.setColor(Color.LIGHT_GRAY);
                        }
                        if (visibleMap.horizontalBorders[i][j].state() == BorderState.DOOR) {
                            g.setColor(Color.RED);
                        }
                        g.drawLine(leftx, y, rightx, y);
                    }
                }
            }
            g.setColor(Color.BLACK);
            for(int i = 0; i < visibleMap.width; ++i) {
                for (int j = 0; j < visibleMap.height; ++j) {
                    int cellx = leftX + (2*i+1)*cellWidth/2 + i*borderWidth;
                    int celly = topY + (2*j+1)*cellWidth/2 + j*borderWidth;
                    paintCell(g, cellx, celly, visibleMap.cells[i][j]);
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
        }
        //super.paint(g);
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
    }

    private void paintCell(Graphics g, int cellx, int celly, Cell cell) {
        cell.paint(g, cellx, celly);
    }

    public void restart() {
        gameOver = false;
        stepNumber = 0;
        myTextArea.setText("");
        textPosition = 0;
        myMiniMapListModel.clear();
    }

    public void setTextArea(JTextArea simpleGameTextArea) {
        myTextArea = simpleGameTextArea;
        textPosition = 0;
    }

    public void setMiniMapList(JList<LabyrinthMap> list, DefaultListModel<LabyrinthMap> model){
        myMiniMapList = list;
        myMiniMapListModel = model;
    }
}
