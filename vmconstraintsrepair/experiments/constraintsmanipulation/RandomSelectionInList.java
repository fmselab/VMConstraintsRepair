package constraintsmanipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RandomSelectionInList<T> {
	private List<T> v;
	private boolean[] selected;
	private int nSelected=0;
	
	public RandomSelectionInList(Collection<T> v) {
		if (v==null) v=new ArrayList<T>();
		this.v=new ArrayList<>(v); 
		selected=new boolean[v.size()];
	}
	
	public T nextRandomElement() {
		int n=-1;
		while (nSelected<selected.length) {
			if (!selected[n=((int)(Math.random()*selected.length))]) {
				selected[n]=true;
				nSelected++;
				return v.get(n);
			}
		}
		return null;
	}
}
