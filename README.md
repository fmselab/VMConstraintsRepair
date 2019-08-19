# VMConstraintsRepair
This repository contains the data set and code for the paper 
[*A Process for Fault-driven Repair of Constraints Among Features*](https://dl.acm.org/citation.cfm?doid=3307630.3342413)
(Paolo Arcaini, Angelo Gargantini, and Marco Radavelli) in 2nd International Workshop on Variability and Evolution of Software-intensive Systems (VariVolution 2019)

If you use this tool for academic research, please cite it as:
```
@inproceedings{arcaini2019varivolution
	author = {Arcaini, Paolo and Gargantini, Angelo and Radavelli, Marco},
	title = {A Process for Fault-driven Repair of Constraints Among Features},
	booktitle = {Proceedings of the 23rd International Systems and Software Product Line Conference - Volume B},
	series = {SPLC '19},
	year = {2019},
	isbn = {978-1-4503-6668-7},
	location = {Paris, France},
	pages = {71:1--71:9},
	articleno = {71},
	numpages = {9},
	url = {http://doi.acm.org/10.1145/3307630.3342413},
	doi = {10.1145/3307630.3342413},
	acmid = {3342413},
	publisher = {ACM},
	address = {New York, NY, USA},
	keywords = {automatic repair, fault, system evolution, variability model},
} 
```

### How to reproduce experiments
The code is an eclipse project, containing all its dependencies as JAR libraries in the [`libs`](vmconstraintsrepair/libs/) folder.

To run experiments on BENCH<sub>MUT</sub>, execute the JUnit test case `exp2` in class [`vmconstraintsrepair/experiments/constraintsmanipulation/Experiment2.java`](./vmconstraintsrepair/experiments/constraintsmanipulation/Experiment2.java) (optionally with Java VM arguments `-Xms8192M -Xmx8192M` to increase heap space). 
To run experiments on selected models and/or manipulators, and use individual mutation operators, set the variables `models`, `man` and `mut` in the same class `Experiment2.java`.

To run experiments on BENCH<sub>REAL</sub>, execute the JUnit test case `execExp3` in class [`vmconstraintsrepair/experiments/constraintsmanipulation/Experiment3.java`](./vmconstraintsrepair/experiments/constraintsmanipulation/Experiment3.java) (optionally with Java VM arguments `-Xms8192M -Xmx8192M` to increase heap space). 

To obtain benchmark properties (for Tab. 2 in the paper), run JUnit test cases `printModelStats()` and `mutatedModelsStats()` in [`eafmupdate\experiments\constraintsmanipulation/PrintStatistics.java`](./eafmupdate\experiments\constraintsmanipulation/PrintStatistics.java).

To obtain the aggregated data for Tab. 3 and Tab. 4, execute the R script in [`vmconstraintsrepair/r_scripts/plots.R`](vmconstraintsrepair/r_scripts/plots.R).

To use this process on other models, it is possible to add the new model in the enum [`vmconstraintsrepair/experiments/constraintsmanipulation/Models.java`](vmconstraintsrepair/experiments/constraintsmanipulation/Models.java) and then reference it from the experiment classes.

In case of problems or questions, please contact one of the authors of the paper.
