package constraintsmanipulation.model;

public enum FICType {
	NONE,
	OR, 
	AND;
	
	public static FICType getRandomFault() {return Math.random()<.5 ? FICType.AND : FICType.OR;}
	
	public static FICType getOtherFault(FICType ft) {return ft==AND ? OR : AND;}
}
