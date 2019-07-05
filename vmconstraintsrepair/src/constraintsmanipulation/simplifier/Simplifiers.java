package constraintsmanipulation.simplifier;

public enum Simplifiers {
	NONE(NoneSimplifier.getInstance()), 
	ATGT(ATGTSimplifier.getInstance()), 
	ESPRESSO(EspressoSimplifier.getInstance()), 
	JBOOL(JBoolSimplifier.getInstance()), 
	QM(QMSimplifier.getInstance());
	
	private Simplifier s;
	private Simplifiers(Simplifier s) {
		this.s=s;
	}
	
	public Simplifier getSimplifier() {return s;}
	
	
	/** The Constant simplifiers. */
	public static final Simplifier[] simplifiers = {
			Simplifiers.NONE.getSimplifier(), 
			Simplifiers.ATGT.getSimplifier(), 
			Simplifiers.ESPRESSO.getSimplifier(), 
			Simplifiers.JBOOL.getSimplifier(), 
			Simplifiers.QM.getSimplifier(), 
	};	
	//, new BestSimplifier(SyntaxTreeDistance.getInstance()), new BestSimplifier(SimplificationDistance.getInstance())};
}
