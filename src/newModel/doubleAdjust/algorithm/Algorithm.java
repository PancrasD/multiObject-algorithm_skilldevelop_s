package newModel.doubleAdjust.algorithm;

import java.util.List;

import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;

public abstract class Algorithm {
	public  String casefile;//输入文件路径
	public String datafile;//输出文件路径
	public List<List<Double>>countResult;//统计结果list
	public Parameter para;//案例参数
	public Algorithm() {
		
	}
	public Algorithm(String _fn, String _fo, List<List<Double>> countResult, Parameter para) {
		this.casefile=_fn;
		this.datafile=_fo;
		this.countResult=countResult;
		this.para=para;
	}
	public abstract void schedule();
}
