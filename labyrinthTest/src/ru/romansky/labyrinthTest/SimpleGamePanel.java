package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Vector;

enum Direction { UP, DOWN, LEFT, RIGHT}

public class SimpleGamePanel extends MapPanelBase {
    private JTextArea myTextArea;
    private int textPosition = 0;
    int stepNumber = 0;
    boolean gameOver = false;
    Character character;
    LabyrinthMap visibleMap;
    int characterx = -1;
    int charactery = -1;
    Font smallFont;
    Font hugeFont;

    public SimpleGamePanel(JFrame frame, JPanel parent) {
        this.myFrame = frame;
        this.myParent = parent;
        myMap = null;
        visibleMap = null;
        setBackground(Color.WHITE);
        smallFont = new Font("TimesRoman", Font.PLAIN, 14);
        hugeFont = new Font("TimesRoman", Font.PLAIN, 48);
    }
    public SimpleGamePanel(JFrame frame, JPanel parent, LabyrinthMap map) {
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
                visibleMap.verticalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height+1; ++j){
                visibleMap.horizontalBorders[i][j].setState(BorderState.UNDEFINED);
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
                visibleMap.verticalBorders[i][j].setState(BorderState.UNDEFINED);
            }
        }

        for(int i = 0; i < myMap.width; ++i){
            for(int j = 0; j < myMap.height+1; ++j){
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
        int bulletx = characterx;
        int bullety = charactery;
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
        if (isMovePossible(direction, characterx, charactery)){
            setBoundaryStateVisible(direction);
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
            eventAfterMove(text);
            repaint();
        } else if(checkVictory(direction, characterx, charactery)) {
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
            visibleMap.horizontalBorders[characterx][charactery + 1].setState(myMap.horizontalBorders[characterx][charactery + 1].state());
        }
        if(direction == Direction.UP) {
            visibleMap.horizontalBorders[characterx][charactery].setState(myMap.horizontalBorders[characterx][charactery].state());
        }
        if(direction == Direction.LEFT) {
            visibleMap.verticalBorders[characterx][charactery].setState(myMap.verticalBorders[characterx][charactery].state());
        }
        if(direction == Direction.RIGHT) {
            visibleMap.verticalBorders[characterx+1][charactery].setState(myMap.verticalBorders[characterx+1][charactery].state());
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
        if(myMap.cells[characterx][charactery].minotaur != null){
            //visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
                if(myMap.cells[characterx][charactery].minotaur.isAlive()){
                    visibleMap.cells[characterx][charactery].characters.removeElement(character);
                    characterx = myMap.hospitalx;
                    charactery = myMap.hospitaly;
                    character.bulletCount = 0;
                    visibleMap.cells[characterx][charactery] = new HospitalCell(characterx,charactery,0);
                    visibleMap.cells[characterx][charactery].characters.add(character);
                    visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
                    text = text + " ,stepped on the minotaur, and he killed you. You woke up in the hospital.";
                    writeTextMessage(text);
                    repaint();
                    return;
                } else {
                    myMap.cells[characterx][charactery].minotaur.confirmKill();
                    text = text + " and stepped on the minotaur's corpse.";
                    writeTextMessage(text);
                    repaint();
                    return;
                }

        }
        if(myMap.cells[characterx][charactery].type == CellType.ARSENAL){
            visibleMap.cells[characterx][charactery] = new ArsenalCell(characterx,charactery,0);
            visibleMap.cells[characterx][charactery].characters.add(character);
            visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
            character.bulletCount = Character.maxBulletCount;
            text = text + " and got into the arsenal.";
            writeTextMessage(text);
            return;
        }
        if(myMap.cells[characterx][charactery].type == CellType.HOSPITAL){
            visibleMap.cells[characterx][charactery] = new HospitalCell(characterx,charactery,0);
            visibleMap.cells[characterx][charactery].characters.add(character);
            visibleMap.cells[characterx][charactery].characters.addAll(myMap.cells[characterx][charactery].characters);
            visibleMap.cells[characterx][charactery].minotaur = myMap.cells[characterx][charactery].minotaur;
            text = text + " and got into the hospital.";
            writeTextMessage(text);
            return;
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
            text = text + ", fall into the potal and got out from another.";
            writeTextMessage(text);
            repaint();
            return;
        }
        text = text + " and went through.";
        writeTextMessage(text);
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
    }

    public void setTextArea(JTextArea simpleGameTextArea) {
        myTextArea = simpleGameTextArea;
        textPosition = 0;
    }
}
