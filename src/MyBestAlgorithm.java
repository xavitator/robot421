import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * An algorithm that computes a solution of the motion planning problem. <br>
 * 
 * @author Luca Castelli Aleardi (INF421, Ecole Polytechnique, dec 2020)
 *
 */
public class MyBestAlgorithm extends MotionAlgorithm {
	/** An input instance of the motion planning problem */
	public Instance input;

	Random rd = new Random();
	/** The solution computed by the algorithm */
	public Solution solution;
	
	/** Current locations of robots */
	Coordinates current;

	Robot[] robots;

	boolean optimizeTime = false;
	
	public MyBestAlgorithm(Instance input) {
		this.input=input;
		this.solution=new Solution(input.name); // create an empty solution (no steps at the beginning)
		this.current=new Coordinates(this.input.starts.getPositions()); // initialize the current locations with the starting input locations
		robots = new Robot[input.n];
		for (int i = 0; i < input.n; i++) {
			int[] start = {input.starts.getPositions()[0][i], input.starts.getPositions()[1][i]};
			int[] end = {input.targets.getPositions()[0][i], input.targets.getPositions()[1][i]};
			robots[i] =  new Robot(start,end, input.obstacles, i);
		}
	}
	
	/**
	 * Return the current solution: it assumes that the solution has been computed
	 */
	public Solution getSolution() {
		return this.solution;
	}
	
	/**
	 * Compute a complete solution to the input problem: compute all steps, until all robots reach their target destinations
	 */
	public boolean run() {

		while(!allArrived()){
			byte[] mov = computeOneStepTwo();
			solution.addStep(mov);
		}
		System.out.println("Solution computed");
		return true;
	}

	public boolean allStayed(byte[] mov){
		for (int i = 0; i < mov.length; i++) {
			if(mov[i] != Solution.FIXED) return false;
		}
		return true;
	}

	public boolean allArrived(){
		for (Robot i :
				robots) {
			if(! i.isArrive){
				return false;
			}
		}
		return true;
	}

	public byte[] computeOneStepTwo(){
		byte[] onestep = computeOneStep();
		while(!allArrived() && allStayed(onestep)){
			onestep = computeOneStep();
		}
		return onestep;
	}

	/**
	 * Add a new motion step to the current solution
	 */
	public byte[] computeOneStep() {
		System.out.println(input.obstacles);
		Boolean b;
		while(true){
			b = canDoOneStep();
			if(b == null) return null;
			if(b){
				int n=this.input.n; // number of robots
				byte[] mov = new byte[n];
				for (int i = 0; i < n; i++) {
					int[] p = robots[i].position;
					mov[i] = robots[i].getMove();
					robots[i].move(input.obstacles);
					System.out.print(i + " : " + Arrays.toString(robots[i].position) + " / bestPath:");
					if(robots[i].bestPath != null) {
						for (int[] k :
								robots[i].bestPath) {
							System.out.print(Arrays.toString(k) + ",");
						}
					}
					else
						System.out.print("null");
					System.out.println();
				}
				System.out.println("move : " + Arrays.toString(mov));
				return mov;
			}
		}
	}

	public Boolean canDoOneStep(){
		int n=this.input.n; // number of robots
		int[][] coord = new int[2][n];
		byte[] mov = new byte[n];
		for (int i = 0; i < n; i++) {
			int[] p = robots[i].position;
			coord[0][i] = p[0];
			coord[1][i] = p[1];
			mov[i] = robots[i].getMove();
		}
		Coordinates co = new Coordinates(coord);
		int[] res;
		Coordinates cop = new Coordinates(coord);
		cop.move(mov);
		boolean b = rd.nextBoolean();
		if(b) {
			res = co.moveBetweenRob2(mov);
			if (res != null) {
				System.out.println("rentrededans, robs : "+ res[0]+", "+res[1]+ "/ mov : " + mov[res[0]] + " , " + mov[res[1]]);
				b = rentreDedans(robots[res[0]], robots[res[1]]);
				return b ? false : null;
			}
		}
		else {
			res = co.choc2(cop);
			if (res != null) {
				System.out.println("bump, robs : "+ res[0]+", "+res[1]+ "/ mov : " + mov[res[0]] + " , " + mov[res[1]]);
				b = bump(robots[res[0]], robots[res[1]]);
				return b ? false : null;
			}
		}
		if(! b) {
			res = co.moveBetweenRob2(mov);
			if (res != null) {
				System.out.println("rentrededans, robs : "+ res[0]+", "+res[1]+ "/ mov : " + mov[res[0]] + " , " + mov[res[1]]);
				b = rentreDedans(robots[res[0]], robots[res[1]]);
				return b ? false : null;
			}
		}
		else {
			res = co.choc2(cop);
			if (res != null) {
				System.out.println("bump, robs : "+ res[0]+", "+res[1]+ "/ mov : " + mov[res[0]] + " , " + mov[res[1]]);
				b = bump(robots[res[0]], robots[res[1]]);
				return b ? false : null;
			}
		}
		return true;
	}

	public boolean rentreDedans(Robot arriere, Robot avant){
		int arrActTime = arriere.getTimeToArrive();
		int avActTime = avant.getTimeToArrive();
		int arrNextTime = arriere.newPathWith(avant.position, input.obstacles);
		if(arrNextTime <= arrActTime && arrNextTime >= 0) {
			arriere.updateBestPath();
			return true;
		}
		if(arrActTime <= avActTime){
			arriere.stay();
			return true;
		}
		if(avant.canBePush(arriere.getMove(), input.obstacles) && optimizeTime){
			avant.newPathAfterMove(arriere.getMove(), input.obstacles);
			avant.updateBestPath();
			return true;
		}
		if(! arriere.isOnMyWay(avant.target.peekFirst())){
			arriere.stay();
			return true;
		}
		int[] nt = avant.getClosestOuter(arriere, input.obstacles);
		if(nt != null){
			avant.changeTarget(nt, input.obstacles);
			return true;
		}
		int[] exchange = arriere.getExchangeCase(input.obstacles.addOne(avant.position));
		if(exchange == null){
			arriere.stay();
			avant.stay();
			return true;
		}
		avant.changeTarget(exchange, input.obstacles);
		return true;
	}

	public static boolean equalsIntArray(int[] a, int[] b){
		if(a.length != b.length)return false;
		for (int i = 0; i < a.length; i++) {
			if(a[i] != b[i])return false;
		}
		return true;
	}

	public boolean bump(Robot r1, Robot r2){
		int[] pos1 = Robot.posAfterMove(r1.getMove(), r1.position);
		int[] pos2 = Robot.posAfterMove(r2.getMove(), r2.position);
		int r1ActTime = r1.getTimeToArrive();
		int r2ActTime = r2.getTimeToArrive();
		int r1NextTime = r1.newPathWith(pos1, input.obstacles);
		if(r1NextTime <= r1ActTime && r1NextTime >= 0) {
			r1.updateBestPath();
			return true;
		}
		int r2NextTime = r2.newPathWith(pos2, input.obstacles);
		if(r2NextTime <= r2ActTime && r2NextTime >= 0) {
			r2.updateBestPath();
			return true;
		}
		int[] tr1 = r1.getClosestOuter(r2, input.obstacles);
		int[] tr2 = r2.getClosestOuter(r1, input.obstacles);
		System.out.println("bump : "+ Arrays.toString(tr1) + ":tr1 / tr2:"+ Arrays.toString(tr2));
		if(tr1 == null && tr2 == null){
			r1.stay();
			r2.stay();
			return true;
		}
		if(tr2 == null){
			r1.changeTarget(tr1, input.obstacles);
			return true;
		}
		if(tr1 == null){
			r2.changeTarget(tr2, input.obstacles);
			return true;
		}
		if(equalsIntArray(tr2, r2.position) && equalsIntArray(tr1, r1.position)){
			Robot t1 = null, t2 = null;
			if(r1ActTime < r2ActTime){
				t1 = r1;
				t2 = r2;
			}
			else{
				t1 = r2;
				t2 = r1;
			}
			if(t2.isArrive)
				t2.changeTarget(tr2, input.obstacles);
			else
				t1.stay();
			return true;
		}
		if(equalsIntArray(tr2, r2.position)){
			if(r1.isArrive)
				r1.changeTarget(tr1, input.obstacles);
			else
				r2.stay();
			return true;
		}
		if(equalsIntArray(tr1, r1.position)){
			if(r2.isArrive)
				r2.changeTarget(tr2, input.obstacles);
			else
				r1.stay();
			return true;
		}
		int r1pathToTarget = r1.newPathToTarget(tr1, input.obstacles.addOne(r2.position));
		int r2pathToTarget = r2.newPathToTarget(tr2, input.obstacles.addOne(r1.position));
		if(r1pathToTarget >= 0 && r1pathToTarget < r2pathToTarget){
			r1.updateBestIntermediairePath(tr1);
			return true;
		}
		if(r1pathToTarget >= 0 && r1pathToTarget == r2pathToTarget){
			if(r2ActTime >= 0 && r1ActTime > r2ActTime) {
				r2.updateBestIntermediairePath(tr2);
				return true;
			}
			if(r1ActTime >= 0 && r2ActTime > r1ActTime) {
				r1.updateBestIntermediairePath(tr1);
				return true;
			}
		}
		r2.updateBestIntermediairePath(tr2);
		return true;
	}
}
