package pathExtracter;

import java.io.File;
import java.util.ArrayList;

import analyzer.Analyzer;
import testAnalyzer.TestAnalyzer;
import testAnalyzer.TestClass;
import testAnalyzer.TestMethod;
import testAnalyzer.InstanceMethodGroup;
import testAnalyzer.Test;
import tracer.Lexer;
import tracer.Trace;

public class PathExtracter {
	
	public ArrayList<TestClass> run(Analyzer analyzer, ArrayList<String> inputTestFileNameLists, ArrayList<String> inputTraceFileNameLists, ArrayList<String> inputFileNameLists){
		
		// test code analyze
		TestAnalyzer testAnalyzer = new TestAnalyzer();
		ArrayList<TestClass> testClassLists = testAnalyzer.run(inputTestFileNameLists, analyzer.getMethodLists());
		
		// Extract test Path
		this.testTestPathExtract(inputTraceFileNameLists, testClassLists);
		this.separateInstance(inputFileNameLists, testClassLists);
		
		return testClassLists;
	}
	
	private void separateInstance(ArrayList<String> inputFileNameLists, ArrayList<TestClass> testClassLists) {
		ArrayList<String> classNameLists = new ArrayList<String>();
		for(int fileNum = 0; fileNum < inputFileNameLists.size(); fileNum++) {
			String fileName = inputFileNameLists.get(fileNum);
			String[] split = fileName.split("/");
			String className = split[split.length - 1].replace(".java", "");
			classNameLists.add(className);
		}
	
		for(int classNum = 0; classNum < testClassLists.size(); classNum++) {
			TestClass testClass = testClassLists.get(classNum);
			ArrayList<Test> testLists = testClass.getTestLists();
			for(int testNum = 0; testNum < testLists.size(); testNum++) {
				Test test = testLists.get(testNum);
				ArrayList<TestMethod> methodLists = test.getMethodLists();
				ArrayList<InstanceMethodGroup> instanceMethodGroupLists = test.getInstanceMethodGroupLists();
				
				for(int methodNum = 0; methodNum < methodLists.size(); methodNum++) {
					TestMethod testMethod = methodLists.get(methodNum);
					String methodReturnType = testMethod.getReturnType();
					
					if(classNameLists.contains(methodReturnType)) {
						InstanceMethodGroup instanceMethodGroup = new InstanceMethodGroup(methodReturnType, testMethod.getReturnVariable());
						instanceMethodGroup.addMethodLists(testMethod);
						instanceMethodGroupLists.add(instanceMethodGroup);
					}else {
						for(int groupNum = 0; groupNum < instanceMethodGroupLists.size(); groupNum++) {
							InstanceMethodGroup instanceMethodGroup = instanceMethodGroupLists.get(groupNum);
							if(instanceMethodGroup.getVariableName().equals(testMethod.getInstance())) {
								instanceMethodGroup.addMethodLists(testMethod);
								break;
							}
						}
					}
				}
			}
		}
		
	}
	
	public ArrayList<TraceMethodBlock> getTraceMethodBlockLists(ArrayList<Trace> traceLists){
		ArrayList<TraceMethodBlock> traceMethodBlockLists = new ArrayList<TraceMethodBlock>();
		TraceMethodBlock traceMethodBlock = null;
		
		for(int traceNum = 0; traceNum < traceLists.size(); traceNum++) {
			Trace trace = traceLists.get(traceNum);
			if(trace.getEvent().equals("METHOD_ENTRY")) {
				traceMethodBlock = new TraceMethodBlock();
				traceMethodBlockLists.add(traceMethodBlock);
			}
			
			traceMethodBlock.addTraceLists(trace);
		}
		
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
		
//		for(int i = 0; i < traceMethodBlockLists.size(); i++) {
//			traceMethodBlock = traceMethodBlockLists.get(i);
//			Trace trace = traceMethodBlock.getTraceLists().get(0);
//			System.out.println(trace.getCname() + ":" + trace.getMname() + ":" + trace.getSeqNum() + ":" + trace.getLine());
//		}
		
//		for(int i = 0; i < traceMethodBlockLists.size(); i++) {
//			System.out.print(traceMethodBlockLists.get(i).getTraceLists().get(0).getSeqNum() +  ", ");
//		}
//		System.out.println();
		
		return traceMethodBlockLists;
	}
	
	public void testTestPathExtract(ArrayList<String> inputTraceFileNameLists, ArrayList<TestClass> testClassLists) {
		for(int inputFileNum = 0; inputFileNum < inputTraceFileNameLists.size(); inputFileNum++) {
			File file = new File(inputTraceFileNameLists.get(inputFileNum));
			Lexer lexer = new Lexer(file);
			ArrayList<Trace> traceLists = lexer.getTraceLists();
			ArrayList<TraceMethodBlock> traceMethodBlockLists = this.getTraceMethodBlockLists(traceLists);
			
			// detect testClass
			TestClass testClass = null;
			TraceMethodBlock firstTraceMethodBlock = null;
			for(int blockNum = 0; blockNum < traceMethodBlockLists.size(); blockNum++) {
				if(traceMethodBlockLists.get(blockNum).getTraceLists().get(0).getCname().contains("_ESTest")) {
					firstTraceMethodBlock = traceMethodBlockLists.get(blockNum);
				}
			}
			
			Trace firstTrace = firstTraceMethodBlock.getTraceLists().get(0);
			for(int testClassNum = 0; testClassNum < testClassLists.size(); testClassNum++) {
				// System.out.println(firstTrace.getCname());
				if(firstTrace.getCname().contains(testClassLists.get(testClassNum).getClassName())) {
					testClass = testClassLists.get(testClassNum);
				}
			}
			
			// 1. test のテスト自体の登録
			for(int blockNum = 0; blockNum < traceMethodBlockLists.size(); blockNum++) {
				TraceMethodBlock traceMethodBlock = traceMethodBlockLists.get(blockNum);
				Trace trace = traceMethodBlock.getTraceLists().get(0);
				String methodName = trace.getMname();
				ArrayList<Test> testTestLists = testClass.getTestLists();
				
				for(int testNum = 0; testNum < testTestLists.size(); testNum++) {
					Test testTest = testTestLists.get(testNum);
					if(testTest.getMethodName().equals(methodName)) {
						testTest.setTraceMethodBlock(traceMethodBlock);
						break;
					}
				}
			}
			
//			System.out.println(testClass.getClassName());
//			System.out.println("border:" + borderSeqNumLists);
			
			// testの並び替え
			ArrayList<Test> testLists = testClass.getTestLists();
			for(int i = 0; i < testLists.size(); i++) {
				for(int j = 0; j < testLists.size() - 1; j++) {
					int frontTestSeqNum = testLists.get(j).getTraceMethodBlock().getTraceLists().get(0).getSeqNum();
					int backTestSeqNum = testLists.get(j+1).getTraceMethodBlock().getTraceLists().get(0).getSeqNum();
					
					if(frontTestSeqNum > backTestSeqNum) {
						Test frontTest = testLists.get(j);
						Test backTest = testLists.get(j+1);
						testLists.set(j, backTest);
						testLists.set(j+1, frontTest);
					}
				}
			}
			
//			System.out.println("border:" + borderSeqNumLists);
			
			// 2. test のメソッドの振り分け
			for(int testNum = 0; testNum < testLists.size(); testNum++) {
				Test test = testLists.get(testNum);
				int firstSeqNum = test.getTraceMethodBlock().getTraceLists().get(0).getSeqNum();
				int lastSeqNum = test.getTraceMethodBlock().getTraceLists().get(test.getTraceMethodBlock().getTraceLists().size() - 1).getSeqNum();
				
				for(int blockNum = 0; blockNum < traceMethodBlockLists.size(); blockNum++) {
					TraceMethodBlock traceMethodBlock = traceMethodBlockLists.get(blockNum);
					int seqNum = traceMethodBlock.getTraceLists().get(0).getSeqNum();
					if(firstSeqNum < seqNum && seqNum < lastSeqNum) {
						test.addTraceMethodBlockLists(traceMethodBlock);
					}
				}
			}
			
//			System.out.println("method block:");
//			for(int i = 0; i < testClass.getTestLists().size(); i++) {
//				testTest testTest = testClass.getTestLists().get(i);
//				System.out.println(testTest.getMethodName());
//				
//				for(int j = 0; j < testTest.getTraceMethodBlockLists().size(); j++) {
//					TraceMethodBlock traceMethodBlock = testTest.getTraceMethodBlockLists().get(j);
//					System.out.println("\t" + traceMethodBlock.getTraceLists().get(0).getMname() + ":" + traceMethodBlock.getTraceLists().get(0).getSeqNum());
//				}
//			}
		
			
			// sort Method Block
			for(int testNum = 0; testNum < testClass.getTestLists().size(); testNum++) {
				Test testTest = testClass.getTestLists().get(testNum);
				
				ArrayList<TraceMethodBlock> sortTraceMethodBlockLists = testTest.getTraceMethodBlockLists();
				for(int i = 0; i < sortTraceMethodBlockLists.size(); i++) {
					for(int j = 0; j < sortTraceMethodBlockLists.size() - 1; j++) {
						TraceMethodBlock frontTraceMethodBlock = sortTraceMethodBlockLists.get(j);
						TraceMethodBlock backTraceMethodBlock = sortTraceMethodBlockLists.get(j+1);
						
						if(frontTraceMethodBlock.getTraceLists().get(0).getSeqNum() > backTraceMethodBlock.getTraceLists().get(0).getSeqNum()) {
							sortTraceMethodBlockLists.set(j, backTraceMethodBlock);
							sortTraceMethodBlockLists.set(j+1, frontTraceMethodBlock);
						}
					}
				}
			}
			
			// 3. set method blockLists
			for(int testNum = 0; testNum < testClass.getTestLists().size(); testNum++) {
				Test test = testClass.getTestLists().get(testNum);
				
				ArrayList<TraceMethodBlock> blockLists = test.getTraceMethodBlockLists();
				ArrayList<TestMethod> methodLists = test.getMethodLists();
				ArrayList<TraceMethodBlock> notRegisterBlockLists = new ArrayList<TraceMethodBlock>();
				
				// method自体
				int blockBorderNum = 0;
				for(int methodNum = 0; methodNum < methodLists.size(); methodNum++) {
					TestMethod testMethod = methodLists.get(methodNum);
					String methodName = testMethod.getMethodName();
					String ownerClass = "";
					if(testMethod.getAnalyzerMethod() != null) {
						ownerClass = testMethod.getAnalyzerMethod().getOwnerClass().getName();					
					}else {
						ownerClass = testMethod.getConstructorClass();
					}
					

					
					for(int blockNum = blockBorderNum; blockNum < blockLists.size(); blockNum++) {
						Trace blockFirstTrace = blockLists.get(blockNum).getTraceLists().get(0);
						String[] split = blockFirstTrace.getFilename().split("/");
						String className = split[split.length - 1];
						
						if(className.equals(ownerClass) && blockFirstTrace.getMname().equals(methodName)) {
							blockBorderNum = blockNum + 1;
							testMethod.addTraceMethodBlockLists(blockLists.get(blockNum));
							break;
						}
					}
				}
				
				// methodの実行経路
				for(int methodNum = 0; methodNum < methodLists.size(); methodNum++) {
					TestMethod testMethod = methodLists.get(methodNum);
					if(testMethod.getTraceMethodBlockLists().size() > 0) {
						int firstSeqNum = testMethod.getTraceMethodBlockLists().get(0).getTraceLists().get(0).getSeqNum();
						int lastSeqNum = testMethod.getTraceMethodBlockLists().get(0).getTraceLists().get(testMethod.getTraceMethodBlockLists().get(0).getTraceLists().size() - 1).getSeqNum();
					
						for(int blockNum = 0; blockNum < blockLists.size(); blockNum++) {
							int seqNum = blockLists.get(blockNum).getTraceLists().get(0).getSeqNum();
							if(firstSeqNum < seqNum && seqNum < lastSeqNum) {
								testMethod.addTraceMethodBlockLists(blockLists.get(blockNum));
							}else {
								if(seqNum != firstSeqNum) {
									notRegisterBlockLists.add(blockLists.get(blockNum));
								}
							}
						}
					}
				}
				
//				System.out.println(test.getMethodName());
//				for(int methodNum = 0; methodNum < methodLists.size(); methodNum++) {
//					TestMethod testMethod = methodLists.get(methodNum);
//					ArrayList<TraceMethodBlock> x = testMethod.getTraceMethodBlockLists();
//					System.out.println(testMethod.getMethodName());
//					for(int j = 0; j < x.size(); j++) {
//						TraceMethodBlock traceMethodBlock = x.get(j);
//						System.out.println(
//							traceMethodBlock.getTraceLists().get(0).getMname() + ":" + 
//							traceMethodBlock.getTraceLists().get(0).getSeqNum() + ":" + 
//							traceMethodBlock.getTraceLists().get(0).getCname());
//					}
//					System.out.println();
//				}
				
//				ArrayList<TestAssertion> testAssertionLists = test.getAssertionLists();
//				for(int assertNum = 0; assertNum < testAssertionLists.size(); assertNum++) {
//					TestAssertion testAssertion = testAssertionLists.get(assertNum);
//					
//					if(!testAssertion.getVariable().equals("")) {
//						int assertBlockBorderNum = 0;
//						
//						String className = testClass.getClassName().replace("_ESTest", "");
//						
//					}
//				}
			}
			
//			System.out.println("method block:");
//			for(int i = 0; i < testClass.getTestLists().size(); i++) {
//				Test testTest = testClass.getTestLists().get(i);
//				System.out.println(testTest.getMethodName());
//				
//				for(int j = 0; j < testTest.getTraceMethodBlockLists().size(); j++) {
//					TraceMethodBlock traceMethodBlock = testTest.getTraceMethodBlockLists().get(j);
//					System.out.println("\t" + 
//						traceMethodBlock.getTraceLists().get(0).getMname() + ":" + 
//						traceMethodBlock.getTraceLists().get(0).getSeqNum() + ":" + 
//						traceMethodBlock.getTraceLists().get(0).getCname());
//				}
//			}
		}
	}
}
