package com.newAlgorithem.gavn.doubleAdjust;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





public class Population {

	private int populationsize;
	private Individual[] population;
	private List<double[]> populationObj;
	private Case project;
    private double[] MaxObj;
	public Population(int populationSize, Case project) {
		this.populationsize = populationSize;
		this.project = project;
		this.population = new Individual[populationSize];
	}
	public Population(Individual[] population,Case project) {
		this.populationsize=population.length;
		this.population=population;
		this.project=project;
		this.populationObj = populationObjCompute(population);
	}
	public Population(int populationSize, Case project,boolean initial) {
		this.populationsize = populationSize;
		this.project = project;
		this.population = new Individual[populationSize];
		if (initial) {
			for (int i = 0; i < populationSize; i++) {
				Individual individual = new Individual(project,initial);
				this.population[i] = individual;
			}
			this.populationObj = populationObjCompute(this.population);
		}
	}

	public Population(int populationSize2, Case project2, boolean b, int i) {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 计算给定种群的个体目标函数
	 * 得到个体目标函数的集合
	 * @param population
	 *            种群
	 * @return populationObj 种群中个体目标函数集合
	 */
	public List<double[]> populationObjCompute(Individual[] population) {
		List<double[]> populationObj = new ArrayList<>();
		
		for (Individual individual : population) {
			populationObj.add(individual.getObj());
		}
		return populationObj;
	}
	
	// 获取种群的个体成员
	public Individual[] getPopulation() {
		return this.population;
	}

	// 获取种群的目标函数集合
	public List<double[]> getPopulationObj() {
		return this.populationObj;
	}

	// 获取种群大小
	public int size() {
		return this.populationsize;
	}

	// 设置种群中的个体
	public Individual setIndividual(int offset, Individual individual) {
		return population[offset] = individual;
	}
		
	
	public int getPopulationsize() {
		return populationsize;
	}

	public void setPopulationsize(int populationsize) {
		this.populationsize = populationsize;
	}

	public Population merged(Population p1,Population p2){
		List<Individual> mergedList = new ArrayList<>();
		for (int i = 0; i < p1.size(); i++) {
			if (!mergedList.contains(p1.getPopulation()[i])) {
			  mergedList.add(p1.getPopulation()[i]);				
			}
		}
		for (int i = 0; i < p2.size(); i++) {
			if (!mergedList.contains(p2.getPopulation()[i])) {
				  mergedList.add(p2.getPopulation()[i]);				
				}			
		}
		Population mergedPopulation = new Population(mergedList.size(),project);
		for (int i =0; i <mergedList.size();i++){
			mergedPopulation.setIndividual(i, mergedList.get(i));
		}
		return mergedPopulation;
	}

	// 选择
	// 从混合种群中，选择前N个个体
	public Population slectPopulation(int num) {
		// 创建新的种群
		Population newPopulation = new Population(num,project);

		// 混合种群进行快速非支配排序
		// 得到不同非支配层，每个非支配层由多个整数组成，该整数为个体在混合种群的索引
		List<List<Integer>> indivIndexRank = Tools.non_Dominated_Sort(this,0, project);
		// 个体数量
		int indivSum = 0;
		int total = 0;
		for (int j = 0; j < indivIndexRank.size(); j++) {
			if (indivSum + indivIndexRank.get(j).size() <= num){
				for (int k = 0; k < indivIndexRank.get(j).size(); k++) {
					int indivIndex = indivIndexRank.get(j).get(k);
					Individual individual = this.getPopulation()[indivIndex];
					newPopulation.setIndividual(total, individual);
					total++;
				}
			}else{
				Map<Integer, double[]> indexAndObj = new HashMap<>();
				for (int k = 0; k < indivIndexRank.get(j).size(); k++) {
					int indivIndex = indivIndexRank.get(j).get(k);
					Individual individual = this.getPopulation()[indivIndex];
					indexAndObj.put(indivIndex, individual.getObj());
				}
				//得到按拥挤度排好序的个体集合
				Map<Integer, Double> crowMap = Tools.crowdingCompute(indexAndObj);
				List<Map.Entry<Integer, Double>> crowmaplist = Tools.sortBycrowd(crowMap);
				// 选择j支配层中的前[indexList.size()-(indivSum-populationSize)]个个体添加到新一代父代种群中
				for (int i = 0; i < (num - indivSum); i++) {
					// 个体在支配层种群中的索引
					int index = crowmaplist.get(i).getKey();
					// 按拥挤度排序后，支配层中前[indexList.size()-(indivSum-populationSize)]个个体
					Individual individual = this.getPopulation()[index];
					newPopulation.setIndividual(total, individual);
					total++;	
				}
			}
			indivSum += indivIndexRank.get(j).size();
			if (indivSum >= num) break;
		}

		return newPopulation;
	}
	/*
	 *  @param newnum 选择的下一代种群数目
	 *  @param save 是否保存目标相同个体
	 */
	public Population slectPopulationC(int newnum,boolean save) {
		// 创建新的种群
		Population newPopulation = new Population(newnum,project);
		
		int populationSize = this.getPopulationsize();
		Individual[] individuals =this.getPopulation();
		List<List<Integer>> indivIndexRank = new ArrayList<List<Integer>>(); // 按非支配等级排序后，各个体在种群中对应的序列号的集合
		// 每个解都可以分成两个实体部分:1、支配该解的其他解的数量np;2、被该解支配的解集合Sp
		List<List<Integer>> spList = new ArrayList<List<Integer>>();// 存储的是个体在种群中的序列号
		for (int i = 0; i < populationSize; i++) {
			spList.add(new ArrayList<Integer>());
		}
		int[] np = new int[populationSize];
		for (int i = 0; i < populationSize; i++) {
			for (int j = i+1; j < populationSize; j++) {
				int flag=Tools.Dominated(individuals[i], individuals[j],project);
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
		int[] npbackup = new int[populationSize];
		for(int i=0;i<np.length;i++) {
			npbackup[i]=np[i];
		}
		// 定义一个集合，用来存储前面已经排好等级的个体在种群的序列号
		int num = 0;
		int Rank =0;
		int total=0;
		boolean saveSame=save;//是否保存相同目标值的个体
		Map<Double,HashMap<Double,Boolean>> map=new HashMap<Double,HashMap<Double,Boolean>>();//去重
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
			if(total+FRank.size()<newnum) {
				for(int k=0;k<FRank.size();k++) {
					if(!saveSame) {
						double[] obj= individuals[FRank.get(k)].getObj();
						if(map.get(obj[0])!=null&&map.get(obj[0]).get(obj[1])!=null) {
							continue;
						}
						if(map.get(obj[0])==null) {
							HashMap<Double,Boolean> map1=new HashMap<Double,Boolean>();
							map1.put(obj[1], true);
							map.put(obj[0], map1);
						}else {
							map.get(obj[0]).put(obj[1], true);
						}
					}
					newPopulation.setIndividual(total, individuals[FRank.get(k)]);
					total++;
				}
			}else {
				Population FP = new Population(FRank.size(),project);
				for (int i = 0; i < FRank.size(); i++) {
					//产生当前层的种群
					FP.setIndividual(i, individuals[FRank.get(i)]);
				}
				Tools.setHyperVolum(FP,project);
				List<Integer> ind = Tools.sortByConsAndHyper(FP,npbackup,FRank);
				/*int lack=newnum-total;
				for(int k=0;k<lack;k++) {
					newPopulation.setIndividual(total, individuals[ind.get(k)]);
					total++;
				}*/
				for(int k=0;k<FRank.size()&&total<newnum;k++) {
					if(!saveSame) {
						double[] obj= individuals[FRank.get(k)].getObj();
						if(map.get(obj[0])!=null&&map.get(obj[0]).get(obj[1])!=null) {
							continue;
						}
						if(map.get(obj[0])==null) {
							HashMap<Double,Boolean> map1=new HashMap<Double,Boolean>();
							map1.put(obj[1], true);
							map.put(obj[0], map1);
						}else {
							map.get(obj[0]).put(obj[1], true);
						}
					}
					newPopulation.setIndividual(total, individuals[ind.get(k)]);
					total++;
				}
				if(total==newnum) {
			    	break;
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
			Rank ++;
		}
		while(total<newnum) {
			newPopulation.setIndividual(total, new Individual(this.project,true));
			total++;
		}
		if(newPopulation.getPopulation()[newPopulation.populationsize-1]==null) {
			System.out.println("这儿出错");
		}
		return newPopulation;
	}
	public Population getOffSpring_TLBO() {
		Population OffSpring = new Population(TLBO.populationSize,project,false);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Population teachers = selectTeachers();
		//teacher时期
		Population teacherPhase = crossTeachers(teachers);
		//student时期
		Population studentPhase = teacherPhase.crossStudents();
		//reinforcement时期
		OffSpring = studentPhase.reinforcement();
		return OffSpring;
	}
	//老师个体变异操作
	public Population reinforcement() {
		int popsize=TLBOF.populationSize;
		Population rein = new Population(popsize, project);
		for(int i=0; i<popsize; i++){
			Individual result = this.getPopulation()[i];
			if(result.getTeacher()){
				//引入强化次数
				result.mutationPopulation(TLBOF.probp, 0);
				result = result.binaryTournament(this.getPopulation()[i], result);
			}
			rein.setIndividual(i, result);
		}
		return rein;
	}
	

	//学生个体之间交叉
	public Population crossStudents() {
		Individual[] individuals = this.getPopulation();
		Population offSpring = new Population(TLBOF.populationSize, project);
		int range = individuals.length;
		for(int i=0; i<range; i++){
			Individual result = individuals[i];
			int r = (int)(Math.random()*range);
			if(r!=i
				&&!individuals[i].getTeacher()
				&&!individuals[r].getTeacher()){
				Individual newIndiv = individuals[i].mating_tlbo(individuals[r]);
				result = newIndiv.binaryTournament(newIndiv, individuals[i]);
			}
			offSpring.setIndividual(i, result);
		}
		return offSpring;
	}
	

	//老师和学生个体交叉
	public Population crossTeachers(Population teachers){
		Individual[] individuals = this.getPopulation();
		Population students = new Population(TLBOF.populationSize, project);
		for(int i = 0; i<individuals.length; i++){
			Individual resultIndividual = individuals[i];
			if(!resultIndividual.getTeacher()){
				int r = (int) (Math.random() * teachers.size());
				Individual newIndividual = individuals[i].mating_tlbo(teachers.getPopulation()[r]);
				resultIndividual = newIndividual.binaryTournament(newIndividual, individuals[i]);
			}
			students.setIndividual(i, resultIndividual);
		}
		//融合筛选
		return students;
	}
	

	//选择老师种群
	public Population selectTeachers(){
		List<List<Integer>> indexs = Tools.setRankAndCrowD(this, project);
		int tSize = indexs.get(0).size();
		Population teachers = new Population(tSize,project);
		for(int i = 0; i<tSize; i++){
			Individual teacher = this.getPopulation()[indexs.get(0).get(i)];
			teacher.setTeacher(true);
			teachers.setIndividual(i, teacher);
		}
		return teachers;
	}


	////遗传算法/////////////////////////////////////////////////////////////////
	/**
	 * 使用遗传算法获得下一代种群
	 * @param v 
	 * 
	 * @param project
	 *            案例集
	 * @return 下一代种群
	 */
	public Population getOffSpring_NSGAV(int v) {
		//computeMax();
		Population OffSpring = new Population(project.getNSGAV_II().populationSize,project,false);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		//Tools.setRankAndCrowD(this, project);
		Tools.setRankAndConsAndHyperVolume(this, project);
		// 选择出交配池
		Population matePool = getMatePool();//1随机 2this相邻
		// 将交配池中的个体按指定的概率进行交配
		Population p1 = matePool.crossoverPopulaiton(project.getNSGAV_II().crossoverRate,1);
		// 将产生的子代种群进行变异（tMutationRate：任务序列变异概率，rMutationRate 资源序列编译概率）
		//Population p2 = p1.mutationPopulation(NSGA_II.tMutationRate,NSGA_II.rMutationRate);
        //使用变邻搜索  B-VND first-improvement
		Population p2 = p1.variableNeighborhoodDescent(1,v);
		//Population p2 = p1.variableNeighborhoodDescentVP();
		// 将两个种群合并
		/*Population p3=p2.extremeSearch();*/
		Population mergedPopulation = merged(this,p2);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		OffSpring = mergedPopulation.slectPopulationC(project.getNSGAV_II().populationSize,false);
		return OffSpring;
	}
	/*
	 * 选取部分个体进行极端搜索
	 */
	private Population extremeSearch() {
		int numC=5;
		Population popCost=new Population(numC,project);
		
		return null;
	}
	/*
	 * @param  p种群数组
	 * 合并多种群
	 */
	public Population merged(Population... p){
		List<Individual> mergedList = new ArrayList<>();
		for(int j=0;j<p.length;j++) {
			for (int i = 0; i < p[j].size(); i++) {
				mergedList.add(p[j].getPopulation()[i]);		
			}
		}
		Population mergedPopulation = new Population(mergedList.size(),project);
		for (int i =0; i <mergedList.size();i++){
			mergedPopulation.setIndividual(i, mergedList.get(i));
		}
		return mergedPopulation;
	}
	private void computeMax() {
	   double[] max= {0,0};
       for(Individual individual:this.population) {
    	   max[0]= max[0]==0?individual.getObj()[0]:max[0]<individual.getObj()[0]?individual.getObj()[0]:max[0];
    	   max[1]= max[1]==0?individual.getObj()[1]:max[1]<individual.getObj()[1]?individual.getObj()[1]:max[1];
       }
       //setMaxObj(max);
	   initialHeuristic().project.setTempObj(max);
	}
	//变邻搜索  第一个是
	private Population variableNeighborhoodDescent(int e, int v) {
		Population newPopulation = new Population(populationsize,project);
		for (int i = 0; i < populationsize; i++) {
			Individual parent = population[i];
			Individual son = parent.variableNeighborDecent(e,v);
			newPopulation.setIndividual(i, son);
		}
		return newPopulation;
	}
	//变邻搜索  保存非支配解
	private Population variableNeighborhoodDescentVP(int e) {
		List<Individual>indivs=new ArrayList<>();
		for (int i = 0; i < populationsize; i++) {
			Individual parent = population[i];
			List<Individual> vs = parent.variableNeighborDecentVP(e);
			indivs.addAll(vs);
			//System.out.println(i);
		}
		Population newPopulation = new Population(indivs.size(),project);
		for(int i=0;i<indivs.size();i++) {
			newPopulation.setIndividual(i, indivs.get(i));
		}
		return newPopulation;
	}
	/**
	 * 获取用于交叉的交配池，其中个体数量等于输入种群的个体数量
	 * 
	 * @param population
	 *            父代种群
	 * @return 用于交叉的交配池
	 */
	public Population getMatePool() {
		Population matePool = new Population(populationsize,project);
		for (int i = 0; i < matePool.size(); i++) {
			int m, n;
			while (true) {
				m = (int) (Math.random() * populationsize);
				n = (int) (Math.random() * populationsize);
				if (m == n)
					continue; 
				else
					break;
			}
			matePool.setIndividual(i, population[m].binaryTournament(population[m], population[n]));
		}
		return matePool;
	}


	/**
	 * 单点交叉方法 任务执行链表和资源分配链表使用同一个交叉点，交叉后，子代个体的任务链表仍然是紧前关系可行链表
	 * @param m 
	 * 
	 * @param matePool
	 *            交配池种群
	 * @return 返回新一代种群
	 */
	public Population crossoverPopulaiton(double crossoverRate, int m) {
		Population newPopulation = new Population(populationsize,project);
		for (int i = 0; i < populationsize - 1; i += 2) {
			Individual parent1 = population[i];
			Individual parent2 = population[i + 1];
			if (crossoverRate > Math.random()) {
				// 获取任务链表的交叉点[1,chromosomeLength]
				int swapPoint = (int) (Math.random() * parent1.getChromosome().get(0).size());
				Individual son1 = parent1.Mating(parent2, swapPoint,m);
				Individual son2 = parent2.Mating(parent1, swapPoint,m);
				newPopulation.setIndividual(i, son1);
				newPopulation.setIndividual(i + 1, son2);
				
			}else{
				newPopulation.setIndividual(i, parent1);
				newPopulation.setIndividual(i + 1, parent2);				
			}
		}
		if(populationsize%2==1) {
			newPopulation.setIndividual(populationsize-1, population[populationsize-1]);
		}
		return newPopulation;
	}

	/**
	 * 变异
	 * 
	 * @param population
	 *            种群
	 * @return 返回新一代种群
	 */
	public Population mutationPopulation(double tMutationRate,double rMutationRate) {
		// 创建新的种群
		Population newPopulation = new Population(populationsize,project);
		for (int i = 0; i < populationsize; i++) {
			Individual parent = population[i];
			Individual son = parent.mutationPopulation(tMutationRate,rMutationRate);
			newPopulation.setIndividual(i, son);
		}
		return newPopulation;
	}
	/*
	 * 变异 
	 * @param k 变异类别
	 */
	private Population mutationPopulationV(double tMutationRate, double rMutationRate,int k) {
		Population newPopulation = new Population(populationsize,project);
		for (int i = 0; i < populationsize; i++) {
			Individual parent = population[i];
			Individual son = parent.mutationPopulationV(tMutationRate,rMutationRate,k);
			newPopulation.setIndividual(i, son);
		}
		return newPopulation;
	}



	/////NSFFA/////////////////////////////////////////////////////////////////////////

	/**
	 * 使用遗传算法获得下一代种群
	 * 
	 * @param project
	 *            案例集
	 * @return 下一代种群
	 */
	public Population getOffSpring_NSFFA() {
	
		Population OffSpring = new Population(NSFFA.NS,project);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Tools.setRankAndCrowD(this, project);
		// 
		// 基于气味搜索，每个个体生成S个个体，种群大小为NS*S
		Population p1 = this.smell_BasedSearch();
		// 将两个种群合并
		Population mp1 = merged(this,p1);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		Population p2 = mp1.slectPopulation(NSFFA.NS);
		
		 // 基于知识的搜索
		Population Q = p2.knowledge_BasedSearch();
		// 将两个种群合并
		Population mp2 = merged(p2,Q);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		OffSpring = mp2.slectPopulation(NSFFA.NS);

		return OffSpring;
	}
	/**
	 * 基于气味搜索方法 遍历输入种群中的每个个体，每个个体经过操作，生成S个子个体
	 * 
	 * @param population
	 * @return
	 */
	public Population smell_BasedSearch() {
		Population newPopulation = new Population(this.size() * NSFFA.S,project );
		List<Individual> indivList = new ArrayList<>();
		// 遍历输入种群中的每个个体，每个个体经过操作，生成S个子个体
		Individual[] individuals = this.getPopulation();
		for (int i = 0; i < this.size(); i++) {
			Individual individual = individuals[i];
			for (int j = 0; j < NSFFA.S; j++) {

				List<List<Integer>> offspringChromosome = new ArrayList<>();
				for (int m = 0; m < individual.getChromosome().size(); m++) {
					List<Integer> list = new ArrayList<>();
					for (int n = 0; n < individual.getChromosome().get(m).size(); n++) {
						list.add(individual.getChromosome().get(m).get(n));
					}
					offspringChromosome.add(list);
				}
				// 随机选择任务序列中的某个位置  对1个任务的资源重新选择 
				int index_t_1 = (int) (Math.random() * offspringChromosome.get(0).size());
				
				int taskID = offspringChromosome.get(0).get(index_t_1);
				Task task = project.getTasks().get(taskID - 1);
				
				List<Integer> capapleResource = task.getresourceIDs();
				double rd = Math.random() ;
				int index_capaple = (int) (rd * capapleResource.size());
				
				int resourceid = capapleResource.get(index_capaple);
				offspringChromosome.get(1).set(index_t_1, resourceid);
				
				// 重复随机选择任务序列中的某个位置，直到两个相邻任务没有紧前任务关系， 做1次任务位置交换
				while (true) {
					int index_t_2 = (int) (Math.random() * offspringChromosome.get(0).size());
					if (index_t_2 != (offspringChromosome.get(0).size() - 1)) {
						
						int taskID1 = offspringChromosome.get(0).get(index_t_2);
						int resourceID1 = offspringChromosome.get(1).get(index_t_2);
						int taskID2 = offspringChromosome.get(0).get(index_t_2 + 1);
						int resourceID2 = offspringChromosome.get(1).get(index_t_2 + 1);

						Task task1 = project.getTasks().get(taskID1 - 1);
						Task task2 = project.getTasks().get(taskID2 - 1);

						if (!project.isPredecessor(task1, task2)) {
							// 交换两个位置上的任务编号以及资源编号
							offspringChromosome.get(0).set(index_t_2, taskID2);
							offspringChromosome.get(1).set(index_t_2, resourceID2);
							offspringChromosome.get(0).set(index_t_2 + 1, taskID1);
							offspringChromosome.get(1).set(index_t_2 + 1, resourceID1);
					
							break;
						} 
					}
				}
				// 创建子代个体对象
				Individual offspring = new Individual(offspringChromosome,project);
				indivList.add(offspring);
			}
		}
		for (int i = 0; i < indivList.size(); i++) {
			newPopulation.setIndividual(i, indivList.get(i));
		}
		return newPopulation;
	}


	/**
	 * 基于知识库搜索
	 * 
	 * @param population
	 * @return
	 */
	public Population knowledge_BasedSearch() {
		Population newPopulation = new Population(this.size(),project);
		// 选择NE个精英个体
		Population EPop = this.slectPopulation(NSFFA.NE);

		List<Task> tasks = project.getTasks();
		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			List<Integer> capapleResourceid = task.getresourceIDs();
			Map<Integer,Double> rp = tasks.get(i).getCapaleResource();	
			
			double sumTemp_P = 0;
			for (int j = 0; j < capapleResourceid.size(); j++) {
				double temp_P = (1 - NSFFA.alpha) * rp.get(capapleResourceid.get(j))
						+ NSFFA.alpha * getISum(EPop, task.getTaskID(), capapleResourceid.get(j)) / NSFFA.NE;
				rp.replace(capapleResourceid.get(j), temp_P);
				sumTemp_P += temp_P;
			}
			for (int j = 0; j < capapleResourceid.size(); j++) {
				double P = rp.get(capapleResourceid.get(j)) / sumTemp_P;
				rp.replace(capapleResourceid.get(j), P);
			}

		}

		// 遍历种群的个体，利用轮盘赌法为每个个体的染色体任务序列重新分配资源,并进行交叉算子操作
		for (int i = 0; i < this.size(); i++) {
			Individual parent = this.getPopulation()[i];
			// 个体i的染色体结构
			List<List<Integer>> chromosome = parent.getChromosome();
			// 创建子个体的染色体对象
			List<List<Integer>> newChromosome = new ArrayList<>();
			for (int m = 0; m < chromosome.size(); m++) {
				List<Integer> list = new ArrayList<>();
				for (int n = 0; n < chromosome.get(m).size(); n++) {
					list.add(chromosome.get(m).get(n));
				}
				newChromosome.add(list);
			}

			for (int j = 0; j < newChromosome.get(0).size(); j++) {
				// 任务ID
				int tID = newChromosome.get(0).get(j);
				// 任务对象
				Task t = tasks.get(tID - 1);
				// 任务t的可执行资源集
				Map<Integer,Double> capapleResource = t.getCapaleResource();
				// 利用轮盘赌法为任务t重新分配资源
				int reassignResourceID = selectResource(capapleResource);
				newChromosome.get(1).set(j, reassignResourceID);
			}

			// 从NE精英个体群中随机选择一个个体
			int randIndex = (int) (Math.random() * EPop.size());
			Individual parent_2 = EPop.getPopulation()[randIndex];
			List<List<Integer>> chromosome_2 = parent_2.getChromosome();

			// 随机选择两个位置，p,q,需要满足1<=p<q<=J-1
			int p, q;
			while (true) {
				p = (int) (Math.random() * newChromosome.get(0).size());
				q = (int) (Math.random() * newChromosome.get(0).size());
				if (p < q) {
					break;
				}
			}
			// 用来存储已排好序的子任务序列
			List<Integer> tID_List = new ArrayList<>();
			for (int j = 0; j < chromosome_2.get(0).size(); j++) {
				if (j < p || j > q) {
					tID_List.add(newChromosome.get(0).get(j));
//					newChromosome.get(0).set(j, newChromosome.get(0).get(j));
//					newChromosome.get(1).set(j, newChromosome.get(1).get(j));
				}
			}
			for (int j = p; j <= q; j++) {
				for (int k = 0; k < chromosome_2.get(0).size(); k++) {
					if (!tID_List.contains(chromosome_2.get(0).get(k))) {
						tID_List.add(chromosome_2.get(0).get(k));
						newChromosome.get(0).set(j, chromosome_2.get(0).get(k));
						newChromosome.get(1).set(j, chromosome_2.get(1).get(k));
						break;
					}
				}
			}

			// 创建子个体
			Individual offspring = new Individual(newChromosome,project);
			newPopulation.setIndividual(i, offspring);
		}

		return newPopulation;
	}
	/**
	 * 轮盘赌法，为每个任务重新分配资源
	 * 
	 * @param capapleResource
	 * @return
	 */
	public int selectResource(Map<Integer, Double> capapleResource) {
		double rouletteWheelPosition = Math.random();
		// 选择资源
		double spinWheel = 0;
		int Resourceid = 0;
		Iterator<Integer> rt = capapleResource.keySet().iterator();
		while(rt.hasNext()){
			Resourceid = rt.next();
			spinWheel += capapleResource.get(Resourceid);
			if (spinWheel >= rouletteWheelPosition) {
				break;
			}		
		}
		return Resourceid;
	}

	/**
	 * 计算在NE个精英个体中，有多少个体满足指定任务，被指定资源分配
	 * 
	 * @param ePop
	 * @param taskID
	 * @param resourceID
	 * @return
	 */
	private int getISum(Population ePop, int taskID, int resourceID) {
		int sum = 0;
		for (int i = 0; i < ePop.size(); i++) {
			Individual indiv = ePop.getPopulation()[i];
			// 指定任务在个体染色体中的索引
			int index_t = indiv.getChromosome().get(0).indexOf(taskID);
			int rID = indiv.getChromosome().get(1).get(index_t);
			// 如果与指定资源ID相同，sum加1
			if (rID == resourceID) {
				sum++;
			}
		}
		return sum;
	}

	public Case getProject() {
		return project;
	}

	public void setProject(Case project) {
		this.project = project;
	}

	public Population getOffSpring_new1() {
		// TODO Auto-generated method stub
		Population OffSpring = new Population(TLBOF.populationSize,project,false);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Population teachers = selectTeachers();
		//teacher时期
		Population teacherPhase = crossTeachers(teachers);//貌似有问题
		//student时期
		Population studentPhase = teacherPhase.crossStudents();
		//reinforcement时期
		OffSpring = studentPhase.reinforcement();

		return OffSpring;
	}
	/*
	 * 通过一系列迭代过后已经获取到近最优的操作序列染色体部分，但是针对精英染色体对应的资源分配序列搜索不足，仅匹配了一条合适的资源序列染色体
	 * 而限制了最终解的多样性，一条精英操作序列染色体可以匹配多种情形的资源序列 因此在最终针对此加强资源序列的搜索以加强解的多样性
	 */
	public Population serchMoreSpaceByRes(int s1) {
		Individual[] indivs=this.getPopulation();
		List<Individual> expands=new ArrayList<>();
		expands.addAll(Arrays.asList(indivs));
		for(int i=0;i<indivs.length;i++) {
			Individual indiv=indivs[i];
			Individual[] expand=indiv.Smell_basedSearch(s1, indiv.getChromosome(), project);
			expands.addAll(Arrays.asList(expand));
		}
		Individual[] population=new Individual[expands.size()];
		for(int i=0;i<expands.size();i++) {
			population[i]=expands.get(i);
		}
		Population pop=new Population(population,project);
		return pop;
	}
	public void setPopulation(Individual[] population) {
		this.population = population;
	}
	
	public double[] getMaxObj() {
		return MaxObj;
	}
	public void setMaxObj(double[] maxObj) {
		MaxObj = maxObj;
	}
	public Population initialHeuristic() {
		Individual[] indivs=this.getPopulation();
		for(int i=0;i<this.getPopulationsize();i++) {
			indivs[i].initialHeuristic();
		}
		return this;
	}
	/**
	 * 使用遗传算法获得下一代种群
	 * 
	 * @param project
	 *            案例集
	 * @return 下一代种群
	 */
	public Population getOffSpring_NSGA() {
	
		Population OffSpring = new Population(project.getNSGA_II().getPopulationSize(),project,false);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Tools.setRankAndCrowD(this, project);
		
		// 选择出交配池
		Population matePool = getMatePool();

		// 将交配池中的个体按指定的概率进行交配
		Population p1 = matePool.crossoverPopulaiton(project.getNSGA_II().crossoverRate,2);

		// 将产生的子代种群进行变异（tMutationRate：任务序列变异概率，rMutationRate 资源序列编译概率）
		Population p2 = p1.mutationPopulation(project.getNSGA_II().tMutationRate,project.getNSGA_II().rMutationRate);

		// 将两个种群合并
		Population mergedPopulation = merged(this,p2);

		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		OffSpring = mergedPopulation.slectPopulation(project.getNSGA_II().populationSize);

		return OffSpring;
	}
	/* 
	 *  变邻搜索
	 */
	public Population getOffSpring_V(int k) {
		/*Population OffSpring = new Population(project.getNSGAV_II().populationSize,project,false);*/
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Tools.setRankAndCrowD(this, project);
		// 选择出交配池
		Population matePool = getMatePool();
		// 将交配池中的个体按指定的概率进行交配
		Population p1 = matePool.crossoverPopulaiton(project.getNSGAV_II().crossoverRate,1);
		// 将产生的子代种群进行变异（tMutationRate：任务序列变异概率，rMutationRate 资源序列编译概率）
		Population p2 = p1.mutationPopulationV(0.1,project.getNSGAV_II().rMutationRate,k);//暂定0.1
		// 将两个种群合并
		Population mergedPopulation = merged(this,p2);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		/*Population p3 = mergedPopulation.slectPopulationC(project.getNSGAV_II().populationSize,false);*/
		Population p3 = mergedPopulation.slectPopulation(project.getNSGAV_II().populationSize);
		return p3;
	}
	public Population getOffSpring_NSGAV_Extreme() {
		Population OffSpring = new Population(project.getNSGAV_II().populationSize,project,false);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Tools.setRankAndConsAndHyperVolume(this, project);
		// 选择出交配池
		Population matePool = getMatePool(); 

		// 将交配池中的个体按指定的概率进行交配
		Population p1 = matePool.crossoverPopulaiton(project.getNSGAV_II().crossoverRate,3);

		//使用变邻搜索  B-VND first-improvement
		Population p2 = p1.variableNeighborhoodDescent(3,0);
		//Population p2 = p1.variableNeighborhoodDescentVP();
		// 将两个种群合并
		/*Population p3=p2.extremeSearch();*/
		Population mergedPopulation = merged(this,p2);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		OffSpring = mergedPopulation.slectPopulationC(project.getNSGAV_II().populationSize,false);

		return OffSpring;
	}
	
	/*
	 * 获得帕累托前沿
	 */
	public List<Individual> getpareto() {
		List<Individual> first=new ArrayList<>();
		Individual[] indivs=this.getPopulation();
		for(int i=0;i<indivs.length;i++) {
			if(indivs[i].getNon_dominatedRank()==0) {
				first.add(new Individual(indivs[i]));
			}
		}
		return first;
	}
	/*
	 * 添加帕累托到种群
	 */
	public Population addPareto(List<Individual> ...indivs) {
		Population OffSpring = new Population(this.populationsize,project,false);
		Individual[] individuals=this.getPopulation();
		int newSize=individuals.length;
		for(int i=0;i<indivs.length;i++) {
			newSize+=indivs[i].size();
		}
		Individual[] combines=new Individual[newSize];
		int total=0;
		for(int i=0;i<individuals.length;i++) {
			combines[total++]=individuals[i];
		}
		for(int i=0;i<indivs.length;i++) {
			List<Individual> indivL=indivs[i];
			for(int j=0;j<indivL.size();j++) {
				combines[total++]=indivL.get(j);
			}
		}
		Population mergedPopulation=new Population(newSize,this.getProject());
		mergedPopulation.setPopulation(combines);
		OffSpring = mergedPopulation.slectPopulationC(this.populationsize,false);
		return OffSpring;
	}
	
	
	/*
	 * 第二种变异 邻概率  遗传算法
	 */
	public Population getOffSpring_V2(int k) {
		Population OffSpring = new Population(project.getNSGAV_II().populationSize,project,false);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Tools.setRankAndCrowD(this, project);
		// 选择出交配池
		Population matePool = getMatePool();
		// 将交配池中的个体按指定的概率进行交配
		Population p1 = matePool.crossoverPopulaiton(project.getNSGAV_II().crossoverRate,2);
		// 将产生的子代种群进行变异（tMutationRate：任务序列变异概率，rMutationRate 资源序列编译概率）
		Population p2 = p1.mutationPopulation(0.6,project.getNSGAV_II().rMutationRate);
		// 将两个种群合并
		Population mergedPopulation = merged(this,p2);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		OffSpring = mergedPopulation.slectPopulation(project.getNSGAV_II().populationSize);
		return OffSpring;
	}
	/*
	 * 将种群加入另外一种群 
	 */
	public Population addPareto(Population paretoPop) {
		Population mergedPopulation=this.merged(this,paretoPop);
		Population OffSpring = new Population(project.getNSGAV_II().populationSize,project,false);
		OffSpring = mergedPopulation.slectPopulation(project.getNSGAV_II().populationSize);
		return OffSpring;
	}
	/*
	 * 帕累托前沿种群的进化采用邻解结构2 效果好  任务变异概率取0.6
	 */
	public Population getOffSpring_Pareto() {
		Population OffSpring = new Population(this.populationsize,project,false);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Tools.setRankAndCrowD(this, project);
		// 选择出交配池
		Population matePool = getMatePool();
		// 将交配池中的个体按指定的概率进行交配
		Population p1 = matePool.crossoverPopulaiton(project.getNSGAV_II().crossoverRate,2);
		// 将产生的子代种群进行变异（tMutationRate：任务序列变异概率，rMutationRate 资源序列编译概率）
		Population p2 = p1.mutationPopulation(0.6,project.getNSGAV_II().rMutationRate);
		// 将两个种群合并
		Population mergedPopulation = merged(this,p2);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		/*Population p=Tools.getbestsolution(mergedPopulation,1, project);//有重复的风险
*/		OffSpring = mergedPopulation.slectPopulationC(project.getNSGAV_II().populationSize,false);
		return OffSpring;
	}
	
	
}
