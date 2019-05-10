package newModel.doubleAdjust.comparator;

import newModel.doubleAdjust.Individual;

public class ParetoObjectiveComparator implements DominanceComparator{

	@Override
	public int compare(Individual individual1, Individual individual2) {
		int flag, n, k;
		flag =  n = k = 0;
		//个体目标函数值
		double[] obj1=individual1.getObj();
		double[] obj2=individual2.getObj();
		for (int i = 0; i < obj1.length; i++) {
			if (obj1[i] < obj2[i]) {
				n++;
			} else if (obj1[i] > obj2[i]) {
				k++;
			}
		}
		if (k == 0 && n > 0) {
			flag = 1;
		}
		if (n == 0 && k > 0) {
			flag = 2;
		}
		return flag;
	  }


}
