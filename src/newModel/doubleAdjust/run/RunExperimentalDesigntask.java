package newModel.doubleAdjust.run;

import newModel.doubleAdjust.basis.Case;

public class RunExperimentalDesigntask implements Runnable {
	String arg;
	Case para;
	String head;
	public RunExperimentalDesigntask(Case project, String arg,String head) {
		this.para=project;
		this.arg=arg;
		this.head=head;
	}
	@Override
	public void run() {
		NumericalTest_RunExperimentalDesign numerical=new NumericalTest_RunExperimentalDesign();
		numerical.runNumerical(arg, para,head);
	}

}
