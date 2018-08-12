package ru.romansky.labyrinthTest;

import javafx.util.Pair;

import java.util.*;

public class Util {
    public static int[][] EvaluateDistancesBFS(LabyrinthMap map, int starti, int startj, boolean checkMinotaurs, boolean direction, Pair<Integer, Integer>[][] parents){
        int[][] result = new int[map.width][map.height];
        int totalCount = map.width*map.height;
        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                result[i][j] = -1;
                if(parents != null) {
                    parents[i][j] = null;
                }
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
                    if(parents != null){
                        parents[tempi][tempj] = new Pair<Integer, Integer>(curi, curj);
                    }
                    if((!checkMinotaurs) || (!map.cells[tempi][tempj].hasMinotaurus())){
                        queue.addLast(new Pair<Integer, Integer>(tempi, tempj));
                    }
                }
            }
        }
        return result;
    }

    public static List<Pair<Integer,Integer>> findWayBetweenCells(LabyrinthMap map, int starti, int startj, int finishi, int finishj, boolean checkMinotaurs, boolean direction){
        LinkedList<Pair<Integer, Integer>> result = new LinkedList<>();
        //result.add(new Pair<Integer, Integer>(starti, startj));
        boolean exists = false;

        LinkedList<Pair<Integer, Integer>> queue = new LinkedList<Pair<Integer, Integer>>();

        queue.addLast(new Pair<>(starti, startj));
        int[][] visited = new int[map.width][map.height];

        Pair<Integer, Integer>[][] parents = new Pair[map.width][map.height];

        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                visited[i][j] = 0;
                parents[i][j] = null;
            }
        }
        visited[starti][startj] = 1;


        while(!queue.isEmpty()){
            Pair<Integer, Integer> pair = queue.pollFirst();
            int curi = pair.getKey();
            int curj = pair.getValue();

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

                if(visited[tempi][tempj] == 0){
                    visited[tempi][tempj] = 1;
                    parents[tempi][tempj] = new Pair<Integer, Integer>(curi, curj);
                    if(tempi == finishi && tempj == finishj){
                        exists = true;
                        break;
                    }
                    if((!checkMinotaurs) || (!map.cells[tempi][tempj].hasMinotaurus())){
                        queue.addLast(new Pair<Integer, Integer>(tempi, tempj));
                    }
                }
            }
        }

        result.addFirst(new Pair<Integer, Integer>(finishi, finishj));
        Pair<Integer, Integer> prev = parents[finishi][finishj];
        while(prev != null){
            int i = prev.getKey();
            int j = prev.getValue();
            result.addFirst(new Pair<Integer, Integer>(i,j));
            prev = parents[i][j];
        }

        if(exists) {
            return result;
        } else {
            return null;
        }
    }

    public static int evaluateNumberOfNearPortals(LabyrinthMap map, int starti, int startj){
        return evaluateNumberOfNearPortals(map, starti, startj, 1, true);
    }

    public static int evaluateNumberOfNearPortals(LabyrinthMap map, int starti, int startj, int length){
        return evaluateNumberOfNearPortals(map, starti, startj, length, false);
    }

    public static int evaluateNumberOfNearPortals(LabyrinthMap map, int starti, int startj, int length, boolean unlimited){
        int[][] distances = new int[map.width][map.height];
        int result = 0;
        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                distances[i][j] = -1;
            }
        }
        distances[starti][startj] = 0;
        LinkedList<Pair<Integer, Integer>> queue = new LinkedList<Pair<Integer, Integer>>();

        queue.addLast(new Pair<>(starti, startj));
        while(!queue.isEmpty()){
            Pair<Integer, Integer> pair = queue.pollFirst();
            int curi = pair.getKey();
            int curj = pair.getValue();
            int dist = distances[curi][curj] + 1;

            if(dist <= length || unlimited) {
                for (Pair<Integer, Integer> tempPair : map.cells[curi][curj].connectedCells) {
                    int tempi = tempPair.getKey();
                    int tempj = tempPair.getValue();
                    if (map.cells[tempi][tempj].type == CellType.PORTAL) {
                        result++;
                    } else {
                        if (distances[tempi][tempj] == -1) {
                            distances[tempi][tempj] = dist;
                            queue.addLast(new Pair<Integer, Integer>(tempi, tempj));
                        }
                    }
                }
            }
        }
        return result;
    }
}
