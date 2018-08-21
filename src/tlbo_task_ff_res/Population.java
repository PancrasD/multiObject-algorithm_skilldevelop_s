package tlbo_task_ff_res;

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
				Individual individual = new Individual(project);
				this.population[i] = individual;
			}
			this.populationObj = populationObjCompute(this.population);
		}
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
			Population rein = new Population(TLBO.populationSize, project);
			for(int i=0; i<TLBO.populationSize; i++){
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
			Population offSpring = new Population(TLBO.populationSize, project);
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
			Population students = new Population(TLBO.populationSize, project);
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
		 * 
		 * @param project
		 *            案例集
		 * @return 下一代种群
		 */
		public Population getOffSpring_NSGA() {
		
			Population OffSpring = new Population(NSGA_II.populationSize,project,false);
			// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
			Tools.setRankAndCrowD(this, project);
			// 

			// 选择出交配池
			Population matePool = getMatePool();

			// 将交配池中的个体按指定的概率进行交配
			Population p1 = matePool.crossoverPopulaiton(NSGA_II.crossoverRate);

			// 将产生的子代种群进行变异（tMutationRate：任务序列变异概率，rMutationRate 资源序列编译概率）
			Population p2 = p1.mutationPopulation(NSGA_II.tMutationRate,NSGA_II.rMutationRate);

			// 将两个种群合并
			Population mergedPopulation = merged(this,p2);

			// 从混合种群中选择前populationSize个个体作为新一代父代种群
			OffSpring = mergedPopulation.slectPopulation(NSGA_II.populationSize);

			return OffSpring;
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
		 * 
		 * @param matePool
		 *            交配池种群
		 * @return 返回新一代种群
		 */
		public Population crossoverPopulaiton(double crossoverRate) {
			Population newPopulation = new Population(populationsize,project);
			for (int i = 0; i < populationsize - 1; i += 2) {
				Individual parent1 = population[i];
				Individual parent2 = population[i + 1];
				if (crossoverRate > Math.random()) {
					
					// 获取任务链表的交叉点[1,chromosomeLength]
					int swapPoint = (int) (Math.random() * parent1.getChromosome().get(0).size());
					Individual son1 = parent1.Mating(parent2, swapPoint);
					Individual son2 = parent2.Mating(parent1, swapPoint);
					newPopulation.setIndividual(i, son1);
					newPopulation.setIndividual(i + 1, son2);
					
				}else{
					newPopulation.setIndividual(i, parent1);
					newPopulation.setIndividual(i + 1, parent2);				
				}
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

	public Population getOffSpring_TLBO_F() {
		// TODO Auto-generated method stub
		Population OffSpring = new Population(TLBO.populationSize,project,false);
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
			Individual[] expand=indiv.Smell_basedSearch(s1, indiv.getChromosome(), indiv.getchromosomeDNA(), project);
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
	
}
