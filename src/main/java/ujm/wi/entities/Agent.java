package ujm.wi.entities;

import java.util.HashMap;
import java.util.List;

public class Agent {

	private String name;
	private String father;
	private List<String> sons;
	private List<String> neighbors;
	//<[Agent1,valueAgent1,Agent2],[[value1Agent2,cost1],[value2Agent2,cost2], ... >
	private HashMap<List<String>, List<List<String>>> cost;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}

	public List<String> getSons() {
		return sons;
	}

	public void setSons(List<String> sons) {
		this.sons = sons;
	}

	public List<String> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<String> neighbors) {
		this.neighbors = neighbors;
	}

	public HashMap<List<String>, List<List<String>>> getCost() {
		return cost;
	}

	public void setCost(HashMap<List<String>, List<List<String>>> cost) {
		this.cost = cost;
	}

}
