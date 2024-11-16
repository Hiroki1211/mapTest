package traceArrange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import executer.Executer;

public class TraceArrange {

	private static String junitFileName = "junit-4.13.2.jar";
	private static String junitAddFileName = "junit-4.12.jar";
	private static String evoSuiteFileName = "evosuite-1.0.6.jar";
	private static String junitRunnerFileName = "evosuite-standalone-runtime-1.0.6.jar";
	private static ArrayList<String> exceptLists = new ArrayList<String>();
	
	public static void addExceptLists() {
		exceptLists.add(junitFileName);
		exceptLists.add(junitAddFileName);
		exceptLists.add(evoSuiteFileName);
		exceptLists.add(junitRunnerFileName);
		exceptLists.add("junit-platform-console-standalone-1.11.0.jar");
		exceptLists.add("jetty-util-9.4.56.v20240826.jar");
		exceptLists.add("jetty-server-9.4.56.v20240826.jar");
		exceptLists.add("jetty-http-9.4.56.v20240826.jar");
		exceptLists.add("jetty-io-9.4.56.v20240826.jar");
		exceptLists.add("jetty-servlet-9.4.56.v20240826.jar");
		exceptLists.add("jetty-servlet-9.4.56.v20240826.jar");
	}
	
	public static void main(String[] args) throws IOException {
		Executer executer = new Executer();
		addExceptLists();
		
		ArrayList<String> inputEvoSuiteTestTraceFileNameLists = executer.getInputEvoSuiteTestTraceFileNameLists();
		inputEvoSuiteTestTraceFileNameLists.add("src/test/resources/IntegrationTestTrace/trace.json");
		
		for(int i = 0; i < inputEvoSuiteTestTraceFileNameLists.size(); i++) {
			File file = new File(inputEvoSuiteTestTraceFileNameLists.get(i));
			ArrayList<String> textLists = new ArrayList<String>();
			
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String readLine;
			while((readLine = br.readLine()) != null) {
				String[] sp = readLine.split("filename");
				
				if(sp.length > 1) {
					String loadedFrom = sp[0];
					loadedFrom = trimDoubleQout(loadedFrom);
					loadedFrom = trimRoundBracket(loadedFrom);
					loadedFrom = trimConma(loadedFrom);
					String[] splitColon = loadedFrom.split("[:/]");
					
					if(!exceptLists.contains(splitColon[splitColon.length - 1])) {
						textLists.add(readLine);
					}
				}
			}
			
			file.delete();
			file = new File(inputEvoSuiteTestTraceFileNameLists.get(i));
			FileWriter fw = new FileWriter(file);
			file.setReadable(true);
			file.setWritable(true);
			
			for(int j = 0; j < textLists.size(); j++) {
				fw.write(textLists.get(j) + "\n");
			}
			
			br.close();
			fw.close();
		}
		
	}			
				
	private static String trimDoubleQout(String input) {
		input = input.replace('"', '?');
		input = input.replace("?", "");
		return input;
	}
	
	private static String trimRoundBracket(String input) {
		input = input.replace('{', '?');
		input = input.replace('}', '?');
		input = input.replace("?", "");
		return input;
	}
	
	public static String trimConma(String input) {
		input = input.replace(',', '?');
		input = input.replace("?", "");
		return input;
	}
}
