# VMConstraintsRepair
Data set and code for the paper:

P. Arcaini, A. Gargantini, M. Radavelli<br/>
A Process for Fault-driven Repair of Constraints Among Features<br/>
in 2nd International Workshop on Variability and Evolution of Software-Intensive Systems (VariVolution 2019), Paris, France, September 9-13, 2019 (to appear)

### How to reproduce experiments
The code is an eclipse project, containing all its dependencies as JAR libraries in the [`libs`](vmconstraintsrepair/libs/) folder.

To run experiments on BENCH<sub>MUT</sub>, execute the JUnit test case `exp2` in class [`vmconstraintsrepair/experiments/constraintsmanipulation/Experiment2.java`](./vmconstraintsrepair/experiments/constraintsmanipulation/Experiment2.java) (optionally with Java VM arguments `-Xms8192M -Xmx8192M` to increase heap space). 
To run experiments on selected models and/or manipulators, and use individual mutation operators, set the variables `models`, `man` and `mut` in the same class `Experiment2.java`.

To run experiments on BENCH<sub>REAL</sub>, execute the JUnit test case `execExp3` in class [`vmconstraintsrepair/experiments/constraintsmanipulation/Experiment3.java`](./vmconstraintsrepair/experiments/constraintsmanipulation/Experiment3.java) (optionally with Java VM arguments `-Xms8192M -Xmx8192M` to increase heap space). 

To obtain benchmark properties (for Tab. 2 in the paper), run JUnit test cases `printModelStats()` and `mutatedModelsStats()` in [`eafmupdate\experiments\constraintsmanipulation/PrintStatistics.java`](./eafmupdate\experiments\constraintsmanipulation/PrintStatistics.java).

To obtain the aggregated data for Tab. 3 and Tab. 4, execute the R script in [`vmconstraintsrepair/r_scripts/plots.R`](vmconstraintsrepair/r_scripts/plots.R).

To use this process on other models, it is possible to add the new model in the enum [`vmconstraintsrepair/experiments/constraintsmanipulation/Models.java`](vmconstraintsrepair/experiments/constraintsmanipulation/Models.java) and then reference it from the experiment classes.

In case of problems or questions, please contact one of the authors of the paper.
