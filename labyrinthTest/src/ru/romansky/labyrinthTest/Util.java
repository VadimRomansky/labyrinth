package ru.romansky.labyrinthTest;

import javafx.util.Pair;

import java.util.*;

public class Util {
    public static int[][] EvaluateDistancesBFS(LabyrinthMap map, int width, int height, int starti, int startj, boolean checkMinotaurs, boolean direction){
        int[][] result = new int[width][height];
        int totalCount = width*height;
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){
                result[i][j] = -1;
            }
        }
        result[starti][startj] = 0;
        LinkedList<Pair<Integer, Integer>> queue = new LinkedList<Pair<Integer, Integer>>();

        queue.addLast(new Pair<>(starti, startj));
        int counter = 0;
        while(!queue.isEmpty()){
            Pair<Integer, Integer> pair = queue.pollFirst();
            int curi = pair.getKey();
            int curj = pair.getValue();
            int dist = result[curi][curj] + 1;

            for (Pair<Integer, Integer> tempPair:map.cells[curi][curj].connectedCells) {
                int tempi = tempPair.getKey();
                int tempj = tempPair.getValue();

                if(map.cells[tempi][tempj].type == CellType.PORTAL){
                    int portali;
                    int portalj;
                    if(direction) {
                        portali = ((PortalCell) map.cells[tempi][tempj]).portalx;
                        portalj = ((PortalCell) map.cells[tempi][tempj]).portaly;
                    } else {
                        portali = ((PortalCell) map.cells[tempi][tempj]).prev.x;
                        portalj = ((PortalCell) map.cells[tempi][tempj]).prev.x;
                    }
                    tempi = portali;
                    tempj = portalj;
                }

                if(result[tempi][tempj] == -1){
                    counter++;
                    result[tempi][tempj] = dist;
                    if((!checkMinotaurs) || (!map.cells[tempi][tempj].hasMinotaurus())){
                        queue.addLast(new Pair<Integer, Integer>(tempi, tempj));
                    }
                }
            }
        }
        return result;
    }
}
