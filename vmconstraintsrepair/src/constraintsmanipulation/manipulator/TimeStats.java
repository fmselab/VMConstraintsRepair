package constraintsmanipulation.manipulator;

public class TimeStats {
	private long timeSelection;
	private long timeSimplification;
	private long timeTotal;
	public TimeStats() {	}
	public TimeStats(long timeSelection, long timeSimplification, long timeTotal) {
		this.timeSelection = timeSelection;
		this.timeSimplification = timeSimplification;
		this.timeTotal = timeTotal;
	}
	public long getTimeSelection() {
		return timeSelection;
	}
	public void setTimeSelection(long timeSelection) {
		this.timeSelection = timeSelection;
	}
	public long getTimeSimplification() {
		return timeSimplification;
	}
	public void setTimeSimplification(long timeSimplification) {
		this.timeSimplification = timeSimplification;
	}
	public long getTimeTotal() {
		return timeTotal;
	}
	public void setTimeTotal(long timeTotal) {
		this.timeTotal = timeTotal;
	}
	
}
