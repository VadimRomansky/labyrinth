package ru.romansky.labyrinthTest;

import java.util.*;

public class Util {
    public static LabyrinthMap compressListToOne(List<LabyrinthMap> mapList) throws Exception {
        if(mapList == null || mapList.size() == 0){
            return null;
        }
        LabyrinthMap newMap = new LabyrinthMap(mapList.get(0).width, mapList.get(0).height, BorderState.UNDEFINED);
        int width = newMap.width;
        int height = newMap.height;
        Iterator<LabyrinthMap> iterator = mapList.iterator();
        while(iterator.hasNext()){
            LabyrinthMap map = iterator.next();
            for(int i = 0; i <= width; ++i){
                for(int j = 0; j < height; ++j) {
                    if (map.verticalBorders[i][j].state() != BorderState.UNDEFINED){
                        if(newMap.verticalBorders[i][j].state() == BorderState.UNDEFINED) {
                            newMap.verticalBorders[i][j].setState(map.verticalBorders[i][j].state());
                        }else if(newMap.verticalBorders[i][j].state() != map.verticalBorders[i][j].state()){
                            throw new Exception("Maps' vertical borders are in conflict");
                        }
                    }
                }
            }

            for(int i = 0; i < width; ++i){
                for(int j = 0; j <= height; ++j) {
                    if (map.horizontalBorders[i][j].state() != BorderState.UNDEFINED){
                        if(newMap.horizontalBorders[i][j].state() == BorderState.UNDEFINED) {
                            newMap.horizontalBorders[i][j].setState(map.horizontalBorders[i][j].state());
                        }else if(newMap.horizontalBorders[i][j].state() != map.horizontalBorders[i][j].state()){
                            throw new Exception("Maps' horizontal borders are in conflict");
                        }
                    }
                }
            }

            for(int i = 0; i < width; ++i){
                for(int j = 0; j < height; ++j){
                    if(map.cells[i][j].state == CellState.VISITED){
                        if(newMap.cells[i][j].state == CellState.UNDEFINED){
                            if(map.cells[i][j].type == CellType.ARSENAL){
                                newMap.cells[i][j] = new ArsenalCell(i,j, 0);
                            }
                            if(map.cells[i][j].type == CellType.HOSPITAL){
                                newMap.cells[i][j] = new HospitalCell(i,j,0);
                            }
                            if(map.cells[i][j].type == CellType.PORTAL){
                                newMap.cells[i][j] = new PortalCell(((PortalCell)map.cells[i][j]).number, ((PortalCell)map.cells[i][j]).visibleNumber,i,j,0);
                            }
                            newMap.cells[i][j].state = CellState.VISITED;
                            if(map.cells[i][j].minotaur != null) {
                                newMap.cells[i][j].minotaur = new Minotaur(map.cells[i][j].minotaur);
                            } else {
                                newMap.cells[i][j].minotaur = null;
                            }
                        } else {
                            if(newMap.cells[i][j].type == CellType.PORTAL && map.cells[i][j].type == CellType.PORTAL){
                                if(((PortalCell)newMap.cells[i][j]).visibleNumber < 0){
                                    ((PortalCell)newMap.cells[i][j]).visibleNumber = ((PortalCell)map.cells[i][j]).visibleNumber;
                                }
                            }
                            if(map.cells[i][j].type != newMap.cells[i][j].type){
                                throw new Exception("Cell types are in conflict");
                            }
                            if(map.cells[i][j].minotaur == null){
                                if(newMap.cells[i][j].minotaur != null){
                                    throw new Exception("Minotaurs are in comflict");
                                }
                            } else {
                                if(newMap.cells[i][j].minotaur == null){
                                    throw new Exception("Minotaurs are in conflict");
                                } else {
                                    if(!map.cells[i][j].minotaur.isAlive()){
                                        newMap.cells[i][j].minotaur.kill();
                                    }
                                    if(!map.cells[i][j].minotaur.isSupposedAlive()){
                                        newMap.cells[i][j].minotaur.confirmKill();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j) {
                for (LabyrinthMap map :
                        mapList) {
                    if(newMap.cells[i][j].type == CellType.PORTAL && map.cells[i][j].type == CellType.PORTAL){
                            ((PortalCell)map.cells[i][j]).visibleNumber = ((PortalCell)newMap.cells[i][j]).visibleNumber;
                    }
                }
            }
        }

        return newMap;
    }

    public static boolean mapFitingToAll(LabyrinthMap mainMap, List<LabyrinthMap> additionalMaps, LabyrinthMap additionalMap, int shiftX, int shiftY) {
        if(!mapFiting(mainMap, additionalMap, shiftX, shiftY)){
            return false;
        }
        for (LabyrinthMap map :
                additionalMaps) {
            if(!mapFiting(map, additionalMap, shiftX, shiftY)){
                return false;
            }
        }
        return true;
    }

    public static boolean mapFiting(LabyrinthMap mainMap, LabyrinthMap additionalMap, int shiftX, int shiftY){
        for(int i = 0; i < additionalMap.width+1; ++i){
            for(int j = 0; j < additionalMap.height; ++j){
                if(!wallFitting(mainMap.verticalBorders[i+shiftX][j+shiftY],additionalMap.verticalBorders[i][j])){
                    return false;
                }
            }
        }

        for(int i = 0; i < additionalMap.width; ++i){
            for(int j = 0; j < additionalMap.height+1; ++j){
                if(!wallFitting(mainMap.horizontalBorders[i+shiftX][j+shiftY],additionalMap.horizontalBorders[i][j])){
                    return false;
                }
            }
        }

        for(int i = 0; i < additionalMap.width; ++i){
            for(int j = 0; j < additionalMap.height; ++j){
                if(!cellFitting(mainMap.cells[i+shiftX][j + shiftY], additionalMap.cells[i][j])){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean cellFitting(Cell mainCell, Cell additionalCell) {
        if(mainCell.state == CellState.UNDEFINED){
            return true;
        }
        if(additionalCell.state == CellState.UNDEFINED){
            return true;
        }
        if(mainCell.type != additionalCell.type){
            return false;
        }
        if(mainCell.minotaur != null){
            if(additionalCell.minotaur == null){
                return false;
            }
            if(mainCell.minotaur.isAlive()){
                if(!additionalCell.minotaur.isAlive()){
                    return false;
                }
            }
            return true;
        }
        if(additionalCell.minotaur != null){
            return false;
        }
        return true;
    }

    private static boolean wallFitting(Border mainBorder, Border additionalBorder) {
        if(mainBorder.state() == BorderState.UNDEFINED){
            return true;
        }
        if(additionalBorder.state() == BorderState.UNDEFINED){
            return true;
        }
        if(mainBorder.state() == additionalBorder.state()){
            return true;
        }
        return false;
    }


    public static int[][] EvaluateDistancesBFS(LabyrinthMap map, int starti, int startj, boolean checkMinotaurs, boolean direction, CoordinatePair[][] parents){
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
        LinkedList<CoordinatePair> queue = new LinkedList<CoordinatePair>();

        queue.addLast(new CoordinatePair(starti, startj));
        int counter = 0;
        while(!queue.isEmpty()){
            CoordinatePair pair = queue.pollFirst();
            int curi = pair.x;
            int curj = pair.y;
            int dist = result[curi][curj] + 1;

            for (CoordinatePair tempPair:map.cells[curi][curj].connectedCells) {
                int tempi = tempPair.x;
                int tempj = tempPair.y;

                if(map.cells[tempi][tempj].type == CellType.PORTAL){
                    int portali;
                    int portalj;
                    if(direction) {
                        portali = ((PortalCell) map.cells[tempi][tempj]).portalx;
                        portalj = ((PortalCell) map.cells[tempi][tempj]).portaly;
                    } else {
                        portali = ((PortalCell) map.cells[tempi][tempj]).prev.x;
                        portalj = ((PortalCell) map.cells[tempi][tempj]).prev.y;
                    }
                    tempi = portali;
                    tempj = portalj;
                }

                if(result[tempi][tempj] == -1){
                    counter++;
                    result[tempi][tempj] = dist;
                    if(parents != null){
                        parents[tempi][tempj] = new CoordinatePair(curi, curj);
                    }
                    if((!checkMinotaurs) || (!map.cells[tempi][tempj].hasMinotaurus())){
                        queue.addLast(new CoordinatePair(tempi, tempj));
                    }
                }
            }
        }
        return result;
    }

    public static List<CoordinatePair> findWayBetweenCells(LabyrinthMap map, int starti, int startj, int finishi, int finishj, boolean checkMinotaurs, boolean direction){
        LinkedList<CoordinatePair> result = new LinkedList<>();
        //result.add(new Pair<Integer, Integer>(starti, startj));
        boolean exists = false;

        LinkedList<CoordinatePair> queue = new LinkedList<CoordinatePair>();

        queue.addLast(new CoordinatePair(starti, startj));
        int[][] visited = new int[map.width][map.height];

        CoordinatePair[][] parents = new CoordinatePair[map.width][map.height];

        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                visited[i][j] = 0;
                parents[i][j] = null;
            }
        }
        visited[starti][startj] = 1;


        while(!queue.isEmpty()){
            CoordinatePair pair = queue.pollFirst();
            int curi = pair.x;
            int curj = pair.y;

            for (CoordinatePair tempPair:map.cells[curi][curj].connectedCells) {
                int tempi = tempPair.x;
                int tempj = tempPair.y;

                if(map.cells[tempi][tempj].type == CellType.PORTAL){
                    int portali;
                    int portalj;
                    if(direction) {
                        portali = ((PortalCell) map.cells[tempi][tempj]).portalx;
                        portalj = ((PortalCell) map.cells[tempi][tempj]).portaly;
                    } else {
                        portali = ((PortalCell) map.cells[tempi][tempj]).prev.x;
                        portalj = ((PortalCell) map.cells[tempi][tempj]).prev.y;
                    }
                    tempi = portali;
                    tempj = portalj;
                }

                if(visited[tempi][tempj] == 0){
                    visited[tempi][tempj] = 1;
                    parents[tempi][tempj] = new CoordinatePair(curi, curj);
                    if(tempi == finishi && tempj == finishj){
                        exists = true;
                        break;
                    }
                    if((!checkMinotaurs) || (!map.cells[tempi][tempj].hasMinotaurus())){
                        queue.addLast(new CoordinatePair(tempi, tempj));
                    }
                }
            }
        }

        result.addFirst(new CoordinatePair(finishi, finishj));
        CoordinatePair prev = parents[finishi][finishj];
        while(prev != null){
            int i = prev.x;
            int j = prev.y;
            result.addFirst(new CoordinatePair(i,j));
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
        LinkedList<CoordinatePair> queue = new LinkedList<CoordinatePair>();

        queue.addLast(new CoordinatePair(starti, startj));
        while(!queue.isEmpty()){
            CoordinatePair pair = queue.pollFirst();
            int curi = pair.x;
            int curj = pair.y;
            int dist = distances[curi][curj] + 1;

            if(dist <= length || unlimited) {
                for (CoordinatePair tempPair : map.cells[curi][curj].connectedCells) {
                    int tempi = tempPair.x;
                    int tempj = tempPair.y;
                    if (map.cells[tempi][tempj].type == CellType.PORTAL) {
                        result++;
                    } else {
                        if (distances[tempi][tempj] == -1) {
                            distances[tempi][tempj] = dist;
                            queue.addLast(new CoordinatePair(tempi, tempj));
                        }
                    }
                }
            }
        }
        return result;
    }
}
