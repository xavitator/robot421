import java.util.*;

public class Aetoile {

    int[] start;
    int[] target;
    Coordinates obstacles;
    public int xmin=Integer.MAX_VALUE, xmax=Integer.MIN_VALUE, ymin=Integer.MAX_VALUE, ymax=Integer.MIN_VALUE;


    private HashSet<Node> closed = new HashSet<>();
    private TreeSet<Node> open = new TreeSet<>((a,b) -> a.compare(a,b));

    public Aetoile(int[] start, int[] target, Coordinates obstacles) {
        this.start = start;
        this.target = target;
        this.obstacles = obstacles;
        obstacles.getBoundingBox();
        xmin = obstacles.xmin;
        xmax = obstacles.xmax;
        ymin = obstacles.ymin;
        ymax = obstacles.ymax;
        xmin = Math.min(start[0], Math.min(target[0], xmin)) - 1;
        ymin = Math.min(start[1], Math.min(target[1], ymin)) - 1;
        xmax = Math.max(start[0], Math.max(target[0], xmax)) + 1;
        ymax = Math.max(start[1], Math.max(target[1], ymax)) + 1;
        Node s = new Node(start[0], start[1], 0, null);
        this.open.add(s);
    }

    public LinkedList<int[]> runAlgo(){
        LinkedList<int[]> res = new LinkedList<>();
        Node courant = open.pollFirst();
        closed.add(courant);
        while (! courant.equals(target)){
            addNode(courant, courant.x + 1, courant.y);
            addNode(courant, courant.x - 1, courant.y);
            addNode(courant, courant.x, courant.y + 1);
            addNode(courant, courant.x, courant.y - 1);
            if(open.isEmpty()){
                return null;
            }
            courant = open.pollFirst();
            closed.add(courant);
        }
        do {
            int[] t = {courant.x, courant.y};
            res.addFirst(t);
            courant = courant.parent;
        }
        while( courant != null && courant.parent != null);
        return res;
    }

    public void addNode(Node parent, int x, int y){
        if(x < xmin || x > xmax || y < ymin || y > ymax) return;
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
            int dist = o1.distance() - o2.distance();
            if(dist == 0){
                int compx = o1.x - o2.x;
                if(compx == 0){
                    return o1.y - o2.y;
                }
                return compx;
            }
            return dist;
        }


    }
}

