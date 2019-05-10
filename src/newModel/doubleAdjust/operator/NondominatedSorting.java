package newModel.doubleAdjust.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.comparator.CrowdingDistanceComparator;
import newModel.doubleAdjust.comparator.DominanceComparator;
import newModel.doubleAdjust.comparator.ParetoObjectiveComparator;

public class NondominatedSorting {
	
	/**
	 * The dominance comparator.
	 */
	protected final DominanceComparator comparator;
	private boolean isComputeCrowdistance;
	/**
	 * Constructs a fast non-dominated sorting operator using Pareto dominance.
	 * @param isComputeCrowdistance 是否进行拥挤度的计算
	 */
	public NondominatedSorting(boolean isComputeCrowdistance) {
		this(new ParetoObjectiveComparator(),isComputeCrowdistance);
	}

	/**
	 * Constructs a non-dominated sorting operator using the specified
	 * dominance comparator.
	 * 
	 * @param comparator the dominance comparator
	 */
	public NondominatedSorting(DominanceComparator comparator,boolean isComputeCrowdistance) {
		super();
		this.comparator = comparator;
	}
    public List<List<Integer>> evaluate(Population population) {
    	
	    int populationSize = population.size();
	    Case project=population.getProject();
		Individual[] individuals = population.getPopulation();

		List<List<Integer>> indivIndexRank = new ArrayList<List<Integer>>(); // 按非支配等级排序后，各个体在种群中对应的序列号的集合
		// 每个解都可以分成两个实体部分:1、支配该解的其他解的数量np;2、被该解支配的解集合Sp
		List<List<Integer>> spList = new ArrayList<List<Integer>>();// 存储的是个体在种群中的序列号
		for (int i = 0; i < populationSize; i++) {
			spList.add(new ArrayList<Integer>());
		}
		int[] np = new int[populationSize];
		for (int i = 0; i < populationSize; i++) {
			for (int j = i+1; j < populationSize; j++) {

				int flag=this.comparator.compare(individuals[i], individuals[j]);
				if (flag == 1) { // 前者支配后者
					spList.get(i).add(j); // 将个体j加入个体i的支配个体队列
					np[j]++;  // 支配个体j的个体数+1
				}
				if (flag == 2) { // 后者支配前者
					spList.get(j).add(i);
					np[i]++;
				}
			}
		}
		// 定义一个集合，用来存储前面已经排好等级的个体在种群的序列号
		int num = 0;
		int Rank =0;
		while (num < populationSize) {
			List<Integer> FRank = new ArrayList<Integer>(); // FRank是种群中，非支配的不同等级,如F1，F2...
			
			for (int i = 0; i < populationSize; i++) {
				if (np[i] == 0) {
					FRank.add(i); //将所有没有被任何其他个体支配的个体加入到层级
					individuals[i].setNon_dominatedRank(Rank);
					np[i] = -1; //标记个体已处理
					num ++;  //已处理的个体数量计数，当已处理个体个数达到种群人数上线即可终止处理
				}
			}
			//被分层的个体所支配的个体的被支配个体数量减1
			Population FP = new Population(FRank.size(),project);
			for (int i = 0; i < FRank.size(); i++) {
				//被分层的个体所支配的个体的被支配个体数量减1
				for (int j = 0; j < spList.get(FRank.get(i)).size(); j++) {
					np[spList.get(FRank.get(i)).get(j)]--;
				}
				//产生当前层的种群
				FP.setIndividual(i, individuals[FRank.get(i)]);
			}
			if(!isComputeCrowdistance) {
				indivIndexRank.add(FRank);
				Rank ++;
				continue;
			}
			setCrowdingDistance(FP);//为当前种群中的个体计算并设置拥挤度值。
			
			CrowdingDistanceComparator cdc=new CrowdingDistanceComparator();
			List<Integer> ind = cdc.sortBycrowd(FP);
			
			List<Integer> FRank2 = new ArrayList<Integer>();  //按拥挤度排序后的个体索引集
			for (int i = 0; i <FP.size();i++){
				FRank2.add(FRank.get(ind.get(i)));
			}
			//对当前层的个体按拥挤度排序
			indivIndexRank.add(FRank2);
			Rank ++;
		}
		return indivIndexRank;
    }
	public static void setCrowdingDistance(Population front){
		//该支配层个体的目标函数集合
		Map<Integer,double[]> indexAndObj=new HashMap<>();
		
		for(int k=0;k<front.size();k++){
			indexAndObj.put(k, front.getPopulation()[k].getObj());
		}
		Map<Integer,Double> crowMap=crowdingCompute(indexAndObj);
		for(Integer index:crowMap.keySet()){
			Individual individual=front.getPopulation()[index];
			individual.setCrowDistance(crowMap.get(index));
		}
	}
	/**
	 * 计算pareto前沿中每个解的拥挤度，每个pareto前沿的第一个和最后一个解的拥挤度都为无穷大，便于计算，用1000表示
	 * 
	 * @param indexAndObj
	 *            pareto前沿的个体在种群的索引值与目标函数的Map集合
	 * @return crowMap 前沿的个体在种群的索引值和个体拥挤度数组的Map集合
	 */
	public static Map<Integer, Double> crowdingCompute(Map<Integer, double[]> indexAndObj) {
		Map<Integer, Double> crowMap = new HashMap<>();

		double[] crowding = new double[indexAndObj.size()]; // 拥挤度距离数组
		List<Map.Entry<Integer, double[]>> index_objList = new ArrayList<>(indexAndObj.entrySet());

		int L = index_objList.size();
		sortByObj(index_objList, 0);//按工期升序排列
		crowding[0] = 1000;
		crowding[L - 1] = 1000;
		for (int i = 0; i < index_objList.get(0).getValue().length; i++) { // i表示第几个目标函数
			for (int j = 1; j < L - 1; j++) {
				crowding[j] = crowding[j]
						+ (index_objList.get(j + 1).getValue()[i] - index_objList.get(j - 1).getValue()[i])
								/ (index_objList.get(L-1).getValue()[i] - index_objList.get(0).getValue()[i]);
			}
		}
		for (int i = 0; i < index_objList.size(); i++) {
			crowMap.put(index_objList.get(i).getKey(), crowding[i]);
			
		}
		return crowMap;
	}
	/**
	 * 根据指定的目标函数值大小，按升序排列各解
	 * 
	 * @param index_objList
	 *            个体在种群中的索引值和个体目标函数数组的List集合
	 * @param m
	 *            指定比较个体目标函数数组的第几个函数
	 * @return
	 */
	public static List<Map.Entry<Integer, double[]>> sortByObj(List<Map.Entry<Integer, double[]>> index_objList,
			int m) {
		// 排序
		Collections.sort(index_objList, new Comparator<Map.Entry<Integer, double[]>>() {

			@Override
			public int compare(Entry<Integer, double[]> o1, Entry<Integer, double[]> o2) {
				int flag = 0;
				if (o1.getValue()[m] > o2.getValue()[m]) {
					flag = 1;
				}
				if (o1.getValue()[m] < o2.getValue()[m]) {
					flag = -1;
				}
				return flag;
			}

		});
		return index_objList;
	}
	
	
}
