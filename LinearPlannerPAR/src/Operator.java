import java.util.ArrayList;


public class Operator {

	ArrayList<Predicate> addList;
	ArrayList<Predicate> deleteList;
	ArrayList<Predicate> precList;
	String name;
	ArrayList<Variable> varList;
	
	/**
	 * Operator constructor.
	 * 
	 * @param al ArrayList<Predicate> with the list of add predicates.
	 * @param dl ArrayList<Predicate> with the list of delete predicates.
	 * @param pl ArrayList<Predicate> with the list of preconditions.
	 * @param n String with the name of the operator.
	 * @param vl ArrayList<Variable> with the list of its variables.
	 */
	public Operator(ArrayList<Predicate> al, ArrayList<Predicate> dl, ArrayList<Predicate> pl, String n, ArrayList<Variable> vl){
		addList = al;
		deleteList = dl;
		precList = pl;
		name = n;
		varList = vl;
	}
	
	/**
	 * Returns the addList of this operator.
	 * 
	 * @return ArrayList<Predicate> add list.
	 */
	public ArrayList<Predicate> getAddList(){
		return addList;
	}
	
	/**
	 * Returns the deleteList of this operator.
	 * 
	 * @return ArrayList<Predicate> delete list.
	 */
	public ArrayList<Predicate> getDeleteList(){
		return deleteList;
	}
	
	/**
	 * Returns the preconditionsList of this operator.
	 * 
	 * @return ArrayList<Predicate> preconditions list.
	 */
	public ArrayList<Predicate> getPrecList(){
		return precList;
	}
	
	/**
	 * Instantiates all the variables of the operator using the ones passed by parameter (they are
	 * substituted even if they are already instantiated).
	 * 
	 * @param vl ArrayList<Variable> list of variable that we have to instantiate.
	 * @return boolean false if the sizes do not match or true if the instantation has been 
	 * 		correctly applied.
	 */
	public boolean instantiate(ArrayList<Variable> vl){
		
		if(vl.size() != varList.size()){
			return false;
		} else {
			
			for(int i = 0; i < varList.size(); i++){ // for each variable in the operator
				
				Variable v = varList.get(i);
				varList.remove(i);
				Variable newV = vl.get(i);
				
				// Goes throw each predicate in addList substituting the variable "v" by "newV"
				for(int j = 0; j < addList.size(); j++){
					Predicate p = addList.get(j);
					addList.remove(j);
					int pos = p.hasVariable(v);
					if(pos != -1){
						p.replaceVariable(newV, pos);
					}
					addList.add(j, p);
				}
				
				// Goes throw each predicate in deleteList substituting the variable "v" by "newV"
				for(int j = 0; j < deleteList.size(); j++){
					Predicate p = deleteList.get(j);
					deleteList.remove(j);
					int pos = p.hasVariable(v);
					if(pos != -1){
						p.replaceVariable(newV, pos);
					}
					deleteList.add(j, p);
				}
				
				// Goes throw each predicate in precList substituting the variable "v" by "newV"
				for(int j = 0; j < precList.size(); j++){
					Predicate p = precList.get(j);
					precList.remove(j);
					int pos = p.hasVariable(v);
					if(pos != -1){
						p.replaceVariable(newV, pos);
					}
					precList.add(j, p);
				}
				
				varList.add(i, newV); // We set the new variable in this list.
				
			}
			
			return true;
		}
	}
}
