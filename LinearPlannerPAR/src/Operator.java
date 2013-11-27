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
	
}
