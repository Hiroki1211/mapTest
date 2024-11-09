package testMatcher;

import java.util.ArrayList;

public class PartiallyMatchingResult {

	private SameExecutePath sameExecuteEvoSuitePath;
	private ArrayList<SameExecuteExtractPath> partiallyMatchingExtractPathLists;
	private boolean isContainObject = false;
	
	public PartiallyMatchingResult(SameExecutePath sEESP, ArrayList<SameExecuteExtractPath> input) {
		sameExecuteEvoSuitePath = sEESP;
		partiallyMatchingExtractPathLists = input;
	}
	
	public void setIsContainObject(boolean input) {
		isContainObject = input;
	}
	
	public SameExecutePath getSameExecuteEvoSuitePath() {
		return sameExecuteEvoSuitePath;
	}
	
	public ArrayList<SameExecuteExtractPath> getPartiallyMatchingExtractPathLists() {
		return partiallyMatchingExtractPathLists;
	}
	
	public boolean getIsContainObject() {
		return isContainObject;
	}
}
