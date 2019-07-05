package constraintsmanipulation.manipulator;

import constraintsmanipulation.simplifier.Simplifiers;

public enum Manipulators {
	
	NAIVE(ManipulatorNaive.getInstance()),
	ONLY_SELECTION(new ManipulatorSAS(Simplifiers.NONE.getSimplifier())), 
	ATGT(new ManipulatorSAS(Simplifiers.ATGT.getSimplifier())),
	ESPRESSO(new ManipulatorSAS(Simplifiers.ESPRESSO.getSimplifier())), 
	JBOOL(new ManipulatorSAS(Simplifiers.JBOOL.getSimplifier())),
	QM(new ManipulatorSAS(Simplifiers.QM.getSimplifier())),
	
	//BEST_BFED(new ManipulatorSAS(new BestSimplifier(BFED.instance))),
	//BEST_BFCD(new ManipulatorSAS(new BestSimplifier(BFCD.instance))),
	;
	
	private Manipulator m;
	private Manipulators(Manipulator m) {
		this.m = m;
	}
	public Manipulator getManipulator() {return m;}
	
	
	public static final Manipulator[] mans = {
			Manipulators.NAIVE.getManipulator(), 
			Manipulators.ONLY_SELECTION.getManipulator(),
			Manipulators.ATGT.getManipulator(),
			Manipulators.ESPRESSO.getManipulator(),
			Manipulators.JBOOL.getManipulator(),
			Manipulators.QM.getManipulator()
	};
	
	public static final Manipulator[] mansExceptQM = {
			Manipulators.NAIVE.getManipulator(), 
			Manipulators.ONLY_SELECTION.getManipulator(),
			Manipulators.ATGT.getManipulator(),
			Manipulators.ESPRESSO.getManipulator(),
			Manipulators.JBOOL.getManipulator(),
	};
	
	public static final Manipulator[] mansExceptQMAndATGT = {
			Manipulators.NAIVE.getManipulator(), 
			Manipulators.ONLY_SELECTION.getManipulator(),
			Manipulators.ESPRESSO.getManipulator(),
			Manipulators.JBOOL.getManipulator()
	};
	
	public static final Manipulator[] mansExceptQMAndATGTAndJBool = {
			Manipulators.NAIVE.getManipulator(), 
			Manipulators.ONLY_SELECTION.getManipulator(),
			Manipulators.ESPRESSO.getManipulator(),
	};
	
	/*public static final Manipulator[] mansBest = {
			Manipulators.BEST_BFED.getManipulator(), //new ManipulatorSAS(new BestSimplifier(SyntaxTreeDistance.getInstance())), 
			Manipulators.BEST_BFCD.getManipulator() //new ManipulatorSAS(new BestSimplifier(SimplificationDistance.getInstance())),
	};*/
}
