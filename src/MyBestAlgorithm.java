
/**
 * An algorithm that computes a solution of the motion planning problem. <br>
 * 
 * @author Luca Castelli Aleardi (INF421, Ecole Polytechnique, dec 2020)
 *
 */
public class MyBestAlgorithm extends MotionAlgorithm {
	/** An input instance of the motion planning problem */
	public Instance input;
	
	/** The solution computed by the algorithm */
	public Solution solution;
	
	/** Current locations of robots */
	Coordinates current;
	
	public MyBestAlgorithm(Instance input) {
		this.input=input;
		this.solution=new Solution(input.name); // create an empty solution (no steps at the beginning)
		this.current=new Coordinates(this.input.starts.getPositions()); // initialize the current locations with the starting input locations
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
	public void run() {
		// TO BE COMPLETED
		System.out.println("Solution computed");
	}
	
	/**
	 * Add a new motion step to the current solution
	 */
	public void computeOneStep() {
		int n=this.input.n; // number of robots
		
		throw new Error("TO BE COMPLETED");
	}

	public boolean rentreDedans(Robot arriere, Robot avant){
		int arrActTime = arriere.getTimeToArrive();
		int avActTime = avant.getTimeToArrive();
		int arrNextTime = arriere.newPathWith(avant.position, input.obstacles);
		if(arrNextTime <= arrActTime) {
			arriere.updateBestPath();
			return true;
		}
		if(arrActTime <= avActTime){
			arriere.stay();
			return true;
		}
		if(avant.canBePush(arriere.getMove(), input.obstacles)){
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
		if(exchange == null)
			return false;
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
		int r1ActTime = r1.getTimeToArrive();
		int r2ActTime = r2.getTimeToArrive();
		int r1NextTime = r1.newPathWith(r2.position, input.obstacles);
		if(r1NextTime <= r1ActTime && r1NextTime >= 0) {
			r1.updateBestPath();
			return true;
		}
		int r2NextTime = r2.newPathWith(r2.position, input.obstacles);
		if(r2NextTime <= r2ActTime && r2NextTime >= 0) {
			r2.updateBestPath();
			return true;
		}
		int[] tr1 = r1.getClosestOuter(r2, input.obstacles);
		int[] tr2 = r2.getClosestOuter(r1, input.obstacles);
		if(tr1 == null && tr2 == null)
			return false;
		if(tr2 == null){
			r1.changeTarget(tr1, input.obstacles);
			return true;
		}
		if(tr1 == null){
			r2.changeTarget(tr2, input.obstacles);
			return true;
		}
		/* Reste à faire : les deux peuvent sortir */
	}
}
