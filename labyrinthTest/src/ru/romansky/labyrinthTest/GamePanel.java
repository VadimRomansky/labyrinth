package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Vector;

enum Direction { UP, DOWN, LEFT, RIGHT}

public class GamePanel  extends MapPanelBase {
    Character character;
    LabyrinthMap visibleMap;
    int characterx = -1;
    int charactery = -1;

    public GamePanel(JFrame frame, JPanel parent) {
        this.myFrame = frame;
        this.myParent = parent;
        myMap = null;
        visibleMap = null;
    }
    public GamePanel(JFrame frame, JPanel parent, LabyrinthMap map) {
        this.myFrame = frame;
        this.myParent = parent;
        myMap = map;
        visibleMap = new LabyrinthMap(myMap.width, myMap.height);

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height; ++j){
                Vector<MapObject> objects = myMap.cells[i][j].characters;
                for (MapObject object :
                        objects) {
                    if (object instanceof Character){
                        characterx = i;
                        charactery = j;
                        visibleMap.cells[characterx][charactery].characters.add(object);
                        character = (Character) object;
                        objects.removeElement(object);
                        break;
                    }
                }

            }
        }
        if(characterx >=0 && characterx < myMap.width && charactery > 0 && charactery < myMap.height){
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
        }

        for(int i = 0; i < myMap.width+1; ++i){
            for(int j = 0; j < myMap.height; ++j){
                visibleMap.verticalBorders[i][j].myExists = false;
            }
        }

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height+1; ++j){
                visibleMap.horizontalBorders[i][j].myExists = false;
            }
        }
    }

    public void resetMap(LabyrinthMap map){
        myMap = map;
        visibleMap = new LabyrinthMap(myMap.width, myMap.height);

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height; ++j){
                Vector<MapObject> objects = myMap.cells[i][j].characters;
                for (MapObject object :
                        objects) {
                    if (object instanceof Character){
                        characterx = i;
                        charactery = j;
                        visibleMap.cells[characterx][charactery].characters.add(object);
                        character = (Character) object;
                        objects.removeElement(object);
                        break;
                    }
                }
            }
        }
        if(characterx >=0 && characterx < myMap.width && charactery > 0 && charactery < myMap.height){
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
        }

        for(int i = 0; i < myMap.width+1; ++i){
            for(int j = 0; j < myMap.height; ++j){
                visibleMap.verticalBorders[i][j].myExists = false;
            }
        }

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height+1; ++j){
                visibleMap.horizontalBorders[i][j].myExists = false;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            moveCharacter(key);
            return;
        }
        if(key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D){
            shootBullet(key);
            return;
        }
    }

    private void shootBullet(int key) {
        int bulletx = characterx;
        int bullety = charactery;
        Direction direction = Direction.DOWN;//todo
        if(key == KeyEvent.VK_S){
            direction = Direction.DOWN;
        } else if (key == KeyEvent.VK_W){
            direction = Direction.UP;
        } else if (key == KeyEvent.VK_A){
            direction = Direction.LEFT;
        } else if (key == KeyEvent.VK_D){
            direction = Direction.RIGHT;
        }
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
                myMap.cells[bulletx][bullety].minotaur.kill();
                return;
            }
            //todo characters

        }
    }

    private void moveCharacter(int key) {
        if(visibleMap == null){
            return;
        }
        Direction direction = Direction.DOWN;//todo;
        if(key == KeyEvent.VK_DOWN){
            direction = Direction.DOWN;
        } else if (key == KeyEvent.VK_UP){
            direction = Direction.UP;
        } else if (key == KeyEvent.VK_LEFT){
            direction = Direction.LEFT;
        } else if (key == KeyEvent.VK_RIGHT){
            direction = Direction.RIGHT;
        }
        if (isMovePossible(direction, characterx, charactery)){
            visibleMap.cells[characterx][charactery].characters.removeElement(character);
            if(direction == Direction.DOWN) {
                    charactery ++;
            }
            if(direction == Direction.UP) {
                    charactery --;
            }
            if(direction == Direction.LEFT) {
                    characterx --;
            }
            if(direction == Direction.RIGHT) {
                    characterx ++;
            }
            Vector<MapObject> objects = myMap.cells[characterx][charactery].characters;
            visibleMap.cells[characterx][charactery].characters.clear();
            visibleMap.cells[characterx][charactery].characters.add(character);
            visibleMap.cells[characterx][charactery].characters.addAll(objects);
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
            repaint();
            eventAfterMove();
        } else {
            addVisibleBoundary(direction);
            repaint();
        }
    }

    private void addVisibleBoundary(Direction direction) {
        if(direction == Direction.DOWN) {
            visibleMap.horizontalBorders[characterx][charactery + 1].myExists = true;
        }
        if(direction == Direction.UP) {
            visibleMap.horizontalBorders[characterx][charactery].myExists = true;
        }
        if(direction == Direction.LEFT) {
            visibleMap.verticalBorders[characterx][charactery].myExists = true;
        }
        if(direction == Direction.RIGHT) {
            visibleMap.verticalBorders[characterx+1][charactery].myExists = true;
        }
    }

    private boolean isMovePossible(Direction direction, int currentx, int currenty) {
        if(direction == Direction.DOWN) {
            if(currenty >= myMap.height - 1){
                return false;
            }
            if(myMap.horizontalBorders[currentx][currenty + 1].exists()){
                return false;
            }
            return true;
        }
        if(direction == Direction.UP) {
            if(currenty <= 0){
                return false;
            }
            if(myMap.horizontalBorders[currentx][currenty].exists()){
                return false;
            }
            return true;
        }
        if(direction == Direction.LEFT) {
            if(currentx <= 0){
                return false;
            }
            if(myMap.verticalBorders[currentx][currenty].exists()) {
                return false;
            }
            return true;
        }
        if(direction == Direction.RIGHT) {
            if(currentx >= myMap.width - 1){
                return false;
            }
            if(myMap.verticalBorders[currentx+1][currenty].exists()) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void eventAfterMove() {
        if(myMap.cells[characterx][charactery].minotaur != null){
                if(myMap.cells[characterx][charactery].minotaur.isAlive()){
                    visibleMap.cells[characterx][charactery].characters.removeElement(character);
                    characterx = myMap.hospitalx;
                    charactery = myMap.hospitaly;
                    visibleMap.cells[characterx][charactery] = new HospitalCell(characterx,charactery,0);
                    visibleMap.cells[characterx][charactery].characters.add(character);
                    visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
                    repaint();
                    return;
                }

        }
        if(myMap.cells[characterx][charactery].type == CellType.ARSENAL){
            visibleMap.cells[characterx][charactery] = new ArsenalCell(characterx,charactery,0);
            visibleMap.cells[characterx][charactery].characters.add(character);
            visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
        }
        if(myMap.cells[characterx][charactery].type == CellType.HOSPITAL){
            visibleMap.cells[characterx][charactery] = new HospitalCell(characterx,charactery,0);
            visibleMap.cells[characterx][charactery].characters.add(character);
            visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
        }
        if(myMap.cells[characterx][charactery].type == CellType.PORTAL) {
            visibleMap.cells[characterx][charactery] = new PortalCell(((PortalCell)myMap.cells[characterx][charactery]).number, characterx,charactery, 0);
            visibleMap.cells[characterx][charactery].characters.add(character);
            visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
            repaint();
            visibleMap.cells[characterx][charactery].characters.removeElement(character);
            PortalCell next = ((PortalCell)myMap.cells[characterx][charactery]).next;
            characterx = next.x;
            charactery = next.y;
            visibleMap.cells[characterx][charactery] = new PortalCell(((PortalCell)myMap.cells[characterx][charactery]).number, characterx,charactery, 0);
            visibleMap.cells[characterx][charactery].characters.add(character);
            visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
            repaint();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        //g.setColor(Color.WHITE);
        if((myMap != null) && (visibleMap != null)){
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;
            int leftX = centerx - myMap.width*cellWidth/2 - (myMap.width + 1)*borderWidth/2;
            int topY = centery - myMap.height*cellWidth/2 - (myMap.height + 1)*borderWidth/2;
            int rightX = centerx + myMap.width*cellWidth/2 + (myMap.width + 1)*borderWidth/2;
            int bottomY = centery + myMap.height*cellWidth/2 + (myMap.height + 1)*borderWidth/2;

            ///draw inner cell borders
            g.setColor(Color.WHITE);
            for(int i = 1; i < myMap.width; ++i){
                int tempX = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                g.drawLine(tempX, topY, tempX, bottomY);
            }
            for(int j = 1; j < myMap.width; ++j) {
                int tempY = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                g.drawLine(leftX, tempY, rightX, tempY);
            }
            g.setColor(Color.GRAY);

            ///draw outer borders
            g.drawLine(leftX, topY, rightX, topY);
            g.drawLine(rightX, topY, rightX, bottomY);
            g.drawLine(rightX, bottomY, leftX, bottomY);
            g.drawLine(leftX, bottomY, leftX, topY);

            g.setColor(Color.BLACK);

            ///draw walls
            for(int i = 0; i < visibleMap.width; ++i){
                for(int j = 0; j < visibleMap.height; ++j){
                    if(visibleMap.verticalBorders[i][j].exists()){
                        int x = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                        int topy = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                        int downy = topY + (j+1)*cellWidth + (2*j+3)*borderWidth/2;
                        g.drawLine(x, topy, x, downy);
                    }
                    if(visibleMap.horizontalBorders[i][j].exists()){
                        int y = topY + j*cellWidth + (2*j + 1)*borderWidth/2;
                        int leftx = leftX + i*cellWidth + (2*i + 1)*borderWidth/2;
                        int rightx = leftX + (i+1)*cellWidth + (2*i+3)*borderWidth/2;
                        g.drawLine(leftx, y, rightx, y);
                    }
                    int cellx = leftX + (2*i+1)*cellWidth/2 + i*borderWidth;
                    int celly = topY + (2*j+1)*cellWidth/2 + j*borderWidth;
                    paintCell(g, cellx, celly, visibleMap.cells[i][j]);
                }
            }
        }
        //super.paint(g);
    }

    private void paintCell(Graphics g, int cellx, int celly, Cell cell) {
        cell.paint(g, cellx, celly);
    }
}
