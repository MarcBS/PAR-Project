import java.util.ArrayList;


public class Predicate {

	private String name;
	private ArrayList<Variable> varList;
	private int varNum;
	
	public Predicate(String n, ArrayList<Variable> vl, int vn){
		name = n;
		varList = vl;
		varNum = vn;
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<Variable> getVariables(){
		return varList;
	}
	
	public int getNumVariables(){
		return varNum;
	}
	
	public Predicate clone(){
		ArrayList<Variable> vl = new ArrayList<Variable>();
		if(varNum > 0){
			for(Variable v : varList){
				vl.add(v.clone());
			}
		}
		Predicate p = new Predicate(new String(name), vl, varNum);
		return p;
	}
	
	/**
	 * Checks if it has the variable passed by parameter in its inner list.
	 * 
	 * @param v Variable to check if it matches some variable in it.
	 * @return int position where the variable is stored, or -1 if it is not found.
	 */
	public int hasVariable(Variable v){
		
		// If this predicate has no variables we can directly return -1.
		if(varNum == 0){
			return -1;
		}
		
		boolean found = false;
		int i = 0;
		while(i < varList.size() && !found){
			
			boolean this_isDef = (varList.get(i) instanceof DefaultVariable);
			boolean v_isDef = (v instanceof DefaultVariable);
			
			// If they belong to the same class and have the same name, then we found it.
			if((this_isDef == v_isDef) && varList.get(i).isName(v.getName())){
				found = true;
			}

			i++;
		}

		if(found){
			return i-1;
		} else {
			return -1;
		}
	}
	
	
	/**
	 * Replaces the variable in the position "pos" by the variable passed by parameter.
	 * 
	 * @param v Variable to introduce.
	 * @param pos int position in the inner list.
	 */
	public void replaceVariable(Variable v, int pos){
		varList.remove(pos);
		varList.add(pos, v);
	}
	
	
	/**
	 * Checks if the given predicate is the same and has the same variables than the current one.
	 * 
	 * @param p Predicate to compare.
	 * @return boolean.
	 */
	public boolean equalsPredicate(Predicate p){
		if(p.getName().equals(this.name)){ // checks the name
			// Checks each variable
			int[] hasV = this.hasVariables(p.getVariables());
			if(hasV[0] == -1){
				return false;
			} else if(hasV[0] == -2){
				return false;
			} else {
				boolean equal = true;
				int i = 0;
				while(i < hasV.length && equal){
					// If one of the pairs of variables is different or contains a DefaultVariable,
					// then we return false.
					if(hasV[i] == 0 || hasV[i] == 2){
						equal = false;
					}
					i++;
				}
				return equal;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks for each pair of variables if they have the same name, comparing the list
	 * passed by parameter with the current one in the class.
	 * Return exceptions:
	 * 		- The given list is shorter than the current: return -1 on the first position.
	 * 		- The given list is longer than the current: return -2 on the first position.
	 * 		- One of the variables is a DefaultVariable: return 2 on the given position.
	 * 		- Both lists have 0 variables: return 1 on the first position.
	 * 
	 * @param vl List of variables to compare.
	 * @return int[] with 0s or 1s treated as false or true.
	 */
	public int[] hasVariables(ArrayList<Variable> vl){
		
		int[] ret = new int[Math.max(1, this.varNum)];
		
		// If both lists have 0 variables then we return a 1 in the first position
		if(this.varNum == 0 && vl.size() == 0){
			ret[0] = 1;
			return ret;
		}
		
		// vl size > varList size
		if(vl.size() < varList.size()){
			ret[0] = -1;
		// vl size < varList size
		} else if(vl.size() > varList.size()){
			ret[0] = -2;
		} else {
			for(int i = 0; i < varList.size(); i++){
				Variable inV = varList.get(i);
				Variable outV = vl.get(i);
				// If the inner variable is a default variable (x, y, z...)
				if(inV instanceof DefaultVariable){
					ret[i] = 2;
				} else {
					// We check if they are the same
					if(inV.isName(outV.getName())){
						ret[i] = 1;
					} else {
						ret[i] = 0;
					}
				}
			}
		}
		
		return ret;
	}
	
}
