package testAnalyzer;

import java.util.ArrayList;
import java.util.regex.Pattern;

import analyzer.AnalyzerMethod;
import pathExtracter.TraceMethodBlock;
import testMatcher.ExecutePath;
import tracer.ValueOption;

public class TestMethod {

	private String returnType = "";
	private String returnVariable = "";
	private String instance = "";
	private String methodName = "";
	private String constructorClass = "";
	private ArrayList<String> argumentLists = new ArrayList<String>();
	private int lineNum;
	
	private String statement = "";
	
	private AnalyzerMethod analyzerMethod = null;
	
	private ValueOption returnValueOption = null;
	
	private ArrayList<TraceMethodBlock> traceMethodBlockLists = new ArrayList<TraceMethodBlock>();
	private ArrayList<ExecutePath> executePathLists = new ArrayList<ExecutePath>();
	
	public int getLineNum() {
		return lineNum;
	}
	
	public void addExecutePathLists(ExecutePath input) {
		executePathLists.add(input);
	}
	
	public ArrayList<ExecutePath> getExecutePathLists() {
		return executePathLists;
	}
	
	public ArrayList<TraceMethodBlock> getTraceMethodBlockLists() {
		return traceMethodBlockLists;
	}
	
	public void addTraceMethodBlockLists(TraceMethodBlock input) {
		traceMethodBlockLists.add(input);
	}
	
	public ValueOption getReturnValueOption() {
		return returnValueOption;
	}
	
	public void setReturnValueOption(ValueOption input) {
		returnValueOption = input;
	}
	
	public void display() {
		System.out.println("returnType:" + returnType);
		System.out.println("returnVariable:" + returnVariable);
		System.out.println("instance:" + instance);
		System.out.println("methodName:" + methodName);
		System.out.println("constructorClass:" + constructorClass);
		System.out.println("argumentLists:" + argumentLists);
		System.out.println("statement:" + statement);
		System.out.println("analzerMethod:");
		if(analyzerMethod != null) {
			analyzerMethod.display();
		}
		System.out.println();
	}
	
	public TestMethod(String input, String mN, int lineNum, String returnVariable, String returnType) {
		statement = input;
		methodName = mN;
		this.lineNum = lineNum;
		this.returnVariable = returnVariable;
		this.returnType = returnType;
	}
	
	public TestMethod(String input, int lineNum) {
		this.lineNum = lineNum;
		statement = input;
		
		String[] splitSpace = input.split(" +");
		String argument = "";
		
		if(splitSpace.length > 3 && splitSpace[3].equals("=")) {
			if(splitSpace[4].equals("new")) {
				this.methodName = "<init>";
				String[] splitEqual = input.split(" = new ");
				String[] split = splitEqual[1].split("[()]", 2);
				this.constructorClass = split[0];
				this.returnType = splitSpace[1];
				this.returnVariable = splitSpace[2];
				
				if(split.length == 1) {
					return;
				}
				
				argument = split[1].replace(");", "");
				
			}else if(splitSpace[4].contains(".")){
				String[] splitEqual = input.split(" = ");
				String[] split = splitEqual[1].split(Pattern.quote("."), 2);
				this.instance = split[0];
				this.returnType = splitSpace[1];
				this.returnVariable = splitSpace[2];
				
				split = split[1].split("[()]", 2);
				this.methodName = split[0];
				
				if(split.length == 1) {
					return;
				}
				
				argument = split[1].replace(");", "");
				
			}
			
//			System.out.print("ARGUMENT:");
//			System.out.println(argument);
			String[] split = argument.split(", ");
			for(int i = 0; i < split.length; i++) {
				String arg = split[i].replace("(", "");
				arg = arg.replace(")", "");
				this.argumentLists.add(arg);
				
			}	
			
		}else if(splitSpace.length > 2 && splitSpace[2].equals("=")) {
			if(splitSpace[3].equals("new")) {
				this.methodName = "<init>";
				String[] splitEqual = input.split(" = new ");
				String[] split = splitEqual[1].split("[()]", 2);
				this.constructorClass = split[0];
				this.returnVariable = splitSpace[1];
				
				if(split.length == 1) {
					return;
				}
				
				argument = split[1].replace(");", "");
				
			}else if(splitSpace[3].contains(".")) {
				String[] splitEqual = input.split(" = ");
				String[] split = splitEqual[1].split(Pattern.quote("."), 2);
				this.instance = split[0];
				split = split[1].split("[()]", 2);
				this.methodName = split[0];
				this.returnVariable = splitSpace[1];
				
				if(split.length == 1) {
					return;
				}
				
				argument = split[1].replace(");", "");
			}
			
			String[] split = argument.split(", ");
			for(int i = 0; i < split.length; i++) {
				String arg = split[i].replace("(", "");
				arg = arg.replace(")", "");
				this.argumentLists.add(arg);
				
			}
		}else if(splitSpace.length > 1 && splitSpace[1].contains(".")){
			String[] split = input.split(Pattern.quote("."), 2);
			this.instance = split[0].replace(" ", "");
			split = split[1].split("[()]", 2);
			this.methodName = split[0];
			
			if(split.length == 1) {
				return;
			}
			
			argument = split[1].replace(");", "");
			
			String[] splitA = argument.split(", ");
			for(int i = 0; i < splitA.length; i++) {
				String arg = splitA[i].replace("(", "");
				arg = arg.replace(")", "");
				this.argumentLists.add(arg);
				
			}
			
		}
////		System.out.println(input);
//		String[] splitBracket = input.split("[()]", 2);
//		if(splitBracket.length > 1) {
//			splitBracket[1] = splitBracket[1].replace(");", "");
////			System.out.println(splitBracket[1]);
//		}
//		String[] splitSpace = splitBracket[0].split(" +");		
//		System.out.println(input);
//		System.out.println(splitBracket[0]);
////		for(int i = 0; i < splitSpace.length; i++) {
////			System.out.print(splitSpace[i] + ", ");
////		}
////		System.out.println();
//		
//		if(splitSpace[1].contains(".")) {
//			String[] split = splitSpace[1].split(Pattern.quote("."));
//			instance = split[0];
//			methodName = split[1];
//		}else {
//			if(splitSpace.length == 2) {
//				methodName = splitSpace[1];
//			}else if(splitSpace[2].equals("=")) {
//				returnVariable = splitSpace[1];
//				
//				if(splitSpace[3].equals("new")) {
//					methodName = "<init>";
//					constructorClass = splitSpace[4];
//				}else {
//					String[] splitQuote = splitSpace[3].split(Pattern.quote("."));
//					if(splitQuote.length != 2) {
//						instance = "Null";
//						methodName = "Null";
//						constructorClass = "Null";
//					}else {
//						instance = splitQuote[0];
//						methodName = splitQuote[1];
//					}
//
//				}
//			}else {
//				returnType = splitSpace[1];
//				returnVariable = splitSpace[2];
//				
//				if(splitSpace[4].equals("new")) {
//					methodName = "<init>";
//					constructorClass = splitSpace[5];
//				}else {
//					String[] splitQuote = splitSpace[4].split(Pattern.quote("."));
//					if(splitQuote.length != 2) {
//						instance = "Null";
//						methodName = "Null";
//						constructorClass = "Null";
//					}else {
//						instance = splitQuote[0];
//						methodName = splitQuote[1];
//					}
//
//				}
//			}
//		}
//		
//		if(splitBracket.length > 1 && !splitBracket[1].equals("")){
//			splitBracket[1] = splitBracket[1].replace(" ", "");
//			String[] splitArgument = splitBracket[1].split(",");
//			
//			for(int argNum = 0; argNum < splitArgument.length; argNum++) {
//				String arg = splitArgument[argNum].replace(" ", "");
//				arg = arg.replace("(", "");
//				arg = arg.replace(")", "");
//				argumentLists.add(arg);
//			}
//		}
	}
	
	public void setAnalyzerMethod(AnalyzerMethod input) {
		analyzerMethod = input;
	}
	
	public String getReturnType() {
		return returnType;
	}
	
	public String getReturnVariable() {
		return returnVariable;
	}
	
	public String getInstance() {
		return instance;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getConstructorClass() {
		return constructorClass;
	}
	
	public ArrayList<String> getArgumentLists(){
		return argumentLists;
	}
	
	public String getStatement() {
		return statement;
	}
	
	public AnalyzerMethod getAnalyzerMethod() {
		return analyzerMethod;
	}
}
