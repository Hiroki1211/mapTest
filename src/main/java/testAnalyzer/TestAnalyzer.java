package testAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import analyzer.AnalyzerMethod;

public class TestAnalyzer {
	
	public ArrayList<TestClass> run(ArrayList<String> inputTestFileNameLists, ArrayList<AnalyzerMethod> analyzerMethodLists) {
		ArrayList<TestClass> testClassLists = this.createtestClassLists(inputTestFileNameLists);
		this.analyzetestClass(testClassLists);
		// assert attach method
		this.attachAssertionAndMethod(testClassLists);
		// detect Analyzer Method
		this.detectAnalzerMethod(testClassLists, analyzerMethodLists);
		
		return testClassLists;
	}
	
	private void detectAnalzerMethod(ArrayList<TestClass> testClassLists, ArrayList<AnalyzerMethod> analyzerMethodLists) {
		for(int classNum = 0; classNum < testClassLists.size(); classNum++) {
			TestClass testClass = testClassLists.get(classNum);
			
			for(int testNum = 0; testNum < testClass.getTestLists().size(); testNum++){
				Test testTest = testClass.getTestLists().get(testNum);
				
				for(int methodNum = 0; methodNum < testTest.getMethodLists().size(); methodNum++) {
					TestMethod testMethod = testTest.getMethodLists().get(methodNum);
					
					ArrayList<String> argumentTypeLists = new ArrayList<String>();
					for(int argNum = 0; argNum < testMethod.getArgumentLists().size(); argNum++) {
						argumentTypeLists.add(this.gudgeVariableType(testMethod.getArgumentLists().get(argNum), testTest.getMethodLists()));
//						System.out.println("+++++++++++");
//						System.out.println(testMethod.getArgumentLists().get(argNum));
//						System.out.println(testMethod.getArgumentLists());
//						System.out.println(this.gudgeVariableType(testMethod.getArgumentLists().get(argNum), testTest.getMethodLists()));
//						System.out.println("-----------");
					}
					
					ArrayList<AnalyzerMethod> sameMethodNameLists = new ArrayList<AnalyzerMethod>();
					for(int anaNum = 0; anaNum < analyzerMethodLists.size(); anaNum++) {
						if(analyzerMethodLists.get(anaNum).getName().equals(testMethod.getMethodName())) {
							sameMethodNameLists.add(analyzerMethodLists.get(anaNum));
						}
					}
					
//					if(testTest.getMethodName().equals("test7")) {
//						System.out.println("AAAAAAAAAAAAA");
//						System.out.println(testMethod.getMethodName());
//						System.out.println(sameMethodNameLists);
//					}
					
					if(sameMethodNameLists.size() == 1) {
						if(sameMethodNameLists.get(0).getName().equals("<init>")) {
							if(sameMethodNameLists.get(0).getOwnerClass().getName().equals(testMethod.getConstructorClass())) {
								testMethod.setAnalyzerMethod(sameMethodNameLists.get(0));
							}
						}else {
							testMethod.setAnalyzerMethod(sameMethodNameLists.get(0));
						}
						
					}else {
						for(int sameNum = 0; sameNum < sameMethodNameLists.size(); sameNum++) {
							AnalyzerMethod tmpAnalyzerMethod = sameMethodNameLists.get(sameNum);
							
//							if(testTest.getMethodName().equals("test7")) {
//								System.out.println("XXXXXXX");
//								System.out.println(tmpAnalyzerMethod.getTypeArgumentLists().size());
//								System.out.println(testMethod.getArgumentLists());
//							}
							
							if(tmpAnalyzerMethod.getTypeArgumentLists().size() == argumentTypeLists.size()) {
								boolean sameMethodFlag = true;
								for(int argNum = 0; argNum < argumentTypeLists.size(); argNum++) {
									if(!tmpAnalyzerMethod.getTypeArgumentLists().get(argNum).equals(argumentTypeLists.get(argNum))) {
//										System.out.println(tmpAnalyzerMethod.getTypeArgumentLists().get(argNum));
//										System.out.println(argumentTypeLists.get(argNum));
										sameMethodFlag = false;
										break;
									}
								}
								
//								System.out.println(sameMethodFlag);
								if(sameMethodFlag ) {
									testMethod.setAnalyzerMethod(tmpAnalyzerMethod);
									break;
								}
							}
						}
					}
					
//					System.out.println(testMethod.getStatement());
//					if(testMethod.getAnalyzerMethod() != null) {
//						testMethod.getAnalyzerMethod().display();
//					}
//					System.out.println("********");
				}
			}
		}
	}
	
	private String gudgeVariableType(String input, ArrayList<TestMethod> testMethodLists) {
		String result = "";
		
		if(input.equals("true") || input.equals("false")) {
			result = "boolean";
		}else if(isNumber(input)) {
			result = "int";
		}else if(input.contains("\"")) {
			result = "String";
		}else if(input.contains("'")) {
			result = "char";
		}else {
			for(int methodNum = 0; methodNum < testMethodLists.size(); methodNum++) {
				String variable = testMethodLists.get(methodNum).getReturnVariable();
				if(input.equals(variable)) {
					result = testMethodLists.get(methodNum).getReturnType();
					break;
				}
			}
			
			if(result.equals("")) {
				// long, float, double
				if(input.contains("l") || input.contains("L")) {
					result = "long";
				}else if(input.contains("f") || input.contains("F")) {
					result = "float";
				}else {
					result = "double";
				}
			}
		}
		
		return result;
	}
	
	private boolean isNumber(String val) {
		String regex = "\\A[-]?[0-9]+\\z";
		Pattern p = Pattern.compile(regex);
		Matcher m1 = p.matcher(val);
		return m1.find();
	}
	
	private void attachAssertionAndMethod(ArrayList<TestClass> testClassLists) {
		for(int classNum = 0; classNum < testClassLists.size(); classNum++) {
			TestClass testClass = testClassLists.get(classNum);
			
			for(int testNum = 0; testNum < testClass.getTestLists().size(); testNum++) {
				Test testTest = testClass.getTestLists().get(testNum);
				
				for(int assertionNum = 0; assertionNum < testTest.getAssertionLists().size(); assertionNum++) {
					TestAssertion testAssertion = testTest.getAssertionLists().get(assertionNum);
					
					if(!testAssertion.getVariable().equals("")) {
						String variable = testAssertion.getVariable();
						
						for(int methodNum = 0; methodNum < testTest.getMethodLists().size(); methodNum++) {
							if(testTest.getMethodLists().get(methodNum).getReturnVariable().equals(variable)) {
								testAssertion.setAssertionTargetMethod(testTest.getMethodLists().get(methodNum));
								break;
							}
						}
					}
				}
			}
		}
	}
	
	private int countDoubleQuote(String input) {
		int count = 0;
		String[] split = input.split("");
		for(int i = 0; i < split.length; i++) {
			if(split[i].equals("\"")) {
				count += 1;
			}
		}
		
		return count;
	}
	
	private void analyzetestClass(ArrayList<TestClass> testClassLists) {
		for(int classNum = 0; classNum < testClassLists.size(); classNum++) {
			TestClass testClass = testClassLists.get(classNum);
			
			for(int testNum = 0; testNum < testClass.getTestLists().size(); testNum++) {
				Test testTest = testClass.getTestLists().get(testNum);
				ArrayList<String> body = testTest.getBody();
				
				for(int bodyNum = 0; bodyNum < body.size(); bodyNum++) {
					String[] splitBracket = body.get(bodyNum).split("[()]");
					if(splitBracket.length > 0) {
						String[] splitSpace = splitBracket[0].split(" +");
						
						if(splitSpace[1].equals("assertEquals")) {
							String assertion = body.get(bodyNum);
							assertion = assertion.replace(" ", "");
							assertion = assertion.replace("assertEquals(", "");
							assertion = assertion.replace(");", "");
							
//							String getter = "";
//							String[] x = assertion.split(",", 2);
//							if(x.length > 1) {
//								getter = x[1];
//							}
							String[] splitAssertionArgument = assertion.split(",");
							splitAssertionArgument[0] = splitAssertionArgument[0].replace("(", "");
							splitAssertionArgument[0] = splitAssertionArgument[0].replace(")", "");
							TestAssertion testAssertion;
							String target = splitAssertionArgument[1];
							if(this.countDoubleQuote(splitAssertionArgument[0]) == 1 || (splitAssertionArgument[0].length() > 1 && splitAssertionArgument[0].substring(0, 1).equals("\"") && !splitAssertionArgument[0].substring(splitAssertionArgument[0].length() - 1, splitAssertionArgument[0].length()).equals("\""))) {
								int i = 1;
								while(i + 1 < splitAssertionArgument.length && !splitAssertionArgument[i].contains("\"")) {
									splitAssertionArgument[0] += "," + splitAssertionArgument[i];
									i += 1;
								}
								splitAssertionArgument[0] += splitAssertionArgument[i];
								target = splitAssertionArgument[i + 1];
							}
							
							if(target.contains(".")) {

								testAssertion = new TestAssertion(splitAssertionArgument[0], "", target, body.get(bodyNum), "assertEquals", bodyNum, target);
							}else {
								testAssertion = new TestAssertion(splitAssertionArgument[0], target, "", body.get(bodyNum), "assertEquals", bodyNum, "");
							}
							
							testTest.addAssertionLists(testAssertion);
						}else if(splitSpace[1].equals("assertNull")) {
							String assertion = body.get(bodyNum);
							assertion = assertion.replace(" ", "");
							assertion = assertion.replace("assertNull(", "");
							assertion = assertion.replace(");", "");
							
							String value = "";
							String variable = "";
							String getterMethodInstance = "";
							String getterMethodName = "";
							String getter = "";
							ArrayList<String> getterMethodArgument = new ArrayList<String>();
							TestMethod assertionTargetMethod = null;
							String statement = body.get(bodyNum);
							String assertMethodName = "assertNull";
							int lineNum = bodyNum;
							
							if(assertion.contains(".")) {
								String[] split = assertion.split(Pattern.quote("."));
								getterMethodInstance = split[0];
								String[] splitBrackets = split[1].split("[()]", 2);
								getterMethodName = splitBrackets[0];
								getter = assertion;
								if(splitBrackets.length > 1 && !splitBrackets[1].substring(0, splitBrackets[1].length() - 1).equals("")) {
									String arguments = splitBrackets[1].substring(0, splitBrackets[1].length() - 1);
									String[] splitComna = arguments.split("[ ,]");
									for(int i = 0; i < splitComna.length; i++) {
										getterMethodArgument.add(splitComna[i]);
									}
								}
								
							}else {
								variable = assertion;
								ArrayList<TestMethod> methodLists = testTest.getMethodLists();
								for(int methodNum = 0; methodNum < methodLists.size(); methodNum++) {
									if(methodLists.get(methodNum).getReturnVariable().equals(variable)) {
										assertionTargetMethod = methodLists.get(methodNum);
										break;
									}
								}
							}
							TestAssertion testAssertion = new TestAssertion(value, variable, getterMethodInstance, getterMethodName, getter, assertionTargetMethod, statement, assertMethodName, lineNum);
							
							testTest.addAssertionLists(testAssertion);
							
						}else if(splitSpace[1].equals("assertTrue")) {
							String assertion = body.get(bodyNum);
							assertion = assertion.replace(" ", "");
							assertion = assertion.replace("assertTrue(", "");
							assertion = assertion.replace(");", "");
							
							String value = "";
							String variable = "";
							String getterMethodInstance = "";
							String getterMethodName = "";
							ArrayList<String> getterMethodArgument = new ArrayList<String>();
							TestMethod assertionTargetMethod = null;
							String statement = body.get(bodyNum);
							String assertMethodName = "assertTrue";
							String getter = "";
							int lineNum = bodyNum;
							
							if(assertion.contains(".")) {
								String[] split = assertion.split(Pattern.quote("."));
								getterMethodInstance = split[0];
								String[] splitBrackets = split[1].split("[()]", 2);
								getterMethodName = splitBrackets[0];
								getter = assertion;
								if(splitBrackets.length > 1 && !splitBrackets[1].substring(0, splitBrackets[1].length() - 1).equals("")) {
									String arguments = splitBrackets[1].substring(0, splitBrackets[1].length() - 1);
									String[] splitComna = arguments.split("[ ,]");
									for(int i = 0; i < splitComna.length; i++) {
										getterMethodArgument.add(splitComna[i]);
									}
								}
								
							}else {
								variable = assertion;
								ArrayList<TestMethod> methodLists = testTest.getMethodLists();
								for(int methodNum = 0; methodNum < methodLists.size(); methodNum++) {
									if(methodLists.get(methodNum).getReturnVariable().equals(variable)) {
										assertionTargetMethod = methodLists.get(methodNum);
										break;
									}
								}
							}
							TestAssertion testAssertion = new TestAssertion(value, variable, getterMethodInstance, getterMethodName, getter, assertionTargetMethod, statement, assertMethodName, lineNum);
							
							testTest.addAssertionLists(testAssertion);
						}else if(splitSpace[1].equals("assertFalse")) {
							String assertion = body.get(bodyNum);
							assertion = assertion.replace(" ", "");
							assertion = assertion.replace("assertFalse(", "");
							assertion = assertion.replace(");", "");
							
							String value = "";
							String variable = "";
							String getterMethodInstance = "";
							String getterMethodName = "";
							ArrayList<String> getterMethodArgument = new ArrayList<String>();
							TestMethod assertionTargetMethod = null;
							String statement = body.get(bodyNum);
							String assertMethodName = "assertFalse";
							String getter = "";
							int lineNum = bodyNum;
							
							if(assertion.contains(".")) {
								String[] split = assertion.split(Pattern.quote("."));
								getter = assertion;
								getterMethodInstance = split[0];
								String[] splitBrackets = split[1].split("[()]", 2);
								getterMethodName = splitBrackets[0];
								if(splitBrackets.length > 1 && !splitBrackets[1].substring(0, splitBrackets[1].length() - 1).equals("")) {
									String arguments = splitBrackets[1].substring(0, splitBrackets[1].length() - 1);
									String[] splitComna = arguments.split("[ ,]");
									for(int i = 0; i < splitComna.length; i++) {
										getterMethodArgument.add(splitComna[i]);
									}
								}
								
							}else {
								variable = assertion;
								ArrayList<TestMethod> methodLists = testTest.getMethodLists();
								for(int methodNum = 0; methodNum < methodLists.size(); methodNum++) {
									if(methodLists.get(methodNum).getReturnVariable().equals(variable)) {
										assertionTargetMethod = methodLists.get(methodNum);
										break;
									}
								}
							}
							TestAssertion testAssertion = new TestAssertion(value, variable, getterMethodInstance, getterMethodName, getter, assertionTargetMethod, statement, assertMethodName, lineNum);
							
							testTest.addAssertionLists(testAssertion);
						}else if(!splitSpace[1].equals("}") && !splitSpace[1].equals("try") && !splitSpace[1].equals("//")){
							TestMethod testMethod = new TestMethod(body.get(bodyNum), bodyNum);
							testTest.addMethodLists(testMethod);
							
						}else if(splitSpace[1].equals("try")) {
							TestMethod testMethod = new TestMethod(body.get(bodyNum), "try", bodyNum, "", "");
							testTest.addMethodLists(testMethod);
							
						}else if(splitSpace.length > 2 && splitSpace[2].equals("catch")){
							TestMethod testMethod = new TestMethod(body.get(bodyNum), "catch", bodyNum, "", "");
							testTest.addMethodLists(testMethod);
							
						}else if(splitSpace[1].equals("}") && body.get(bodyNum).substring(2, 3).equals(" ")) {
							TestMethod testMethod = new TestMethod(body.get(bodyNum), "}", bodyNum, "", "");
							testTest.addMethodLists(testMethod);
						}
					}
				}
			}
		}
	}
	
	private ArrayList<TestClass> createtestClassLists(ArrayList<String> inputFileNameLists){
		ArrayList<TestClass> testClassLists = new ArrayList<TestClass>();
		
		for(int fileNum = 0; fileNum < inputFileNameLists.size(); fileNum ++) {
			File inputFile = new File(inputFileNameLists.get(fileNum));
			
			try {
				FileReader fr = new FileReader(inputFile);
				BufferedReader br = new BufferedReader(fr);
				
				String readLine;
				
				String packageName = "";
				ArrayList<TestImport> importLists = new ArrayList<TestImport>();
				TestClass testClass = null;
				Test testTest = null;
				
				while((readLine = br.readLine()) != null) {
					if(readLine.length() != 0) {
						String[] splitBracket = readLine.split("[()]");
						String[] splitSpace = splitBracket[0].split(" +");
						
						if(splitSpace.length > 0) {
							if(splitSpace[0].equals("package")) {
								packageName = splitSpace[1].replace(";", "");
								
							}else if(splitSpace[0].equals("import")) {
								TestImport testImport;
								if(splitSpace[1].equals("static")) {
									testImport = new TestImport(readLine, splitSpace[2].replace(";", ""));
								}else {
									testImport = new TestImport(readLine, splitSpace[1].replace(";", ""));
								}
								
								importLists.add(testImport);
								
							}else if(splitSpace[0].equals("public")) {
								testClass = new TestClass(splitSpace[2].replace("{", ""), packageName, importLists);
								testClassLists.add(testClass);
								
							}else {
								if(splitSpace.length > 1) {
									if(splitSpace[1].equals("@Test")) {
										testTest = new Test();
										testClass.addTestLists(testTest);
										
									}else if(splitSpace[1].equals("public")) {
										testTest.setMethodName(splitSpace[3]);
										
									}else {
										if(testTest != null ) {
											testTest.addBody(readLine);
										}
									}
									
									if(testTest != null) {
										testTest.addContents(readLine);
									}
								}
							}
						
						}
					}
				}
				
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return testClassLists;
	}
}
