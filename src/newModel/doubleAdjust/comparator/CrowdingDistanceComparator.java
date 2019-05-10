package newModel.doubleAdjust.comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;

public class CrowdingDistanceComparator implements DominanceComparator{
	
	@Override
	public int compare(Individual individual1, Individual individua2) {
		int flag = 0;
		if (individual1.getCrowDistance() > individua2.getCrowDistance()) {
			flag = 1;
		}
		if (individual1.getCrowDistance() > individua2.getCrowDistance()) {
			flag = -1;
		}
		return flag;
	}
	
	/**
	 * 根据指定的目标函数值大小，按升序排列各解
	 * 
	 * @param index_objList
	 *            个体在全局种群中的索引值集合
	 * @param FP
	 *            个体组成的局部种群集合
	 * @return
	 */
	public  List<Integer>  sortBycrowd(Population FP) {
		List<Integer> indexlist = new ArrayList<>();
		Map<Integer, Double> crowMap = new HashMap<>();
		for (int i = 0; i< FP.size(); i++){
			crowMap.put(i, FP.getPopulation()[i].getCrowDistance());
		}
		List<Map.Entry<Integer, Double>> crowSort = new ArrayList<>(crowMap.entrySet());
	   //根据拥挤度进行排序 
		Collections.sort(crowSort, new Comparator<Map.Entry<Integer, Double>>() {

			@Override
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				int flag = 0;
				if (o2.getValue() > o1.getValue()) {
					flag = 1;
				}
				if (o1.getValue() > o2.getValue()) {
					flag = -1;
				}
				return flag;
			}

		});
		//将排好序的个体索引号加入到索引列表中，此索引号是局部种群中的索引号
		for (int i = 0; i <crowSort.size();i++){
			indexlist.add(crowSort.get(i).getKey());
			}
		return indexlist;
	}


}
