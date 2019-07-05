package constraintsmanipulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import constraintsmanipulation.manipulator.Manipulator;
import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FIC;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.sat.FICGenerator;
import constraintsmanipulation.utils.ConfigurationUtils;

/**
 * Class with JUnit tests for Experiment 1
 * @author Marco Radavelli
 */
public class Experiment1 {
	
	static Models[] models = {
			Models.EXAMPLE,
			Models.REGISTER,
			Models.DJANGO,
			//Models.TIGHT_VNC,
			//Models.GPL,
			//Models.RHISCOM3
			Models.WINDOWS80
	};
	
	@org.junit.Test
	public void exp1() {
		for (Models m : models) {
			System.out.println(m.getName());
			Configuration c = m.loadConfiguration();
			performExperiment1(c, new File("output/exp1_"+m.getName()+".txt"), FaultGenerationStrategy.CONSTRAINED_SCALABLE, 10, 1000);
		}
	}
	
	/** Perform experiment 1
	 * @param c the configuration to which apply the modifications
	 * @param outputStats the file into which to write the CSV output with statistics of the process.
	 * @param strategu the Fault generation strategy to use: some do not scale well, or are better for a certain model.
	 * @param N the number of faults
	 */
	public void performExperiment1(Configuration c, File outputStats, FaultGenerationStrategy strategy, int N, long timeout) {
		int andFaults=0, orFaults=0;
		try {
			Writer fout=null;
			// see http://stackoverflow.com/questions/1001540/how-to-write-a-utf-8-file-with-java/1001562#1001562
			if (outputStats!=null) fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputStats), "UTF-8"));
			if (fout!=null) fout.write("id,idconf,method,"+ConfigurationUtils.getStatisticsHeader()+"\n");
			//if (fout!=null) fout.write("none,"+ConfigurationUtils.getStatistics(c,c, null)+"\n");
			FICGenerator generator = new FICGenerator(c).setTimeout(timeout);
			int row=0;
			for (int i=0; i<N; i++) {
				int size = i+1;
				if (size>c.model.parameters.size()) size=(int)(Math.random()*c.model.parameters.size())+1;
				for (int j=0; j<2; j++) {
					FICType type= j==0 ? FICType.AND : FICType.OR;
					System.out.println(i);
					FIC f = strategy.nextFault(generator, timeout, size, type);
					if (f==null) continue;
					c.setFIC(f);
					if (f.getFaultType()==FICType.AND) andFaults++; else orFaults++;
					List<Configuration> conf = new ArrayList<>();
					for (Manipulator m : Manipulators.mans) {
						Configuration c1 = m.repair(c);
						conf.add(c1);
						if (fout!=null) fout.write((row++)+1+","+(i+1)+","+m.getSimplifierName()+","+ConfigurationUtils.getStatistics(c1,c, m)+"\n");
					}
					if (fout!=null) fout.flush();
					System.out.println(andFaults+" "+orFaults);
					//Experiment2.checkEquivalence(conf);
				}
			}
			fout.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	enum FaultGenerationStrategy {
		RANDOM {

			/** The size and FaultType are ignored */
			@Override
			public FIC nextFault(FICGenerator generator, long timeout, int size, FICType type) {
				long start = Calendar.getInstance().getTimeInMillis();
				while (start+timeout > Calendar.getInstance().getTimeInMillis()) {
					FIC f = generator.generateFIC(false);
					System.out.println(f==null ? "null" : f.getAlpha());
					if (f!=null) return f;
				}
				return null;
			}
			
		},
		CONSTRAINED_SCALABLE {

			@Override
			public FIC nextFault(FICGenerator generator, long timeout, int size, FICType type) {
				long start = Calendar.getInstance().getTimeInMillis();
				FIC f = null;
				while (start+timeout > Calendar.getInstance().getTimeInMillis()) {
					f = generator.generateFIC(type, size, start+timeout-Calendar.getInstance().getTimeInMillis(), false);
					System.out.println(f==null ? "null" : f.getAlpha());
					if (f!=null) return f;
				}
				return f;
			}
			
		};
		
		abstract FIC nextFault(FICGenerator generator, long timeout, int size, FICType type);
	}
	
	
	/*
	@org.junit.Test
	public void testRegister() {
		Configuration c = Configuration.newConfigurationFromFile(new File("data/example/register.txt"));
		ReadFaults.readFaults(c, new File("data/output/stats_register.txt"));
		performExperiment1(c, new File("data/example/stats_register.txt"), FaultGenerationStrategy.RANDOM, 70, 4000);
		//performExperiment1(c, null, FaultGenerationStrategy.RANDOM, 70, 4000);
	}
	@org.junit.Test
	public void testGPL() {
		Configuration c = ConfigurationUtils.loadConfigurationFromFeatureIDEModel(new File("data/featureide/gpl_ahead.m"));
		performExperiment1(c, new File("data/output/stats_gpl.txt"), FaultGenerationStrategy.CONSTRAINED_SCALABLE, 100, 5000);
		//performExperiment1(c, null, FaultGenerationStrategy.CONSTRAINED_SCALABLE, 100, 5000);
	}
	@org.junit.Test
	public void testTightVNC() {
		Configuration c = Models.TIGHT_VNC.loadConfiguration();
		performExperiment1(c, new File("data/output/stats_TightVNC.txt"), FaultGenerationStrategy.RANDOM, 70, 5000);
		//performExperiment1(c, null, FaultGenerationStrategy.CONSTRAINED_SCALABLE, 100, 5000);
	}*/
}
