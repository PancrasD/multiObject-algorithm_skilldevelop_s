package com.newAlgorithem.gavn.doubleAdjust;

public class Runtask implements Runnable {
	String arg;
	Case para;
	public Runtask(Case project, String arg) {
		this.para=project;
		this.arg=arg;
	}
	@Override
	public void run() {
		NumericalTest_Run numerical=new NumericalTest_Run();
		numerical.runNumerical(arg, para);
	}

}
