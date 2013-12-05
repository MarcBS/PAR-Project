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
				Variable newV = vl.get(i).clone();
				
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
	 * @param s State current state.
	 * @param plan ArrayList<Operator> plan currently applied.
	 * @param fs State final state.
	 */
	public boolean instantiate(State s, ArrayList<Operator> plan, State fs){
		
		/***************    Checking current state    *********************/
		Variable newV = null;
		Variable v = null;
		
		// PARK and ATTACH
		if(name.equals("PARK") || name.equals("ATTACH")){
			newV = s.getWagonOnLocomotive();
			v = varList.get(0);
			varList.remove(0);
			substituteVar(v, newV);
			varList.add(0, newV); // We set the new variable in this list.
			//if(this.isInstantiated()){
				//return true;
			//}
		// DETACH
		} else if(name.equals("DETACH")){
			boolean[] allOK = {false, false};
			while(!(allOK[0] && allOK[1])){
				for(int j = 0; j < 2; j++){
					if(varList.get(j) instanceof DefaultVariable){
						boolean found = false;
						int i = 0;
						Predicate p;
						while(i < s.getPredList().size() && !found){
							p = s.getPredList().get(i);
							if(p.getName().equals("IN-FRONT-OF") && p.getVariables().get((j+1)%2).isName(varList.get((j+1)%2).getName())){
								this.instantiate(p);
								found = true;
							}
							i++;
						}
					} else {
						allOK[j] = true;
					}
				}
			}
		}
		
		/***************    Checking final state    *********************/
		// COUPLE
		if(name.equals("COUPLE")){
			ArrayList<Variable> candidates = new ArrayList<Variable>();
			for(Predicate p : s.getPredList()){
				// Finds all the current variables ON-STATION
				if(p.getName().equals("ON-STATION")){
					boolean found = false;
					int i = 0;
					while(i < fs.getPredList().size() && !found){
						Predicate pred = fs.getPredList().get(i);
						// If on the final state there is the same variable ON-STATION
						if(pred.getName().equals("ON-STATION") && p.getVariables().get(0).isName(pred.getVariables().get(0).getName())){
							found = true;
						}
						i++; 
					}
					// Then we do not have to add it to the candidates list.
					if(!found){
						candidates.add(p.getVariables().get(0).clone());
					}
				}
			}
			
			// From all the found possible candidates, we chose one randomly.
			int Min = 1;
			int Max = candidates.size();
			int chosen = Min + (int)(Math.random() * ((Max - Min) + 1));
			newV = candidates.get(chosen-1).clone();
			v = varList.get(0);
			varList.remove(0);
			substituteVar(v, newV);
			varList.add(0, newV); // We set the new variable in this list.
			
		}
		
		
		/***************    Checking plan    *********************/
		// ATTACH
		if(name.equals("ATTACH")){
			Variable not_wanted = null;
			Variable towed = null;
			boolean found = false;
			int i = 0;
			// Finds the towed variable
			while(i < s.getPredList().size() && !found){
				found = s.getPredList().get(i).getName().equals("TOWED");
				towed = s.getPredList().get(i).getVariables().get(0);
				i++;
			}
			
			found = false;
			i = plan.size()-1;
			int last = Math.max(0, plan.size()-10);
			// If we have recently Detached the towed variable, then we should not attach it again on the same place.
			while(i >= last && !found){
				if(plan.get(i).getName().equals("DETACH") && plan.get(i).getVarList().get(0).isName(towed.getName())){
					not_wanted = plan.get(i).getVarList().get(1);
					found = true;
				}
				i--;
			}
			// Find all the wagons that are FREE() except "not_wanted" and "towed"
			ArrayList<Variable> free_vars = new ArrayList<Variable>();
			for(Predicate p : s.getPredList()){
				if(p.getName().equals("FREE") && !p.getVariables().get(0).isName(towed.getName())){
					// If there is any "not_wanted" variable, then we have to check that is is not chosen.
					if(not_wanted != null){
						if(!p.getVariables().get(0).isName(not_wanted.getName())){
							free_vars.add(p.getVariables().get(0));
						}
					// If there is no "no_wanted" varaible, then we can choose this operator.
					} else {
						free_vars.add(p.getVariables().get(0));
					}
				}
			}
			// Instantiate randomly without using "not_wanted"
			int Min = 1;
			int Max = free_vars.size();
			int chosen = Min + (int)(Math.random() * ((Max - Min) + 1));
			newV = free_vars.get(chosen-1).clone();
			v = varList.get(1);
			varList.remove(1);
			substituteVar(v, newV);
			varList.add(1, newV); // We set the new variable in this list.
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
	
	
	public String toString(){
		String ret = "Operator: " + name + " ";
		for(Variable v : varList){
			ret += v.getName() + " ";
		}
		return ret;
	}
	
}
