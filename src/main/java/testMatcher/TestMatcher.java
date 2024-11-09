package testMatcher;

//import java.io.File;
import java.util.ArrayList;

//import analyzer.Analyzer;
//import analyzer.AnalyzerVariable;
//import exportFileGenerator.ExportFileGenerator;
//import exportFileGenerator.MatchingMethod;
//import exportFileGenerator.NaturalTest;
//import exportFileGenerator.NaturalTestClass;
import paramaterExtracter.ExtractClass;
import paramaterExtracter.ExtractMethod;
//import paramaterExtracter.ExtractMethod;
import paramaterExtracter.Instance;
//import paramaterExtracter.ParamaterExtracter;
//import paramaterExtracter.PutInstanceVariable;
//import pathExtracter.PathExtracter;
import pathExtracter.TraceMethodBlock;
import testAnalyzer.InstanceMethodGroup;
import testAnalyzer.Test;
//import testAnalyzer.TestAssertion;
import testAnalyzer.TestClass;
import testAnalyzer.TestMethod;
//import tracer.Lexer;
import tracer.Trace;
//import tracer.ValueOption;

public class TestMatcher {

	public ArrayList<Result> run(ArrayList<TestClass> evoSuiteTestClassLists, ArrayList<ExtractClass> extractClassLists){
		// 4. analyze extract path
		this.analyzeExtractPath(evoSuiteTestClassLists);
		this.analyzeExtractClassExtractPath(extractClassLists);
		
		// 5. summarize same extract method
		this.summarizeSameExecutePath(evoSuiteTestClassLists);
		this.summarizeExtractClassSameExecutePath(extractClassLists);
		
		// 6. matching test
		ArrayList<Result> resultLists = this.testMatching(evoSuiteTestClassLists, extractClassLists);
		
		return resultLists;
	}
	
	private ArrayList<Result> testMatching(ArrayList<TestClass> evoSuiteTestClassLists, ArrayList<ExtractClass> extractClassLists) {
		ArrayList<Result> resultLists = new ArrayList<Result>();
		for(int evoSuiteClassNum = 0; evoSuiteClassNum < evoSuiteTestClassLists.size(); evoSuiteClassNum++) {
			TestClass evoSuiteTestClass = evoSuiteTestClassLists.get(evoSuiteClassNum);
			ArrayList<SameExecutePath> sameExecutePathLists = evoSuiteTestClass.getSameExecutePathLists();
			
			Result result = new Result(evoSuiteTestClass);
			resultLists.add(result);
			
			ExtractClass mainExtractClass = null;
			String ownerClassName = evoSuiteTestClass.getClassName().replace("_ESTest", "");
			for(int extractClassNum = 0; extractClassNum < extractClassLists.size(); extractClassNum++) {
				ExtractClass targetExtractClass = extractClassLists.get(extractClassNum);
				
				if(targetExtractClass.getOwnerClass().equals(ownerClassName)) {
					mainExtractClass = targetExtractClass;
				}
			}
			result.setExtractClass(mainExtractClass);
			
			for(int samePathNum = 0; samePathNum < sameExecutePathLists.size(); samePathNum++) {
				SameExecutePath sameExecutePath = sameExecutePathLists.get(samePathNum);
				
				if(mainExtractClass == null) {
					result.addNotMatchingEvoSuiteLists(sameExecutePath);
				}else {
					Test evoSuiteTest = sameExecutePath.getTestLists().get(0);
					ArrayList<InstanceMethodGroup> instanceMethodGroupLists = evoSuiteTest.getInstanceMethodGroupLists();
					boolean isMatch = true;
					ArrayList<MatchingInstance> matchingInstanceLists = new ArrayList<MatchingInstance>();
					
					// 1. 各InstanceMethodGroupが全てマッチしているかをチェックする
					for(int groupNum = 0; groupNum < instanceMethodGroupLists.size(); groupNum++) {
						InstanceMethodGroup instanceMethodGroup = instanceMethodGroupLists.get(groupNum);
						String className = instanceMethodGroup.getClassname();
						ExtractClass targetExtractClass = null;
						for(int extractClassNum = 0; extractClassNum < extractClassLists.size(); extractClassNum++) {
							ExtractClass extractClass = extractClassLists.get(extractClassNum);
							if(className.equals(extractClass.getOwnerClass())) {
								targetExtractClass = extractClass;
								break;
							}
						}
						
						if(targetExtractClass == null) {
							isMatch = false;
							break;
						}else {
							ArrayList<MatchingPath> matchingPathLists = this.isMatchGroup(instanceMethodGroup, targetExtractClass);
							
							if(matchingPathLists.size() > 0) {
								MatchingInstance matchingIntance = new MatchingInstance(targetExtractClass.getOwnerClass(), matchingPathLists);
								matchingInstanceLists.add(matchingIntance);
							}else {
								isMatch = false;
								break;
							}
						}
					}
					
					// 2. 全てのInstanceMethodGroupがマッチしているなら、成功
					if(isMatch) {
						MatchingResult matchingResult = new MatchingResult(sameExecutePath, matchingInstanceLists);
						result.addMatchingResultLists(matchingResult);
						
						// 3. マッチしたSameExecuteExtractPathを match = true にする
						for(int matchInstanceNum = 0; matchInstanceNum < matchingInstanceLists.size(); matchInstanceNum++) {
							MatchingInstance matchingInstance = matchingInstanceLists.get(matchInstanceNum);
							ArrayList<MatchingPath> matchingPathLists = matchingInstance.getMatchingPathLists();
							
							for(int matchPathNum = 0; matchPathNum < matchingPathLists.size(); matchPathNum++) {
								MatchingPath matchingPath = matchingPathLists.get(matchPathNum);
								SameExecuteExtractPath sameExecuteExtractPath = matchingPath.getSameExecuteExtractPath();
								sameExecuteExtractPath.setIsMatch(true);
							}
						}
						
						
					}else {
						result.addNotMatchingEvoSuiteLists(sameExecutePath);
					}
				}
			}
			
			
		}
		
		return resultLists;
	}
	
	private ArrayList<MatchingPath> isMatchGroup(InstanceMethodGroup instanceMethodGroup, ExtractClass extractClass) {
		ArrayList<MatchingPath> matchingPathLists = new ArrayList<MatchingPath>();
		ArrayList<SameExecuteExtractPath> sameExecuteExtractPathLists = extractClass.getSameExcuteExtractPathLists();
		ArrayList<TestMethod> testMethodLists = instanceMethodGroup.getMethodLists();
		
		for(int pathNum = 0; pathNum < sameExecuteExtractPathLists.size(); pathNum++) {
			SameExecuteExtractPath sameExecuteExtractPath = sameExecuteExtractPathLists.get(pathNum);
			ArrayList<ExtractMethod> extractMethodLists = sameExecuteExtractPath.getInstanecLists().get(0).getExtractMethodLists();
			
			ArrayList<MatchingMethod> matchingMethodLists = this.isMatchMethod(testMethodLists, extractMethodLists, instanceMethodGroup.getClassname(), sameExecuteExtractPath.getInstanecLists().get(0).getOwnerClass());
			
			if(matchingMethodLists.size() == testMethodLists.size()) {
				MatchingPath matchingPath = new MatchingPath(sameExecuteExtractPath, matchingMethodLists);
				matchingPathLists.add(matchingPath);
			}
		}
		
		return matchingPathLists;
	}
	
	private ArrayList<MatchingMethod> isMatchMethod(ArrayList<TestMethod> testMethodLists, ArrayList<ExtractMethod> extractMethodLists, String testClass, String extractClass){
		ArrayList<MatchingMethod> matchingMethodLists = new ArrayList<MatchingMethod>();
		for(int testMethodNum = 0; testMethodNum < testMethodLists.size(); testMethodNum++) {
			TestMethod testMethod = testMethodLists.get(testMethodNum);
			
			for(int extractMethodNum = 0; extractMethodNum < extractMethodLists.size(); extractMethodNum++) {
				ExtractMethod extractMethod = extractMethodLists.get(extractMethodNum);
				
				if(this.isSameExecutePath(testMethod, extractMethod, testClass, extractClass)) {
					MatchingMethod matchingMethod = new MatchingMethod(testMethod, extractMethod);
					matchingMethodLists.add(matchingMethod);
					break;
				}
			}
		}
		
		return matchingMethodLists;
	}
	
	private boolean isSameExecutePath(TestMethod testMethod, ExtractMethod extractMethod, String testClass, String extractClass) {
		ArrayList<TraceMethodBlock> evoSuiteBlock = testMethod.getTraceMethodBlockLists();
		ArrayList<TraceMethodBlock> extractBlock = extractMethod.getTraceMethodBlock();
		ArrayList<ExecutePath> evoSuiteExecutePathLists = this.createExecutePath(evoSuiteBlock, testMethod.getMethodName(), testClass);
		ArrayList<ExecutePath> extractExecutePathLists = this.createExecutePath(extractBlock, extractMethod.getMethodName()	, extractClass);	
		
		if(evoSuiteExecutePathLists.size() != extractExecutePathLists.size()) {
			return false;
		}
		
		for(int pathNum = 0; pathNum < evoSuiteExecutePathLists.size(); pathNum++) {
			ExecutePath evoSuitePath = evoSuiteExecutePathLists.get(pathNum);
			ExecutePath extractPath = extractExecutePathLists.get(pathNum);
			
			if(!evoSuitePath.getFileName().equals(extractPath.getFileName())) {
				return false;
			}
			
			if(!evoSuitePath.getMname().equals(extractPath.getMname())) {
				return false;
			}
			
			if(evoSuitePath.getLineLists().size() != extractPath.getLineLists().size()) {
				return false;
			}
			
			ArrayList<Integer> evoSuiteLineLists = evoSuitePath.getLineLists();
			ArrayList<Integer> extractLineLists = extractPath.getLineLists();
			for(int lineNum = 0; lineNum < evoSuiteLineLists.size(); lineNum++) {
				if(evoSuiteLineLists.get(lineNum) != extractLineLists.get(lineNum)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private ArrayList<ExecutePath> createExecutePath(ArrayList<TraceMethodBlock> blockLists, String methodName, String fileName){
		ArrayList<ExecutePath> executePathLists = new ArrayList<ExecutePath>();
		for(int blockNum = 0; blockNum < blockLists.size(); blockNum++) {
			TraceMethodBlock block = blockLists.get(blockNum);
			ArrayList<Trace> traceLists = block.getTraceLists();
			
			ArrayList<Integer> lineLists = new ArrayList<Integer>();
			for(int traceNum = 0; traceNum < traceLists.size(); traceNum++) {
				int lineNum = traceLists.get(traceNum).getLine();
				if(!lineLists.contains(lineNum)) {
					lineLists.add(lineNum);
				}
			}
			
			ExecutePath executePath = new ExecutePath(lineLists, methodName, fileName);
			executePathLists.add(executePath);
		}
		
		return executePathLists;
	}
	
	// 同じ実行経路のテストをまとめる
	private void summarizeSameExecutePath(ArrayList<TestClass> testClassLists) {
		
		for(int testClassNum = 0; testClassNum < testClassLists.size(); testClassNum++) {
			ArrayList<SameExecutePath> sameExecutePathLists = new ArrayList<SameExecutePath>();
			TestClass testClass = testClassLists.get(testClassNum);
			
			ArrayList<Test> testLists = testClass.getTestLists();
			for(int testNum = 0; testNum < testLists.size(); testNum++) {
				Test test = testLists.get(testNum);
				
				if(sameExecutePathLists.size() == 0) {
					SameExecutePath sameExecutePath = new SameExecutePath();
					sameExecutePath.addTestLists(test);
					sameExecutePathLists.add(sameExecutePath);
					
				}else {
					boolean registeredFlag = false;
					for(int sameExecutePathNum = 0; sameExecutePathNum < sameExecutePathLists.size(); sameExecutePathNum++) {
						if(this.isSameExecutePath(test, sameExecutePathLists.get(sameExecutePathNum).getTestLists().get(0))) {
							sameExecutePathLists.get(sameExecutePathNum).addTestLists(test);
							registeredFlag = true;
							break;
						}
					}
					
					if(!registeredFlag) {
						SameExecutePath sameExtractPath = new SameExecutePath();
						sameExtractPath.addTestLists(test);
						sameExecutePathLists.add(sameExtractPath);
					}
				}
			}
			
			testClass.setSameExecutePathLists(sameExecutePathLists);
		
//			System.out.println("##########");
//			System.out.println(testClass.getClassName());
//			for(int i = 0; i < sameExecutePathLists.size(); i++) {
//				SameExecutePath x = sameExecutePathLists.get(i);
//				for(int j = 0; j < x.getTestLists().size(); j++) {
//					System.out.println(x.getTestLists().get(j).getMethodName());
//					ArrayList<ExecutePath> a = x.getTestLists().get(j).getExtractPathLists();
//					for(int n = 0; n < a.size(); n++) {
//						a.get(n).display();
//					}
//				}
//				
//				System.out.println();
//			}
//			System.out.println();
		}
	}
	
	private boolean isSameExecutePath(Test test1, Test test2) {
		ArrayList<ExecutePath> pathLists1 = test1.getExtractPathLists();
		ArrayList<ExecutePath> pathLists2 = test2.getExtractPathLists();
		
		if(pathLists1.size() != pathLists2.size()) {
			return false;
		}
		
		for(int pathNum = 0; pathNum < pathLists1.size(); pathNum++) {
			ExecutePath executePath1 = pathLists1.get(pathNum);
			ExecutePath executePath2 = pathLists2.get(pathNum);
			
			if(!(executePath1.getMname().equals(executePath2.getMname()) && executePath1.getFileName().equals(executePath2.getFileName()))) {
				return false;
			}
			
			ArrayList<Integer> lineLists1 = executePath1.getLineLists();
			ArrayList<Integer> lineLists2 = executePath2.getLineLists();
			
			if(lineLists1.size() != lineLists2.size()) {
				return false;
			}
			
			for(int lineNum = 0; lineNum < lineLists1.size(); lineNum++) {
				if(lineLists1.get(lineNum) != lineLists2.get(lineNum)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void summarizeExtractClassSameExecutePath(ArrayList<ExtractClass> extractClassLists) {
		for(int classNum = 0; classNum < extractClassLists.size(); classNum++) {
			ExtractClass extractClass = extractClassLists.get(classNum);
			ArrayList<SameExecuteExtractPath> sameExecuteExtractPathLists = new ArrayList<SameExecuteExtractPath>();
			
			ArrayList<Instance> instanceLists = extractClass.getInstanceLists();
			for(int instanceNum = 0; instanceNum < instanceLists.size(); instanceNum++) {
				Instance instance = instanceLists.get(instanceNum);
				
				if(sameExecuteExtractPathLists.size() == 0) {
					SameExecuteExtractPath sameExecuteExtractPath = new SameExecuteExtractPath();
					sameExecuteExtractPath.addInstanceLists(instance);
					sameExecuteExtractPathLists.add(sameExecuteExtractPath);
					
				}else {
					boolean isRegistered = false;
					for(int sameExecuteNum = 0; sameExecuteNum < sameExecuteExtractPathLists.size(); sameExecuteNum++) {
						ArrayList<ExecutePath> instanceExecutePathLists = instance.getExecutePathLists();
						ArrayList<ExecutePath> listExecutePathLists = sameExecuteExtractPathLists.get(sameExecuteNum).getInstanecLists().get(0).getExecutePathLists();
						if(this.isSameExecutePath(instanceExecutePathLists, listExecutePathLists)) {
							sameExecuteExtractPathLists.get(sameExecuteNum).addInstanceLists(instance);
							isRegistered = true;
							break;
						}
					}
					
					if(!isRegistered) {
						SameExecuteExtractPath sameExecuteExtractPath = new SameExecuteExtractPath();
						sameExecuteExtractPath.addInstanceLists(instance);
						sameExecuteExtractPathLists.add(sameExecuteExtractPath);
					}
				}
			}
			
			extractClass.setSameExecuteExtractPathLists(sameExecuteExtractPathLists);
			
		}
	}
	
	private boolean isSameExecutePath(ArrayList<ExecutePath> lists1, ArrayList<ExecutePath> lists2) {
		if(lists1.size() == lists2.size()) {
			for(int listNum = 0; listNum < lists1.size(); listNum++) {
				ExecutePath executePath1 = lists1.get(listNum);
				ExecutePath executePath2 = lists2.get(listNum);
				
				if(!(executePath1.getMname().equals(executePath2.getMname()) && executePath1.getFileName().equals(executePath2.getFileName()))) {
					return false;
				}
				
				ArrayList<Integer> lineLists1 = executePath1.getLineLists();
				ArrayList<Integer> lineLists2 = executePath2.getLineLists();
				
				if(lineLists1.size() != lineLists2.size()) {
					return false;
				}
				
				for(int lineNum = 0; lineNum < lineLists1.size(); lineNum++) {
					if(lineLists1.get(lineNum) != lineLists2.get(lineNum)) {
						return false;
					}
				}
			}
			
			return true;
		}else {
			return false;
		}
	}
	
	private void analyzeExtractClassExtractPath(ArrayList<ExtractClass> extractClassLists) {
		for(int classNum = 0; classNum < extractClassLists.size(); classNum++) {
			ExtractClass extractClass = extractClassLists.get(classNum);
			ArrayList<Instance> instanceLists = extractClass.getInstanceLists();
			for(int instanceNum = 0; instanceNum < instanceLists.size(); instanceNum++) {
				Instance instance = instanceLists.get(instanceNum);
				ArrayList<TraceMethodBlock> traceMethodBlockLists = instance.getTraceMethodBlockLists();
				
				for(int i = 0; i < traceMethodBlockLists.size(); i++) {
					for(int j = 0; j < traceMethodBlockLists.size() - 1; j++) {
						TraceMethodBlock frontTraceMethodBlock = traceMethodBlockLists.get(j);
						TraceMethodBlock backTraceMethodBlock = traceMethodBlockLists.get(j + 1);
						
						int frontSeqNum = frontTraceMethodBlock.getTraceLists().get(0).getSeqNum();
						int backSeqNum = backTraceMethodBlock.getTraceLists().get(0).getSeqNum();
						
						if(frontSeqNum > backSeqNum) {
							traceMethodBlockLists.set(j, backTraceMethodBlock);
							traceMethodBlockLists.set(j+1, frontTraceMethodBlock);
						}
						
					}
				}
				
				for(int blockNum = 0; blockNum < traceMethodBlockLists.size(); blockNum++) {
					TraceMethodBlock traceMethodBlock = traceMethodBlockLists.get(blockNum);
					ArrayList<Trace> traceLists = traceMethodBlock.getTraceLists();
					
					ArrayList<Integer> lineLists = new ArrayList<Integer>();
					
					for(int traceNum = 0; traceNum < traceLists.size(); traceNum++) {
						Trace trace = traceLists.get(traceNum);
						
						if(lineLists.size() == 0) {
							lineLists.add(trace.getLine());
						}else {
							if(!lineLists.contains(trace.getLine())) {
								lineLists.add(trace.getLine());
							}
						}
					}
					
					ExecutePath executePath = new ExecutePath(lineLists, traceLists.get(0).getMname(), traceLists.get(0).getFilename());
					instance.addExecutePathLists(executePath);
					
				}
				
//				System.out.println("~~~~~~~");
//				instance.display();
//				for(int i = 0; i < instance.getExecutePathLists().size(); i++) {
//					instance.getExecutePathLists().get(i).display();
//				}
//				System.out.println();
			}
			
		}
	}
	
	// TraceMethodBlockから実行経路を解析する
	private void analyzeExtractPath(ArrayList<TestClass> testClassLists) {
		for(int testClassNum = 0; testClassNum < testClassLists.size(); testClassNum++) {
			TestClass testClass = testClassLists.get(testClassNum);
//			System.out.println(testClass.getClassName());
			
			ArrayList<Test> testLists = testClass.getTestLists();
			for(int testNum = 0; testNum < testLists.size(); testNum++) {
				Test test = testLists.get(testNum);
//				System.out.println(test.getMethodName());
				
				
				ArrayList<TraceMethodBlock> traceMethodBlockLists = test.getTraceMethodBlockLists();
//				System.out.println(traceMethodBlockLists.size());
				for(int traceMethodBlockNum = 0; traceMethodBlockNum < traceMethodBlockLists.size(); traceMethodBlockNum++) {
					ArrayList<Integer> lineLists = new ArrayList<Integer>();
					TraceMethodBlock traceMethodBlock = traceMethodBlockLists.get(traceMethodBlockNum);
					ArrayList<Trace> traceLists = traceMethodBlock.getTraceLists();
					
					for(int traceNum = 0; traceNum < traceLists.size(); traceNum++) {
						Trace trace = traceLists.get(traceNum);
						
						if(lineLists.size() == 0) {
							lineLists.add(trace.getLine());
						}else {
							if(!lineLists.contains(trace.getLine())) {
								lineLists.add(trace.getLine());
							}
						}
					}
					
					ExecutePath extractPath = new ExecutePath(lineLists, traceLists.get(0).getMname(), traceLists.get(0).getFilename());
					test.addExtractPathLists(extractPath);
					
					
//					System.out.println(traceLists.get(0).getMname());
//					System.out.println(lineLists);
				}
			}
		}
	}

}
