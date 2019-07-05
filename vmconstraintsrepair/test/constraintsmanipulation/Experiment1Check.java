package constraintsmanipulation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import constraintsmanipulation.manipulator.Manipulator;
import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FIC;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.sat.FICGenerator;
import constraintsmanipulation.sat.SATUtils;
import constraintsmanipulation.sat.YicesJNA;
import constraintsmanipulation.utils.ConfigurationUtils;
import constraintsmanipulation.utils.ReadFaults;
import tgtlib.definitions.expression.IdExpression;

/**
 * Checks if the resulting models all preserve equivalence.
 * @author Marco
 *
 */
public class Experiment1Check {
	
	static int N=100;
	
	public void performExperiment1Check(Configuration c) {
		FICGenerator generator = new FICGenerator(c, 10000);
		System.out.println("Initial Model: ");
		System.out.println("Parameters: "+c.model.parameters);
		System.out.println(c);
		System.out.println();
		int andFaults=0, orFaults=0;
		
		//Formula fc = new Formula(AndExpression.makeAndExpression(c.model.constraints).toString());
		//int nOp=fc.getNumOperators(), nLit=fc.getNumLiterals(), nFeat=fc.getNumFeatures();
		//System.out.println(SyntaxTreeDistance.getInstance().getDistance(c1, c));
		try {
			//int wasNull=0, nAND=2, na=0;
			int THRESHOLD=0;
			int nOR=2, no=0;
			for (int i=0; i<N; i++) {
				FICType type = i%2==0 ? FICType.AND : FICType.OR;
				FIC f = null;
				if (i%2==0) {
					continue;
					/*if (na>THRESHOLD) {na=0; nAND++;}
					if (nAND>c.model.parameters.size()) continue;
					System.out.print(i+" "+type+" "+nAND);
					f = generator.generateFault(type,nAND);
					System.out.println(f==null ? "null" : f.getAlpha());
					//if (f==null) {if (wasNull>THRESHOLD) break; else {wasNull++; continue;}}
					//else wasNull=0;
					na++;
					if (f==null) continue;*/
				} else {
					if (no>THRESHOLD) {no=0; nOR++;}
					if (nOR>c.model.parameters.size()) continue;
					System.out.print(i+" "+type+" "+nOR);
					f = generator.generateFIC(type,nOR, false);
					System.out.println(f==null ? "null" : f.getAlpha());
					//if (f==null) {if (wasNull>THRESHOLD) break; else {wasNull++; continue;}}
					//else wasNull=0;
					no++;
					if (f==null) continue;
				}
				c.setFIC(f);
				if (c.fic.getFaultType()==FICType.AND) andFaults++; else orFaults++;
				List<Configuration> configurations = new ArrayList<>();
				for (Manipulator m : Manipulators.mans) {
					Configuration c1 = m.repair(c);
					configurations.add(c1);
				}
				Configuration[] a = configurations.toArray(new Configuration[0]);
				Set<IdExpression> ids = new HashSet<>();
				for (Configuration conf : configurations) ids.addAll(conf.model.parameters);
				SATUtils.checkEquivalence(a[0], a, new YicesJNA(ids));
				System.out.println(andFaults+" "+orFaults);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	@org.junit.Test
	public void testRegister() {
		Configuration c = Configuration.newConfigurationFromFile(new File("data/example/register.txt"));
		ReadFaults.readFaults(c, new File("data/example/register_faults.txt"));
		performExperiment1Check(c);
	}
	@org.junit.Test
	public void testGPL() {
		performExperiment1Check(ConfigurationUtils.loadConfigurationFromFeatureIDEModel(new File("data/featureide/gpl_ahead.m")));
	}
	
}
