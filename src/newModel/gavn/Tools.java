package newModel.gavn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 定义一个工具类，主要功能有: 1.计算各pareto前沿中每个解的拥挤度 2.非支配排序
 * 
 * 
 *
 */
public class Tools {

	/**
	 * 对种群进行非支配排序，将解分成不同的非支配级 此处为了便于处理，用个体在种群中的序列号进行一系列计算
	 * 同时对每一等级的个体，计算其在该等级中的拥挤度值。
	 * @param population
	 *            种群
	 * @return 返回种群分成不同非支配等级后，每个个体在对应种群中序列号集合
	 */
	
	public static List<List<Integer>> setRankAndCrowD (Population population,Case project) {
		int populationSize = population.size();
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

				int flag=Dominated(individuals[i], individuals[j],project);
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
			List<Integer> FRank2 = new ArrayList<Integer>();  //按拥挤度排序后的个体索引集
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
			
			setcorwd(FP,project);//为当前种群中的个体计算并设置拥挤度值。
			
			List<Integer> ind = sortBycrowd(FP);
			for (int i = 0; i <FP.size();i++){
				FRank2.add(FRank.get(ind.get(i)));
			}
			//对当前层的个体按拥挤度排序
			indivIndexRank.add(FRank2);
			Rank ++;
			
		}
		return indivIndexRank;
	}

	
	public static void setcorwd(Population fp,Case project){
		//该支配层个体的目标函数集合
		//List<double[]> objList=fp.populationObjCompute(fp.getPopulation(),project);
		Map<Integer,double[]> indexAndObj=new HashMap<>();
		for(int k=0;k<fp.size();k++){
			indexAndObj.put(k, fp.getPopulation()[k].getObj());
		}
		Map<Integer,Double> crowMap=crowdingCompute(indexAndObj);
		for(Integer index:crowMap.keySet()){
			Individual individual2=fp.getPopulation()[index];
			individual2.setCrowDistance(crowMap.get(index));
		}
	}
	
	public static void setHyperVolum(Population fp,Case project){
		Map<Integer,double[]> indexAndObj=new HashMap<>();
		for(int k=0;k<fp.size();k++){
			indexAndObj.put(k, fp.getPopulation()[k].getObj());
		}
		Map<Integer,Double> hypervolum=hyperVolumeCompute(indexAndObj);
		for(Integer index:hypervolum.keySet()){
			Individual individual2=fp.getPopulation()[index];
			individual2.setHyperVolume(hypervolum.get(index));
		}
	}
	public static List<List<Integer>> setRankAndConsAndHyperVolume (Population population,Case project) {
		int populationSize = population.size();
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

				int flag=Dominated(individuals[i], individuals[j],project);
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
		while (num < populationSize) {//可以优化 k*n->n
			List<Integer> FRank = new ArrayList<Integer>(); // FRank是种群中，非支配的不同等级,如F1，F2...
			List<Integer> FRank2 = new ArrayList<Integer>();  //按拥挤度排序后的个体索引集
			for (int i = 0; i < populationSize; i++) {
				if (np[i] == 0) {
					FRank.add(i); //将所有没有被任何其他个体支配的个体加入到层级
					individuals[i].setNon_dominatedRank(Rank);
					np[i] = -1; //标记个体已处理
					num ++;  //已处理的个体数量计数，当已处理个体个数达到种群人数上线即可终止处理
				}
			}
			/***似乎可以去掉***/
			//按被支配个体数量及超体积排序
			Population FP = new Population(FRank.size(),project);
			for (int i = 0; i < FRank.size(); i++) {
				//产生当前层的种群
				FP.setIndividual(i, individuals[FRank.get(i)]);
			}
			setHyperVolum(FP,project);
			for (int i = 0; i <FP.size();i++){
				FRank2.add(FRank.get(i));
			}
			/***似乎可以去掉***/
			//对当前层的个体按拥挤度排序
			indivIndexRank.add(FRank2);
			Rank ++;
			for (int i = 0; i < FRank.size(); i++) {
				//被分层的个体所支配的个体的被支配个体数量减1
				List<Integer> sp=spList.get(FRank.get(i));
				for (int j = 0; j < sp.size(); j++) {
					np[sp.get(j)]--;
				}
			}
			
		}
		return indivIndexRank;
	}
	

	/**
	 * 对种群进行非支配排序，将解分成不同的非支配级 此处为了便于处理，用个体在种群中的序列号进行一系列计算
	 * 
	 * @param population
	 *            种群
	 *        level  计算非支配排序的最大层级，0，计算所有层，1，计算第1层。
	 * @return 返回种群分成不同非支配等级后，每个个体在对应种群中序列号集合
	 */
	
	public static List<List<Integer>> non_Dominated_Sort(Population population,int level,Case project) {
		int populationSize = population.size();
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

				int flag=Dominated(individuals[i], individuals[j],project);
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
			for (int i = 0; i < FRank.size(); i++) {
				List<Integer> sp=spList.get(FRank.get(i));
				for (int j = 0; j < sp.size(); j++) {
					np[sp.get(j)]--;
				}
			}
			indivIndexRank.add(FRank);
			Rank ++;//Rank(0-->1)>=level=1
			if ((level != 0)&&(Rank >= level)){
				break;
			}
		}
		return indivIndexRank;
	}


	
	/**
	 * 两个个体之间是否存在支配关系
	 * flag=1代表individual1支配individual2；flag=2表示individual2支配individual1；flag=0表示两者之间没有支配关系
	 * 
	 * @param individual1
	 * @param individual2
	 * @return
	 */
	public static int Dominated(Individual individual1, Individual individual2,Case project) {
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

	/**
	 * 计算pareto前沿中每个解的拥挤度，每个pareto前沿的第一个和最后一个解的拥挤度都为无穷大，便于计算，用1000表示
	 * 
	 * @param indexAndObj
	 *            pareto前沿的个体在种群的索引值与目标函数的Map集合
	 * @return pareto 前沿的个体在种群的索引值和个体拥挤度数组的Map集合
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
//		crowMap = sortBycrowd(crowMap);

		return crowMap;
	}
	public static Map<Integer, Double> hyperVolumeCompute(Map<Integer, double[]> indexAndObj) {
		Map<Integer, Double> HyperVMap = new HashMap<>();

		double[] hpervolume = new double[indexAndObj.size()]; // 拥挤度距离数组
		for(int i=0;i<hpervolume.length;i++) {
			hpervolume[i]=1;
		}
		List<Map.Entry<Integer, double[]>> index_objList = new ArrayList<>(indexAndObj.entrySet());

		int L = index_objList.size();
		sortByObj(index_objList, 0);//按工期升序排列
		hpervolume[0] = Double.MAX_VALUE;
		hpervolume[L - 1] = Double.MAX_VALUE;
		for (int j = 1; j < L - 1; j++) {
			double x0=index_objList.get(L-1).getValue()[0];
			double y0=index_objList.get(0).getValue()[1];
			double x1=index_objList.get(j-1).getValue()[0];
			double y1=index_objList.get(j-1).getValue()[1];
			double x=index_objList.get(j).getValue()[0];
			double y=index_objList.get(j).getValue()[1];
			double x2=index_objList.get(j+1).getValue()[0];
			double y2=index_objList.get(j+1).getValue()[1];
			
			/*hpervolume[j] = hpervolume[j]*(index_objList.get(L-1).getValue()[0] - index_objList.get(j).getValue()[0])/
					(index_objList.get(L-1).getValue()[0] - index_objList.get(0).getValue()[0])
					*(index_objList.get(0).getValue()[1] - index_objList.get(j).getValue()[1])/
					(index_objList.get(0).getValue()[1] - index_objList.get(L-1).getValue()[0]);*/
			/*hpervolume[j] =0.5*(Math.abs(x*(y1-y0)+x1*(y0-y)+x0*(y-y1))+Math.abs(x*(y2-y0)+x2*(y0-y)+x0*(y-y2)));*/
			hpervolume[j]=0.5*(Math.abs(y*(x1-x0)+y1*(x0-x)+y0*(x-x1))+Math.abs(y*(x2-x0)+y2*(x0-x)+y0*(x-x2)));
			/*hpervolume[j] = hpervolume[j]*0.5*Math.abs(x1*(y2-y0)+x2*(y0-y1)+x0*(y1-y2))/(x0*y0);*/
		}
	 	/*for (int i = 0; i < index_objList.get(0).getValue().length; i++) { // i表示第几个目标函数
			for (int j = 1; j < L - 1; j++) {
				hpervolume[j] = hpervolume[j]*(index_objList.get(j + 1).getValue()[i] - index_objList.get(j - 1).getValue()[i]);
			}
		}*/
		for (int i = 0; i < index_objList.size(); i++) {
			HyperVMap.put(index_objList.get(i).getKey(), hpervolume[i]);
			
		}
//		crowMap = sortBycrowd(crowMap);

		return HyperVMap;
		
	}
	
	
	/**
	 * 根据指定的目标函数值大小，按升序排列各解
	 * 
	 * @param crowMap
	 *            个体集合
	 * @return    排好序的个体集合
	 */
	public static List<Map.Entry<Integer, Double>>  sortBycrowd(Map<Integer, Double> crowMap) {
		
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

		return crowSort;
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
	public static List<Integer>  sortBycrowd(Population FP) {
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
	//按约束个数和超体积排序  约束个数升序排 超体积降序排
	public static List<Integer>  sortByConsAndHyper(Population FP, int[] npbackup, List<Integer> fRank) {
		List<Integer> rank=new ArrayList<>(fRank);
		Map<Integer, Double> HyperVmap = new HashMap<>();
		for (int i = 0; i< FP.size(); i++){
			HyperVmap.put(fRank.get(i), FP.getPopulation()[i].getHyperVolume());
		}
		Collections.sort(rank,new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				int flag=0;
				int li=npbackup[o1];
				int l2=npbackup[o2];
			    if(npbackup[o1]>npbackup[o2]) {
			    	flag=1;
			    }else if(npbackup[o1]<npbackup[o2]) {
			    	flag=-1;
			    }else if(HyperVmap.get(o1)>HyperVmap.get(o1)){
			    	flag=-1;
			    }else if(HyperVmap.get(o1)<HyperVmap.get(o1)) {
			    	flag=1;
			    }
				return flag;
			}
			
		});
		return rank;
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
		// List<Map.Entry<Integer, double[]>> index_objList = new ArrayList<>();
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

				// return (int) (o1.getValue()[m] - o2.getValue()[m]);
			}

		});
		return index_objList;
	}
	
	
	
	public static Population sortByObj(Population p, int m) {
		Population PS = new Population(p.size(),p.getProject());
		
		Map<Integer, double[]> objs = new HashMap<>();
		for (int i = 0; i< p.size(); i++){
			objs.put(i, p.getPopulation()[i].getObj());
		}
        List<Map.Entry<Integer, double[]>> objslist = new ArrayList<>(objs.entrySet());
        objslist = sortByObj(objslist,m);
        
		for (int i =0; i<p.size();i++){
			PS.getPopulation()[i] = p.getPopulation()[objslist.get(i).getKey()] ;
		}
		return PS;
	}
	// public static List<double[]> sortByObj(List<double[]> objList, int m) {
	// objList.sort(new Comparator<double[]>() {
	// @Override
	// public int compare(double[] o1, double[] o2) {
	// return (int) (o1[m] - o2[m]);
	// }
	// });
	// return objList;
	// }
/*
		
	
	/**
	 * 选择种群中个体指定目标函数的最大值
	 * 
	 * @param index_objList
	 * @param m
	 * @return
	 */
	public static double selectMaxObj(List<Map.Entry<Integer, double[]>> index_objList, int m) {
		double max_Obj = 0.0;
		max_Obj = sortByObj(index_objList, m).get(index_objList.size() - 1).getValue()[m];
		return max_Obj;
	}

	/**
	 * 选择种群中个体指定目标函数的最大值
	 * 
	 * @param index_objList
	 * @param m
	 * @return
	 */
	public static double selectMinObj(List<Map.Entry<Integer, double[]>> index_objList, int m) {
		double min_Obj = 0.0;
		min_Obj = sortByObj(index_objList, m).get(0).getValue()[m];
		return min_Obj;
	}

	/**
	 * 选择种群中个体指定目标函数的最大值
	 * 
	 * @param index_objList
	 * @param m
	 * @return
	 */
	public static Population getbestsolution(Population p,int le,Case project) {
		Population[] pop=new Population[le];
		Population solutions = null;
		// P种群进行非支配排序
		List<List<Integer>> indivIndexRank = non_Dominated_Sort(p,le, project);
		 
		/*for(int m=0;m<indivIndexRank.size();m++) {*/
			List<Integer> rank0=indivIndexRank.get(0);
		if (rank0.size() != 0) {
			// 算法求得的最优解集
			solutions = new Population(rank0.size(),project);
			for (int i = 0; i < rank0.size(); i++) {
				solutions.setIndividual(i, p.getPopulation()[rank0.get(i)]);
			}
			solutions = sortByObj(solutions, 0);
			
		}else{
			solutions = new Population(0,project);
		}
	/*	pop[m]=solutions;
		}
		Population mergedPopulation =solutions.merged(pop);
		return mergedPopulation;	*/
		return solutions;
	}
	
	
	
	/**
	 * 选择种群中个体指定目标函数的最大值
	 * @param countResult 
	 * 
	 * @param index_objList
	 * @param m
	 * @return
	 */
	public static void printsolutions(Population solutions,long startTime, List<List<Double>> countResult) {
		long endTime = System.currentTimeMillis();
		if (solutions.getPopulationsize()>0){
			
			Individual[] bestIndividuals = solutions.getPopulation();
			// 存储个体的目标函数
			List<double[]> betterObjs = new ArrayList<>();
			// 遍历输出每个个体,并格式化输出染色体结构以及目标函数
			for (int i = 0; i < bestIndividuals.length; i++) {
				double[] obj = bestIndividuals[i].getObj();
				betterObjs.add(obj);
				System.out.println("项目工期为:" + obj[0] + ":项目成本为:" + obj[1]);
			}
			 //计算反转超体积  反转的工期为所有工期之和  反转的成本为工期*最大的薪水
			Case project=solutions.getPopulation()[0].getProject();
			double MaxDuration=project.getBorderDuration();
			double MaxCost=project.getBorderCost();
			//反转   归一   计算超体积
			List<double[]> inversObj=new ArrayList<>();
			double hyperVolume=0;
			for(int i=0;i<betterObjs.size();i++) {
				double dura=(MaxDuration-betterObjs.get(i)[0])/MaxDuration;
				double cost=(MaxCost-betterObjs.get(i)[1])/MaxCost;
				inversObj.add(new double[]{dura,cost});
				if(i==0) {
					hyperVolume+=inversObj.get(i)[0]*inversObj.get(i)[1];
				}else {
					hyperVolume+=(inversObj.get(i)[1]-inversObj.get(i-1)[1])*inversObj.get(i)[0];
				}
			}
			List<Double> result=new ArrayList<>();
			result.add(hyperVolume);
			result.add((double) ((endTime-startTime)/1000));
			countResult.add(result);
			System.out.println("超体积:"+hyperVolume);
			if(countResult.size()==NSGAV_II.RunTime) {
				double countHyper=0;
				double countTime=0;
				int maxHyperVolumIndex=0;
				double maxHyperVolum=countResult.get(maxHyperVolumIndex).get(0);
				for(int i=0;i<countResult.size();i++) {
					countHyper+=countResult.get(i).get(0);
					countTime+=countResult.get(i).get(1);
					if(maxHyperVolum<countResult.get(i).get(0)) {
						maxHyperVolum=countResult.get(i).get(0);
						maxHyperVolumIndex=i;
					}
				}
				System.out.println("平均超体积:"+countHyper/countResult.size());
				System.out.println("平均时间:"+countTime/countResult.size());
				System.out.println(Arrays.toString(countResult.toArray()));
				System.out.println("最大超体积位置:"+(maxHyperVolumIndex+1)+":最大超体积"+maxHyperVolum);
			}
			//输出资源技能水平
			outputSkillEvalute(solutions);
			
		}
		// 如果没有非支配解
		else {
			System.out.println("该算法无法求得最优解");
		}
		System.out.println("共计用时：" + (endTime - startTime) / 1000 + "秒");

	
	}
	/*
	 * 输出技能的变化对比
	 */
	private static void outputSkillEvalute(Population solutions) {
		//输出一个
		Individual indiv=solutions.getPopulation()[0];
		Case project=indiv.getProject();
		List<Resource> r1List=project.getResources();
		List<IResource> r2List=indiv.getResourceslist();
		System.out.println("起始情况");
		for(int i=0;i<r1List.size();i++) {
			Resource r1=r1List.get(i);
			System.out.print(r1.getResourceID()+"  ");
			System.out.println(r1.getSkils());
		}
		System.out.println("执行之后");
		for(int i=0;i<r2List.size();i++) {
			IResource r2=r2List.get(i);
			System.out.print(r2.getResourceID()+"  ");
			HashMap<String,Double> skillsInfo=r2.getSkillsInfo();
			StringBuffer sb=new  StringBuffer();
			for(String skill:r2.getSkils().trim().split(" ")){
				String[] skillInfo = skill.trim().split(":");
				sb.append(skillInfo[0]);
				sb.append(":");
				sb.append((int)(skillsInfo.get(skillInfo[0])*1.0)+" ");
			}
			System.out.println(sb);
		}
		
	}


	private static void outPutResource(Individual individual) {
		for(int i=0;i<individual.getProject().getResources().size();i++) {
			List<Resource> res1=individual.getProject().getResources();
			List<IResource> res2=individual.getResourceslist();
			for(int k=0;k<res2.size();k++) {
			HashMap<String,Double> skillsInfo=res2.get(k).getSkillsInfo();
			
		}
		}
	}


	/**
	 * 计算Mean Ideal Distance
	 * 
	 * @param betterObjs
	 * @return
	 */
	public static double calMeanIdealDistance(List<double[]> betterObjs, double best_f1, double best_f2) {
		double MID = 0.0;
		sort(betterObjs, 0);
		double max_f1 = betterObjs.get(betterObjs.size() - 1)[0];
		double min_f1 = betterObjs.get(0)[0];
		double max_f2 = betterObjs.get(0)[1];
		double min_f2 = betterObjs.get(betterObjs.size() - 1)[1];
		// double max_f1 = selectMaxObj(betterObjs, 0);
		// double min_f1 = selectMinObj(betterObjs, 0);
		// double max_f2 = selectMaxObj(betterObjs, 1);
		// double min_f2 = selectMinObj(betterObjs, 1);

		double z = 0.0;
		for (int i = 0; i < betterObjs.size(); i++) {
			double x = (betterObjs.get(i)[0] - best_f1) / (max_f1 - min_f1);
			double y = (betterObjs.get(i)[1] - best_f2) / (max_f2 - min_f2);
			z += Math.sqrt((x * x) + (y * y));
		}

		MID = z / betterObjs.size();
		return MID;
	}

	/**
	 * 计算 Spread of non-dominated solutions 衡量算法获得解的多样性
	 * 
	 * @param betterObjs
	 * @param best_f1
	 * @param best_f2
	 * @return
	 */
	public static double calSNS(List<double[]> betterObjs, double MID, double best_f1, double best_f2) {
		double SNS = 0.0;
		sort(betterObjs, 0);
		double max_f1 = betterObjs.get(betterObjs.size() - 1)[0];
		double min_f1 = betterObjs.get(0)[0];
		double max_f2 = betterObjs.get(0)[1];
		double min_f2 = betterObjs.get(betterObjs.size() - 1)[1];

		double z = 0.0;
		for (int i = 0; i < betterObjs.size(); i++) {
			double x = (betterObjs.get(i)[0] - best_f1) / (max_f1 - min_f1);
			double y = (betterObjs.get(i)[1] - best_f2) / (max_f2 - min_f2);
			double C = Math.sqrt((x * x) + (y * y));
			// double MID=calMeanIdealDistance(betterObjs, best_f1, best_f2);
			z += Math.pow((MID - C), 2);
		}
		SNS = Math.sqrt(z / (betterObjs.size() - 1));
		return SNS;
	}

	/**
	 * 计算 Spacing Metric 衡量均匀分布特性
	 * 
	 * @param betterObjs
	 * @return
	 */
	public static double calSpace_Metric(List<double[]> betterObjs) {
		double SM = 0.0;
		// 将目标值按照f1升序排序
		sort(betterObjs, 0);
		double sum = 0;
		for (int i = 0; i < betterObjs.size() - 1; i++) {
			sum += Math.sqrt(Math.pow(betterObjs.get(i + 1)[0] - betterObjs.get(i)[0], 2)
					+ Math.pow(betterObjs.get(i + 1)[1] - betterObjs.get(i)[1], 2));
		}
		double AD = sum / (betterObjs.size() - 1);
		double z = 0.0;
		for (int i = 0; i < betterObjs.size() - 1; i++) {
			double d = Math.sqrt(Math.pow(betterObjs.get(i + 1)[0] - betterObjs.get(i)[0], 2)
					+ Math.pow(betterObjs.get(i + 1)[1] - betterObjs.get(i)[1], 2));
			z += Math.abs(AD - d);
		}
		SM = z / ((betterObjs.size() - 1) * AD);
		return SM;
	}

	/**
	 * 计算 Diversification Metric pareto解集的扩展性
	 * 
	 * @param betterObjs
	 * @return
	 */
	public static double calDiversification(List<double[]> betterObjs) {
		double DM = 0.0;

		sort(betterObjs, 0);
		double max_f1 = betterObjs.get(betterObjs.size() - 1)[0];
		double min_f1 = betterObjs.get(0)[0];
		double max_f2 = betterObjs.get(0)[1];
		double min_f2 = betterObjs.get(betterObjs.size() - 1)[1];

		double d1 = Math.pow((min_f1 - max_f1)/max_f1, 2);
		double d2 = Math.pow((min_f2 - max_f2)/max_f2, 2);

		DM = Math.sqrt(d1 + d2);
		return DM;
	}

	/**
	 * 根据目标函数大小进行排序
	 * 
	 * @param objs
	 * @param m
	 */
	public static void sort(List<double[]> objs, int m) {
		objs.sort(new Comparator<double[]>() {
			@Override
			public int compare(double[] o1, double[] o2) {
				int flag = 0;
				if (o1[m] > o2[m]) {
					flag = 1;
				}
				if (o1[m] < o2[m]) {
					flag = -1;
				}
				return flag;
			}

		});
	}

	/**
	 * 选择最大值
	 * 
	 * @param objs
	 * @param m
	 * @return
	 */
	public static double selectMaxObj1(List<double[]> objs, int m) {
		double max = 0.0;
		sort(objs, m);
		max = objs.get(objs.size() - 1)[m];
		return max;
	}


	public static Population selectPopulation(Population p1, Population p2) {
		Case project=p1.getProject();
		int popsize=p1.getPopulationsize();
		Population p=new Population(popsize,project);
		Population pop=new Population(2*popsize,project);
		Individual[] indivs=new Individual[2*popsize];
		for(int i=0;i<p1.getPopulationsize();i++) {
			indivs[i]=p1.getPopulation()[i];
		}
		for(int i=0;i<p2.getPopulationsize();i++) {
			indivs[p1.getPopulationsize()+i]=p2.getPopulation()[i];
		}
		pop.setPopulation(indivs);
		List<List<Integer>> indexs = Tools.setRankAndCrowD(pop, project);
		List<Integer> size=new ArrayList<>();
		for(int i=0;i<indexs.size();i++) {
			List<Integer> index=indexs.get(i);
			for(int k=0;k<index.size();k++) {
				size.add(index.get(k));
				if(size.size()==popsize) {
					break;
				}
			}
			if(size.size()==popsize) {
				break;
			}
		}
		for(int i = 0; i<popsize; i++){
			Individual indiv =indivs[size.get(i)];
			p.setIndividual(i, indiv);
		}
		
		return p;
	}


	public static Population getbestsolution(Population p, Case project,int... ranks) {
		Population solutions ;
		Individual indiv[]=p.getPopulation();
		List<Integer> indexs=new ArrayList<>();
		for(int i=0;i<ranks.length;i++) {
			int rank=ranks[i];
			for(int k=0;k<indiv.length;k++) {
				if(indiv[k].getNon_dominatedRank()==rank) {
					indexs.add(k);
				}
			}
		}
		if (indexs.size() != 0) {
			// 算法求得的最优解集
			solutions = new Population(indexs.size(),project);
			for (int i = 0; i < indexs.size(); i++) {
				solutions.setIndividual(i, p.getPopulation()[indexs.get(i)]);
			}
			solutions = sortByObj(solutions, 0);
			
		}else{
			solutions = new Population(0,project);
		}
		return solutions;
	}


	
}
