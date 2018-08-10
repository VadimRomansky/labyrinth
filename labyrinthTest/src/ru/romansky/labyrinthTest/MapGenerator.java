package ru.romansky.labyrinthTest;

import javafx.util.Pair;

import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Created by Vadim on 24.07.2018.
 */
public class MapGenerator {
    int height;
    int width;
    Stack<Mole> moles;
    final int randomCount = 1024;
    double branchingProbability;
    double stopProbability;
    Random random;
    int[][] visited;
    boolean allowCycles;
    boolean stopAfterCycle;
    int minRegionSize;
    int minotaurusCount;
    int portalsCount;
    int regionsCount;
    private MapPanel myMapPanel;

    public MapGenerator(int w, int h, int minSize, double stopP, double branchP, boolean allowCyclesV, boolean stopAfterCycleV, int minotaurs, int portals, MapPanel mapPanel) {
        myMapPanel = mapPanel;
        random = new Random();
        moles = new Stack<Mole>();
        height = h;
        width = w;
        visited = new int[width][height];
        minRegionSize = minSize;
        stopProbability = stopP;
        branchingProbability = branchP;
        allowCycles = allowCyclesV;
        stopAfterCycle = stopAfterCycleV;
        minotaurusCount = minotaurs;
        portalsCount = portals;
    }

    public LabyrinthMap generateEmptyMap() {
        return new LabyrinthMap(height, width);
    }

    public LabyrinthMap generateMap() throws InterruptedException {
        LabyrinthMap map = new LabyrinthMap(height, width);
        generateMap(map);
        return map;
    }

    public void generateMap(LabyrinthMap map) throws InterruptedException {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                visited[i][j] = 0;
            }
        }
        int visitedCount = 0;
        int totalCount = width * height;
        int currentSet = 1;

        while (visitedCount < totalCount) {
            Mole mole;
            Integer currentx = 0;
            Integer currenty = 0;
            if (moles.empty()) {
                Pair<Integer, Integer> current = findRandomNotVisited();
                currentx = current.getKey();
                currenty = current.getValue();
                mole = new Mole(currentx, currenty, branchingProbability, stopProbability, currentSet);
                visited[currentx][currenty] = 1;
                visitedCount++;
                currentSet++;
                map.cells[currentx][currenty].setId = mole.setId;
            } else {
                mole = moles.pop();
            }
            while (!mole.isstopped()) {
                Vector<Pair<Integer, Integer>> possibleSteps = collectPossibleSteps(mole, map);

                if (!possibleSteps.isEmpty()) {
                    int rand = random.nextInt(possibleSteps.size());

                    int nextx = possibleSteps.get(rand).getKey();
                    int nexty = possibleSteps.get(rand).getValue();

                    deleteBorderBetween(map, mole.currentx, mole.currenty, nextx, nexty);

                    if (visited[nextx][nexty] == 0) {
                        visited[nextx][nexty] = 1;
                        map.cells[nextx][nexty].setId = mole.setId;
                        visitedCount++;
                    } else {
                        if (stopAfterCycle) {
                            mole.stopped = true;
                        }
                    }
                    while (mole.isbranched()) {
                        possibleSteps = collectPossibleSteps(mole, map);
                        if (!possibleSteps.isEmpty()) {
                            rand = random.nextInt(possibleSteps.size());
                            int branchx = possibleSteps.get(rand).getKey();
                            int branchy = possibleSteps.get(rand).getValue();
                            deleteBorderBetween(map, mole.currentx, mole.currenty, branchx, branchy);
                            if (visited[branchx][branchy] == 0) {
                                visited[branchx][branchy] = 1;
                                map.cells[branchx][branchy].setId = mole.setId;
                                visitedCount++;
                                Mole branchMole = new Mole(branchx, branchy, branchingProbability, stopProbability, mole.setId);
                                moles.push(branchMole);
                            }
                        } else {
                            break;
                        }
                    }
                    mole.currentx = nextx;
                    mole.currenty = nexty;
                } else {
                    mole.stopped = true;
                }
            }
        }

        Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions = cleanupIsolatedRegions(map);

        updateConnectedCells(map);

        placePortalToEveryRegion(regions, map);
        placeArsenal(regions, map);
        placeHospital(regions, map);

        for(int i = 0; i < minotaurusCount; ++i) {
            placeMinotaur(regions, map);
        }
        placeCharacter(regions, map);
    }

    private void placeMinotaur(Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions, LabyrinthMap map) {
        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.width);
            if (cellFitToMinotaur(map, i, j)) {
                map.cells[i][j].addObject(new Minotaur());
                return;
            }
        }
    }

    private boolean cellFitToMinotaur(LabyrinthMap map, int i, int j) {
        return ((map.cells[i][j].type == CellType.SIMPLE_CELL) && map.cells[i][j].myObjects.isEmpty());
    }

    private void placeCharacter(Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions, LabyrinthMap map) {
        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.width);
            if (cellFitToCharacter(map, i, j)) {
                map.cells[i][j].addObject(new Character());
                return;
            }
        }
    }

    private boolean cellFitToCharacter(LabyrinthMap map, int i, int j) {
        return map.cells[i][j].myObjects.isEmpty();
    }

    private void placeHospital(Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions, LabyrinthMap map) {
        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.width);
            if (cellFitToHospital(map, i, j)) {
                HospitalCell cell = new HospitalCell(map.cells[i][j]);
                map.cells[i][j] = cell;
                return;
            }
        }
    }

    private boolean cellFitToHospital(LabyrinthMap map, int i, int j) {
        Cell cell = map.cells[i][j];
        return cell.type == CellType.SIMPLE_CELL;
    }

    private void placeArsenal(Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions, LabyrinthMap map) {
        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.width);
            if (cellFitToArsenal(map, i, j)) {
                ArsenalCell cell = new ArsenalCell(map.cells[i][j]);
                map.cells[i][j] = cell;
                return;
            }
        }
    }

    private boolean cellFitToArsenal(LabyrinthMap map, int i, int j) {
        Cell cell = map.cells[i][j];
        return cell.type == CellType.SIMPLE_CELL;
    }

    private void placePortalToEveryRegion(Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions, LabyrinthMap map) {
        for (int i = 0; i < regions.size(); ++i) {
            Vector<Pair<Integer, Integer>> region = regions.get(i).getValue();
            int hiddenCellNumber = 0;
            int hiddenCellBorders = 0;
            for (int j = 0; j < region.size(); ++j) {
                int x = region.get(j).getKey();
                int y = region.get(j).getValue();
                int borderCount = 4 - map.cells[x][y].connectedCells.size();
                if (borderCount >= hiddenCellBorders) {
                    hiddenCellNumber = j;
                    hiddenCellBorders = borderCount;
                }
                //todo rendomize!
            }
            int px = region.get(hiddenCellNumber).getKey();
            int py = region.get(hiddenCellNumber).getValue();
            //PortalCell portal = new PortalCell(-1, map.cells[px][py]);
            PortalCell portal = new PortalCell(i, map.cells[px][py]);
            map.cells[px][py] = portal;
        }
    }

    private void updateConnectedCells(LabyrinthMap map) {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                updateConnectedCells(i, j, map);
            }
        }
    }

    private void updateConnectedCells(int i, int j, LabyrinthMap map) {
        if (!map.horizontalBorders[i][j].exists()) {
            map.cells[i][j].connectedCells.add(new Pair<>(i, j - 1));
        }
        if (!map.horizontalBorders[i][j + 1].exists()) {
            map.cells[i][j].connectedCells.add(new Pair<>(i, j + 1));
        }
        if (!map.verticalBorders[i][j].exists()) {
            map.cells[i][j].connectedCells.add(new Pair<>(i - 1, j));
        }
        if (!map.verticalBorders[i + 1][j].exists()) {
            map.cells[i][j].connectedCells.add(new Pair<>(i + 1, j));
        }
    }

    private Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> cleanupIsolatedRegions(LabyrinthMap map) throws InterruptedException {
        Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions = collectRegions(map);
        sortRegionsByNumber(regions);
        if (regions.size() > 1) {
            while (regions.lastElement().getValue().size() < minRegionSize) {
                cleanupSmallestRegion(map, regions);
            }
            while(regions.size() > portalsCount && regions.size() > 1){
                cleanupSmallestRegion(map, regions);
            }
        }

        regionsCount = regions.size();
        return regions;
    }

    private void cleanupSmallestRegion(LabyrinthMap map, Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions) throws InterruptedException {
        Pair<Integer, Vector<Pair<Integer, Integer>>> temp = regions.lastElement();
        Vector<Pair<Integer, Integer>> region = temp.getValue();

        Pair<Integer, Integer> connectionCoordinates = findConnectionCoordinates(region, map);

        Pair<Integer, Integer> toConnectionCoordinates = findToConnectionoordinates(connectionCoordinates, map);

        int i = connectionCoordinates.getKey();
        int j = connectionCoordinates.getValue();
        int tempi = toConnectionCoordinates.getKey();
        int tempj = toConnectionCoordinates.getValue();

        int newSetId = map.cells[tempi][tempj].setId;
        int newRegionNumber = -1;
        for (int l = 0; l < regions.size(); ++l) {
            if (regions.get(l).getKey().equals(newSetId)) {
                newRegionNumber = l;
                break;
            }
        }
        Vector<Pair<Integer, Integer>> newRegion = regions.get(newRegionNumber).getValue();
        for (int l = 0; l < region.size(); ++l) {
            map.cells[region.get(l).getKey()][region.get(l).getValue()].setId = newSetId;
        }
        newRegion.addAll(region);
        regions.removeElementAt(regions.size() - 1);
        moveUpRegion(regions, newRegionNumber);

        deleteBorderBetween(map, i, j, tempi, tempj);
    }

    private void moveUpRegion(Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions, int newRegionNumber) {
        for (int i = newRegionNumber; i > 0; --i) {
            int curNumber = regions.get(i).getValue().size();
            int prevNumber = regions.get(i - 1).getValue().size();
            if (curNumber > prevNumber) {
                Pair<Integer, Vector<Pair<Integer, Integer>>> temp = regions.get(i - 1);
                regions.set(i - 1, regions.get(i));
                regions.set(i, temp);
            } else {
                break;
            }
        }
    }

    private Pair<Integer, Integer> findToConnectionoordinates(Pair<Integer, Integer> connectionCoordinates, LabyrinthMap map) {
        Vector<Pair<Integer, Integer>> coords = new Vector<>();
        int i = connectionCoordinates.getKey();
        int j = connectionCoordinates.getValue();

        if (i > 0) {
            if (map.cells[i - 1][j].setId != map.cells[i][j].setId) {
                coords.add(new Pair<>(i - 1, j));
            }
        }
        if (i < width - 1) {
            if (map.cells[i + 1][j].setId != map.cells[i][j].setId) {
                coords.add(new Pair<>(i + 1, j));
            }
        }
        if (j > 0) {
            if (map.cells[i][j - 1].setId != map.cells[i][j].setId) {
                coords.add(new Pair<>(i, j - 1));
            }
        }
        if (j < height - 1) {
            if (map.cells[i][j + 1].setId != map.cells[i][j].setId) {
                coords.add(new Pair<>(i, j + 1));
            }
        }

        int k = random.nextInt(coords.size());
        return coords.get(k);
    }

    private Pair<Integer, Integer> findConnectionCoordinates(Vector<Pair<Integer, Integer>> region, LabyrinthMap map) {
        Pair<Integer, Integer> result = null;
        while (true) {
            int k = random.nextInt(region.size());

            int i = region.get(k).getKey();
            int j = region.get(k).getValue();

            if (i > 0) {
                if (map.cells[i - 1][j].setId != map.cells[i][j].setId) {
                    result = new Pair<Integer, Integer>(i, j);
                    return result;
                }
            }
            if (i < width - 1) {
                if (map.cells[i + 1][j].setId != map.cells[i][j].setId) {
                    result = new Pair<Integer, Integer>(i, j);
                    return result;
                }
            }

            if (j > 0) {
                if (map.cells[i][j - 1].setId != map.cells[i][j].setId) {
                    result = new Pair<Integer, Integer>(i, j);
                    return result;
                }
            }

            if (j < width - 1) {
                if (map.cells[i][j + 1].setId != map.cells[i][j].setId) {
                    result = new Pair<Integer, Integer>(i, j);
                    return result;
                }
            }
        }
    }

    private void sortRegionsByNumber(Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions) {
        if (regions.size() <= 1) return;
        for (int i = regions.size(); i > 1; --i) {
            for (int j = 1; j < i; ++j) {
                int prevNumber = regions.get(j - 1).getValue().size();
                int curNumber = regions.get(j).getValue().size();
                if (curNumber > prevNumber) {
                    Pair<Integer, Vector<Pair<Integer, Integer>>> temp = regions.get(j - 1);
                    regions.set(j - 1, regions.get(j));
                    regions.set(j, temp);
                }
            }
        }
    }

    private Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> collectRegions(LabyrinthMap map) {
        Vector<Pair<Integer, Vector<Pair<Integer, Integer>>>> regions = new Vector<>();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int setId = map.cells[i][j].setId;
                Vector<Pair<Integer, Integer>> vector = null;
                for (int k = 0; k < regions.size(); ++k) {
                    Pair<Integer, Vector<Pair<Integer, Integer>>> pair = regions.get(k);
                    if (pair.getKey().equals(setId)) {
                        vector = pair.getValue();
                        break;
                    }
                }
                if (vector == null) {
                    vector = new Vector<>();
                    vector.add(new Pair<Integer, Integer>(i, j));
                    regions.add(new Pair(setId, vector));
                } else {
                    vector.add(new Pair<Integer, Integer>(i, j));
                }
            }
        }
        return regions;
    }

    private void deleteBorderBetween(LabyrinthMap map, int currentx, int currenty, int nextx, int nexty) throws InterruptedException {
        if (currentx == nextx) {
            deleteHorizontalBorderBetween(map, currentx, currenty, nexty);
        } else if (currenty == nexty) {
            deleteVerticalBorderBetween(map, currenty, currentx, nextx);
        }
        //myMapPanel.paintImmediately(0,0, myMapPanel.getWidth(), myMapPanel.getHeight());
        //sleep(500);
    }

    private void deleteVerticalBorderBetween(LabyrinthMap map, int currenty, int currentx, int nextx) throws InterruptedException {
        if (currentx > nextx) {
            map.verticalBorders[currentx][currenty].myExists = false;
        } else if (currentx < nextx) {
            map.verticalBorders[currentx + 1][currenty].myExists = false;
        }
    }

    private void deleteHorizontalBorderBetween(LabyrinthMap map, int currentx, int currenty, int nexty) throws InterruptedException {
        if (currenty > nexty) {
            map.horizontalBorders[currentx][currenty].myExists = false;
        } else if (currenty < nexty) {
            map.horizontalBorders[currentx][currenty + 1].myExists = false;
        }
    }

    private Vector<Pair<Integer, Integer>> collectPossibleSteps(Mole mole, LabyrinthMap map) {
        Vector<Pair<Integer, Integer>> result = new Vector<>();

        int tempi = mole.currentx;
        int tempj = mole.currenty;

        tempi = mole.currentx;
        tempj = mole.currenty + 1;
        if (stepUpIsPossible(mole, tempi, tempj, map)) {
            result.add(new Pair<>(tempi, tempj));
        }

        tempi = mole.currentx;
        tempj = mole.currenty - 1;
        if (stepDownIsPossible(mole, tempi, tempj, map)) {
            result.add(new Pair<>(tempi, tempj));
        }

        tempi = mole.currentx - 1;
        tempj = mole.currenty;
        if (stepLeftIsPossible(mole, tempi, tempj, map)) {
            result.add(new Pair<>(tempi, tempj));
        }

        tempi = mole.currentx + 1;
        tempj = mole.currenty;
        if (stepRightIsPossible(mole, tempi, tempj, map)) {
            result.add(new Pair<>(tempi, tempj));
        }

        return result;
    }

    private boolean stepUpIsPossible(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        if (tempi >= 0 && tempi < width && tempj >= 0 && tempj < height) {
            if ((visited[tempi][tempj] == 0) || (allowCycles && (map.cells[tempi][tempj].setId == mole.setId) && (map.horizontalBorders[tempi][mole.currenty + 1].exists()) && canDoUpCycle(mole, tempi, tempj, map))) {
                return true;
            }
        }
        return false;
    }

    private boolean canDoUpCycle(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        return oneOfThreeRightWallsExists(tempi, tempj, map) && oneOfThreeLeftWallsExists(tempi, tempj, map);
    }

    private boolean canDoDownCycle(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        return oneOfThreeRightWallsExists(tempi, tempj + 1, map) && oneOfThreeLeftWallsExists(tempi, tempj + 1, map);
    }

    private boolean oneOfThreeLeftWallsExists(int tempi, int tempj, LabyrinthMap map) {
        if (map.verticalBorders[tempi][tempj - 1].exists()) return true;
        if (map.verticalBorders[tempi][tempj].exists()) return true;
        if (map.horizontalBorders[tempi - 1][tempj].exists()) return true;
        return false;
    }

    private boolean oneOfThreeRightWallsExists(int tempi, int tempj, LabyrinthMap map) {
        if (map.verticalBorders[tempi + 1][tempj - 1].exists()) return true;
        if (map.verticalBorders[tempi + 1][tempj].exists()) return true;
        if (map.horizontalBorders[tempi + 1][tempj].exists()) return true;
        return false;
    }

    private boolean stepDownIsPossible(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        if (tempi >= 0 && tempi < width && tempj >= 0 && tempj < height) {
            if ((visited[tempi][tempj] == 0) || (allowCycles && (map.cells[tempi][tempj].setId == mole.setId) && (map.horizontalBorders[tempi][mole.currenty].exists()) && canDoDownCycle(mole, tempi, tempj, map))) {
                return true;
            }
        }
        return false;
    }

    private boolean stepLeftIsPossible(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        if (tempi >= 0 && tempi < width && tempj >= 0 && tempj < height) {
            if ((visited[tempi][tempj] == 0) || (allowCycles && (map.cells[tempi][tempj].setId == mole.setId) && (map.verticalBorders[mole.currentx][tempj].exists()) && canDoLeftCycle(mole, tempi, tempj, map))) {
                return true;
            }
        }
        return false;
    }

    private boolean canDoLeftCycle(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        return oneOfThreeUpWallsExists(tempi + 1, tempj, map) && oneOfThreeDownWallsExists(tempi + 1, tempj, map);
    }

    private boolean oneOfThreeDownWallsExists(int tempi, int tempj, LabyrinthMap map) {
        if (map.horizontalBorders[tempi - 1][tempj + 1].exists()) return true;
        if (map.horizontalBorders[tempi][tempj + 1].exists()) return true;
        if (map.verticalBorders[tempi][tempj + 1].exists()) return true;
        return false;
    }

    private boolean oneOfThreeUpWallsExists(int tempi, int tempj, LabyrinthMap map) {
        if (map.horizontalBorders[tempi - 1][tempj].exists()) return true;
        if (map.horizontalBorders[tempi][tempj].exists()) return true;
        if (map.verticalBorders[tempi][tempj - 1].exists()) return true;
        return false;
    }

    private boolean canDoRightCycle(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        return oneOfThreeUpWallsExists(tempi, tempj, map) && oneOfThreeDownWallsExists(tempi, tempj, map);
    }

    private boolean stepRightIsPossible(Mole mole, int tempi, int tempj, LabyrinthMap map) {
        if (tempi >= 0 && tempi < width && tempj >= 0 && tempj < height) {
            if ((visited[tempi][tempj] == 0) || (allowCycles && (map.cells[tempi][tempj].setId == mole.setId) && (map.verticalBorders[mole.currentx + 1][tempj].exists()) && canDoRightCycle(mole, tempi, tempj, map))) {
                return true;
            }
        }
        return false;
    }

    private Pair<Integer, Integer> findRandomNotVisited() {
        Vector<Pair<Integer, Integer>> notVisited = new Vector<Pair<Integer, Integer>>();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (visited[i][j] == 0) {
                    notVisited.add(new Pair<>(i, j));
                }
            }
        }

        int k = random.nextInt(notVisited.size());

        Pair<Integer, Integer> result = notVisited.get(k);

        return result;
    }

    class Mole {
        int currentx;
        int currenty;
        double branchingProbability;
        double stopProbability;
        boolean stopped;
        int setId;

        public Mole(int x, int y, double bp, double sp, int set) {
            currentx = x;
            currenty = y;
            branchingProbability = bp;
            stopProbability = sp;
            stopped = false;
            setId = set;
        }


        public boolean isstopped() {
            if (!stopped) {
                int stop = random.nextInt(randomCount);
                stopped = stop * 1.0 / randomCount < stopProbability;
            }
            return stopped;
        }

        public boolean isbranched() {
            int branched = random.nextInt(randomCount);
            return branched * 1.0 / randomCount < branchingProbability;
        }
    }
}
