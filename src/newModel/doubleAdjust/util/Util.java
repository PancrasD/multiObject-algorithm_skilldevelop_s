package newModel.doubleAdjust.util;

import java.util.HashMap;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;

public class Util {
    /*
       * 保存已经探索过的空间
     */
	public static void update(HashMap<Integer, Boolean> hashCode, Population p) {
		Individual[] indivs=p.getPopulation();
		for(int i=0;i<indivs.length;i++) {
			if(!hashCode.containsKey(indivs[i].getHashCode())) {
				hashCode.put(indivs[i].getHashCode(), true);
			}else {
				System.out.println("has");
			}
		}
		
	}

}
