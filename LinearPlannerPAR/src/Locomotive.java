import java.util.ArrayList;
import java.util.Stack;


public class Locomotive {
	
	private ArrayList<Operator> opsList; // list of possibly applicable operations on our problem
	private ArrayList<Predicate> predList; // list of possibly found predicates in the states.
	private State state; // current state
	private Stack<Object> stack; // stack for solving the Linear Planner
	private State finalState; // final state, our goal
	private ArrayList<Operator> plan; // plan designed by the Linear Planner
	private int maxN; // Number of available railways on our station.
	
	/**
	 * Constructor of our Locomotive, it prepares and initializes the data structures for starting
	 * the Linear Planner algorithm.
	 * 
	 * @param ol ArrayList<Operator> list of operators applicable in this world.
	 * @param pl ArrayList<Predicate> list of predicates available in this world.
	 * @param iniState State initial state of our problem.
	 * @param finState State final state, the goal that we want to accomplish.
	 * @param mN int number of maximum railways available on the station.
	 */
	public Locomotive(ArrayList<Operator> ol, ArrayList<Predicate> pl, State iniState, State finState, int mN){
		opsList = ol;
		predList = pl;
		state = iniState;
		finalState = finState;
		maxN = mN;
		stack = new Stack<Object>();
		plan = new ArrayList<Operator>();
	}
	
	
	/**
	 * Main method for solving the planning problem, it applies the Linear Planner algorithm in order
	 * to get to the final state from the given initial state.
	 */
	public void solve(){
		
		stack.push(finalState);
		Object top;		// Top element in the stack
		while (!stack.isEmpty())
		{
			top = stack.peek();
			if (top instanceof State)
			{ 
				State s = (State)top;
				// If top of stack is goal that matches state, then pop element from stack. Matches means that ONLY predicates in s must be in current state
				if (state.matchWith(s))
					stack.pop();
				else	// if top of stack is a conjunctive goal (i.e Sub state)
				{
					// Select an ordering for the subgoals(Predicates) and push them on stack
					// TODO IMPORTANT: Here we should add some intelligence for deciding the order!
					for (Predicate p : s.getPredList())
					{
						stack.push(p);
					}
				}
			}
			else if (top instanceof Predicate)
			{
				Predicate pred = (Predicate)top;
				
				// Checks if the predicate is true in the present state.
				if (state.isPredicateInState(pred))
				{
					stack.pop();
				} 
				// Treating the particular state USED-RAILWAYS
				else if (pred.getName().equals("USED-RAILWAYS")){
					// If enough railways then the precondition is accomplished
					if(state.getOccupied() < this.maxN){
						stack.pop();
					} else {
						
						// Choose an operator o whose add-list matches USED-RAILWAYS(n-1)
						DefaultVariable v = new DefaultVariable("n-1");
						ArrayList<Variable> vlist = new ArrayList<Variable>();
						vlist.add(v);
						Predicate p = new Predicate("USED-RAILWAYS", vlist, 1);
						Operator op = chooseOperator(p);
						
						// We make a copy of the object
						ArrayList<Variable> vl = new ArrayList<Variable>(op.getVarList());
						Operator cloned = new Operator(op.getAddList(), op.getDeleteList(), op.getPrecList(), op.getName(), vl);
						
						// Replace goal sg with operator o
						stack.pop();
						stack.push(cloned);
						stack.push(new State(cloned.getPrecList()));
						
					}
				}
				else
				{
					// Choose an operator o whose add-list matches goal sg
					Operator op = chooseOperator(pred);
					
					// We make a copy of the object
					Operator cloned = op.deepCopy();

					// Instantiate the operator with state variable
					cloned.instantiate(pred);
					
					// Replace goal sg with operator o
					stack.pop();
					stack.push(cloned);
					stack.push(new State(cloned.getPrecList()));
				}
			}
			else if (top instanceof Operator)
			{
				Operator op = (Operator)stack.pop();
				// Particular case of instantiation when there is a towed wagon.
				if(!op.isInstantiated()){
					op.instantiate(state);
				}
				applyOperator(op); 
				plan.add(op);
				
				String output = "";
				for (Variable var : op.getVarList())
				{
					output = output + var.getName()+" ";
				}
				System.out.println("New action in the plan: "+op.getName()+" "+output);
			}
		}
	}
	
	
	
	/**
	 * Applies the given operator instantiated with the given variables on the current 
	 * state (only if the preconditions are accomplished).
	 * 
	 * @param op Operator that we will apply on the current state.
	 * @param varList ArrayList<Variable> list of variables what must be instantiated on the given operator.
	 * @return boolean saying whether the operator has been applied or not.
	 */
	private boolean applyOperator(Operator op){
		
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
	
	/**
	 * Choose an operator in order to satisfy simple goal.
	 * 
	 * @param pred Predicate that must be satisfied
	 * @return Operator an operator object
	 */
	private Operator chooseOperator (Predicate pred)
	{
		ArrayList<Operator> candidates = new ArrayList<Operator>();
		
		for (Operator op : opsList)
		{
			for (Predicate p : op.getAddList())
			{
				if (p.getName().equals(pred.getName()))
				{
					candidates.add(op);
					break;
				}				
			}
		}
		// TODO ATENTION: For the moment, the method return the first operator of the list.
		// It must be changed to be smarter.
		return candidates.get(0);
	}
}
