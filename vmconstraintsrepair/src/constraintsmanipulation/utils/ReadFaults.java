package constraintsmanipulation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FIC;
import constraintsmanipulation.model.FICType;
import tgtlib.definitions.expression.parser.ExpressionParser;

public class ReadFaults {
	public static void readFaults(Configuration c, File file) {
		try {
			BufferedReader fin = new BufferedReader(new FileReader(file));
			String s="";
			while ((s=fin.readLine())!=null) {
				if (s.contains("+ ")) c.addFault(new FIC(ExpressionParser.parseAsBooleanExpression(s.substring(s.indexOf("+ ")+2), Configuration.idc), FICType.OR));
				else if (s.contains("* ")) c.addFault(new FIC(ExpressionParser.parseAsBooleanExpression(s.substring(s.indexOf("* ")+2), Configuration.idc), FICType.AND));
			}
			fin.close();
		} catch (Exception e) {e.printStackTrace();}
	}
}
