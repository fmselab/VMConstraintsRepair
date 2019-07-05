package constraintsmanipulation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tgtlib.definitions.expression.Expression;

public class Difference {
	/** Common constraints, with index as of c1 */
	public Map<Integer,Expression> commonConstraints;
	
	/** Constraints which are different in c1, with the index in c1 */
	public Map<Integer,Expression> differentConstraints1;
	
	/** Constraints peculiar of c2, with the index of ??? */
	public Map<Integer,Expression> differentConstraints2;
	public Difference(Map<Integer,Expression> commonConstraints, Map<Integer,Expression> differentConstraints1, Map<Integer,Expression> differentConstraints2) {
		this.commonConstraints = commonConstraints;
		this.differentConstraints1 = differentConstraints1;
		this.differentConstraints2 = differentConstraints2;
	}
	
	public Difference(Configuration a, Configuration b) {
		Map<Integer,Expression> c = new HashMap<>();
		Map<Integer,Expression> c1=new HashMap<>(), c2=new HashMap<>(); // Expressions which are different in c1 and c2
		List<Integer> eq = new ArrayList<>();
		for (int i=0; i<a.model.constraints.size(); i++) {
			boolean found=false;
			String s = a.model.constraints.get(i).toString();
			for (int j=0; j<b.model.constraints.size(); j++) if (s.equals(b.model.constraints.get(j).toString())) {eq.add(j); found=true; break;}
			if (found) c.put(i, a.model.constraints.get(i));
			else c1.put(i, a.model.constraints.get(i));
		}
		for (int i=0; i<b.model.constraints.size(); i++) if (!eq.contains(i)) c2.put(i, b.model.constraints.get(i)); // add the unique constraints of C2
		setValues(c,c1,c2);
	}
	
	public void setValues(Map<Integer,Expression> commonConstraints, Map<Integer,Expression> differentConstraints1, Map<Integer,Expression> differentConstraints2) {
		this.commonConstraints = commonConstraints;
		this.differentConstraints1 = differentConstraints1;
		this.differentConstraints2 = differentConstraints2;
	}
	
	//TODO maybe?? create a compact difference representation that allows to derive c2
	
	
}
