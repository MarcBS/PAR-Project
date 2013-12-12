import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Master in Artificial Intelligence
 * Planning and Approximate Reasoning
 * 
 * Linear Planner Exercise
 * 
 * @author Marc Bolaños and Albert Busqué
 *
 */
public class Main {
	
	private static int numRailways; // number of maximum railways

	public static void main(String[] args) {
		
		State[] states;
		try {
			
			states = readInput("input.txt");
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
			// Initialization of the problem
			
			// Create predicates
			Predicate p1 = new Predicate("USED-RAILWAYS", new ArrayList<Variable>(), 1);
			p1.getVariables().add(new DefaultVariable("n"));
			
			Predicate p1_1 = new Predicate("USED-RAILWAYS", new ArrayList<Variable>(), 1);
			p1_1.getVariables().add(new DefaultVariable("n-1"));
			
			Predicate p1_2 = new Predicate("USED-RAILWAYS", new ArrayList<Variable>(), 1);
			p1_2.getVariables().add(new DefaultVariable("n+1"));
			
			Predicate p2 = new Predicate("ON-STATION", new ArrayList<Variable>(), 1);
			p2.getVariables().add(new DefaultVariable("x"));
			
			Predicate p3 = new Predicate("FREE-LOCOMOTIVE", new ArrayList<Variable>(), 0);
			
			Predicate p4 = new Predicate("FREE", new ArrayList<Variable>(), 1);
			p4.getVariables().add(new DefaultVariable("x"));
			
			Predicate p5 = new Predicate("TOWED", new ArrayList<Variable>(), 1);
			p5.getVariables().add(new DefaultVariable("x"));
			
			Predicate p6 = new Predicate("IN-FRONT-OF", new ArrayList<Variable>(), 2);
			p6.getVariables().add(new DefaultVariable("x"));
			p6.getVariables().add(new DefaultVariable("y"));
			
			Predicate p7 = new Predicate("EMPTY", new ArrayList<Variable>(), 1);
			p7.getVariables().add(new DefaultVariable("x"));
			
			Predicate p8 = new Predicate("LOADED", new ArrayList<Variable>(), 1);
			p8.getVariables().add(new DefaultVariable("x"));
			
			ArrayList<Predicate> lp = new ArrayList<Predicate>();
			lp.add(p1);
			lp.add(p2);
			lp.add(p3);
			lp.add(p4);
			lp.add(p5);
			lp.add(p6);
			lp.add(p7);
			lp.add(p8);
			
			// Create operators
			
			/////// COUPLE
			Operator op1 = new Operator(new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										"COUPLE", 
										new ArrayList<Variable>());
			//op1.getPrecList().add(p1);
			op1.getPrecList().add(p2);
			op1.getPrecList().add(p3);
			op1.getPrecList().add(p4);
			op1.getDeleteList().add(p2);
			op1.getDeleteList().add(p3);
			//op1.getDeleteList().add(p4);
			op1.getAddList().add(p5);
			op1.getAddList().add(p1_1);
			op1.getVarList().add(new DefaultVariable("x"));
			
			/////// PARK
			Operator op2 = new Operator(new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										"PARK", 
										new ArrayList<Variable>());
			op2.getPrecList().add(p5);
			op2.getPrecList().add(p1);
			op2.getDeleteList().add(p5);
			//op2.getDeleteList().add(p1);
			op2.getAddList().add(p2);
			op2.getAddList().add(p1_2);
			op2.getAddList().add(p3);
			//op2.getAddList().add(p4);
			op2.getVarList().add(new DefaultVariable("x"));
			
			/////// DETACH
			Operator op3 = new Operator(new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										"DETACH", 
										new ArrayList<Variable>());
			op3.getPrecList().add(p6);
			op3.getPrecList().add(p4);
			op3.getPrecList().add(p3);
			op3.getDeleteList().add(p6);
			op3.getDeleteList().add(p3);
			//op3.getDeleteList().add(p4);
			op3.getAddList().add(p5);
			// FREE(Y)
			Variable v = new DefaultVariable("y");
			ArrayList<Variable> aV = new ArrayList<Variable>();
			aV.add(v);
			op3.getAddList().add(new Predicate("FREE", aV, 1));
			op3.getVarList().add(new DefaultVariable("x"));
			op3.getVarList().add(new DefaultVariable("y"));
			
			/////// ATTACH
			Operator op4 = new Operator(new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										"ATTACH", 
										new ArrayList<Variable>());
			op4.getPrecList().add(p5);
			// FREE(Y)
			v = new DefaultVariable("y");
			aV = new ArrayList<Variable>();
			aV.add(v);
			op4.getPrecList().add(new Predicate("FREE", aV, 1));
			op4.getDeleteList().add(p5);
			// FREE(Y)
			v = new DefaultVariable("y");
			aV = new ArrayList<Variable>();
			aV.add(v);
			op4.getDeleteList().add(new Predicate("FREE", aV, 1));
			op4.getAddList().add(p6);
			op4.getAddList().add(p3);
			//op4.getAddList().add(p4);
			op4.getVarList().add(new DefaultVariable("x"));
			op4.getVarList().add(new DefaultVariable("y"));
			
			/////// LOAD
			Operator op5 = new Operator(new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										"LOAD", 
										new ArrayList<Variable>());
			op5.getPrecList().add(p2);
			op5.getPrecList().add(p7);
			op5.getDeleteList().add(p7);
			op5.getAddList().add(p8);
			op5.getVarList().add(new DefaultVariable("x"));
			
			/////// UNLOAD
			Operator op6 = new Operator(new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										new ArrayList<Predicate>(), 
										"UNLOAD", 
										new ArrayList<Variable>());
			op6.getPrecList().add(p2);
			op6.getPrecList().add(p8);
			op6.getDeleteList().add(p8);
			op6.getAddList().add(p7);
			op6.getVarList().add(new DefaultVariable("x"));
			
			ArrayList<Operator> lo = new ArrayList<Operator>();
			lo.add(op1);
			lo.add(op2);
			lo.add(op3);
			lo.add(op4);
			lo.add(op5);
			lo.add(op6);
			
			Locomotive locomotive = new Locomotive(lo, lp, states[0], states[1], numRailways);
			
			
			// Prepares the log file
			BufferedWriter writer = null;
			File logFile = null;
	        try {
	            logFile = new File("log.txt");

	            writer = new BufferedWriter(new FileWriter(logFile));

				// Plan solving
				ArrayList<Operator> plan = locomotive.solve(writer);
				
				// Final plan print
				writer.write("\nFinal plan found:" + "\n");
				for(Operator o : plan){
					writer.write("\t" + o.toString() + "\n");
				}
				writer.write("Number of operations applied: " + String.valueOf(plan.size()) + "\n");
				
				// This will output the full path where the file will be written to...
	            System.out.println("\nLog file saved in: " + logFile.getCanonicalPath());
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {
	            }
	        }
	}
	
	/**
	 * Reads the initial and final states from the .txt input file.
	 * 
	 * @param filename String with the name of the input file.
	 * @return State[] an array with the initial state as the first and 
	 * 		the final state as the second parameters.
	 * @throws IOException
	 */
	public static State[] readInput(String filename) throws IOException
	{
		FileReader fileReader;
		String str;
		HashMap<String, Variable> varMap = new HashMap<String, Variable>();
		State initialState = null, finalState = null;
		try {
			fileReader = new FileReader(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		BufferedReader br = new BufferedReader(fileReader);
		
		while ((str = br.readLine()) != null)
		{
			String[] parts = str.split("=");
			String variableLabel = null;
			ArrayList<Predicate> pl = new ArrayList<Predicate>();
			
			if (parts[0].equals("Wagons"))
			{
				parts = parts[1].split(",");
				for (String token : parts)
				{
					variableLabel = token.substring(0, 1);
					varMap.put(variableLabel, new Variable(variableLabel));
				}
			}
			else /*if (parts[0].equals("Initial_state"))*/
			{
				String stateLabel = parts[0];
				parts = parts[1].split(";");
				for (String token : parts)
				{
					String[] aux = token.split("\\(");
					String predicateLabel = aux[0];	//aux[0] is the name of the predicate, aux[1] could be "X)" or "X,Y)" or null in the case of a predicate without parameters
					ArrayList<Variable> varList = new ArrayList<Variable>();
					int numVariables = 0;
					
					if (aux.length > 1)
					{
						aux = aux[1].split(",");	
						for (String tok : aux)	// tok could be "X" or "X)"
						{
							variableLabel = tok.substring(0, 1);
							Variable var = varMap.get(variableLabel);
							varList.add(var);
						}
						numVariables = aux.length;
					}
					
					pl.add(new Predicate(predicateLabel, varList, numVariables));
				}
				
				if (stateLabel.equals("Initial_state"))
				{
					initialState = new State(pl);
				}
				else if (stateLabel.equals("Goal_state"))
				{
					finalState = new State(pl);
				}
				else if (stateLabel.equals("Max_Railways"))
				{
					String[] string = str.split("=");
					numRailways = Integer.parseInt(string[1]);
				}
			}
			/*
			else if (parts[0].equals("Goal_state"))
			{
				parts = parts[1].split(";");
				for (String token : parts)
				{
					System.out.println(token);
				}
			}*/
		}
		br.close();
		fileReader.close();
		
		State[] s = new State[2];
		s[0] = initialState;
		s[1] = finalState;
		
		return s;
	}
}
