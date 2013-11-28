import java.util.ArrayList;


public class Operator {

	ArrayList<Predicate> addList;
	ArrayList<Predicate> deleteList;
	ArrayList<Predicate> precList;
	String name;
	ArrayList<Variable> varList;
	
	
	public Operator(ArrayList<Predicate> al, ArrayList<Predicate> dl, ArrayList<Predicate> pl, String n, ArrayList<Variable> vl){
		addList = al;
		deleteList = dl;
		precList = pl;
		name = n;
		varList = vl;
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
			// TODO: instantiate!!
			
			return true;
		}
		
	}
	
}
