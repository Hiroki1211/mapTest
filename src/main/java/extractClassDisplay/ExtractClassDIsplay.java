package extractClassDisplay;

import java.io.File;
import java.util.ArrayList;

import analyzer.Analyzer;
import executer.Executer;
import paramaterExtracter.ExtractClass;
import paramaterExtracter.ParamaterExtracter;
import tracer.Lexer;
import tracer.Trace;

public class ExtractClassDIsplay {

	public static void main(String[] args) {
		Executer executer = new Executer();
		String integrationTestTrace = "src/test/resources/IntegrationTestTrace/trace.json";
		ArrayList<String> inputFileNameLists = executer.getInputFileNameLists();
		
		// 1. product code analyze
		Analyzer analyzer = new Analyzer();
		analyzer.run(inputFileNameLists);
		
		File file = new File(integrationTestTrace);
		Lexer lexer = new Lexer(file);
		ArrayList<Trace> traceLists = lexer.getTraceLists();
		ParamaterExtracter paramaterExtracter = new ParamaterExtracter();
		ArrayList<ExtractClass> extractClassLists = paramaterExtracter.run(analyzer.getMethodLists(), analyzer.getVariableLists(), traceLists);
		for(int i = 0; i < extractClassLists.size(); i++) {
			ExtractClass extractClass = extractClassLists.get(i);
			if(extractClass.getInstanceLists().size() > 0) {
				System.out.println(extractClass.getOwnerClass());
			}
		}
	}
	
}
