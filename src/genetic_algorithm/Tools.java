package genetic_algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ����һ�������࣬��Ҫ������: 1.�����paretoǰ����ÿ�����ӵ���� 2.��֧������
 * 
 * @author �ܿ� 2017��3��28��
 *
 */
public class Tools {

	/**
	 * ����Ⱥ���з�֧�����򣬽���ֳɲ�ͬ�ķ�֧�伶 �˴�Ϊ�˱��ڴ����ø�������Ⱥ�е����кŽ���һϵ�м���
	 * ͬʱ��ÿһ�ȼ��ĸ��壬�������ڸõȼ��е�ӵ����ֵ��
	 * @param population
	 *            ��Ⱥ
	 * @return ������Ⱥ�ֳɲ�ͬ��֧��ȼ���ÿ�������ڶ�Ӧ��Ⱥ�����кż���
	 */
	
	public static List<List<Integer>> setRankAndCrowD (Population population,Case project) {
		int populationSize = population.size();
		Individual[] individuals = population.getPopulation();

		List<List<Integer>> indivIndexRank = new ArrayList<List<Integer>>(); // ����֧��ȼ�����󣬸���������Ⱥ�ж�Ӧ�����кŵļ���
		// ÿ���ⶼ���Էֳ�����ʵ�岿��:1��֧��ý�������������np;2�����ý�֧��Ľ⼯��Sp
		List<List<Integer>> spList = new ArrayList<List<Integer>>();// �洢���Ǹ�������Ⱥ�е����к�
		for (int i = 0; i < populationSize; i++) {
			spList.add(new ArrayList<Integer>());
		}
		int[] np = new int[populationSize];
		for (int i = 0; i < populationSize; i++) {
			for (int j = i+1; j < populationSize; j++) {

				int flag=Dominated(individuals[i], individuals[j],project);
				if (flag == 1) { // ǰ��֧�����
					spList.get(i).add(j); // ������j�������i��֧��������
					np[j]++;  // ֧�����j�ĸ�����+1
				}
				if (flag == 2) { // ����֧��ǰ��
					spList.get(j).add(i);
					np[i]++;
				}
			}
		}
		// ����һ�����ϣ������洢ǰ���Ѿ��źõȼ��ĸ�������Ⱥ�����к�
		int num = 0;
		int Rank =0;
		while (num < populationSize) {
			List<Integer> FRank = new ArrayList<Integer>(); // FRank����Ⱥ�У���֧��Ĳ�ͬ�ȼ�,��F1��F2...
			List<Integer> FRank2 = new ArrayList<Integer>();  //��ӵ���������ĸ���������
			for (int i = 0; i < populationSize; i++) {
				if (np[i] == 0) {
					FRank.add(i); //������û�б��κ���������֧��ĸ�����뵽�㼶
					individuals[i].setNon_dominatedRank(Rank);
					np[i] = -1; //��Ǹ����Ѵ���
					num ++;  //�Ѵ���ĸ����������������Ѵ����������ﵽ��Ⱥ�������߼�����ֹ����
				}
			}
			//���ֲ�ĸ�����֧��ĸ���ı�֧�����������1
			Population FP = new Population(FRank.size(),project);
			for (int i = 0; i < FRank.size(); i++) {
				//���ֲ�ĸ�����֧��ĸ���ı�֧�����������1
				for (int j = 0; j < spList.get(FRank.get(i)).size(); j++) {
					np[spList.get(FRank.get(i)).get(j)]--;
				}
				//������ǰ�����Ⱥ
				FP.setIndividual(i, individuals[FRank.get(i)]);
			}
			
			setcorwd(FP,project);//Ϊ��ǰ��Ⱥ�еĸ�����㲢����ӵ����ֵ��
			
			List<Integer> ind = sortBycrowd(FP);
			for (int i = 0; i <FP.size();i++){
				FRank2.add(FRank.get(ind.get(i)));
			}
			//�Ե�ǰ��ĸ��尴ӵ��������
			indivIndexRank.add(FRank2);
			Rank ++;
			
		}
		return indivIndexRank;
	}

	
	public static void setcorwd(Population fp,Case project){
		//��֧�������Ŀ�꺯������
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
	
	
	

	/**
	 * ����Ⱥ���з�֧�����򣬽���ֳɲ�ͬ�ķ�֧�伶 �˴�Ϊ�˱��ڴ����ø�������Ⱥ�е����кŽ���һϵ�м���
	 * 
	 * @param population
	 *            ��Ⱥ
	 *        level  �����֧����������㼶��0���������в㣬1�������1�㡣
	 * @return ������Ⱥ�ֳɲ�ͬ��֧��ȼ���ÿ�������ڶ�Ӧ��Ⱥ�����кż���
	 */
	
		public static List<List<Integer>> non_Dominated_Sort(Population population,int level,Case project) {
		int populationSize = population.size();
		Individual[] individuals = population.getPopulation();

		List<List<Integer>> indivIndexRank = new ArrayList<List<Integer>>(); // ����֧��ȼ�����󣬸���������Ⱥ�ж�Ӧ�����кŵļ���
		// ÿ���ⶼ���Էֳ�����ʵ�岿��:1��֧��ý�������������np;2�����ý�֧��Ľ⼯��Sp
		List<List<Integer>> spList = new ArrayList<List<Integer>>();// �洢���Ǹ�������Ⱥ�е����к�
		for (int i = 0; i < populationSize; i++) {
			spList.add(new ArrayList<Integer>());
		}
		int[] np = new int[populationSize];
		for (int i = 0; i < populationSize; i++) {
			for (int j = i+1; j < populationSize; j++) {

				int flag=Dominated(individuals[i], individuals[j],project);
				if (flag == 1) { // ǰ��֧�����
					spList.get(i).add(j); // ������j�������i��֧��������
					np[j]++;  // ֧�����j�ĸ�����+1
				}
				if (flag == 2) { // ����֧��ǰ��
					spList.get(j).add(i);
					np[i]++;
				}
			}
		}
		// ����һ�����ϣ������洢ǰ���Ѿ��źõȼ��ĸ�������Ⱥ�����к�
		int num = 0;
		int Rank =0;
		while (num < populationSize) {
			List<Integer> FRank = new ArrayList<Integer>(); // FRank����Ⱥ�У���֧��Ĳ�ͬ�ȼ�,��F1��F2...
			for (int i = 0; i < populationSize; i++) {
				if (np[i] == 0) {
					FRank.add(i); //������û�б��κ���������֧��ĸ�����뵽�㼶
					individuals[i].setNon_dominatedRank(Rank);
					np[i] = -1; //��Ǹ����Ѵ���
					num ++;  //�Ѵ���ĸ����������������Ѵ����������ﵽ��Ⱥ�������߼�����ֹ����
				}
			}
			//���ֲ�ĸ�����֧��ĸ���ı�֧�����������1
			for (int i = 0; i < FRank.size(); i++) {
				for (int j = 0; j < spList.get(FRank.get(i)).size(); j++) {
					np[spList.get(FRank.get(i)).get(j)]--;
				}
			}
			
			indivIndexRank.add(FRank);
			
			Rank ++;
			if ((level != 0)&&(Rank >= level)){
				break;
			}
			
		}
		return indivIndexRank;
	}


	
	/**
	 * ��������֮���Ƿ����֧���ϵ
	 * flag=1����individual1֧��individual2��flag=2��ʾindividual2֧��individual1��flag=0��ʾ����֮��û��֧���ϵ
	 * 
	 * @param individual1
	 * @param individual2
	 * @return
	 */
	public static int Dominated(Individual individual1, Individual individual2,Case project) {
		int flag, n, k;
		flag =  n = k = 0;

		//����Ŀ�꺯��ֵ
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
	 * ����paretoǰ����ÿ�����ӵ���ȣ�ÿ��paretoǰ�صĵ�һ�������һ�����ӵ���ȶ�Ϊ����󣬱��ڼ��㣬��1000��ʾ
	 * 
	 * @param indexAndObj
	 *            paretoǰ�صĸ�������Ⱥ������ֵ��Ŀ�꺯����Map����
	 * @return pareto ǰ�صĸ�������Ⱥ������ֵ�͸���ӵ���������Map����
	 */
	public static Map<Integer, Double> crowdingCompute(Map<Integer, double[]> indexAndObj) {
		Map<Integer, Double> crowMap = new HashMap<>();

		double[] crowding = new double[indexAndObj.size()]; // ӵ���Ⱦ�������
		List<Map.Entry<Integer, double[]>> index_objList = new ArrayList<>(indexAndObj.entrySet());

		int L = index_objList.size();
		sortByObj(index_objList, 0);//��������������
		crowding[0] = 1000;
		crowding[L - 1] = 1000;
		for (int i = 0; i < index_objList.get(0).getValue().length; i++) { // i��ʾ�ڼ���Ŀ�꺯��
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

	
	/**
	 * ����ָ����Ŀ�꺯��ֵ��С�����������и���
	 * 
	 * @param crowMap
	 *            ���弯��
	 * @return    �ź���ĸ��弯��
	 */
	public static List<Map.Entry<Integer, Double>>  sortBycrowd(Map<Integer, Double> crowMap) {
		
	List<Map.Entry<Integer, Double>> crowSort = new ArrayList<>(crowMap.entrySet());
	//����ӵ���Ƚ�������
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
	 * ����ָ����Ŀ�꺯��ֵ��С�����������и���
	 * 
	 * @param index_objList
	 *            ������ȫ����Ⱥ�е�����ֵ����
	 * @param FP
	 *            ������ɵľֲ���Ⱥ����
	 * @return
	 */
	public static List<Integer>  sortBycrowd(Population FP) {
		List<Integer> indexlist = new ArrayList<>();
		Map<Integer, Double> crowMap = new HashMap<>();
		for (int i = 0; i< FP.size(); i++){
			crowMap.put(i, FP.getPopulation()[i].getCrowDistance());
		}
		List<Map.Entry<Integer, Double>> crowSort = new ArrayList<>(crowMap.entrySet());
	//����ӵ���Ƚ�������
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
		//���ź���ĸ��������ż��뵽�����б��У����������Ǿֲ���Ⱥ�е�������
		for (int i = 0; i <crowSort.size();i++){
			indexlist.add(crowSort.get(i).getKey());
			}
		return indexlist;
	}
	
	
	/**
	 * ����ָ����Ŀ�꺯��ֵ��С�����������и���
	 * 
	 * @param index_objList
	 *            ��������Ⱥ�е�����ֵ�͸���Ŀ�꺯�������List����
	 * @param m
	 *            ָ���Ƚϸ���Ŀ�꺯������ĵڼ�������
	 * @return
	 */
	public static List<Map.Entry<Integer, double[]>> sortByObj(List<Map.Entry<Integer, double[]>> index_objList,
			int m) {
		// List<Map.Entry<Integer, double[]>> index_objList = new ArrayList<>();
		// ����
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
	 * ѡ����Ⱥ�и���ָ��Ŀ�꺯�������ֵ
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
	 * ѡ����Ⱥ�и���ָ��Ŀ�꺯�������ֵ
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
	 * ѡ����Ⱥ�и���ָ��Ŀ�꺯�������ֵ
	 * 
	 * @param index_objList
	 * @param m
	 * @return
	 */
	public static Population getbestsolution(Population p,Case project) {
		Population solutions;
		// P��Ⱥ���з�֧������
		List<List<Integer>> indivIndexRank = non_Dominated_Sort(p,1, project);
		if (indivIndexRank.get(0).size() != 0) {
			// �㷨��õ����Ž⼯
			solutions = new Population(indivIndexRank.get(0).size(),project);
			for (int i = 0; i < indivIndexRank.get(0).size(); i++) {
				solutions.setIndividual(i, p.getPopulation()[indivIndexRank.get(0).get(i)]);
			}
			solutions = sortByObj(solutions, 0);
			
		}else{
			solutions = new Population(0,project);
		}
		return solutions;	
	}
	
	
	
	/**
	 * ѡ����Ⱥ�и���ָ��Ŀ�꺯�������ֵ
	 * 
	 * @param index_objList
	 * @param m
	 * @return
	 */
	public static void printsolutions(Population solutions,long startTime) {
		if (solutions.getPopulationsize()>0){
			   
			Individual[] bestIndividuals = solutions.getPopulation();

			// �洢�����Ŀ�꺯��
			List<double[]> betterObjs = new ArrayList<>();

			// �������ÿ������,����ʽ�����Ⱦɫ��ṹ�Լ�Ŀ�꺯��
			for (int i = 0; i < bestIndividuals.length; i++) {
				//System.out.print("Ⱦɫ��ṹΪ:\n" + bestIndividuals[i].getChromosome().get(0).toString());


				double[] obj = bestIndividuals[i].getObj();

				betterObjs.add(obj);
				System.out.println("��Ŀ����Ϊ��" + obj[0] + "����Ŀ�ɱ�Ϊ��" + obj[1]);
			}

			// �������۱�׼��MID��SM��DM��SNS
			// ����100_10_65_15����
			double best_f1 = 0;
	     	double best_f2 = 0;

			// ����200_20_145_15����
			 //double best_f1=198;
			 //double best_f2=143497;

			double MID = calMeanIdealDistance(betterObjs, best_f1, best_f2);
			double DM = calDiversification(betterObjs);
			//double SNS = calSNS(betterObjs, MID, best_f1, best_f2);
			//double SM = calSpace_Metric(betterObjs);
			// // �������
			//System.out.println("MID=" + MID);
			//System.out.println("DM=" + DM);
			//System.out.println("SNS=" + SNS);
			//System.out.println("SM=" + SM);			
			
			

			// �������

			double MOCV = MID / DM;
			System.out.println("ָ��MOCV:"+MOCV);
			/*System.out.println("ָ��MID:"+MID);
			System.out.println("ָ��DM:"+DM);*/
			//System.out.println(betterObjs.size());
			
		}
		// ���û�з�֧���
		else {
			System.out.println("���㷨�޷�������Ž�");
		}
		long endTime = System.currentTimeMillis();
		System.out.println("������ʱ��" + (endTime - startTime) / 1000 + "��");

	
	}
	
	/**
	 * ����Mean Ideal Distance
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
	 * ���� Spread of non-dominated solutions �����㷨��ý�Ķ�����
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
	 * ���� Spacing Metric �������ȷֲ�����
	 * 
	 * @param betterObjs
	 * @return
	 */
	public static double calSpace_Metric(List<double[]> betterObjs) {
		double SM = 0.0;
		// ��Ŀ��ֵ����f1��������
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
	 * ���� Diversification Metric pareto�⼯����չ��
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

		double d1 = Math.pow(min_f1 - max_f1, 2);
		double d2 = Math.pow(min_f2 - max_f2, 2);

		DM = Math.sqrt(d1 + d2);
		return DM;
	}

	/**
	 * ����Ŀ�꺯����С��������
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
	 * ѡ�����ֵ
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


	
}
