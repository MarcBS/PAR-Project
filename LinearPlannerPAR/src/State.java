import java.util.ArrayList;


public class State {

	ArrayList<Predicate> predList;
	
	public State(ArrayList<Predicate> pl){
		predList = pl;
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
	
}
