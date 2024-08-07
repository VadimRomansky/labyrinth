package ru.romansky.labyrinthTest;

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
    long randomSeed;
    int[][] visited;
    boolean allowCycles;
    boolean stopAfterCycle;
    int minRegionSize;
    int minotaurusCount;
    int portalsCount;
    int regionsCount;
    int maxRegionsCount;
    int fakeKeysCount;
    private MapPanelBase myMapPanel;

    public MapGenerator(int w, int h, int minSize, double stopP, double branchP, boolean allowCyclesV, boolean stopAfterCycleV, int minotaurs, int portals, int maxRegions, int fakeKeys, MapPanelBase mapPanel) {
        myMapPanel = mapPanel;
        random = new Random();
        randomSeed = random.nextInt();
        //randomSeed = 85779687;
        random.setSeed(randomSeed);
        System.out.print("random seed = ");
        System.out.print(randomSeed);
        System.out.println();
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
        maxRegionsCount = maxRegions;
        fakeKeysCount = fakeKeys;
    }

    public MapGenerator(MapGeneratorInfo info, MapPanelBase mapPanel){
        myMapPanel = mapPanel;
        random = new Random();
        randomSeed = random.nextInt();
        //randomSeed = -1518730460;
        random.setSeed(randomSeed);
        System.out.print("random seed = ");
        System.out.print(randomSeed);
        System.out.println();
        moles = new Stack<Mole>();
        height = info.height;
        width = info.width;
        visited = new int[width][height];
        minRegionSize = info.minSize;
        stopProbability = info.stopP;
        branchingProbability = info.branchP;
        allowCycles = info.allowCycle;
        stopAfterCycle = info.stopAfterCycle;
        minotaurusCount = info.minotaurs;
        portalsCount = info.portals;
        maxRegionsCount = info.maxRegions;
        fakeKeysCount = info.fakeKeys;
    }

    public LabyrinthMap generateEmptyMap() {
        return new LabyrinthMap(width, height);
    }

    public LabyrinthMap generateMap() throws InterruptedException {
        LabyrinthMap map = new LabyrinthMap(width, height);
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
                CoordinatePair current = findRandomNotVisited();
                currentx = current.x;
                currenty = current.y;
                mole = new Mole(currentx, currenty, branchingProbability, stopProbability, currentSet);
                visited[currentx][currenty] = 1;
                visitedCount++;
                currentSet++;
                map.cells[currentx][currenty].setId = mole.setId;
            } else {
                mole = moles.pop();
            }
            while (!mole.isstopped()) {
                Vector<CoordinatePair> possibleSteps = collectPossibleSteps(mole, map);

                if (!possibleSteps.isEmpty()) {
                    int rand = random.nextInt(possibleSteps.size());

                    int nextx = possibleSteps.get(rand).x;
                    int nexty = possibleSteps.get(rand).y;

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
                            int branchx = possibleSteps.get(rand).x;
                            int branchy = possibleSteps.get(rand).y;
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

        Vector<Pair<Integer, Vector<CoordinatePair>>> regions = cleanupIsolatedRegions(map);

        updateConnectedCells(map);

        placePortalToEveryRegion(regions, map);
        placeRestPortals(regions, map);

        //todo add rest of portals
        connectPortals(map);
        placeArsenal(regions, map);
        placeHospital(regions, map);

        List<CoordinatePair> wayFromHtoA = Util.findWayBetweenCells(map, map.hospitalx, map.hospitaly, map.arsenalx, map.arsenaly, true, true);

        for(int i = 0; i < minotaurusCount; ++i) {
            placeMinotaur(regions, map, wayFromHtoA);
        }
        placeKeys(map);
        placeMob(map);
        placeCharacter(regions, map);

        placeExit(map);

        outputDebubInfo(map);
    }

    private void placeMob(LabyrinthMap map) {

        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.height);
            if (cellFitToMob(map, i, j)) {
                map.cells[i][j].addMob(new Mob());
                map.mobx = i;
                map.moby = j;
                return;
            }
        }
    }

    private boolean cellFitToMob(LabyrinthMap map, int i, int j) {

        return map.cells[i][j].minotaur == null;
    }


    private void placeExit(LabyrinthMap map) {
        int randN = random.nextInt(2);
        if(randN == 0){
            //vertical;
            randN = random.nextInt(2);
            int i = width*randN;
            int j = random.nextInt(height);
            map.verticalBorders[i][j].setState(BorderState.DOOR);
        } else {
            //horizontal;
            randN = random.nextInt(2);
            int i = random.nextInt(width);
            int j = height*randN;
            map.horizontalBorders[i][j].setState(BorderState.DOOR);
        }
    }

    private void outputDebubInfo(LabyrinthMap map) {
        List<CoordinatePair> wayFromHtoA;
        int[][] dist = Util.EvaluateDistancesBFS(map, map.arsenalx, map.arsenaly, false, false, null);
        int[][] dist1 = Util.EvaluateDistancesBFS(map, map.arsenalx, map.arsenaly, true, true, null);

        System.out.print("distances from arsenal without minotaurs\n");
        for(int j = 0; j < height; ++j){
            for(int i = 0; i < width; ++i){
                System.out.print(dist[i][j]);
                System.out.print(' ');
            }
            System.out.print('\n');
        }
        System.out.print("distances from arsenal with minotaurs\n");
        for(int j = 0; j < height; ++j){
            for(int i = 0; i < width; ++i){
                System.out.print(dist1[i][j]);
                System.out.print(' ');
            }
            System.out.print('\n');
        }

        dist = Util.EvaluateDistancesBFS(map, map.hospitalx, map.hospitaly, false, true, null);
        dist1 = Util.EvaluateDistancesBFS(map, map.hospitalx, map.hospitaly, true, true, null);

        System.out.print("distances from hospital without minotaurs\n");
        for(int j = 0; j < height; ++j){
            for(int i = 0; i < width; ++i){
                System.out.print(dist[i][j]);
                System.out.print(' ');
            }
            System.out.print('\n');
        }
        System.out.print("distances from hospital with minotaurs\n");
        for(int j = 0; j < height; ++j){
            for(int i = 0; i < width; ++i){
                System.out.print(dist1[i][j]);
                System.out.print(' ');
            }
            System.out.print('\n');
        }

        wayFromHtoA = Util.findWayBetweenCells(map, map.hospitalx, map.hospitaly, map.arsenalx, map.arsenaly, false, true);
        if(wayFromHtoA != null){
            System.out.print("way from hospital to arsenal\n");
            for (CoordinatePair pair :
                    wayFromHtoA) {
                System.out.print(pair.x);
                System.out.print(' ');
                System.out.print(pair.y);
                System.out.print('\n');
            }
        } else {
            System.out.print("there is no way from hospital to arsenal\n");
        }
        wayFromHtoA = Util.findWayBetweenCells(map, map.hospitalx, map.hospitaly, map.arsenalx, map.arsenaly, true, true);
        if(wayFromHtoA != null){
            System.out.print("way from hospital to arsenal free from minotaurs\n");
            for (CoordinatePair pair :
                    wayFromHtoA) {
                System.out.print(pair.x);
                System.out.print(' ');
                System.out.print(pair.y);
                System.out.print('\n');
            }
        } else {
            System.out.print("there is no way from hospital to arsenal free from minotaurs\n");
        }
    }

    private void placeRestPortals(Vector<Pair<Integer, Vector<CoordinatePair>>> regions, LabyrinthMap map) {
        int curPortalsCount = regions.size();
        while(curPortalsCount < portalsCount){
            while (true) {
                int i = random.nextInt(map.width);
                int j = random.nextInt(map.height);
                if (cellFitToPortal(map.cells[i][j], map)) {
                    PortalCell portal = new PortalCell(curPortalsCount, map.cells[i][j]);
                    map.cells[i][j] = portal;
                    map.portalsCount++;
                    break;
                }
            }
            curPortalsCount++;
        }
    }

    private boolean cellFitToPortal(Cell cell, LabyrinthMap map) {
        if(cell.type != CellType.SIMPLE_CELL){
            return false;
        }
        //must have some simple neighbout
        boolean hasSimpleNeighbour = hasSimpleNeighbour(cell, map);
        if(!hasSimpleNeighbour){
            return false;
        }
        //neighbours must have simple neighbour
        for (CoordinatePair tempCell :
                cell.connectedCells) {
            int tempi = tempCell.x;
            int tempj = tempCell.y;
            if(map.cells[tempi][tempj].type == CellType.PORTAL) {
                if (!hasSimpleNeighbourExceptThis(map.cells[tempi][tempj], map, cell.x, cell.y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasSimpleNeighbourExceptThis(Cell cell, LabyrinthMap map, int i, int j) {
        boolean result = false;
        for (CoordinatePair tempCell :
                cell.connectedCells) {
            int tempi = tempCell.x;
            int tempj = tempCell.y;
            if((tempi != i) && (tempj != j) && map.cells[tempi][tempj].type == CellType.SIMPLE_CELL){
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean hasSimpleNeighbour(Cell cell, LabyrinthMap map) {
        boolean result = false;
        for (CoordinatePair tempCell :
                cell.connectedCells) {
            int tempi = tempCell.x;
            int tempj = tempCell.y;
            if(map.cells[tempi][tempj].type == CellType.SIMPLE_CELL){
                result = true;
                break;
            }
        }
        return result;
    }

    private void connectPortals(LabyrinthMap map) {
        LinkedList<PortalCell> portals = new LinkedList<>();
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){
                if(map.cells[i][j] instanceof PortalCell){
                    portals.add((PortalCell) map.cells[i][j]);
                }
            }
        }
        Vector<PortalCell> sortedPortals = new Vector<>(portals.size());
        int portalsCount = portals.size();
        for(int i = 0; i < portalsCount; ++i){
            int index = random.nextInt(portals.size());
            PortalCell portal = portals.remove(index);
            sortedPortals.add(i, portal);
        }
        if(portalsCount == 1){
            System.out.println("portals number can not be 1");
            System.exit(0);
        }
        if(portalsCount > 1) {
            for (int i = 0; i < portalsCount - 1; ++i) {
                PortalCell portal = sortedPortals.get(i);
                PortalCell nextPortal = sortedPortals.get(i + 1);
                portal.setNumber(i + 1);
                portal.setPortalCoordinates(nextPortal.x, nextPortal.y);
                portal.next = nextPortal;
                if (i > 0) {
                    portal.prev = sortedPortals.get(i - 1);
                }
            }
            PortalCell lastPortal = sortedPortals.get(portalsCount - 1);
            PortalCell firstPortal = sortedPortals.get(0);
            lastPortal.setNumber(portalsCount);
            lastPortal.setPortalCoordinates(firstPortal.x, firstPortal.y);
            lastPortal.prev = sortedPortals.get(portalsCount - 2);
            lastPortal.next = firstPortal;
            firstPortal.prev = lastPortal;
        }
    }

    private void placeKeys(LabyrinthMap map){
        int n = random.nextInt(4);

        for(int i = 0; i < fakeKeysCount+1; ++i) {
            boolean value = (i == n);
            KeyMapObject key = new KeyMapObject(value, i);
            placeKey(map, key);
        }
    }

    private void placeKey(LabyrinthMap map, KeyMapObject key) {
        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.height);
            //int i = 2;
            //int j = 4;
            if (cellFitToKey(map, i, j)) {
                map.cells[i][j].mapObjects.add(key);
                System.out.print("key at " + i + " " + j + "\n");
                break;
            }
        }
    }

    private boolean cellFitToKey(LabyrinthMap map, int i, int j) {
        if(map.cells[i][j].type == CellType.SIMPLE_CELL){
            if(map.cells[i][j].mapObjects.size() == 0){
                return true;
            }
        }
        return false;
    }

    private void placeMinotaur(Vector<Pair<Integer, Vector<CoordinatePair>>> regions, LabyrinthMap map, List<CoordinatePair> wayFromHtoA) {
        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.height);
            if (cellFitToMinotaur(map, i, j, wayFromHtoA)) {
                map.cells[i][j].minotaur = new Minotaur();
                map.minotaursCount++;
                map.aliveMinotaursCount = map.minotaursCount;
                return;
            }
        }
    }

    private boolean cellFitToMinotaur(LabyrinthMap map, int i, int j, List<CoordinatePair> wayFromHtoA) {
        if (!((map.cells[i][j].type == CellType.SIMPLE_CELL) && map.cells[i][j].minotaur == null)){
            return false;
        }
        for (CoordinatePair pair : wayFromHtoA) {
            int tempi = pair.x;
            int tempj = pair.y;
            if(tempi == i && tempj == j){
                return false;
            }
        }
        return true;
    }

    private void placeCharacter(Vector<Pair<Integer, Vector<CoordinatePair>>> regions, LabyrinthMap map) {
        while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.height);
            if (cellFitToCharacter(map, i, j)) {
                map.cells[i][j].addCharacter(new Character());
                return;
            }
        }
    }

    private boolean cellFitToCharacter(LabyrinthMap map, int i, int j) {
        if (map.cells[i][j].minotaur != null){
            return false;
        }
        if((map.mobx == i)&&(map.moby == j)){
            return false;
        };
        return true;
    }

    private void placeHospital(Vector<Pair<Integer, Vector<CoordinatePair>>> regions, LabyrinthMap map) {
        /*while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.height);
            if (cellFitToHospital(map, i, j)) {
                HospitalCell cell = new HospitalCell(map.cells[i][j]);
                map.cells[i][j] = cell;
                map.hospitalx = i;
                map.hospitaly = j;
                return;
            }
        }*/
        CoordinatePair[][] parents = new CoordinatePair[width][height];
        int[][] dist = Util.EvaluateDistancesBFS(map, map.arsenalx, map.arsenaly, false, false, parents);
        CoordinatePair tempCoords = findPlaceForHospital(map, dist);

        int i = tempCoords.x;
        int j = tempCoords.y;
        HospitalCell cell = new HospitalCell(map.cells[i][j]);
        map.cells[i][j] = cell;
        map.hospitalx = i;
        map.hospitaly = j;
    }

    private CoordinatePair findPlaceForHospital(LabyrinthMap map, int[][] dist) {
        Vector<Triplet<Integer, Integer, Integer>> candidates = new Vector<>();
        int preferedDistToArsenal = (map.width + map.height);
        int lengthToPortal = preferedDistToArsenal/4;
        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                if((dist[i][j] >= preferedDistToArsenal - 1) && (dist[i][j] <= preferedDistToArsenal + 1) && (map.cells[i][j].type == CellType.SIMPLE_CELL)){
                    int nearPortals = Util.evaluateNumberOfNearPortals(map, i, j, lengthToPortal);
                    boolean added = false;
                    for(int l = 0; l < candidates.size(); ++l){
                        if(nearPortals > candidates.get(l).getFirst()){
                            candidates.add(l, new Triplet<Integer, Integer, Integer>(nearPortals, i, j));
                            added = true;
                            break;
                        }
                    }
                    if(!added){
                        candidates.add(candidates.size(), new Triplet<Integer, Integer, Integer>(nearPortals, i, j));
                    }
                }
            }
        }

        //todo
        if(candidates.isEmpty()){
            for(int currentDist = (map.width + map.height)/3 - 2; currentDist > 0; --currentDist){
                for(int i = 0; i < map.width; ++i){
                    for(int j = 0; j < map.height; ++j){
                        if((dist[i][j] == currentDist) && (map.cells[i][j].type == CellType.SIMPLE_CELL)){
                            return new CoordinatePair(i, j);
                        }
                    }
                }
            }
        }

        int candidatesNumber = 3;
        while(candidates.size() > candidatesNumber){
            candidates.remove(candidates.size() - 1);
        }

        Triplet<Integer, Integer, Integer> triplet = candidates.get(random.nextInt(candidates.size()));
        return new CoordinatePair(triplet.getSecond(), triplet.getThird());
    }

    private boolean cellFitToHospital(LabyrinthMap map, int i, int j) {
        Cell cell = map.cells[i][j];
        return cell.type == CellType.SIMPLE_CELL;
    }

    private void placeArsenal(Vector<Pair<Integer, Vector<CoordinatePair>>> regions, final LabyrinthMap map) {
        /*while (true) {
            int i = random.nextInt(map.width);
            int j = random.nextInt(map.height);
            if (cellFitToArsenal(map, i, j)) {
                ArsenalCell cell = new ArsenalCell(map.cells[i][j]);
                map.cells[i][j] = cell;
                map.arsenalx = i;
                map.arsenaly = j;
                return;
            }
        }*/
        int lengthToPortal = (width + height);
        Vector<Triplet<Integer, Integer, Integer>> candidates = new Vector<>();
        for(int i = 0; i < map.width; ++i){
            for(int j = 0; j < map.height; ++j){
                if (cellFitToArsenal(map, i, j)) {
                    int nearPortals = Util.evaluateNumberOfNearPortals(map, i, j, lengthToPortal);
                    boolean added = false;
                    for(int l = 0; l < candidates.size(); ++l){
                        if(nearPortals > candidates.get(l).getFirst()){
                            candidates.add(l, new Triplet<Integer, Integer, Integer>(nearPortals, i, j));
                            added = true;
                            break;
                        }
                    }
                    if(!added){
                        candidates.add(candidates.size(), new Triplet<Integer, Integer, Integer>(nearPortals, i, j));
                    }
                }
            }
        }
        //todo always first?
        int candidatesNumber = 3;
        while(candidates.size() > candidatesNumber){
            candidates.remove(candidates.size() - 1);
        }


        Triplet<Integer, Integer, Integer> pair = candidates.get(random.nextInt(candidates.size()));
        map.arsenalx = pair.getSecond();
        map.arsenaly = pair.getThird();
        ArsenalCell cell = new ArsenalCell(map.cells[map.arsenalx][map.arsenaly]);
        map.cells[map.arsenalx][map.arsenaly] = cell;
    }

    private boolean cellFitToArsenal(LabyrinthMap map, int i, int j) {
        Cell cell = map.cells[i][j];
        return cell.type == CellType.SIMPLE_CELL;
    }

    private void placePortalToEveryRegion(Vector<Pair<Integer, Vector<CoordinatePair>>> regions, LabyrinthMap map) {
        if(regions.size() > 1) {
            for (int i = 0; i < regions.size(); ++i) {
                Vector<CoordinatePair> region = regions.get(i).getSecond();
                int hiddenCellNumber = 0;
                int hiddenCellBorders = 0;
                for (int j = 0; j < region.size(); ++j) {
                    int x = region.get(j).x;
                    int y = region.get(j).y;
                    int borderCount = 4 - map.cells[x][y].connectedCells.size();
                    if (borderCount >= hiddenCellBorders) {
                        hiddenCellNumber = j;
                        hiddenCellBorders = borderCount;
                    }
                    //todo rendomize!
                }
                int px = region.get(hiddenCellNumber).x;
                int py = region.get(hiddenCellNumber).y;
                //PortalCell portal = new PortalCell(-1, map.cells[px][py]);
                PortalCell portal = new PortalCell(i, map.cells[px][py]);
                map.cells[px][py] = portal;
                map.portalsCount++;
            }
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
        if (map.horizontalBorders[i][j].state() == BorderState.NOTEXISTS) {
            map.cells[i][j].connectedCells.add(new CoordinatePair(i, j - 1));
        }
        if (map.horizontalBorders[i][j + 1].state() == BorderState.NOTEXISTS) {
            map.cells[i][j].connectedCells.add(new CoordinatePair(i, j + 1));
        }
        if (map.verticalBorders[i][j].state() == BorderState.NOTEXISTS) {
            map.cells[i][j].connectedCells.add(new CoordinatePair(i - 1, j));
        }
        if (map.verticalBorders[i + 1][j].state() == BorderState.NOTEXISTS) {
            map.cells[i][j].connectedCells.add(new CoordinatePair(i + 1, j));
        }
    }

    private Vector<Pair<Integer, Vector<CoordinatePair>>> cleanupIsolatedRegions(LabyrinthMap map) throws InterruptedException {
        Vector<Pair<Integer, Vector<CoordinatePair>>> regions = collectRegions(map);
        sortRegionsByNumber(regions);
        if (regions.size() > 1) {
            while (regions.lastElement().getSecond().size() < minRegionSize) {
                cleanupSmallestRegion(map, regions);
            }
            while(((regions.size() > portalsCount) || (regions.size() > maxRegionsCount)) && regions.size() > 1){
                cleanupSmallestRegion(map, regions);
            }
        }

        regionsCount = regions.size();
        return regions;
    }

    private void cleanupSmallestRegion(LabyrinthMap map, Vector<Pair<Integer, Vector<CoordinatePair>>> regions) throws InterruptedException {
        Pair<Integer, Vector<CoordinatePair>> temp = regions.lastElement();
        Vector<CoordinatePair> region = temp.getSecond();

        CoordinatePair connectionCoordinates = findConnectionCoordinates(region, map);

        CoordinatePair toConnectionCoordinates = findToConnectionoCordinates(connectionCoordinates, map);

        int i = connectionCoordinates.x;
        int j = connectionCoordinates.y;
        int tempi = toConnectionCoordinates.x;
        int tempj = toConnectionCoordinates.y;

        int newSetId = map.cells[tempi][tempj].setId;
        int newRegionNumber = -1;
        for (int l = 0; l < regions.size(); ++l) {
            if (regions.get(l).getFirst().equals(newSetId)) {
                newRegionNumber = l;
                break;
            }
        }
        Vector<CoordinatePair> newRegion = regions.get(newRegionNumber).getSecond();
        for (int l = 0; l < region.size(); ++l) {
            map.cells[region.get(l).x][region.get(l).y].setId = newSetId;
        }
        newRegion.addAll(region);
        regions.removeElementAt(regions.size() - 1);
        moveUpRegion(regions, newRegionNumber);

        deleteBorderBetween(map, i, j, tempi, tempj);
    }

    private void moveUpRegion(Vector<Pair<Integer, Vector<CoordinatePair>>> regions, int newRegionNumber) {
        for (int i = newRegionNumber; i > 0; --i) {
            int curNumber = regions.get(i).getSecond().size();
            int prevNumber = regions.get(i - 1).getSecond().size();
            if (curNumber > prevNumber) {
                Pair<Integer, Vector<CoordinatePair>> temp = regions.get(i - 1);
                regions.set(i - 1, regions.get(i));
                regions.set(i, temp);
            } else {
                break;
            }
        }
    }

    private CoordinatePair findToConnectionoCordinates(CoordinatePair connectionCoordinates, LabyrinthMap map) {
        Vector<CoordinatePair> coords = new Vector<>();
        int i = connectionCoordinates.x;
        int j = connectionCoordinates.y;

        if (i > 0) {
            if (map.cells[i - 1][j].setId != map.cells[i][j].setId) {
                coords.add(new CoordinatePair(i - 1, j));
            }
        }
        if (i < width - 1) {
            if (map.cells[i + 1][j].setId != map.cells[i][j].setId) {
                coords.add(new CoordinatePair(i + 1, j));
            }
        }
        if (j > 0) {
            if (map.cells[i][j - 1].setId != map.cells[i][j].setId) {
                coords.add(new CoordinatePair(i, j - 1));
            }
        }
        if (j < height - 1) {
            if (map.cells[i][j + 1].setId != map.cells[i][j].setId) {
                coords.add(new CoordinatePair(i, j + 1));
            }
        }

        int k = random.nextInt(coords.size());
        return coords.get(k);
    }

    private CoordinatePair findConnectionCoordinates(Vector<CoordinatePair> region, LabyrinthMap map) {
        CoordinatePair result = null;
        while (true) {
            int k = random.nextInt(region.size());

            int i = region.get(k).x;
            int j = region.get(k).y;

            if (i > 0) {
                if (map.cells[i - 1][j].setId != map.cells[i][j].setId) {
                    result = new CoordinatePair(i, j);
                    return result;
                }
            }
            if (i < width - 1) {
                if (map.cells[i + 1][j].setId != map.cells[i][j].setId) {
                    result = new CoordinatePair(i, j);
                    return result;
                }
            }

            if (j > 0) {
                if (map.cells[i][j - 1].setId != map.cells[i][j].setId) {
                    result = new CoordinatePair(i, j);
                    return result;
                }
            }

            if (j < height - 1) {
                if (map.cells[i][j + 1].setId != map.cells[i][j].setId) {
                    result = new CoordinatePair(i, j);
                    return result;
                }
            }
        }
    }

    private void sortRegionsByNumber(Vector<Pair<Integer, Vector<CoordinatePair>>> regions) {
        if (regions.size() <= 1) return;
        for (int i = regions.size(); i > 1; --i) {
            for (int j = 1; j < i; ++j) {
                int prevNumber = regions.get(j - 1).getSecond().size();
                int curNumber = regions.get(j).getSecond().size();
                if (curNumber > prevNumber) {
                    Pair<Integer, Vector<CoordinatePair>> temp = regions.get(j - 1);
                    regions.set(j - 1, regions.get(j));
                    regions.set(j, temp);
                }
            }
        }
    }

    private Vector<Pair<Integer, Vector<CoordinatePair>>> collectRegions(LabyrinthMap map) {
        Vector<Pair<Integer, Vector<CoordinatePair>>> regions = new Vector<>();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int setId = map.cells[i][j].setId;
                Vector<CoordinatePair> vector = null;
                for (int k = 0; k < regions.size(); ++k) {
                    Pair<Integer, Vector<CoordinatePair>> pair = regions.get(k);
                    if (pair.getFirst().equals(setId)) {
                        vector = pair.getSecond();
                        break;
                    }
                }
                if (vector == null) {
                    vector = new Vector<>();
                    vector.add(new CoordinatePair(i, j));
                    regions.add(new Pair(setId, vector));
                } else {
                    vector.add(new CoordinatePair(i, j));
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
            map.verticalBorders[currentx][currenty].setState(BorderState.NOTEXISTS);
        } else if (currentx < nextx) {
            map.verticalBorders[currentx + 1][currenty].setState(BorderState.NOTEXISTS);
        }
    }

    private void deleteHorizontalBorderBetween(LabyrinthMap map, int currentx, int currenty, int nexty) throws InterruptedException {
        if (currenty > nexty) {
            map.horizontalBorders[currentx][currenty].setState(BorderState.NOTEXISTS);
        } else if (currenty < nexty) {
            map.horizontalBorders[currentx][currenty + 1].setState(BorderState.NOTEXISTS);
        }
    }

    private Vector<CoordinatePair> collectPossibleSteps(Mole mole, LabyrinthMap map) {
        Vector<CoordinatePair> result = new Vector<>();

        int tempi = mole.currentx;
        int tempj = mole.currenty;

        tempi = mole.currentx;
        tempj = mole.currenty + 1;
        if (stepUpIsPossible(mole, tempi, tempj, map)) {
            result.add(new CoordinatePair(tempi, tempj));
        }

        tempi = mole.currentx;
        tempj = mole.currenty - 1;
        if (stepDownIsPossible(mole, tempi, tempj, map)) {
            result.add(new CoordinatePair(tempi, tempj));
        }

        tempi = mole.currentx - 1;
        tempj = mole.currenty;
        if (stepLeftIsPossible(mole, tempi, tempj, map)) {
            result.add(new CoordinatePair(tempi, tempj));
        }

        tempi = mole.currentx + 1;
        tempj = mole.currenty;
        if (stepRightIsPossible(mole, tempi, tempj, map)) {
            result.add(new CoordinatePair(tempi, tempj));
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

    private CoordinatePair findRandomNotVisited() {
        Vector<CoordinatePair> notVisited = new Vector<CoordinatePair>();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (visited[i][j] == 0) {
                    notVisited.add(new CoordinatePair(i, j));
                }
            }
        }

        int k = random.nextInt(notVisited.size());

        CoordinatePair result = notVisited.get(k);

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
