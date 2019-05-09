package newModel.doubleAdjust.run;

import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;

public class Runtask implements Runnable {
	String arg;
	Parameter para;
	public Runtask(Parameter parameter, String arg) {
		this.para=parameter;
		this.arg=arg;
	}
	@Override
	public void run() {
		NumericalTest_Run numerical=new NumericalTest_Run();
		numerical.runNumerical(arg, para);
	}

}
