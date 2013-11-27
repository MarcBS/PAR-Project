import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			readInput("input.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	public static void readInput(String filename) throws IOException
	{
		FileReader fileReader;
		String str;
		HashMap<String, Variable> varMap = new HashMap<String, Variable>();
		try {
			fileReader = new FileReader(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		BufferedReader br = new BufferedReader(fileReader);
		
		while ((str = br.readLine()) != null)
		{
			String[] parts = str.split("=");
			String variableLabel = null;
			
			if (parts[0].equals("Wagons"))
			{
				parts = parts[1].split(",");
				for (String token : parts)
				{
					variableLabel = token.substring(0, 1);
					varMap.put(variableLabel, new Variable(variableLabel));
				}
			}
			else if (parts[0].equals("Initial_state"))
			{
				parts = parts[1].split(";");
				for (String token : parts)
				{
					String[] aux = token.split("\\(");
					String predicateLabel = aux[0];	//aux[0] is the name of the predicate, aux[1] could be "X)" or "X,Y)" or null in the case of a predicate without parameters
					ArrayList<Variable> varList = null;
					int numVariables = 0;
					
					if (aux.length > 1)
					{
						varList = new ArrayList<Variable>();
						aux = aux[1].split(",");	
						for (String tok : aux)	// tok could be "X" or "X)"
						{
							variableLabel = tok.substring(0, 1);
							Variable var = varMap.get(variableLabel);
							varList.add(var);
						}
						numVariables = aux.length;
					}
					
					Predicate predicate = new Predicate(predicateLabel, varList, numVariables);
				}
			}
			else if (parts[0].equals("Goal_state"))
			{
				parts = parts[1].split(";");
				for (String token : parts)
				{
					System.out.println(token);
				}
			}
		}
	}
}
