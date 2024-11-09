package testMatcher;

import java.util.ArrayList;

public class MatchingPath {

	private SameExecuteExtractPath sameExecuteExtractPath;
	private ArrayList<MatchingMethod> matchingMethodLists;
	
	public MatchingPath(SameExecuteExtractPath sameExecuteExtractPath, ArrayList<MatchingMethod> matchingMethodLists) {
		this.sameExecuteExtractPath = sameExecuteExtractPath;
		this.matchingMethodLists = matchingMethodLists;
	}
	
	public SameExecuteExtractPath getSameExecuteExtractPath() {
		return sameExecuteExtractPath;
	}
	
	public ArrayList<MatchingMethod> getMatchingMethodLists(){
		return matchingMethodLists;
	}
}
