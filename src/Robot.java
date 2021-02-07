import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Robot {

    int[] position;
    LinkedList<int[]> resolvedBlock = new LinkedList<>();
    LinkedList<int[]> bestPath;
    LinkedList<int[]> tempPath = new LinkedList<>();
    LinkedList<int[]> travRoad = new LinkedList<>();
    LinkedList<int[]> target = new LinkedList<>();

    int id;

    boolean isArrive = false;

    public Robot(int[] position, int[] target, Coordinates obs, int id) {
        this.id = id;
        this.position = position;
        this.target.addFirst(target);
        travRoad.addFirst(position);
        Aetoile calcPath = new Aetoile(position, target, obs);
        this.bestPath = calcPath.runAlgo();
    }
    public void changeTarget(int[] nt, Coordinates obs){
        if(MyBestAlgorithm.equalsIntArray(nt, position)) {
            stay();
            return;
        }
        target.addFirst(nt);
        isArrive = false;
        resolvedBlock.add(bestPath.peekFirst());
        for (int[] i :
                resolvedBlock) {
            if(i != null)
            obs = obs.addOne(i);
        }
        Aetoile calcPath = new Aetoile(position, nt, obs);
        LinkedList<int[]> temp = calcPath.runAlgo();
        if(temp == null){
            stay();
            return;
        }
        this.bestPath = temp;
    }
    public int getTimeToArrive(){
        if(bestPath == null) return -1;
        return this.bestPath.size();
    }
    public byte getMove(){
        if(bestPath == null || bestPath.isEmpty() || isArrive) return Solution.FIXED;
        return Solution.getMove(position, bestPath.peekFirst());
    }
    public void move(Coordinates obs){
        resolvedBlock = new LinkedList<>();
        if(isArrive || bestPath == null) return;
        position = bestPath.pollFirst();
        travRoad.addFirst(position);
        if(position == null) System.out.println(id + " / ");
        if(MyBestAlgorithm.equalsIntArray(target.peekFirst(), position)) {
            if(target.size() == 1) {
                isArrive = true;
                return;
            }
            else{
                target.pollFirst();
                int[] nt = target.peekFirst();
                Aetoile calcPath = new Aetoile(position, nt, obs);
                this.bestPath = calcPath.runAlgo();
            }
        }
    }
    public void stay(){
        if(bestPath == null) return;
        if(MyBestAlgorithm.equalsIntArray(bestPath.peek(), position))return;
        resolvedBlock.add(bestPath.peekFirst());
        bestPath.addFirst(position);
    }
    public int newPathWith(int[] oRob, Coordinates obs){
        for (int[] i :
                resolvedBlock) {
            if(i != null)
            obs = obs.addOne(i);
        }
        Aetoile calcPath = new Aetoile(position, target.peekFirst(), obs.addOne(oRob));
        tempPath = calcPath.runAlgo();
        if(tempPath == null) return -1;
        return tempPath.size();
    }

    public static int[] posAfterMove(byte mov, int[] pos){
        int[] np = new int[2];
        np[0] = pos[0];
        np[1] = pos[1];
        switch (mov){
                case Solution.FIXED : break ;
                case Solution.N: np[1]++; break;
                case Solution.S: np[1]--;break;
                case Solution.E: np[0]++;break;
                case Solution.W: np[0]--;break;
        }
        return np;
    }
    public int newPathAfterMove(byte mov, Coordinates obs){
        int[] np = posAfterMove(mov, position);
        Aetoile calcPath = new Aetoile(np, target.peekFirst(), obs);
        tempPath = calcPath.runAlgo();
        tempPath.addFirst(np);
        return tempPath.size();
    }
    public void updateBestPath() {
        if(bestPath != null)
            this.resolvedBlock.add(bestPath.peekFirst());
        if(tempPath != null)
            this.bestPath = tempPath;
        else
            stay();
    }

    public void updateBestIntermediairePath(int[] targ) {
        if(MyBestAlgorithm.equalsIntArray(targ, position)){
            stay();
            return;
        }
        if(bestPath != null)
            this.resolvedBlock.add(bestPath.peekFirst());
        if(tempPath != null) {
            this.bestPath = tempPath;
            this.target.addFirst(targ);
        }
        else
            stay();
    }
    public boolean canBePush(byte mov, Coordinates obs){
        for (int[] i :
                resolvedBlock) {
            if(i != null)
            obs = obs.addOne(i);
        }
        int[] nm = posAfterMove(mov, position);
        return ! obs.contains(nm);
    }
    public boolean isOnMyWay(int[] el){
        if(position[0] == el[0] && position[1] == el[1]) return true;
        if(bestPath == null) return false;
        for (int[] i :
                bestPath) {
            if(i[0] == el[0] && i[1] == el[1]) return true;
        }
        return false;
    }
    private int[] sumTab(int[] a, int[] b){
        if(a.length != b.length) return null;
        int[] c = new int[a.length];
        for (int i = 0; i < c.length; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    public int newPathToTarget(int[] targ, Coordinates obs){
        for (int[] i :
                resolvedBlock) {
            if(i != null)
            obs = obs.addOne(i);
        }
        Aetoile calcPath = new Aetoile(position, targ, obs);
        tempPath = calcPath.runAlgo();
        if(tempPath == null) return -1;
        return tempPath.size();
    }

    public int[] getClosestOuter(Robot other, Coordinates obs){
        for (int[] i :
                resolvedBlock) {
            if(i != null)
            obs = obs.addOne(i);
        }
        Coordinates obst = obs.addOne(other.position);
        LinkedList<int[]> box = new LinkedList<>();
        LinkedList<int[]> visited = new LinkedList<>();
        box.add(position);
        Function<int[], Boolean> containsIntArray = (arr) -> {
            for (int[] i :
                    visited) {
                boolean comp = true;
                for (int j = 0; j < i.length && i.length == arr.length; j++) {
                    if (i[j] != arr[j]) {
                        comp = false;
                        break;
                    }
                }
                if(comp){
                    return true;
                }
            }
            return false;
        };
        int[] targ = new int[2];
        Function<Byte, Boolean> addToList = (mov) -> {
            int[] np = posAfterMove(mov, targ);
            if(! obst.contains(np) && !containsIntArray.apply(np)) {
                box.addFirst(np);
                return true;
            }
            return false;
        };
        do {
            int[] temp = box.pollLast();
            targ[0] = temp[0];
            targ[1] = temp[1];
            List<Byte> depl = Arrays.asList(Solution.N,Solution.S,Solution.E,Solution.W);
            Collections.shuffle(depl);
            for (Byte i :
                    depl) {

                addToList.apply(i);
            }
            visited.add(temp);
        }
        while((other.isOnMyWay(targ)) && (!box.isEmpty()));
        if(! other.isOnMyWay(targ)) return targ;
        return null;
    }

    public int[] getExchangeCase(Coordinates obs){
        for (int[] i :
                resolvedBlock) {
            if(i != null)
            obs = obs.addOne(i);
        }
        Coordinates obst = obs;
        LinkedList<int[]> box = new LinkedList<>();
        LinkedList<int[]> visited = new LinkedList<>();
        box.add(position);
        Function<int[], Boolean> containsIntArray = (arr) -> {
            for (int[] i :
                    visited) {
                boolean comp = true;
                for (int j = 0; j < i.length && i.length == arr.length; j++) {
                    if (i[j] != arr[j]) {
                        comp = false;
                        break;
                    }
                }
                if(comp){
                    return true;
                }
            }
            return false;
        };
        int[] targ = new int[2];
        Function<Byte, Boolean> addToList = (mov) -> {
            int[] np = posAfterMove(mov, targ);
            if(! obst.contains(np) && !containsIntArray.apply(np)) {
                box.addFirst(np);
                return true;
            }
            return false;
        };
        do {
            int[] temp = box.pollLast();
            targ[0] = temp[0];
            targ[1] = temp[1];
            int adj = 0;
            List<Byte> depl = Arrays.asList(Solution.N,Solution.S,Solution.E,Solution.W);
            Collections.shuffle(depl);
            for (Byte i :
                    depl) {
                if (addToList.apply(i)) {
                    adj += 1;
                    if(adj >= 2){
                        return box.peekFirst();
                    }
                }
            }
            visited.add(temp);
        }
        while(!box.isEmpty());
        return null;
    }
}
