package testAnalyzer;

import java.util.ArrayList;

public class InstanceMethodGroup {

	private String className;
	private String variableName;
	private ArrayList<TestMethod> methodLists = new ArrayList<TestMethod>();
	
	public InstanceMethodGroup(String className, String variableName) {
		this.className = className;
		this.variableName = variableName;
	}
	
	public void addMethodLists(TestMethod input) {
		methodLists.add(input);
	}
	
	public String getClassname() {
		return className;
	}
	
	public String getVariableName() {
		return variableName;
	}
	
	public ArrayList<TestMethod> getMethodLists(){
		return methodLists;
	}
}
