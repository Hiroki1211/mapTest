package testAnalyzer;

import java.util.ArrayList;
import java.util.regex.Pattern;

import pathExtracter.TraceMethodBlock;

public class TestAssertion {

	private String value = "";
	private String variable = "";
	private String getterMethodInstance = "";
	private String getterMethodName = "";
	private ArrayList<String> getterMethodArgument = new ArrayList<String>();
	private TestMethod assertionTargetMethod = null;
	private String assertMethodName;
	private int lineNum;
	private TraceMethodBlock traceMethodBlock = null;
	
	private String statement = "";
	
	public void setTraceMethodBlock(TraceMethodBlock input) {
		traceMethodBlock = input;
	}
	
	public TraceMethodBlock getTraceMethodBlock() {
		return traceMethodBlock;
	}
	
	public TestAssertion(String v, String var, String gMI, String gMN, ArrayList<String> gMA, TestMethod aTM, String s, String assertMethodName, int lineNum){
		value = v;
		variable = var;
		getterMethodInstance = gMI;
		getterMethodName = gMN;
		getterMethodArgument = gMA;
		assertionTargetMethod = aTM;
		statement = s;
		this.assertMethodName = assertMethodName;
		this.lineNum = lineNum;
	}
	
	public TestAssertion(String v, String var, String get, String s, String assertMethodName, int lineNum) {
		value = v;
		variable = var;
		if(!get.equals("")) {
			this.getterMethod(get);
		}
		statement = s;
		this.assertMethodName = assertMethodName;
		this.lineNum = lineNum;
	}
	
	public void display() {
		System.out.println("value:" + value);
		System.out.println("variable:" + variable);
		System.out.println("getterMethodInstance:" + getterMethodInstance);
		System.out.println("getterMethodName:" + getterMethodName);
		System.out.println("getterMethodArgument:" + getterMethodArgument);
		System.out.println(assertMethodName);
	}
	
	public void getterMethod(String getterMethod) {
		getterMethod = getterMethod.replace(" ", "");
		String[] splitBracket = getterMethod.split("[()]");
		String[] splitGetterMethod = splitBracket[0].split(Pattern.quote("."));
		
		getterMethodInstance = splitGetterMethod[0];
		getterMethodName = splitGetterMethod[1];
		
		if(splitBracket.length > 1) {			
			String[] splitArgument = splitBracket[1].split(",");
			for(int i = 0; i < splitArgument.length; i++) {
				getterMethodArgument.add(splitArgument[i]);
			}
		}
	}
	
	public void setAssertionTargetMethod(TestMethod input) {
		assertionTargetMethod = input;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getVariable() {
		return variable;
	}
	
	public String getGetterMethodInstance() {
		return getterMethodInstance;
	}
	
	public String getGetterMethodName() {
		return getterMethodName;
	}
	
	public ArrayList<String> getGetterMethodArgument(){
		return getterMethodArgument;
	}
	
	public TestMethod getAssertionTargetMethod() {
		return assertionTargetMethod;
	}
	
	public String getStatement() {
		return statement;
	}
	
	public String getAssertMethodName() {
		return assertMethodName;
	}
	
	public int getLineNum() {
		return lineNum;
	}
}
