package constraintsmanipulation;

import java.io.File;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.utils.ConfigurationUtils;

public enum Models {
	REGISTER {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/example/register.txt"));}
		@Override String getName() {return "Register";}
	},REGISTER2 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/example/register2.txt"));}
		@Override String getName() {return "Register2";}
	}, GPL {
		@Override Configuration loadConfiguration() {return ConfigurationUtils.loadConfigurationFromFeatureIDEModel(new File("data/featureide/gpl_ahead.m"));}
		@Override String getName() {return "GPL_AHEAD";}
	}, TIGHT_VNC {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/featureide/TightVNC.txt"));}
		@Override String getName() {return "TightVNC";}
	}, AIRCRAFT {
		@Override Configuration loadConfiguration() {return ConfigurationUtils.loadConfigurationFromCitlab(new File("data/citlab/aircraft/aircraft.citl"));}
		@Override String getName() {return "Aircraft";}
	}, DJANGO {
		@Override Configuration loadConfiguration() {return ConfigurationUtils.loadConfigurationFromCitlab(new File("data/citlab/django/Django_ridotto_iniziale.citl"));}
		@Override String getName() {return "Django";}
	}, DJANGO_REPAIRED {
		@Override Configuration loadConfiguration() {return ConfigurationUtils.loadConfigurationFromCitlab(new File("data/citlab/django/Django_ridotto_iniziale.citl"));}
		@Override String getName() {return "DjangoRepaired";}
	}, EXAMPLE {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/example/example.txt"));}
		@Override String getName() {return "Example";}
	}, LINUX_CITLAB {
		@Override Configuration loadConfiguration() {return ConfigurationUtils.loadConfigurationFromCitlab(new File("data/citlab/linux/Linux.citl"));}
		@Override String getName() {return "Linux";}
	}, LINUX28 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/Linux2_6_28.txt"));}
		@Override String getName() {return "Linux";}
	}, LINUX32 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/2.6.32-2var.bool"));}
		@Override String getName() {return "Linux32";}
	}, LINUX33 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/2.6.33.3-2var.bool"));}
		@Override String getName() {return "Linux33";}
	}, LINUX32_REDUCED {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/2.6.32-2var-reduced.bool"));}
		@Override String getName() {return "Linux32Reduced";}
	}, LINUX33_REDUCED {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/2.6.33.3-2var-reduced.bool"));}
		@Override String getName() {return "Linux33Reduced";}
	}, TOYBOX {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/toybox.bool"));}
		@Override String getName() {return "Toybox";}
	}, TOYBOX2 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/toybox2.bool"));}
		@Override String getName() {return "Toybox2";}
	}, FREEBSD {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/freebsd-icse11.bool"));}
		@Override String getName() {return "FreeBSD";}
	}, FREEBSD2 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/freebsd-icse12.bool"));}
		@Override String getName() {return "FreeBSD2";}
	}, LINUX_ERROR {
		@Override
		Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/2.6.28.6-icse11.bool"));}
		@Override String getName() {return "Linux";}
	}, RHISCOM1 {
		@Override
		Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/featureide/rhiscom1.cnf"));}
		@Override String getName() {return "Rhiscom1";}
	}, RHISCOM2 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/featureide/rhiscom2.cnf"));}
		@Override String getName() {return "Rhiscom2";}
	}, RHISCOM3 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/featureide/rhiscom3.cnf"));}
		@Override String getName() {return "Rhiscom3";}
	}, ECOS1 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/eCos-3.0-pc_vmWare-naiveCTCs.constraints.txt"));}
		@Override String getName() {return "eCos1";}
	}, ECOS2 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/eCos-3.0-pc_vmWare-naiveCTCs.constraints_modified.txt"));}
		@Override String getName() {return "eCos2";}
	}, WINDOWS70 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/featureide/windows70.cnf"));}
		@Override String getName() {return "Windows70";}
	}, WINDOWS80 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/featureide/windows80.cnf"));}
		@Override String getName() {return "Windows80";}
	}, BUSYBOX1_80 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/busybox-1.18.0.bool"));}
		@Override String getName() {return "BusyBox1.80";}
	}, BUSYBOX1_85 {
		@Override Configuration loadConfiguration() {return Configuration.newConfigurationFromFile(new File("data/lvat/busybox-all_mined_constraints.txt"));}
		@Override String getName() {return "BusyBox1.85";}
	}, ERP_SPL_1 {
		@Override Configuration loadConfiguration() {return ConfigurationUtils.loadConfigurationFromFeatureIDECNF(new File("data/featureide/ERP_SPL_1.txt"));}
		@Override String getName() {return "ERP_SPL_1";}
	}, ERP_SPL_2 {
		@Override Configuration loadConfiguration() {return ConfigurationUtils.loadConfigurationFromFeatureIDECNF(new File("data/featureide/ERP_SPL_2.txt"));}
		@Override String getName() {return "ERP_SPL_2";}
	};
	
	abstract Configuration loadConfiguration();
	
	abstract String getName();
	
	public String toString() {return getName();}

}
