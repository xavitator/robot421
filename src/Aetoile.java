import java.util.*;

public class Aetoile {

    int[] start;
    int[] target;
    Coordinates obstacles;

    private HashSet<Node> closed = new HashSet<>();
    private TreeSet<Node> open = new TreeSet<>((a,b) -> a.compare(a,b));

    public Aetoile(int[] start, int[] target, Coordinates obstacles) {
        this.start = start;
        this.target = target;
        this.obstacles = obstacles;
        Node s = new Node(start[0], start[1], 0, null);
        this.open.add(s);
    }

    public LinkedList<int[]> runAlgo(){
        Node courant = open.pollFirst();
        closed.add(courant);
        while (! courant.equals(target)){
            addNode(courant, courant.x + 1, courant.y);
            addNode(courant, courant.x - 1, courant.y);
            addNode(courant, courant.x, courant.y + 1);
            addNode(courant, courant.x, courant.y - 1);
            if(open.isEmpty()) return null;
            courant = open.pollFirst();
            closed.add(courant);
        }
        LinkedList<int[]> res = new LinkedList<>();
        do {
            int[] t = {courant.x, courant.y};
            res.addFirst(t);
            courant = courant.parent;
        }
        while(courant.parent != null);
        return res;
    }

    public void addNode(Node parent, int x, int y){
        int[] point = {x, y};
        int prevDist = parent.prevDist + 1;
        if(obstacles.contains(point)) return;
        Node p = new Node(point[0], point[1], prevDist, parent);
        if(closed.contains(p)) return;
        if(open.contains(p)){
            if(open.removeIf((a) -> a.equals(point) && a.prevDist > prevDist))
                open.add(p);
        }
        else{
            open.add(p);
        }

    }

    private class Node {

        int x, y, prevDist;
        final int absDist;
        Node parent;

        public Node(int x, int y, int prevDist, Node parent) {
            this.x = x;
            this.y = y;
            this.absDist = this.calcAbsDist();
            this.prevDist = prevDist;
            this.parent = parent;
        }

        public int calcAbsDist(){
            return Math.abs(x - target[0]) + Math.abs(y - target[1]);
        }

        public int distance(){
            return prevDist + absDist;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public boolean equals(int[] other) {
            if(other.length != 2) return false;
            return x == other[0] && y == other[1];
        }

        public int compare(Object o, Object t1) {
            if(o == null && t1 == null) return 0;
            if (o == null) return 1;
            if (t1 == null) return -1;
            Node o1 = (Node) o;
            Node o2 = (Node) t1;
            return o1.distance() - o2.distance();
        }


    }

    public static void main(String[] args){
        int[] s = {0,0};
        int[] e = {10,10};
        int[][] t = {
                {1,2,1,1},
                {2,3,1,0}
        };
        Coordinates ob = new Coordinates(t);
        Aetoile el = new Aetoile(s,e,ob);
        for (int[] i : el.runAlgo()){
            System.out.println(i[0] + " " + i[1]);
        }
    }
}

