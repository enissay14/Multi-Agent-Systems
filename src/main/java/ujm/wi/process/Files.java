package ujm.wi.process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ujm.wi.entities.Agent;
import ujm.wi.enums.Paths;
import ujm.wi.write.WriteInFile;

public class Files {

	public static List<String> fileVar = new ArrayList<String>();
	public static HashMap<String, List<String>> fileDom = new HashMap<String, List<String>>();
	public static List<String> fileCtr = new ArrayList<String>();
	public static HashMap<String, List<String>> neighbors = new HashMap<String, List<String>>();
	public static HashMap<String, String> variableDomain = new HashMap<String, String>();
	public static List<List<String>> constraintList = new ArrayList<List<String>>();
	public static HashMap<List<String>, List<List<String>>> cost = new HashMap<List<String>, List<List<String>>>();
	public static List<Agent> agents = new ArrayList<Agent>();

	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		System.out.println("start at : " + startTime);
		fileToList();

		/* List of All Agent */
		agents = generateListOfAgents();
		// showHashMapStringString(variableDomain);
		System.out.println("nombre d'agent : "+agents.size());

		// printCostHashMap();
		System.out.println("cost size : " + cost.size());

		WriteInFile.writeFile(agents);

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);

	}

	
	
	
	/**
	 * this function generate the list of all agents
	 * 
	 * @return
	 */
	public static List<Agent> generateListOfAgents() {
		List<Agent> agents = new ArrayList<Agent>();
		Set<String> keys = neighbors.keySet();
		Set<List<String>> keysCost = cost.keySet();

		for (String s : keys) {
			Agent agent = new Agent();
			agent.setName(s);
			agent.setNeighbors(neighbors.get(s));
			agent.setSons(neighbors.get(s));
			// neighbors.remove(s);
			HashMap<List<String>, List<List<String>>> costAgent = new HashMap<List<String>, List<List<String>>>();
			for (List<String> conf : keysCost) {
				if (conf.get(0).equals(s)) {
					// conf.remove(0);
					costAgent.put(conf, cost.get(conf));
				}
				// cost.remove(conf);
			}
			agent.setCost(costAgent);
			agents.add(agent);
		}

		return agents;
	}

	public static void printCostHashMap() {
		Set<List<String>> keys = cost.keySet();
		List<List<String>> listOfcost = new ArrayList<List<String>>();
		for (List<String> key : keys) {
			listOfcost = cost.get(key);
			for (List<String> cost : listOfcost) {
				System.out
						.println("value of agent '" + key.get(0) + "' is '" + key.get(1) + "' and " + "value of agent '"
								+ key.get(2) + "' is '" + cost.get(0) + "' with cost '" + cost.get(1) + "'");
			}
		}
	}

	/**
	 * This function return a HashMap that return all cost of all agents
	 * 
	 * @return
	 */
	public static HashMap<List<String>, List<List<String>>> getCostHashMap() {
		HashMap<List<String>, List<List<String>>> costHashMap = new HashMap<List<String>, List<List<String>>>();
		Set<String> keysNeighbors = null;
		keysNeighbors = neighbors.keySet();
		List<String> myNeighbors = new ArrayList<String>();

		for (String me : keysNeighbors) {
			myNeighbors = neighbors.get(me);

			for (String myNeighbor : myNeighbors) {
				String myDomain = variableDomain.get(me);
				String neighborDomain = variableDomain.get(myNeighbor);
				List<String> myDomainList = fileDom.get(myDomain);
				List<String> neighborDomainList = fileDom.get(neighborDomain);

				for (String myValue : myDomainList) {
					List<List<String>> listCostNeighbor = new ArrayList<List<String>>();
					for (String neighborValue : neighborDomainList) {
						String cost = null;
						cost = getCost(me, myValue, myNeighbor, neighborValue);
						List<String> neighborValueAndCost = new ArrayList<String>();
						neighborValueAndCost.add(0, neighborValue);
						neighborValueAndCost.add(1, cost);
						listCostNeighbor.add(neighborValueAndCost);
					}
					List<String> keyCostNeighbor = new ArrayList<String>();
					keyCostNeighbor.add(0, me);
					keyCostNeighbor.add(1, myValue);
					keyCostNeighbor.add(2, myNeighbor);
					costHashMap.put(keyCostNeighbor, listCostNeighbor);
				}
			}
		}
		return costHashMap;
	}

	/**
	 * Function that return cost between two agent
	 * 
	 * @param agent1
	 * @param valueAgent1
	 * @param agent2
	 * @param valueAgent2
	 * @return
	 */
	public static String getCost(String agent1, String valueAgent1, String agent2, String valueAgent2) {
		int operation = 0;
		for (List<String> line : constraintList) {
			if (line.get(0).equals(agent1) && line.get(1).equals(agent2)
					|| line.get(0).equals(agent2) && line.get(1).equals(agent1)) {
				String operator = line.get(3);
				int intValueAgent1 = Integer.parseInt(valueAgent1);
				int intValueAgent2 = Integer.parseInt(valueAgent2);
				if ((intValueAgent1 - intValueAgent2) >= 0) {
					operation = intValueAgent1 - intValueAgent2;
				} else {
					operation = intValueAgent2 - intValueAgent1;
				}

				if (operator.equals(">")) {
					if (operation > Integer.parseInt(line.get(4))) {
						return "0";
					} else {
						return (String.valueOf((Integer.parseInt(line.get(4)) - operation)));
					}

				} else if (operator.equals("=")) {
					if (operation == Integer.parseInt(line.get(4))) {
						return "0";
					} else {
						if ((Integer.parseInt(line.get(4)) - operation) > 0) {
							return (String.valueOf((Integer.parseInt(line.get(4)) - operation)));
						} else {
							return (String.valueOf(operation - Integer.parseInt(line.get(4))));
						}
					}
				} else {
					System.out.println("Operator not supported");
				}
			}
		}
		return null;
	}

	/**
	 * function that transform list of constraints to splited list of
	 * constraints
	 * 
	 * @param file
	 * @return
	 */
	public static List<List<String>> getConstraints(List<String> file) {
		List<List<String>> constraints = new ArrayList<List<String>>();
		List<String> splitedLine = new ArrayList<String>();
		for (String line : file) {
			splitedLine = splitLine(line);
			constraints.add(splitedLine);
		}

		return constraints;
	}

	/**
	 * Function that get all neighbors a
	 * 
	 * @param file
	 * @return
	 */
	public static HashMap<String, List<String>> getNeighbors(List<String> file) {
		HashMap<String, List<String>> hashMapNeighbors = new HashMap<String, List<String>>();
		List<String> splitedLine = new ArrayList<String>();
		Set<String> keys = null;

		for (String line : file) {
			splitedLine = splitLine(line);
			keys = hashMapNeighbors.keySet();
			if (keys.contains(splitedLine.get(0))) {
				hashMapNeighbors.get(splitedLine.get(0)).add(splitedLine.get(1));
			} else {
				List<String> listWithFirstElement = new ArrayList<String>();
				listWithFirstElement.add(splitedLine.get(1));
				hashMapNeighbors.put(splitedLine.get(0), listWithFirstElement);
			}

			if (keys.contains(splitedLine.get(1))) {
				hashMapNeighbors.get(splitedLine.get(1)).add(splitedLine.get(0));
			} else {
				List<String> listWithFirstElement = new ArrayList<String>();
				listWithFirstElement.add(splitedLine.get(0));
				hashMapNeighbors.put(splitedLine.get(1), listWithFirstElement);
			}
		}

		return hashMapNeighbors;
	}

	/**
	 * function that print HashMap <String, List<String>> (print Hashmap of
	 * neighbors)
	 * 
	 * @param hashMap
	 */
	public static void showHashMap(HashMap<String, List<String>> hashMap) {
		Set<String> keys = hashMap.keySet();

		for (String s : keys) {
			System.out.print(s + " : [");
			for (String ss : hashMap.get(s)) {
				System.out.print(ss + " ");
			}
			System.out.print("]\n");
		}
	}

	/**
	 * function that print HashMap <String,String>
	 * 
	 * @param hashMap
	 */
	public static void showHashMapStringString(HashMap<String, String> hashMap) {
		Set<String> keys = hashMap.keySet();

		for (String s : keys) {
			System.out.println(s + "," + hashMap.get(s));
		}
	}

	/**
	 * function that split line
	 * 
	 * @param line
	 * @return
	 */
	public static List<String> splitLine(String line) {
		List<String> splitedLine = new ArrayList<String>();
		String[] parts = line.split(" ");
		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].equals("") && !parts[i].contains(" ")) {
				splitedLine.add(parts[i]);
			}
		}
		return splitedLine;
	}

	/**
	 * File to List of String
	 * 
	 * @throws IOException
	 */
	public static void fileToList() throws IOException {
		fileVar = readFile(Paths.VAR.toString());
		fileCtr = readFile(Paths.CTR.toString());
		fileDom = domainFileToHashMap(Paths.DOM.toString());

		neighbors = getNeighbors(fileCtr);
		variableDomain = variableFileToHashMap(fileVar);
		constraintList = getConstraints(fileCtr);
		cost = getCostHashMap();

	}

	/**
	 * function that transform variable file (list of string) to HashMap
	 * 
	 * @param file
	 * @return
	 */
	public static HashMap<String, String> variableFileToHashMap(List<String> file) {
		HashMap<String, String> variableHashMap = new HashMap<String, String>();
		List<String> splitedLine = new ArrayList<String>();

		for (String line : file) {
			splitedLine = splitLine(line);
			variableHashMap.put(splitedLine.get(0), splitedLine.get(1));
		}

		return variableHashMap;
	}

	/**
	 * Convert file to HashMap
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, List<String>> domainFileToHashMap(String path) {
		HashMap<String, List<String>> domainHashMap = new HashMap<String, List<String>>();
		List<String> file = new ArrayList<String>();
		List<String> splitedLine = new ArrayList<String>();
		try {
			file = readFile(path);
		} catch (IOException e) {
			System.out.println("Can't read File !!");
		}
		String key = new String();

		for (String line : file) {
			splitedLine = splitLine(line);
			key = splitedLine.get(0);
			splitedLine.remove(0);
			domainHashMap.put(key, splitedLine);
		}

		return domainHashMap;
	}

	/**
	 * This function extract all data from file and put it in list of String
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> readFile(String path) throws IOException {
		List<String> file = new ArrayList<String>();
		String l = new String();

		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			while ((l = br.readLine()) != null) {
				file.add(l);
			}
		} finally {
			br.close();
		}
		return file;
	}

}
