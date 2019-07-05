package constraintsmanipulation.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map.Entry;

import tgtlib.definitions.expression.Expression;

/**
 * The Class Formula. It has some methods removed from the one used in the thesis, to make it lighter.
 * It has instead a few more methods useful for this work.
 *
 * @author Marco
 */
public class Formula implements Comparable<Formula> {
	
	private String s;
	private long time;
	private boolean timedOut;
	/** List of OR SIGNS: important is that they have decreasing length (the longest with the same prefix should compare before the shortest) */
	private static final String[] OR_SIGNS = {"or", "||", "|", "+"};
	private static final String[] AND_SIGNS = {"and", "&&", "&"};
	private static final String[] NOT_SIGNS = {"not", "!", "~", "-"};
	//private static final String[] IMPLY_SIGNS = {"->", "=>"};
	//private static final String[] COIMPLY_SIGNS = {"<->", "<=>"};
	private static final String[] EXCLUDED_LITERALS = {"not", "or", "and"};
	
	/** The simplification method */
	private String method;
	
	public Formula(String s) {
		if (s==null) s="";
		if (s.contains("\t")) s=s.substring(s.lastIndexOf("\t")+1);
		s = removeDefinedEx(s);
		s = replaceKeywords(s, Formula.generateConversionEntries("&","|","!"));
		this.s=s;
	}
	public Formula(String s, long time, boolean timedOut) {
		this(s);
		this.time=time; this.timedOut=timedOut;
	}
	public Formula(String s, String method) {
		this(s);
		this.method=method;
	}
	
	public String getMethod() {
		return method==null ? "original" : method;
	}
	public int getNumChars() {
		return s.length();
	}
	private int countOccurrences(String... keywords) {
		int count=0;
		for (int i=0; i<s.length(); i++) {
			for (String sign : keywords) {
				if (i+sign.length()<s.length() && s.substring(i,i+sign.length()).equals(sign)) {
					i+=sign.length()-1;
					count++;
				}
			}
		}
		return count;
	}
	public int getNumAnd() {
		return countOccurrences(AND_SIGNS);
	}
	public int getNumOr() {
		return countOccurrences(OR_SIGNS);
	}
	public int getNumNot() {
		return countOccurrences(NOT_SIGNS);
	}

	public int getNumImply() {
		int count=0;
		for (int i=0; i<s.length(); i++) {
			if (i>1 && i<s.length()-1 && s.substring(i, i+2).equals("->") && s.charAt(i-1)!='<') count++;
		}
		return count;
	}
	public int getNumIffs() {
		int count=0;
		for (int i=0; i<s.length(); i++) {
			if ((i<s.length()-2 && s.substring(i, i+3).equals("<->"))) count++;
		}
		return count;
	}
	public int getNumOperators() {
		return getNumAnd() + getNumOr() + getNumNot() + getNumImply() + getNumIffs();
	}
	private static boolean isLetter(char c) {
		return (c>='A' && c<='Z') || c=='_' || (c>='0' && c<='9') || (c>='a' && c<='z') ;
	}
	/** @return the list of literals with the position of the initial letter in the formula, in the order which are in the equation (repetitions are possible) */
	protected ArrayList<Entry<Integer,String>> getLiterals() {
		StringBuffer sb = new StringBuffer();
		ArrayList<Entry<Integer,String>> v = new ArrayList<>();
		for (int i=0; i<s.length(); i++) {
			for (String ex : EXCLUDED_LITERALS) {
				if (i+ex.length()<s.length() && s.substring(i, i+ex.length()).equalsIgnoreCase(ex) 
					&& (i==0 || !isLetter(s.charAt(i-1)))
					&& (i+ex.length()==s.length()-1 || !isLetter(s.charAt(i+ex.length())))) {
					i+= ex.length()-1; 
					continue;
				}
			}
			if (isLetter(s.charAt(i)) && (i==0 || !isLetter(s.charAt(i-1)))) sb.append(s.charAt(i)+"");
			else if (sb.length()>0 && isLetter(s.charAt(i))) sb.append(s.charAt(i));
			if (sb.length()>0 && isLetter(s.charAt(i)) && (i==s.length()-1 || !isLetter(s.charAt(i+1)))) {
				v.add(new SimpleEntry<Integer,String>(i-sb.toString().length()+1,sb.toString()));
				sb = new StringBuffer();
			}
		}
		return v;
	}
	public int getNumLiterals() {
		return getLiterals().size();
	}
	/** @return a string containing a list of all the feature names, separated by the specified separator */
	public String getFeatureNames(String separator) {
		String s="";
		ArrayList<String> v = getFeatures();
		for (String t : v) s+=separator+t;
		return s.substring(separator.length());
	}
	/** @return the list of features (literals without repetitions), in the order which are in the equation (repetitions are NOT possible) */
	public ArrayList<String> getFeatures() {
		ArrayList<Entry<Integer,String>> a = getLiterals();
		ArrayList<String> v = new ArrayList<>();
		for (Entry<Integer,String> e : a) v.add(e.getValue());
		for (int i=0; i<v.size(); i++) {
			for (int j=i+1; j<v.size(); j++) {
				if (v.get(j).equals(v.get(i))) {
					v.remove(j);
					j--;
				}
			}
		}
		return v;
	}
	public int getNumFeatures() {
		return getFeatures().size();
	}
	/*
	public int getNumFeaturesCorrect() {
		FeatureExprParserJava parser = new FeatureExprParserJava(FeatureExprFactory.sat());
        SATFeatureExpr fexpr = (SATFeatureExpr) parser.parse(s);
        return fexpr.countDistinctFeatures();
	}
	public int getNumLiteralsCorrect() {
        return StringUtils.countMatches(s, "CONFIG_");
	}*/
	
	public int getMaxNestingLevel() {
		int level=0, maxLevel=0;
		for (int i=1; i<s.length(); i++) {
			if (s.charAt(i)=='(') {
				level++; if (level>maxLevel) maxLevel=level;
			}
			else if (s.charAt(i)==')') level--;
		}
		return maxLevel;
	}
	public String toString() {
		return s;
	}
	public String toStringWithTimeout(String separator) {
		return time+separator+(timedOut?1:0)+separator+s;
	}
	public String getStatsFormula() {
		return getNumChars()+","+getNumOperators()+","+getNumLiterals()+","+getNumFeatures()+","+getMaxNestingLevel();
	}
	public String getStatsFormulaMethod(Formula originalFormula) {
		return time+","+(timedOut?"1":"0")+","+(isSimplerThan(originalFormula)?"1":"0")+","+getStatsFormula();
	}
	
	public boolean isSimplerThan(Formula f) {
		return (getNumFeatures()<f.getNumFeatures() || (getNumFeatures()<=f.getNumFeatures() && getNumLiterals()<f.getNumLiterals()) || (getNumFeatures()<=f.getNumFeatures() && getNumLiterals()<=f.getNumLiterals() && getNumOperators()<f.getNumOperators())); // && (this.getNumChar()<=f.getNumChar() || this.getNumOperators()<=f.getNumOperators());
	}
	
	public static String removeDefinedEx(String s) {
		//String t = s.replace("definedEx(", "(");
		StringBuffer sb = new StringBuffer();
		boolean removeFirstClosedParenthesis=false;
		for (int i=0; i<s.length(); i++) {
			if (s.substring(i).indexOf("definedEx(")==0) {
				removeFirstClosedParenthesis=true;
				i=i+"definedEx(".length();
			}
			if (removeFirstClosedParenthesis && s.charAt(i)==')') {
				removeFirstClosedParenthesis=false;
			} else {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	public static String removeParenthesis(String s) {
		String t = s;
		int opened=-1;
		for (int i=0; i<t.length()-1; i++) {
			if (t.charAt(i)=='(' && Formula.isLetter(t.charAt(i+1))) {
				opened=i;
			}
			else if (!Formula.isLetter(t.charAt(i))) opened=-1;
			if (Formula.isLetter(t.charAt(i)) && t.charAt(i+1)==')' && opened>-1) {
				t = t.substring(0,opened)+t.substring(opened+1,i+1)+(i+2<t.length() ? t.substring(i+2,t.length()) : "");
				i-=2;
				opened=-1;
			}
		}
		return t;
	}
	
	
	public String getFeatureNamesAsSingleLetters(String separator) {
		int numOfLetters = getNumFeatures();
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<numOfLetters; i++) {
			sb.append(separator+(char)('A'+i));
		}
		return sb.toString().substring(separator.length());
	}
	private ArrayList<String> getFeatureNamesOrdered() {
		ArrayList<String> a = getFeatures();
		ArrayList<String> orderedFeatures = new ArrayList<>(a.size());
		while (a.size()>0) {
			int maxI=0;
			for (int i=0; i<a.size(); i++) {
				if (a.get(i).length()>a.get(maxI).length()) maxI=i;
			}
			orderedFeatures.add(a.get(maxI));
			a.remove(maxI);
		}
		return orderedFeatures;
	}
	private String getStringWithSingleLettersFeatureNames() {
		ArrayList<String> a = getFeatureNamesOrdered();
		String s = this.s;
		for (int i=0; i<a.size(); i++) {
			s=s.replace(a.get(i), ""+(char)('A'+i));
		}
		return s;
	}
	public String codeStringForQuineMcCluskey() {
		return getStringWithSingleLettersFeatureNames().replace(" & ","").replace("!","~").replace("|", "+").replace("(","").replace(")","");
	}
	public String decodeStringFromMcCluskey(String s) {
		//System.out.println("DECODE INPUT: "+s);
		s=s.replace("\n","").replace("\t","").replace("\r","");
		StringBuilder sb = new StringBuilder();
		ArrayList<String> a = getFeatureNamesOrdered();
		for (int i=0; i<s.length(); i++) {
			if (isLetter(s.charAt(i))) {
				sb.append(a.get(s.charAt(i)-'A'));
				if (i<s.length()-1 && (isLetter(s.charAt(i+1)) || s.charAt(i+1)=='~')) sb.append(" & ");
			} else sb.append(s.charAt(i));
		}
		s=sb.toString();
		s="("+s.replace('~','!').replace("+",") | (")+")";
		s=clean(s);
		//System.out.println("DECODE OUTPUT: "+s);
		return s;
	}
	
	/** @return the list of literals, in the order which are in the equation (repetitions are possible) */
	public static String replaceKeywords(String s, ArrayList<Entry<String, String>> substitutions) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<s.length(); i++) {
			for (Entry<String,String> e : substitutions) {
				String ex = e.getKey();
				if ( (i+ex.length()<s.length() && s.substring(i, i+ex.length()).equalsIgnoreCase(ex)) 
					&& ( !isLetter(ex.charAt(0)) || (i==0 || !isLetter(s.charAt(i-1))) )
					&& ( !isLetter(s.charAt(ex.length()-1))	|| (i+ex.length()==s.length()-1 || !isLetter(s.charAt(i+ex.length()))) ) ) {
							sb.append(" "+e.getValue()+" ");
							i+= ex.length(); 
							continue;
				}
			}
			sb.append(s.charAt(i));
		}
		return clean(sb.toString()); // removes double spaces
	}
	
	public static ArrayList<Entry<String,String>> generateConversionEntries(String wishedAndSign, String wishedOrSign, String wishedNotSign) {
		ArrayList<Entry<String,String>> replacements = new ArrayList<>();
		for (String s : AND_SIGNS) if (!s.equals(wishedAndSign)) replacements.add(new SimpleEntry<String,String>(s,wishedAndSign));
		for (String s : OR_SIGNS) if (!s.equals(wishedAndSign)) replacements.add(new SimpleEntry<String,String>(s,wishedOrSign));
		for (String s : NOT_SIGNS) if (!s.equals(wishedAndSign)) replacements.add(new SimpleEntry<String,String>(s,wishedNotSign));
		return replacements;
	}
	
	/** Format a formula for better readability in the algebraic representation,
	 * supposing that the only operator symbols are: | & ! */
	public static String clean(String s) {
		/*s=s.replace("  ", " ");
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<s.length(); i++) {
			if (s.charAt(i)==' ' && ( (i>0 && s.charAt(i-1)=='!') || ( (i==0 || isUChar(s.charAt(i-1))) && (i==s.length()-1 || isUChar(s.charAt(i+1))) ) ) ) continue;
			else sb.append(s.charAt(i));
		}
		return sb.toString();*/
		return s.replace("|"," | ").replace("&", " & ").replace("  "," ").replace("( ", "(").replace(" )", ")").replace("! ","!").replace("  "," ");
	}
	/*private static boolean isUChar(char c) {
		return c=='(' || c==')' || c==' ' || c=='!';
	}*/
	
	/** This method is needed to create a representation compatible with TXL grammar */
	public String toStringForTXL() {
		return toStringWithParenthesis().replace('!', '-');
	}
	private String toStringWithParenthesis() {
		return clean(toStringWithParenthesis(0, s.length()));
	}
	private String toStringWithParenthesis(int start, int end) {
		int numPar=0;
		boolean addPar=false;
		boolean checked=false;
		int and=-1,or=-1;
		int a=start,b=end;
		for (int i=start; i<end; i++) {
			if (or==-1 && s.charAt(i)=='|') {
				or=i;
			}
			if (and==-1 && s.charAt(i)=='&') {
				and=i;
			}
			if (s.charAt(i)=='(') {
				numPar=1;
				if (!checked && and==-1 && or==-1) {
					for (int j=i+1; j<end; j++) {
						numPar+= (s.charAt(j)=='(')?1:((s.charAt(j)==')')?-1:0);
						if (numPar==0 && (s.charAt(j)=='&' || s.charAt(j)=='|')) {addPar=true; break;}
					}
					checked=true;
					if (!addPar) {
						numPar=0;
						a=i+1;
						b=s.substring(a, end).lastIndexOf(')')+a;
						continue;
					}
					numPar=1;
				}
				for(i=i+1; i<end; i++) {
					if (s.charAt(i)=='(') numPar++;
					else if (s.charAt(i)==')') {
						numPar--;
						if (numPar==0) break;
					}
				}
			}
		}
		//System.out.println(s.substring(start, end)+" - "+s.substring(a, b));
		if (or>-1) return (a-1>start?s.substring(start,a-1):"")+"("+ ( toStringWithParenthesis(a, or)+" | "+toStringWithParenthesis(or+1, b) ) +")";
		else if (and>-1) return (a-1>start?s.substring(start,a-1):"")+"("+ ( toStringWithParenthesis(a, and)+" & "+toStringWithParenthesis(and+1, b) ) +")";
		else return (a-1>start?s.substring(start,a):"")+s.substring(a, b)+(a-1>start?")":"");
	}
	
	public int getNumParenthesis() {
		int count=0;
		for (int i=0; i<s.length(); i++) if (s.charAt(i)==')' || s.charAt(i)=='(') count++;
		return count;
	}
	
	@Override
	public int compareTo(Formula f) {
		if (isSimplerThan(f)) return -1;
		if (f.isSimplerThan(this)) return 1;
		return 0;
	}
	
	
	
	public String toStringJavaNotation() {
		return s.replace("|", "||").replace("&", "&&");
	}
	
	//XXXX Added features
	/**
	 * Gets the features with occurrences.
	 *
	 * @return the list of literals with their occurrences
	 */
	public HashMap<String,Integer> getFeaturesWithOccurrences() {
		ArrayList<Entry<Integer,String>> a = getLiterals();
		HashMap<String,Integer> m = new HashMap<>();
		for (Entry<Integer,String> e : a) m.put(e.getValue(), m.getOrDefault(e.getValue(), 0)+1);
		return m;
	}
	
	/**
	 * Gets the common features.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the common features
	 */
	public static ArrayList<String> getCommonFeatures(Expression a, Expression b) {
		ArrayList<String> commonFeatures = new ArrayList<>();
		HashMap<String,Integer> f1 = new Formula(a==null ? "" : a.toString()).getFeaturesWithOccurrences();
		HashMap<String,Integer> f2 = new Formula(b==null ? "" : b.toString()).getFeaturesWithOccurrences();
		for (String s : f1.keySet()) if (f2.containsKey(s)) commonFeatures.add(s);
		return commonFeatures;
	}
	
	/**
	 * Gets the common features with occurrencies.
	 *
	 * @param a the a
	 * @param b the b
	 * @return an HashMap containing the common features with their common occurrences
	 */
	public static HashMap<String,Integer> getCommonFeaturesWithOccurrencies(Expression a, Expression b) {
		if (a==null || b==null) return new HashMap<>();
		HashMap<String,Integer> commonFeatures = new HashMap<>();
		HashMap<String,Integer> f1 = new Formula(a.toString()).getFeaturesWithOccurrences();
		HashMap<String,Integer> f2 = new Formula(b.toString()).getFeaturesWithOccurrences();
		for (Entry<String, Integer> e : f1.entrySet()) if (f2.containsKey(e.getKey())) commonFeatures.put(e.getKey(), Math.min(e.getValue(), f2.get(e.getKey())));
		return commonFeatures;
	}
	
	/**
	 * Gets the affinity.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the affinity
	 */
	public static int getAffinity(Expression a, Expression b) {
		int n=0; 
		for (Integer i : getCommonFeaturesWithOccurrencies(a, b).values()) n+=i;
		return n;
	}
	
	/*public String toTreeFormat() {
		String str="";
		try {
			System.out.println(this.toString());
			Expression expr = ExpressionParser.parseAsNewBooleanExpression(this.toString());
			System.out.println(expr.toString());
			str = expr.accept(new ToTreeRTED()).replace(" ","");
			System.out.println(str);
		} catch (ParseException e) {e.printStackTrace();}
		return str;
	}*/
	
	/**
	 * Convert from pre notation.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String convertFromPreNotation(String s) {
		try {
			//Expression e = ExpressionParser.parseAsNewBooleanExpression(s);
			StringBuffer sb = new StringBuffer();
			Deque<Integer> stack = new ArrayDeque<Integer>(); 
			for (int i=0; i<s.length(); i++) {
				if ("And(".equalsIgnoreCase(s.substring(i,Math.min(s.length(), i+4)))) {
					stack.push(1); i+=2;
				} else if ("Or(".equalsIgnoreCase(s.substring(i,Math.min(s.length(), i+3)))) {
					stack.push(2); i+=1;
				} else if ("Not(".equalsIgnoreCase(s.substring(i,Math.min(s.length(), i+4)))) {
					stack.push(3); i+=2;
				} else if (s.charAt(i)==',') {
					int c = stack.getFirst(); // LIFO strategy
					sb.append(c==1?" &":(c==2?" |":"!"));
				} else {
					sb.append(s.charAt(i));
				}
				if (s.charAt(i)==')') {
					stack.pop();
				}
			}
			return sb.toString();
		} catch (Exception e) {e.printStackTrace();}
		return "";
	}
	
}
