import java.util.ArrayList;
import java.util.Stack;


public class Locomotive {
	
	private ArrayList<Operator> opsList; // list of possibly applicable operations on our problem
	private State state; // current state
	private Stack<Object> stack; // stack for solving the Linnear Planner
	private State finalState; // final state, our goal
	
	/**
	 * Constructor of our Locomotive, it prepares and initializes the data structures for starting
	 * the Linnear Planner algorithm.
	 * 
	 * @param ol ArrayList<Operator> list of operators applicable in this world.
	 * @param iniState State initial state of our problem.
	 * @param finState State final state, the goal that we want to accomplish.
	 */
	public Locomotive(ArrayList<Operator> ol, State iniState, State finState){
		opsList = ol;
		state = iniState;
		finalState = finState;
		stack = new Stack<Object>();
	}
	
	/**
	 * Applies the given operator instantiated with the given variables on the current state.
	 * 
	 * @param op Operator that we will apply on the current state.
	 * @param varList ArrayList<Variable> list of variables what must be instantiated on the given operator.
	 */
	private void applyOperator(Operator op, ArrayList<Variable> varList){
		
		// TODO: prec? -> delete(), add()
		
		
	}
	
}
