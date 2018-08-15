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
	 * ���������Ⱥ�ĸ���Ŀ�꺯��
	 * �õ�����Ŀ�꺯���ļ���
	 * @param population
	 *            ��Ⱥ
	 * @return populationObj ��Ⱥ�и���Ŀ�꺯������
	 */
	public List<double[]> populationObjCompute(Individual[] population) {
		List<double[]> populationObj = new ArrayList<>();
		for (Individual individual : population) {
			populationObj.add(individual.getObj());
		}
		return populationObj;
	}
	
	
	
	
	// ��ȡ��Ⱥ�ĸ����Ա
	public Individual[] getPopulation() {
		return this.population;
	}

	// ��ȡ��Ⱥ��Ŀ�꺯������
	public List<double[]> getPopulationObj() {
		return this.populationObj;
	}

	// ��ȡ��Ⱥ��С
	public int size() {
		return this.populationsize;
	}

	// ������Ⱥ�еĸ���
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

	// ѡ��
	// �ӻ����Ⱥ�У�ѡ��ǰN������
	public Population slectPopulation(int num) {
		// �����µ���Ⱥ
		Population newPopulation = new Population(num,project);

		// �����Ⱥ���п��ٷ�֧������
		// �õ���ͬ��֧��㣬ÿ����֧����ɶ��������ɣ�������Ϊ�����ڻ����Ⱥ������
		List<List<Integer>> indivIndexRank = Tools.non_Dominated_Sort(this,0, project);
		// ��������
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
				//�õ���ӵ�����ź���ĸ��弯��
				Map<Integer, Double> crowMap = Tools.crowdingCompute(indexAndObj);
				List<Map.Entry<Integer, Double>> crowmaplist = Tools.sortBycrowd(crowMap);
				// ѡ��j֧����е�ǰ[indexList.size()-(indivSum-populationSize)]��������ӵ���һ��������Ⱥ��
				for (int i = 0; i < (num - indivSum); i++) {
					// ������֧�����Ⱥ�е�����
					int index = crowmaplist.get(i).getKey();
					// ��ӵ���������֧�����ǰ[indexList.size()-(indivSum-populationSize)]������
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
		// ��Ⱥ���з�֧������,������Ⱥ��ÿ������ķ�֧��ȼ���ӵ����ֵ
		Population teachers = selectTeachers();
		//teacherʱ��
		Population teacherPhase = crossTeachers(teachers);
		//studentʱ��
		Population studentPhase = teacherPhase.crossStudents();
		//reinforcementʱ��
		OffSpring = studentPhase.reinforcement();

		return OffSpring;
	}
	
	//��ʦ����������
	public Population reinforcement() {
		Population rein = new Population(TLBO.populationSize, project);
		for(int i=0; i<TLBO.populationSize; i++){
			Individual result = this.getPopulation()[i];
			if(result.getTeacher()){
				//����ǿ������
				result.mutationPopulation(TLBOF.probp, 0);
				result = result.binaryTournament(this.getPopulation()[i], result);
			}
			rein.setIndividual(i, result);
		}
		return rein;
	}
	
	//ѧ������֮�佻��
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
	
	//��ʦ��ѧ�����彻��
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
		//�ں�ɸѡ
		return students;
	}
	
	
	
	//ѡ����ʦ��Ⱥ
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

	////�Ŵ��㷨/////////////////////////////////////////////////////////////////
	/**
	 * ʹ���Ŵ��㷨�����һ����Ⱥ
	 * 
	 * @param project
	 *            ������
	 * @return ��һ����Ⱥ
	 */
	public Population getOffSpring_NSGA() {
	
		Population OffSpring = new Population(NSGA_II.populationSize,project,false);
		// ��Ⱥ���з�֧������,������Ⱥ��ÿ������ķ�֧��ȼ���ӵ����ֵ
		Tools.setRankAndCrowD(this, project);
		// 

		// ѡ��������
		Population matePool = getMatePool();

		// ��������еĸ��尴ָ���ĸ��ʽ��н���
		Population p1 = matePool.crossoverPopulaiton(NSGA_II.crossoverRate);

		// ���������Ӵ���Ⱥ���б��죨tMutationRate���������б�����ʣ�rMutationRate ��Դ���б�����ʣ�
		Population p2 = p1.mutationPopulation(NSGA_II.tMutationRate,NSGA_II.rMutationRate);

		// ��������Ⱥ�ϲ�
		Population mergedPopulation = merged(this,p2);

		// �ӻ����Ⱥ��ѡ��ǰpopulationSize��������Ϊ��һ��������Ⱥ
		OffSpring = mergedPopulation.slectPopulation(NSGA_II.populationSize);

		return OffSpring;
	}
	
	
	
	
	/**
	 * ��ȡ���ڽ���Ľ���أ����и�����������������Ⱥ�ĸ�������
	 * 
	 * @param population
	 *            ������Ⱥ
	 * @return ���ڽ���Ľ����
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
	 * ���㽻�淽�� ����ִ���������Դ��������ʹ��ͬһ������㣬������Ӵ����������������Ȼ�ǽ�ǰ��ϵ��������
	 * 
	 * @param matePool
	 *            �������Ⱥ
	 * @return ������һ����Ⱥ
	 */
	public Population crossoverPopulaiton(double crossoverRate) {
		Population newPopulation = new Population(populationsize,project);
		for (int i = 0; i < populationsize - 1; i += 2) {
			Individual parent1 = population[i];
			Individual parent2 = population[i + 1];
			if (crossoverRate > Math.random()) {
				
				// ��ȡ��������Ľ����[1,chromosomeLength]
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
	 * ����
	 * 
	 * @param population
	 *            ��Ⱥ
	 * @return ������һ����Ⱥ
	 */
	public Population mutationPopulation(double tMutationRate,double rMutationRate) {
		// �����µ���Ⱥ
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
	 * ʹ���Ŵ��㷨�����һ����Ⱥ
	 * 
	 * @param project
	 *            ������
	 * @return ��һ����Ⱥ
	 */
	public Population getOffSpring_NSFFA() {
	
		Population OffSpring = new Population(NSFFA.NS,project);
		// ��Ⱥ���з�֧������,������Ⱥ��ÿ������ķ�֧��ȼ���ӵ����ֵ
		Tools.setRankAndCrowD(this, project);
		// 
		// ������ζ������ÿ����������S�����壬��Ⱥ��СΪNS*S
		Population p1 = this.smell_BasedSearch();
		// ��������Ⱥ�ϲ�
		Population mp1 = merged(this,p1);
		// �ӻ����Ⱥ��ѡ��ǰpopulationSize��������Ϊ��һ��������Ⱥ
		Population p2 = mp1.slectPopulation(NSFFA.NS);
		
		 // ����֪ʶ������
		Population Q = p2.knowledge_BasedSearch();
		// ��������Ⱥ�ϲ�
		Population mp2 = merged(p2,Q);
		// �ӻ����Ⱥ��ѡ��ǰpopulationSize��������Ϊ��һ��������Ⱥ
		OffSpring = mp2.slectPopulation(NSFFA.NS);

		return OffSpring;
	}
	
	
	
	/**
	 * ������ζ�������� ����������Ⱥ�е�ÿ�����壬ÿ�����徭������������S���Ӹ���
	 * 
	 * @param population
	 * @return
	 */
	public Population smell_BasedSearch() {
		Population newPopulation = new Population(this.size() * NSFFA.S,project );
		List<Individual> indivList = new ArrayList<>();
		// ����������Ⱥ�е�ÿ�����壬ÿ�����徭������������S���Ӹ���
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
				// ���ѡ�����������е�ĳ��λ��  ��1���������Դ����ѡ�� 
				int index_t_1 = (int) (Math.random() * offspringChromosome.get(0).size());
				
				int taskID = offspringChromosome.get(0).get(index_t_1);
				Task task = project.getTasks().get(taskID - 1);
				
				List<Integer> capapleResource = task.getresourceIDs();
				double rd = Math.random() ;
				int index_capaple = (int) (rd * capapleResource.size());
				
				int resourceid = capapleResource.get(index_capaple);
				offspringChromosome.get(1).set(index_t_1, resourceid);
				
				// �ظ����ѡ�����������е�ĳ��λ�ã�ֱ��������������û�н�ǰ�����ϵ�� ��1������λ�ý���
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
							// ��������λ���ϵ��������Լ���Դ���
							offspringChromosome.get(0).set(index_t_2, taskID2);
							offspringChromosome.get(1).set(index_t_2, resourceID2);
							offspringChromosome.get(0).set(index_t_2 + 1, taskID1);
							offspringChromosome.get(1).set(index_t_2 + 1, resourceID1);
					
							break;
						} 
					}
				}
				// �����Ӵ��������
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
	 * ����֪ʶ������
	 * 
	 * @param population
	 * @return
	 */
	public Population knowledge_BasedSearch() {
		Population newPopulation = new Population(this.size(),project);
		// ѡ��NE����Ӣ����
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

		// ������Ⱥ�ĸ��壬�������̶ķ�Ϊÿ�������Ⱦɫ�������������·�����Դ,�����н������Ӳ���
		for (int i = 0; i < this.size(); i++) {
			Individual parent = this.getPopulation()[i];
			// ����i��Ⱦɫ��ṹ
			List<List<Integer>> chromosome = parent.getChromosome();
			// �����Ӹ����Ⱦɫ�����
			List<List<Integer>> newChromosome = new ArrayList<>();
			for (int m = 0; m < chromosome.size(); m++) {
				List<Integer> list = new ArrayList<>();
				for (int n = 0; n < chromosome.get(m).size(); n++) {
					list.add(chromosome.get(m).get(n));
				}
				newChromosome.add(list);
			}

			for (int j = 0; j < newChromosome.get(0).size(); j++) {
				// ����ID
				int tID = newChromosome.get(0).get(j);
				// �������
				Task t = tasks.get(tID - 1);
				// ����t�Ŀ�ִ����Դ��
				Map<Integer,Double> capapleResource = t.getCapaleResource();
				// �������̶ķ�Ϊ����t���·�����Դ
				int reassignResourceID = selectResource(capapleResource);
				newChromosome.get(1).set(j, reassignResourceID);
			}

			// ��NE��Ӣ����Ⱥ�����ѡ��һ������
			int randIndex = (int) (Math.random() * EPop.size());
			Individual parent_2 = EPop.getPopulation()[randIndex];
			List<List<Integer>> chromosome_2 = parent_2.getChromosome();

			// ���ѡ������λ�ã�p,q,��Ҫ����1<=p<q<=J-1
			int p, q;
			while (true) {
				p = (int) (Math.random() * newChromosome.get(0).size());
				q = (int) (Math.random() * newChromosome.get(0).size());
				if (p < q) {
					break;
				}
			}
			// �����洢���ź��������������
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

			// �����Ӹ���
			Individual offspring = new Individual(newChromosome,project);
			newPopulation.setIndividual(i, offspring);
		}

		return newPopulation;
	}

	/**
	 * ���̶ķ���Ϊÿ���������·�����Դ
	 * 
	 * @param capapleResource
	 * @return
	 */
	public int selectResource(Map<Integer, Double> capapleResource) {
		double rouletteWheelPosition = Math.random();
		// ѡ����Դ
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
	 * ������NE����Ӣ�����У��ж��ٸ�������ָ�����񣬱�ָ����Դ����
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
			// ָ�������ڸ���Ⱦɫ���е�����
			int index_t = indiv.getChromosome().get(0).indexOf(taskID);
			int rID = indiv.getChromosome().get(1).get(index_t);
			// �����ָ����ԴID��ͬ��sum��1
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
		// ��Ⱥ���з�֧������,������Ⱥ��ÿ������ķ�֧��ȼ���ӵ����ֵ
		Population teachers = selectTeachers();
		//teacherʱ��
		Population teacherPhase = crossTeachers(teachers);//ò��������
		//studentʱ��
		Population studentPhase = teacherPhase.crossStudents();
		//reinforcementʱ��
		OffSpring = studentPhase.reinforcement();

		return OffSpring;
	}
	/*
	 * ͨ��һϵ�е��������Ѿ���ȡ�������ŵĲ�������Ⱦɫ�岿�֣�������Ծ�ӢȾɫ���Ӧ����Դ���������������㣬��ƥ����һ�����ʵ���Դ����Ⱦɫ��
	 * �����������ս�Ķ����ԣ�һ����Ӣ��������Ⱦɫ�����ƥ��������ε���Դ���� �����������Դ˼�ǿ��Դ���е������Լ�ǿ��Ķ�����
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
	
}
