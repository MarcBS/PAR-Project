import java.util.ArrayList;


public class State {

	private ArrayList<Predicate> predList;
	private int occupied; // Number of occupied railways on this state.
	
	public State(ArrayList<Predicate> pl){
		predList = pl;
		occupied = 0;
		
		for(Predicate p : pl){
			if(p.getName().equals("ON-STATION")){
				occupied++;
			}
		}
	}
	
	/**
	 * Returns the number of occupied railways.
	 * 
	 * @return int.
	 */
	public int getOccupied(){
		return occupied;
	}
	
	/**
	 * Returns the list of predicates accomplished on this state.
	 * 
	 * @return ArrayList<Predicate> with the list of predicates of this state.
	 */
	public ArrayList<Predicate> getPredList(){
		return predList;
	}
	
	/**
	 * Checks if all the preconditions passed by parameter are accomplished in the
	 * current state.
	 * 
	 * @param pl ArrayList<Predicate> list of preconditions.
	 * @return boolean saying if the preconditions are accomplished or not for 
	 * 			the current state.
	 */
	public boolean checkPreconditions(ArrayList<Predicate> pl){
		boolean accomplished = true;
		int i = 0;
		while(i < pl.size() && accomplished){ // for each predicate
			boolean found = false;
			int j = 0;
			while(j < predList.size() && !found){
				found = predList.get(j).equalsPredicate(pl.get(i));
				j++;
			}
			accomplished = found;
			i++;
		}
		return accomplished;
	}
	
	/**
	 * Adds each predicate passed by parameter only if it is not already in our
	 * predList.
	 * 
	 * @param pl ArrayList<Predicate> about to add into our list.
	 */
	public void add(ArrayList<Predicate> pl){
		for(int i = 0; i < pl.size(); i++){ // for each predicate
			Predicate p = pl.get(i);
			
			// If the predicate is USED-RAILWAYS
			if(p.getName().equals("USED-RAILWAYS")){
				// We increment or decrement the number of occupied spaces.
				String varName = p.getVariables().get(0).getName();
				switch (varName){
					case "n+1":	this.occupied++;
								break;
					case "n-1":	this.occupied--;
								break;
				}
				
			} else {
			
				boolean found = false;
				int j = 0;
				while(j < predList.size() && !found){
					found = predList.get(j).equalsPredicate(p);
					j++;
				}
				
				// We add it to the predicates list only if it is not already in.
				if(!found){
					predList.add(p);
				}
				
			}
			
		}
	}
	
	/**
	 * Deletes each of the predicates passed by parameter from our predList only
	 * if it is in our list.
	 * 
	 * @param pl ArrayList<Predicate> about to delete from our list.
	 */
	public void delete(ArrayList<Predicate> pl){
		for(int i = 0; i < pl.size(); i++){ // for each predicate
			Predicate p = pl.get(i);
			
			boolean found = false;
			int j = 0;
			while(j < predList.size() && !found){
				found = predList.get(j).equalsPredicate(p);
				j++;
			}
			
			if(found){
				predList.remove(j-1);
			}	
		}
	}
	
	/**
	 * Check if a state matches with other state. Matches means that all the predicates in the 
	 * state passed as a parameter, must be in the state which is being checked
	 * 
	 * @param other State 
	 */
	public boolean matchWith(State other)
	{
		for (Predicate p : other.predList)
		{
			if (!isPredicateInState(p))
				return false;
		}
		return true;
	}
	
	/**
	 * Check if a state have a specific predicate
	 * 
	 * @param pred Predicate to perform the checking.
	 */
	public boolean isPredicateInState(Predicate pred)
	{
		for (Predicate p : predList)
		{
			if (p.equalsPredicate(pred))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Treats the particular case when we need to release the towed wagon.
	 * 
	 * @return Variable describing the wagon.
	 */
	public Variable getWagonOnLocomotive(){
		Variable v;
		boolean found = false;
		int i = 0;
		while(i < predList.size() && !found){
			found = predList.get(i).getName().equals("TOWED");
			i++;
		}
		v = new Variable(predList.get(i-1).getVariables().get(1).getName());
		return v;
	}
}
