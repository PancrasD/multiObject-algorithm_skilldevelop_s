package com.newAlgorithem.gavn.doubleAdjust;

public class RuntaskExperimentalDesign implements Runnable {
	String arg;
	Case para;
	public RuntaskExperimentalDesign(Case project, String arg) {
		this.para=project;
		this.arg=arg;
	}
	@Override
	public void run() {
		NumericalTest_RunExperimentalDesign numerical=new NumericalTest_RunExperimentalDesign();
		numerical.runNumerical(arg, para);
	}

}
