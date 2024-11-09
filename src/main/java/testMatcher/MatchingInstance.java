package testMatcher;

import java.util.ArrayList;

public class MatchingInstance {

	private String className;
	private ArrayList<MatchingPath> matchingPathLists;
	
	public MatchingInstance(String className, ArrayList<MatchingPath> matchingPathLists) {
		this.className = className;
		this.matchingPathLists = matchingPathLists;
	}
	
	public String getClassName() {
		return className;
	}
	
	public ArrayList<MatchingPath> getMatchingPathLists(){
		return matchingPathLists;
	}
}
