package testMatcher;

import java.util.ArrayList;

import paramaterExtracter.Instance;

public class SameExecuteExtractPath {

	private ArrayList<Instance> instanceLists = new ArrayList<Instance>();
	
	private boolean isMatch = false;
	
	public void addInstanceLists(Instance input) {
		instanceLists.add(input);
	}
	
	public void setIsMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}
	
	public ArrayList<Instance> getInstanecLists(){
		return instanceLists;
	}
	
	public boolean getIsMatch() {
		return isMatch;
	}
}
