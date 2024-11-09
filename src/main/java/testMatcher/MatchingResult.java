package testMatcher;

import java.util.ArrayList;

public class MatchingResult {

	private SameExecutePath sameExecuteEvoSuitePath;
	private ArrayList<MatchingInstance> matchingInstanceLists;
	
	public MatchingResult(SameExecutePath sEESP, ArrayList<MatchingInstance> matchingInstanceLists) {
		sameExecuteEvoSuitePath = sEESP;
		this.matchingInstanceLists = matchingInstanceLists;
	}
	
	public SameExecutePath getSameExecuteEvoSuitePath() {
		return sameExecuteEvoSuitePath;
	}
	
	public ArrayList<MatchingInstance> getMatchingInstanceLists() {
		return matchingInstanceLists;
	}


}
