import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Locomotive {
	
	private ArrayList<Operator> opsList; // list of possibly applicable operations on our problem
	private ArrayList<Predicate> predList; // list of possibly found predicates in the states.
	private State state; // current state
	private Stack<Object> stack; // stack for solving the Linear Planner
	private State finalState; // final state, our goal
	private ArrayList<Operator> plan; // plan designed by the Linear Planner
	private int maxN; // Number of available railways on our station.
	
	private BufferedWriter writer; // writer to the log file
	
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
	public ArrayList<Operator> solve(BufferedWriter writer){
		
		this.writer = writer;
		
		stack.push(finalState);
		Object top;		// Top element in the stack
		while (!stack.isEmpty())
		{
			top = stack.peek();
			write(top.toString());
			if (top instanceof State)
			{ 
				State s = (State)top;
				// If top of stack is goal that matches state, then pop element from stack. Matches means that ONLY predicates in s must be in current state
				if (state.matchWith(s, maxN))
					stack.pop();
				else	// if top of stack is a conjunctive goal (i.e Sub state)
				{
					// Select an ordering for the subgoals(Predicates) and push them on stack
					ArrayList<Predicate> orderedPred = orderPredicates(s.getPredList());
					//ArrayList<Predicate> orderedPred = s.getPredList();
					for (Predicate p : orderedPred)
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
						
						boolean ok = false;
						Operator cloned = null;
						while(!ok) {
							// Choose an operator o whose add-list matches USED-RAILWAYS(n-1)
							DefaultVariable v = new DefaultVariable("n-1");
							ArrayList<Variable> vlist = new ArrayList<Variable>();
							vlist.add(v);
							Predicate p = new Predicate("USED-RAILWAYS", vlist, 1);
							Operator op = chooseOperator(p, state, plan);
							
							// We make a copy of the object
							cloned = op.deepCopy();
							
							// Particular cases of instantiation.
							if(!cloned.isInstantiated()){
								ok = cloned.instantiate(state, plan, finalState);
							} else {
								ok = true;
							}
						}
						
						// Replace goal sg with operator o
						stack.pop();
						stack.push(cloned);
						write("Desired operator: " + cloned.toString());
						stack.push(new State(cloned.getPrecList()));
						
					}
				}
				else
				{
					boolean ok = false;
					Operator cloned = null;
					while(!ok) {
						// Choose an operator o whose add-list matches goal sg
						Operator op = chooseOperator(pred, state, plan);
						
						// We make a copy of the object
						cloned = op.deepCopy();
	
						// Instantiate the operator with state variable
						cloned.instantiate(pred);
						
						// Particular cases of instantiation.
						if(!cloned.isInstantiated()){
							ok = cloned.instantiate(state, plan, finalState);
						} else {
							ok = true;
						}
					}
					
					// Replace goal sg with operator o
					stack.pop();
					stack.push(cloned);
					write("Desired operator: " + cloned.toString());
					stack.push(new State(cloned.getPrecList()));
				}
			}
			else if (top instanceof Operator)
			{
				Operator op = (Operator)stack.pop();
				// Particular case of instantiation when there is a towed wagon.
				if(!op.isInstantiated()){
					op.instantiate(state, plan, finalState);
				}
				applyOperator(op, maxN); 
				plan.add(op);
				
				String output = "";
				for (Variable var : op.getVarList())
				{
					output = output + var.getName()+" ";
				}
				write(">>>>>>>>>>>>>>  " + "New action in the plan: "+op.getName()+" "+output + "  <<<<<<<<<<<<<<");
			}
			
		}
		
		
		// Prints state achieved.
		boolean[] contained = new boolean[finalState.getPredList().size()];
		write("\nState achieved checking (on true final state):");
		for(Predicate p : state.getPredList()){
			boolean found = false;
			int i = 0;
			while(i < finalState.getPredList().size() && !found){
				found = finalState.getPredList().get(i).toString().equals(p.toString());
				i++;
			}
			contained[i-1] = found;
			write("\t" + found + "\t" + p.toString());
		}
		
		// Prints all the predicates on the final state that have been not achieved (if any).
		write("\nPredicates not achieved and necessary:");
		int count = 0;
		int countWrong = 0;
		for(boolean a : contained){
			if(!a){
				write("\t" + finalState.getPredList().get(count).toString());
				countWrong++;
			}
			count++;
		}
		if(countWrong == 0){
			write("\tNone");
		}
		
		// Planning finished
		return plan;
		
	}
	
	
	
	/**
	 * Applies the given operator instantiated with the given variables on the current 
	 * state (only if the preconditions are accomplished).
	 * 
	 * @param op Operator that we will apply on the current state.
	 * @param int number of maximum occupied railways
	 * @return boolean saying whether the operator has been applied or not.
	 */
	private boolean applyOperator(Operator op, int maxN){
		
		// Checks if the preconditions are accomplished.
		if(!state.checkPreconditions(op.getPrecList(), maxN)){
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
	private Operator chooseOperator (Predicate pred, State s, ArrayList<Operator> plan)
	{
		ArrayList<Operator> candidates = new ArrayList<Operator>();
		
		for (Operator op : opsList)
		{
			for (Predicate p : op.getAddList())
			{
				if (p.getName().equals(pred.getName()))
				{
					if(p.getName().equals("USED-RAILWAYS")){
						// Only if both "USED-RAILWAYS" have the same variable "n-1", "n+1" or "n", then they are valid.
						if(p.getVariables().get(0).getName().equals(pred.getVariables().get(0).getName())){
							candidates.add(op);
							break;
						}
					} else {
						candidates.add(op);
						break;
					}
				}				
			}
		}
		
		return chooseCandidate(candidates, pred, s, plan);
	}
	
	/**
	 * Chooses the most adequate operation for the given predicate.
	 * 
	 * @param opL list of possible operators.
	 * @param p Predicate to solve.
	 * @return Operator chosen.
	 */
	private Operator chooseCandidate(ArrayList<Operator> candidates, Predicate p, State s, ArrayList<Operator> plan){
		
		int chosen = 0;
		if(p.getName().equals("FREE-LOCOMOTIVE")){
			if(s.getOccupied() == this.maxN){ // If there is not any free station position
				
				boolean found = false;
				int i = 0;
				while(i < candidates.size() && !found){
					if(candidates.get(i).getName().equals("ATTACH")){
						found = true;
					} else {
						i++;
					}
				}
				chosen = i;
				
			} else {
				
				Variable towed = null;
				boolean found = false;
				int i = 0;
				// Finds the towed variable
				while(i < s.getPredList().size() && !found){
					found = s.getPredList().get(i).getName().equals("TOWED");
					if(found){
						towed = s.getPredList().get(i).getVariables().get(0);
					}
					i++;
				}
				
				// Only continues if we have found the towed variable
				if(found){
					found = false;
					i = plan.size()-1;
					int last = Math.max(0, plan.size()-10);
					// If we have recently Coupled the towed variable, then we should not park it again.
					while(i >= last && !found){
						if(plan.get(i).getName().equals("COUPLE") && plan.get(i).getVarList().get(0).isName(towed.getName())){
							found = true;
						}
						i--;
					}
					
					if(found){
						if(1 + (int)(Math.random() * ((2 - 1) + 1)) == 1){
							for(Operator c : candidates){
								if(c.getName().equals("PARK")){
									candidates.remove(c);
								}
							}
						}
					}
				}
				
				chosen = randomChoose(candidates)-1;
			}
		} else if (p.getName().equals("TOWED")){
			
			boolean found = false;
			int i = 0;
			int i2 = 0;
			while(i < s.getPredList().size() && !found){
				Predicate state_pred = s.getPredList().get(i);
				// We have the desired variable (the one that we want to TOWE) on the station
				if(state_pred.getName().equals("ON-STATION") && state_pred.hasVariable(p.getVariables().get(0)) > -1){
					i2 = 0;
					while(i2 < candidates.size() && !found){
						if(candidates.get(i2).getName().equals("COUPLE")){
							found = true;
						} else {
							i2++;
						}
					}
				} else if(state_pred.getName().equals("IN-FRONT-OF") && state_pred.hasVariable(p.getVariables().get(0)) > -1) {
					i2 = 0;
					while(i2 < candidates.size() && !found){
						if(candidates.get(i2).getName().equals("DETACH")){
							found = true;
						} else {
							i2++;
						}
					}
				}
				i++;
			}
			chosen = i2;
			
		} else {
			chosen = randomChoose(candidates)-1;
		}
		
		return candidates.get(chosen);
	}
	
	/**
	 * Random choose of an operator candidate.
	 * 
	 * @param candidates
	 * @return
	 */
	private int randomChoose(ArrayList<Operator> candidates){
		int Min = 1;
		int Max = candidates.size();
		int chosen = Min + (int)(Math.random() * ((Max - Min) + 1));
		return chosen;
	}
	
	/**
	 * Establishes an order for the predicates inserted in the stack.
	 * 
	 * @return
	 */
	private ArrayList<Predicate> orderPredicates(ArrayList<Predicate> predList){
		
		Collections.sort(predList, new Comparator<Predicate>(){
			
			@Override
			public int compare(Predicate p1, Predicate p2) {
				
				// order of priority (the lower the number the higher the priority
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				map.put("FREE-LOCOMOTIVE", 5);
				map.put("LOADED", 3	);
				map.put("EMPTY", 3);
				map.put("TOWED", 3);
				map.put("USED-RAILWAYS", 8);
				map.put("ON-STATION", 3);
				map.put("IN-FRONT-OF", 5);
				map.put("FREE", 6);
				
		        if(map.get(p1.getName()) < map.get(p2.getName())){
		        	return 1;
		        } else if(map.get(p1.getName()) == map.get(p2.getName())){
		        	return 0;
		        } else {
		        	return -1;
		        }
		    }
		});
		return predList;
	}
	
	
	// Writes to the log file
	private void write(String s){
		try {
			this.writer.write(s + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
