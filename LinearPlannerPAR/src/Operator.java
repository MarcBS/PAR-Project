import java.util.ArrayList;


public class Operator {

	private ArrayList<Predicate> addList;
	private ArrayList<Predicate> deleteList;
	private ArrayList<Predicate> precList;
	private String name;
	private ArrayList<Variable> varList;
	
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
	 * Returns the name of this operator.
	 * 
	 * @return String name.
	 */
	public String getName(){
		return name;
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
	 * Returns the variablesList of this operator.
	 * 
	 * @return ArrayList<Variable> variables list.
	 */
	public ArrayList<Variable> getVarList(){
		return varList;
	}
	
	/**
	 * Checks if the current operator is instantiated.
	 * 
	 * @return boolean saying if it is instantiated.
	 */
	public boolean isInstantiated(){
		for(Variable v : varList){
			if(v instanceof DefaultVariable){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Instantiates all the variables of the operator using the ones passed by parameter (they are
	 * substituted even if they are already instantiated).
	 * 
	 * @param vl ArrayList<Variable> list of variable that we have to instantiate.
	 * @return boolean false if the sizes do not match or true if the instantiation has been 
	 * 		correctly applied.
	 */
	public boolean instantiate(Predicate pred){
		
		ArrayList<Variable> vl = pred.getVariables();
		
		if(vl.size() != varList.size()){
			
			int i = 0;
			Variable v = null;
			Variable newV = null;
			boolean change = false;
			switch (name) {
				case "DETACH": 	if(pred.getName().equals("TOWED")){
									i = 0;
									v = varList.get(i);
									varList.remove(i);
									if(!(vl.get(0) instanceof DefaultVariable)){
										change = true;
										newV = new Variable(vl.get(0).getName());
									}
								} else if(pred.getName().equals("FREE")){
									i = 1;
									v = varList.get(i);
									varList.remove(i);
									if(!(vl.get(0) instanceof DefaultVariable)){
										change = true;
										newV = new Variable(vl.get(0).getName());
									}
								}
								if(change){
									substituteVar(v, newV);
									varList.add(i, newV); // We set the new variable in this list.
								}
								break;
			}

			
			
		} else {
			
			for(int i = 0; i < varList.size(); i++){ // for each variable in the operator
				
				Variable v = varList.get(i);
				varList.remove(i);
				Variable newV = new Variable(vl.get(i).getName());
				
				substituteVar(v, newV);
				varList.add(i, newV); // We set the new variable in this list.
			}
		}
		
		return true;
	}
	
	/**
	 * Substitutes v by newV in all the lists.
	 * 
	 * @param v
	 * @param newV
	 */
	private void substituteVar(Variable v, Variable newV){
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
		
	}
	
	/**
	 * Instantiates the operator wisely for the particular operation of 
	 * releasing a wagon using the current state.
	 * 
	 * @param s current State.
	 */
	public boolean instantiate(State s){
		
		Variable newV = null;
		Variable v = null;
		if(name.equals("PARK") || name.equals("ATTACH")){
			newV = s.getWagonOnLocomotive();
			v = varList.get(0);
			varList.remove(0);
			substituteVar(v, newV);
			varList.add(0, newV); // We set the new variable in this list.
			return true;
		}
		
		return false;
	}
	
	/**
	 * Performs a deep copy of the operator in order not to overwrite information.
	 * 
	 * @return Operator copied.
	 */
	public Operator deepCopy(){
		ArrayList<Variable> vl = new ArrayList<Variable>();
		ArrayList<Predicate> adl = new ArrayList<Predicate>();
		ArrayList<Predicate> del = new ArrayList<Predicate>();
		ArrayList<Predicate> prl = new ArrayList<Predicate>();
		for(Variable v : varList){
			vl.add(v.clone());
		}
		for(Predicate p : addList){
			adl.add(p.clone());
		}
		for(Predicate p : deleteList){
			del.add(p.clone());
		}
		for(Predicate p : precList){
			prl.add(p.clone());
		}
		String n = new String(name);
		Operator oNew = new Operator(adl, del, prl, n, vl);
		return oNew;
	}
	
}
