package paramaterExtracter;

import java.util.ArrayList;

import analyzer.AnalyzerMethod;
import pathExtracter.TraceMethodBlock;
import testMatcher.ExecutePath;
import tracer.ValueOption;

public class ExtractMethod {

	private String methodName;
	private long seqNum;
	private ArrayList<ValueOption> argumentLists = new ArrayList<ValueOption>();
	private ValueOption returnValueOption = null;
	private AnalyzerMethod analyzerMethod;
	private ArrayList<TraceMethodBlock> blockLists = new ArrayList<TraceMethodBlock>();
	
	private ExecutePath executePath = null;
	
	public void display() {
		System.out.print(methodName + ":");
		for(int argNum = 0; argNum < argumentLists.size(); argNum++) {
			System.out.print(argumentLists.get(argNum).getValue() + ", ");
		}
		System.out.println();
	}
	
	public void setExecutePath(ExecutePath input) {
		executePath = input;
	}
	
	public ExecutePath getExecutePath() {
		return executePath;
	}
	
	public ExtractMethod(String mN, AnalyzerMethod aM) {
		methodName = mN;
		analyzerMethod = aM;
	}
	
	public ExtractMethod(String s, AnalyzerMethod aM, long sN) {
		methodName = s;
		analyzerMethod = aM;
		seqNum = sN;
	}
	
	public void addTraceMethodBlock(TraceMethodBlock input) {
		blockLists.add(input);
	}
	
	public ArrayList<TraceMethodBlock> getTraceMethodBlock() {
		return blockLists;
	}
	
	public void addArgumentLists(ValueOption input) {
		argumentLists.add(input);
	}
	
	public void setReturnValueOption(ValueOption input) {
		returnValueOption = input;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public long getSeqNum() {
		return seqNum;
	}
	
	public ArrayList<ValueOption> getArgmentLists(){
		return argumentLists;
	}
	
	public ValueOption getReturnValueOption() {
		return returnValueOption;
	}
	
	public AnalyzerMethod getAnalyzerMethod() {
		return analyzerMethod;
	}
}
