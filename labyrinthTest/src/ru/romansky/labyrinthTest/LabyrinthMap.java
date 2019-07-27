package ru.romansky.labyrinthTest;

/**
 * Created by Vadim on 24.07.2018.
 */
public class LabyrinthMap {
    int width;
    int height;

    int arsenalx = -1;
    int arsenaly = -1;
    int hospitalx = -1;
    int hospitaly = -1;
    int exitx = -1;
    int exity = -1;

    int minotaursCount = 0;
    int aliveMinotaursCount = 0;
    int portalsCount = 0;

    Cell[][] cells;
    VerticalBorder[][] verticalBorders;
    HorizontalBorder[][] horizontalBorders;

    LabyrinthMap(int w, int h) {
        width = w;
        height = h;

        cells = new Cell[width][height];
        verticalBorders = new VerticalBorder[width+1][height];
        horizontalBorders = new HorizontalBorder[width][height+1];
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){
                cells[i][j] = new Cell(i,j,0);
            }
        }

        for(int i = 0; i < width+1; ++i){
            for(int j = 0; j < height; ++j){
                verticalBorders[i][j] = new VerticalBorder(BorderState.EXISTS);
            }
        }

        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height+1; ++j){
                horizontalBorders[i][j] = new HorizontalBorder(BorderState.EXISTS);
            }
        }
    }

    public void print(){
        for(int j = 0; j < height; ++j){
            printHorizontalBorderLine(j);
            printCellLine(j);
        }
        printHorizontalBorderLine(height);
    }

    private void printCellLine(int j) {
        for(int i = 0; i < width; ++i){
            verticalBorders[i][j].print();
            cells[i][j].print();
        }
        verticalBorders[width][j].print();
        System.out.println();
    }

    private void printHorizontalBorderLine(int j) {
        for(int i = 0; i < width; ++i){
            System.out.print('*');
            horizontalBorders[i][j].print();
        }
        System.out.print('*');
        System.out.println();
    }
}
