import java.util.Arrays;

/**
 * This class encapsulates an array storing 2D integer coordinates (for representing robots, obstacles and target locations)
 * 
 * @author Luca Castelli Aleardi (Ecole Polytechnique, INF421, dec 2020)
 */
public class Coordinates {

    /** array storing 'n' 2D integer coordinates: <br>
     * e.g.  for the i-th object (e.g. robot or target)
     * pos[0][i] stores the 'x' coordinate, pos[1][i] contains its 'y' coordinate 
     * */
    private int[][] pos;
    
    /** size of the array: length of the rows */
    int n;
    
    /**
     * Initialize an array of size 'n'. All coordinates are set to (0, 0) by default.
     * 
     * @param n  number of objects in the array (e.g. robots or targets)
     */
    public Coordinates(int n) {
    	this.n=n;
    	this.pos=new int[2][n];
    }
    
    /**
     * Initialize an array, making a COPY of the input coordinates stored in the array 't'
     * 
     * @param t  input coordinates
     */
    public Coordinates(int[][] t) {
    	this.n=t[0].length;
    	this.pos=new int[2][n];
    	for(int i=0;i<n;i++) {
    		this.pos[0][i]=t[0][i];
    		this.pos[1][i]=t[1][i];
    	}
    }
    
    /**
     * Return the number of objects (e.g. robots, obstacles, or targets)
     * @return the size of the array
     */
    public int size() {
    	return this.n;
    }

    /**
     * Return the entire array storing the positions of all objects
     */
    public int[][] getPositions() {
    	return this.pos;
    }

    /**
     * Return the x-coordinate of an object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     * @return  the x coordinate
     */
    public int getX(int i) {
    	return this.pos[0][i];
    }

    /**
     * Return the y-coordinate of an object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     * @return  the y coordinate
     */
    public int getY(int i) {
    	return this.pos[1][i];
    }

    /**
     * Set the x-coordinate of the i-th object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     * @param x  value of the x coordinate
     */
    public void setX(int i, int x) {
    	this.pos[0][i]=x;
    }

    /**
     * Set the y-coordinate of the i-th object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     * @param y  value of the y coordinate
     */
    public void setY(int i, int y) {
    	this.pos[1][i]=y;
    }

    /**
     * Increase by 1 the x coordinate of an object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     */
    public void increaseX(int i) {
    	this.pos[0][i]++;
    }

    /**
     * Increase by 1 the y coordinate of an object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     */
    public void increaseY(int i) {
    	this.pos[1][i]++;
    }

    /**
     * Decrease by 1 the x coordinate of an object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     */
    public void decreaseX(int i) {
    	this.pos[0][i]--;
    }

    /**
     * Decrease by 1 the y coordinate of an object
     * 
     * @param i  index of the i-th robot, obstacle, or target
     */
    public void decreaseY(int i) {
    	this.pos[1][i]--;
    }

    public boolean contains(int[] point){
        for (int i = 0; i < n; i++) {
            if(getX(i) == point[0] && getY(i) == point[1]) return true;
        }
        return false;
    }

    public boolean containsTwo(){
        int[][] cop = new int[this.n][2];
        for (int i = 0; i < this.n; i++) {
            cop[i][0] = this.getX(i);
            cop[i][1] = this.getY(i);
        }
        Arrays.sort(cop, (a,b) -> {
            int compX = Integer.compare(a[0], b[0]);
            if(compX == 0){
                return Integer.compare(a[1], b[1]);
            }
            return  compX;
        });
        for (int i = 0; i < n-1; i++) {
            if (cop[i][0] == cop[i+1][0] && cop[i][1] == cop[i+1][1]){
                return true;
            }
        }
        return false;
    }

    public boolean choc(Coordinates obs){
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < obs.n; j++) {
                if(this.getX(i) == obs.getX(j) && this.getY(i) == obs.getY(j)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean moveBetweenRob(byte[] mov){
        for (int i = 0; i < this.n; i++) {
            int x = this.getX(i);
            int y = this.getY(i);
            switch (mov[i]){
                case Solution.FIXED : ;
                case Solution.N: y++;
                case Solution.S: y--;
                case Solution.E: x++;
                case Solution.W: x--;
            }
            for (int j = 0; j < this.n; j++) {
                if (i == j) continue;
                if(this.getX(j) == x && this.getY(j) == y){
                    if(mov[i] != mov[j]){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void move(byte[] mov){
        for (byte i = 0; i < mov.length; i++) {
            switch (mov[i]){
                case Solution.FIXED : ;
                case Solution.N: this.increaseY(i);
                case Solution.S: this.decreaseY(i);
                case Solution.E: this.increaseX(i);
                case Solution.W: this.decreaseX(i);
            }
        }
    }
}
