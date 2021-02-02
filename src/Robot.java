import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class Robot {

    int[] position;
    LinkedList<int[]> bestPath;
    LinkedList<int[]> tempPath = new LinkedList<>();
    LinkedList<int[]> travRoad = new LinkedList<>();
    LinkedList<int[]> target = new LinkedList<>();

    boolean isArrive = false;

    public Robot(int[] position, int[] target, Coordinates obs) {
        this.position = position;
        this.target.addFirst(target);
        travRoad.addFirst(position);
        Aetoile calcPath = new Aetoile(position, target, obs);
        this.bestPath = calcPath.runAlgo();
        if(bestPath != null)
        System.out.println(bestPath.size());
    }
    public void changeTarget(int[] nt, Coordinates obs){
        target.addFirst(nt);
        isArrive = false;
        Aetoile calcPath = new Aetoile(position, nt, obs);
        this.bestPath = calcPath.runAlgo();
    }
    public int getTimeToArrive(){
        if(bestPath == null) return 0;
        return this.bestPath.size();
    }
    public byte getMove(){
        if(bestPath == null) return Solution.FIXED;
        if(bestPath.isEmpty() || isArrive) return Solution.FIXED;
        return Solution.getMove(position, bestPath.peekFirst());
    }
    public void move(Coordinates obs){
        if(isArrive || bestPath == null) return;
        position = bestPath.pollFirst();
        travRoad.addFirst(position);
        if(bestPath.isEmpty()) {
            if(target.size() == 1) {
                isArrive = true;
                return;
            }
            else{
                int[] nt = target.pollFirst();
                Aetoile calcPath = new Aetoile(position, nt, obs);
                this.bestPath = calcPath.runAlgo();
            }
            bestPath.addFirst(position);
        };
    }
    public void stay(){
        if(bestPath == null) return;
        bestPath.addFirst(position);
    }
    public int newPathWith(int[] oRob, Coordinates obs){
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
    public void updateBestPath(){
        this.bestPath = tempPath;
    }
    public boolean canBePush(byte mov, Coordinates obs){
        int[] nm = posAfterMove(mov, position);
        return ! obs.contains(nm);
    }
    public boolean isOnMyWay(int[] el){
        if(position[0] == el[0] && position[1] == el[1]) return true;
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

    public int[] getClosestOuter(Robot other, Coordinates obs){
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
            if(! obs.contains(np) && !containsIntArray.apply(np)) {
                box.addFirst(np);
                return true;
            }
            return false;
        };
        do {
            int[] temp = box.pollLast();
            targ[0] = temp[0];
            targ[1] = temp[1];
            addToList.apply(Solution.N);
            addToList.apply(Solution.S);
            addToList.apply(Solution.E);
            addToList.apply(Solution.W);
            visited.add(temp);
        }
        while((!other.isOnMyWay(targ)) && (!box.isEmpty()));
        if(other.isOnMyWay(targ)) return targ;
        return null;
    }

    public int[] getExchangeCase(Coordinates obs){
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
            if(! obs.contains(np) && !containsIntArray.apply(np)) {
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
            if (addToList.apply(Solution.N))
                adj += 1;
            if (addToList.apply(Solution.S)){
                adj +=1;
                if(adj >= 2)
                    return box.peekFirst();
            }
            if (addToList.apply(Solution.E)){
                adj +=1;
                if(adj >= 2)
                    return box.peekFirst();
            }
            if (addToList.apply(Solution.W)){
                adj +=1;
                if(adj >= 2)
                    return box.peekFirst();
            }
            visited.add(temp);
        }
        while(!box.isEmpty());
        return null;
    }
}
