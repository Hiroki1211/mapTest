package analyzer;

public class Class {

	private String accessModifier = "";
	private String name = "";
	private String packageName;
	
	public Class(String aM, String n, String packageName) {
		accessModifier = aM;
		name = n;
	}
	
	public String getAccessModifier() {
		return accessModifier;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPackageName() {
		return packageName;
	}
}
