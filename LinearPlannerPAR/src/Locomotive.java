import java.util.ArrayList;
import java.util.Stack;


public class Locomotive {
	
	private ArrayList<Operator> opsList; // list of possibly applicable operations on our problem
	private ArrayList<Predicate> predList; // list of possibly found predicates in the states.
	private State state; // current state
	private Stack<Object> stack; // stack for solving the Linear Planner
	private State finalState; // final state, our goal
	
	/**
	 * Constructor of our Locomotive, it prepares and initializes the data structures for starting
	 * the Linear Planner algorithm.
	 * 
	 * @param ol ArrayList<Operator> list of operators applicable in this world.
	 * @param pl ArrayList<Predicate> list of predicates available in this world.
	 * @param iniState State initial state of our problem.
	 * @param finState State final state, the goal that we want to accomplish.
	 */
	public Locomotive(ArrayList<Operator> ol, ArrayList<Predicate> pl, State iniState, State finState){
		opsList = ol;
		predList = pl;
		state = iniState;
		finalState = finState;
		stack = new Stack<Object>();
	}
	
	
	/**
	 * Main method for solving the planning problem, it applies the Linear Planner algorithm in order
	 * to get to the final state from the given initial state.
	 */
	public void solve(){
		
	}
	
	
	
	/**
	 * Applies the given operator instantiated with the given variables on the current 
	 * state (only if the preconditions are accomplished).
	 * 
	 * @param op Operator that we will apply on the current state.
	 * @param varList ArrayList<Variable> list of variables what must be instantiated on the given operator.
	 * @return boolean saying whether the operator has been applied or not.
	 */
	private boolean applyOperator(Operator op, ArrayList<Variable> varList){
		
		// Instantation of the variables in the current operator
		op.instantiate(varList); 
		
		// Checks if the preconditions are accomplished.
		if(!state.checkPreconditions(op.getPrecList())){
			return false;
		} else {
			
			// applies the delete list on the current state.
			state.delete(op.getDeleteList());
			// applies the add list on the current state.
			state.add(op.getAddList());
			
			return true;
		}
		
	}
	
}
