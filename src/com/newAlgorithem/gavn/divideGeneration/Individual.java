package com.newAlgorithem.gavn.divideGeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Individual {
	// 个体染色体的维数
	static final int chromosomeLayer = 2;
	// 每个个体的目标函数个数
	static final int objNum = 2;
	// 个体中任务
	private List<ITask> taskslist = new ArrayList<ITask>();//ITask在计算目标时随着资源的执行后续任务能够执行的资源数在更新
	//个体中资源
	private List<IResource> resourceslist = new ArrayList<IResource>();
	// 染色体
	private List<List<Integer>> chromosome = new ArrayList<List<Integer>>();
	// 目标函数 duration  cost
	private double[] obj = new double[objNum];
	private int maxtime = 0;
	private double cost = 0.0;
    private int[] heuristics=new int[New1.heuristics];
	// 个体在种群中的非支配等级
	private int non_dominatedRank;
	// 个体的在非支配层中的拥挤度
	private double crowDistance;
	private double hyperVolume;
	private Case project;
	private boolean isTeacher = false;
	//超体积 成本 工期加权和 
	private double weightSum;
	//极端个体区分标识
	private int mark;// 0 duration  1 cost  2 banlance
	public Individual(List<List<Integer>> _chromosome,List<List<Double>> _chromosomemDNA,Case project) {
		// 创建个体的染色体
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		this.chromosome = _chromosome;
		//计算个体的目标函数值，输出计算了起停时间的任务对象list
		learnObjCompute();		
	}
	
	public Individual(List<List<Integer>> _chromosome,Case project) {
		// 创建个体的染色体
		this.project = project;
		settaskslist(project);	
		setResourcesList(project);
		this.chromosome = _chromosome;
		//计算个体的目标函数值，输出计算了起停时间的任务对象list
		learnObjCompute();		
	}
		
	//
	public Individual(Case project) {
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		//随机产生DNA及任务序列
		deciphering( project);
		//随机产生资源序列，计算目标函数值
		learnObjCompute();
		//计算个体的目标函数值，输出计算了起停时间的任务对象list

	}
    //初始化建立 资源random
	public Individual(Case project, boolean initial) {
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		//随机产生DNA及任务序列
		deciphering( project);
		//随机产生资源序列，计算目标函数值
		learnObjCompute(initial);
	}
	//复制个体
	public Individual(Individual indiv) {
		this.project = indiv.project;
		settaskslist(project);
		setResourcesList(project);
		List<List<Integer>>chromosome_copy =new ArrayList<>();
		for(int i=0;i<indiv.chromosome.size();i++) {
			List<Integer> list=indiv.chromosome.get(i);
			List<Integer> list_copy=new ArrayList<>();
			for(int j=0;j<list.size();j++) {
				list_copy.add(list.get(j));
			}
			chromosome_copy.add(i, list_copy);
		}
 		this.chromosome = chromosome_copy;
		this.obj=new double[] {indiv.getObj()[0],indiv.getObj()[1]};
	}
	public Individual(List<List<Integer>> _chromosome, Case project, int mark) {
		// 创建个体的染色体
		this.project = project;
		settaskslist(project);	
		setResourcesList(project);
		this.chromosome = _chromosome;
		this.mark=mark;
		//计算个体的目标函数值，输出计算了起停时间的任务对象list
		learnObjCompute();		
	}

	private void settaskslist(Case project){
		for (int i = 0; i < project.getTasks().size();i++){
			ITask itask = new ITask(project.getTasks().get(i));//完全新建避免复制引用
			taskslist.add(itask);
		}
	}
	
	private void setResourcesList(Case project) {
		for(int i = 0; i<project.getResources().size(); i++){
			IResource iresource = new IResource(project.getResources().get(i));
			resourceslist.add(iresource);
		}
	}
	
	//从indiv中提取nums个序列
	public void insert(List<Integer> _tasks, List<Double> _taskdna, int nums, Individual indiv){
		int m = 0;
		for(int n = 0; n < nums; n++){
			boolean met = false;
			while(!met){
				Integer task = indiv.chromosome.get(0).get(m);
				if(!_tasks.contains(task)){
					_tasks.add(task);
					met = true;
				}
				m++;
			}
		}
	}
	
	//tlbo交叉
	public Individual mating_tlbo(Individual husband) {
		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();
		List<List<Double>> son_chromosomeDNA = new ArrayList<List<Double>>();
		List<Integer> _tasks = new ArrayList<>();
		List<Double> _taskdna = new ArrayList<>();
		
		int chrosomeLength = this.chromosome.get(0).size();
		int u1 = (int)(Math.random()*chrosomeLength)+1;
		int u2 = (int)(Math.random()*chrosomeLength)+1;
		
		insert(_tasks, _taskdna, Math.min(u1, u2), this);
		insert(_tasks, _taskdna, Math.abs(u2-u1), husband);
		insert(_tasks, _taskdna, chrosomeLength-Math.max(u2, u1), this);
		
		son_chromosome.add(_tasks);
		son_chromosomeDNA.add(_taskdna);
		//使用果蝇算法  气味搜索和视觉搜索Smell-based search and vision-based search
		Individual[] smells=Smell_basedSearch(TLBOF.s,son_chromosome,project);
		Individual son = vision_basedSearch(smells,project);
		return son;
	}
	

	//基于气味搜索
	public Individual[] Smell_basedSearch(int s, List<List<Integer>> son_chromosome, Case project) {
		Individual[] smells=new Individual[s];
		for(int i=0;i<s;i++) {
			Individual son = new Individual(son_chromosome,  project);
			smells[i]=son;
		}
		return smells;
	}
	//基于视觉搜索
	private Individual vision_basedSearch(Individual[] smells, Case project) {
		Population pop=new Population(smells,project);
		List<List<Integer>> rank=Tools.setRankAndCrowD(pop, project);
		return smells[rank.get(0).get(0)];
	}
	//交配生子
	public Individual Mating(Individual husband, int crosspoint) {

		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();
		List<Integer> _tasks = new ArrayList<>();
		/*List<Integer> _resources = new ArrayList<>();
		 * List<Double> _resourcesdna = new ArrayList<>();*/
		for (int i = 0; i < crosspoint;i++){
			_tasks.add(this.chromosome.get(0).get(i));
			/*_resources.add(this.chromosome.get(1).get(i));*/
			/*_resourcesdna.add(this.chromosomeDNA.get(1).get(i));*/
		}
		for (int i = 0; i<husband.chromosome.get(0).size();i++){
			if (!_tasks.contains(husband.chromosome.get(0).get(i))){
				_tasks.add(husband.chromosome.get(0).get(i));
				/*_resources.add(husband.chromosome.get(1).get(i));
				_resourcesdna.add(husband.chromosomeDNA.get(1).get(i));*/
			}
		}
		son_chromosome.add(_tasks);
		/*son_chromosome.add(_resources);
		son_chromosomeDNA.add(_taskdna);*/
		Individual son = new Individual(son_chromosome, project,this.mark);
		/*Individual son = new Individual(son_chromosome,son_chromosomeDNA,project);*/	
		return son;
	}
	
	//个体变异
	public Individual mutationPopulation(double tMutationRate,double rMutationRate) {
		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();
		List<List<Double>> son_chromosomeDNA = new ArrayList<List<Double>>();
		List<Integer> _tasks = new ArrayList<>();
		/*List<Integer> _resources = new ArrayList<>();*/
		/*List<Double> _resourcesdna = new ArrayList<>();*/
		int chromosomeLength = this.chromosome.get(0).size();
		for (int i =0; i<chromosomeLength; i++){
			_tasks.add(this.chromosome.get(0).get(i));
			/*_resources.add(this.chromosome.get(1).get(i));*/
			/*_resourcesdna.add(this.chromosomeDNA.get(0).get(i));*/			
		}
		//相邻交换往后
		double rand=Math.random();
		if(rand<0.5) {
			//相邻交换1  根据变异概率
		for (int geneIndex = 0; geneIndex < chromosomeLength - 1; geneIndex++) {
			if (tMutationRate > Math.random()) {
				int taskGene1=_tasks.get(geneIndex);
				/*int resourceGene1=_resources.get(geneIndex);*/
				/*double randres = _resourcesdna.get(geneIndex);*/
				int taskGene2=_tasks.get(geneIndex+1);
				/*int resourceGene2=_resources.get(geneIndex+1);*/
				
				Task t1 = project.getTasks().get(taskGene1 - 1);
				Task t2 = project.getTasks().get(taskGene2 - 1);
				if (!project.isPredecessor(t1, t2)) {//t1不是t2的紧前任务
					// 交换两个位置上的任务编号以及资源编号
					_tasks.set(geneIndex, taskGene2);
					_tasks.set(geneIndex+1, taskGene1);
					/*_resources.set(geneIndex, resourceGene2);
					_resources.set(geneIndex+1, resourceGene1);*/
					/*_resourcesdna.set(geneIndex, _resourcesdna.get(geneIndex+1));
					_resourcesdna.set(geneIndex+1, randres);*/
				}
			}
			
			/*if (rMutationRate > Math.random()) {
				// 每个任务可供选择的资源集合
				// 任务ID
				int gene=_tasks.get(geneIndex);
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// 在可行资源中，随机选择一种
				double  randnum = Math.random();
				_resourcesdna.set(geneIndex, randnum);
				int r = (int) (randnum * capapleResource.size());
				_resources.set(geneIndex, capapleResource.get(r));
			}*/
		}
		son_chromosome.add(_tasks);
		/*son_chromosome.add(_resources);*/
		/*son_chromosomeDNA.add(_resourcesdna);*///！！！有误son_chromosomeDNA.add(_resourcesdna)
		}else if(rand<1) {
			//相邻交换2  交换一次
			while (true) {
				int index_t_2 = (int) (Math.random() * chromosomeLength);
				if (index_t_2 != (chromosomeLength - 1)) {
					
					int taskID1 = _tasks.get(index_t_2);
					int taskID2 = _tasks.get(index_t_2 + 1);

					Task task1 = project.getTasks().get(taskID1 - 1);
					Task task2 = project.getTasks().get(taskID2 - 1);

					if (!project.isPredecessor(task1, task2)) {
						// 
						_tasks.set(index_t_2, taskID2);
						_tasks.set(index_t_2 + 1, taskID1);
						break;
					} 
				}
			}
			son_chromosome.add(_tasks);
			/*son_chromosome.add(_resources);*/
		}/*else if(rand<0) {
			//随机交换
			while(true) {
				int index_1 = (int) (Math.random() * chromosomeLength);
				int index_2 = (int) (Math.random() * chromosomeLength);
				int taskID1 = _tasks.get(index_1);
				int taskID2 = _tasks.get(index_2);
				Task task1 = project.getTasks().get(taskID1 - 1);
				Task task2 = project.getTasks().get(taskID2 - 1);
				//验证错误 
				if (!project.isPredecessor(task1, task2)) {
					//
					_tasks.set(index_1,taskID2);
					_tasks.set(index_2, taskID1);
					break;
				}
			}
		}else {
			//随机插入
			int index_1 = (int) (Math.random() * chromosomeLength);
			int index_2 = (int) (Math.random() * chromosomeLength);
			while(index_1==index_2) {
				index_2 = (int) (Math.random() * chromosomeLength);
			}
			int taskID1 = _tasks.get(index_1);
			if(index_1<index_2) {
				//后插
				
			}else {
				//前插
			}
			
		}*/
		
		Individual son = new Individual(son_chromosome,son_chromosomeDNA,project);	

		return son;
	}

	// 获取该个体的染色体结构(taskid)
	public List<List<Integer>> getChromosome() {
		return this.chromosome;
	}
	
	public List<ITask> getTaskslist() {
		return taskslist;
	}

	public void setTaskslist(List<ITask> taskslist) {
		this.taskslist = taskslist;
	}

	
	public double getWeightSum() {
		return weightSum;
	}

	public void setWeightSum(double weightSum) {
		this.weightSum = weightSum;
	}

	// 设置染色体任务序列指定位置的任务编号
	public void setTaskGene(int offset, int gene) {
		this.chromosome.get(0).set(offset, gene);
	}

	// 获取染色体任务序列指定位置的任务编号
	public int getTaskGene(int offset) {
		return this.chromosome.get(0).get(offset);
	}

	// 设置染色体资源分配序列指定位置的资源编号
	public void setResourceGene(int offset, int gene) {
		this.chromosome.get(1).set(offset, gene);
	}

	// 获取染色体资源分配序列指定位置的资源编号
	public int getResourceGene(int offset) {
		return this.chromosome.get(1).get(offset);
	}

	// 获取该个体的目标函数值
	public double[] getObj() {
		return this.obj;
	}
	
	public void setObj(double[] obj) {
		this.obj = obj;
	}
	// non_dominatedRank的getter方法
	public int getNon_dominatedRank() {
		return non_dominatedRank;
	}

	// non_dominatedRank的setter方法
	public void setNon_dominatedRank(int non_dominatedRank) {
		this.non_dominatedRank = non_dominatedRank;
	}

	// crowDistance的getter方法
	public double getCrowDistance() {
		return crowDistance;
	}

	// crowDistance的setter方法
	public void setCrowDistance(double crowDistance) {
		this.crowDistance = crowDistance;
	}
	
	public void setTeacher(boolean flag) {
		this.isTeacher = flag;
	}
	
	public boolean getTeacher() {
		return this.isTeacher;
	}

	/**
	 * 个体的toString方法
	 */
	/*
	public String toString() {
		String output = "";
		for (int i = 0; i < this.chromosome.size(); i++) {
			for (int j = 0; j < this.chromosome.get(0).size(); j++) {
				output += this.chromosome.get(i).get(j);
				output+="\t";
			}
			output+="\n";
		}
		return output;
	}
	*/

	/**
	 * 每个chromosome解密之后对应的目标函数值，用一个一维数组表示，数组长度等于目标函数的个数
	 * 
	 * @param chromosome
	 *            解密后的解
	 * @return 计算好起停时间的任务队列
	 */
	public void objCompute(Case project) {
		List<Task> tasks = project.getTasks();
		List<Resource> resourses = project.getResources();
		List<Integer> pretaskids;
		int maxtime = 0;
		double cost = 0.0;
		
		int[] endtime_res = new int[project.getM()];
		for (int i = 0; i < endtime_res.length ; i++) {
			//用于记录每个资源释放时间
			endtime_res[i] = 0;
		}
		
		for (int i = 0; i < project.getN(); i++){
			int endtime = 0;
			Task curtask = tasks.get(chromosome.get(0).get(i)-1);
			//得到所有前置任务,循环每一前置任务，取最晚结束时间
			pretaskids = curtask.getPredecessorIDs();
			for (int j = 0; j < pretaskids.size();j++){
				if (endtime < tasks.get(pretaskids.get(j)-1).getFinishTime()){
					endtime = tasks.get(pretaskids.get(j)-1).getFinishTime();
				}
			}
			//当前任务所对应的资源最晚时间
			if (endtime < endtime_res[chromosome.get(1).get(i)-1]){
				endtime = endtime_res[chromosome.get(1).get(i)-1];
			}
			//设置当前任务的开始时间及完成时间
			taskslist.get(chromosome.get(0).get(i)-1).setstarttime(endtime +1,1);
			//更新当前任务资源的最后完工时间
			endtime_res[chromosome.get(1).get(i)-1] = taskslist.get(chromosome.get(0).get(i)-1).finishTime;
            //当前个体最后的完成时间
			if (maxtime < endtime_res[chromosome.get(1).get(i)-1]){
            	maxtime = endtime_res[chromosome.get(1).get(i)-1];
            }
	     
			// 计算成本			
			int duration = tasks.get(chromosome.get(0).get(i) - 1).getDuaration();
			double salary = resourses.get(chromosome.get(1).get(i) - 1).getSalary();
			cost += duration * salary;			
			
		}
		this.obj[0] =  (double)maxtime;
		this.obj[1] = cost;
	}


	/**
	 * 将随机初始化解，解密成整数向量表示任务序列、资源序列的染色体结构
	 * 
	 * @param _chromosome
	 *            随机数组成的二维数组
	 * @return 返回由任务执行序列和资源分配序列组成的集合
	 */
	public void deciphering(Case project) {
		
		List<Integer> taskList = new ArrayList<Integer>();
		/*List<Integer> resourceList = new ArrayList<Integer>();*/
		// 可执行任务集合
		List<Integer> executableTaskIDS = new ArrayList<Integer>();	
		List<Task> tasks = project.getTasks();

		List<Double> _list1 = new ArrayList<>();
		/*List<Double> _list2 = new ArrayList<>();*/
		
		/*int[] endtime_res = new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//用于记录每个资源释放时间
			endtime_res[j] = 0;
		}*/

		// 求taskList任务执行序列和resourceList资源分配序列
		for (int i = 0; i < project.getN(); i++) {  
			
			executableTaskIDS.clear();
			
			for (int k = 0; k < tasks.size(); k++) {
				if (taskslist.get(k).pretasknum == 0){//找到没有紧前任务的任务集合作为优先执行任务集合
					executableTaskIDS.add(tasks.get(k).getTaskID());
					double rand1 = Math.random();
					_list1.add(rand1);
				}
			}
			if (executableTaskIDS.size() == 0){
				break;
			}
			//1随机  2最大紧后集   34先选择资源然后选择任务3最大执行时间 4 最大紧后集执行时间和
			double rand3=Math.random();
			if(rand3<0.7) {
			   scheduleTaskByRandomRule(executableTaskIDS,taskList);
			}else if(rand3<0.8) {
				scheduleTaskByMaxSuccessorsRule(executableTaskIDS,taskList);
			}else if(rand3>0.9) {
				scheduleTaskByMaxProcessTimeRule(executableTaskIDS,taskList);
			}else {
				scheduleTaskByMaxSumSuccessorsProcessTimeRule(executableTaskIDS,taskList);
			}
			// 求对应的资源分配序列resourceList
			// 可执行该任务的资源集合
			/*ITask curTask = taskslist.get(currentTaskID -1);
			List<Integer> list = curTask.getresourceIDs();
			int B = (int) (rand2 * list.size());
			int resourceid = list.get(B);
			resourceList.add( resourceid );*/
			//单步计算目标值
			/*singleCompute(resourceid,currentTaskID,endtime_res);*/
		}
		/*this.chromosomeDNA.add(_list2);*/
		this.chromosome.add(taskList);
		/*this.chromosome.add(resourceList);*/
		return ;
	}
	private void scheduleTaskByMaxSumSuccessorsProcessTimeRule(List<Integer> executableTaskIDS,
			List<Integer> taskList) {
		List<Task> tasks=this.getProject().getTasks();
		//按降序排列
		Collections.sort(executableTaskIDS, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				List<Integer> list1=tasks.get(o1-1).getsuccessortaskIDS();
				List<Integer> list2=tasks.get(o2-1).getsuccessortaskIDS();
				int sum1=0;
				int sum2=0;
				for(int i=0;i<list1.size();i++) {
					int id=list1.get(i);
					sum1+=tasks.get(id-1).getStandardDuration();
				}
				for(int i=0;i<list2.size();i++) {
					int id=list2.get(i);
					sum2+=tasks.get(id-1).getStandardDuration();
				}
				int flag=1;
				if(sum1>sum2) {
					flag=-1;
				}else if(sum1<sum2) {
					flag=1;
				}else {
					flag=0;
				}
				return flag;
				
			}
		});
		 while(executableTaskIDS.size()>0) {
				int A =0;
				int currentTaskID = executableTaskIDS.get(A);
				executableTaskIDS.remove(A);
				taskList.add(currentTaskID);
				taskslist.get(currentTaskID -1).pretasknum = -1;   //当前任务已经被使用，做上标记以防止下次被选用
				//处理后续任务
				for (int k = 0; k < tasks.size(); k++) {
					//把所有以任务j为前置任务的前置任务数减1；
					if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
						taskslist.get(k).pretasknum--;	
					}
				}
		 }
		
	}

	private void scheduleTaskByMaxProcessTimeRule(List<Integer> executableTaskIDS, List<Integer> taskList) {
		List<Task> tasks=this.getProject().getTasks();
		//按降序排列
		Collections.sort(executableTaskIDS, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				int flag=1;
				if(tasks.get(o1-1).getStandardDuration()<tasks.get(o2-1).getStandardDuration()) {
					flag=1;
				}else if(tasks.get(o1-1).getStandardDuration()>tasks.get(o2-1).getStandardDuration()) {
					flag=-1;
				}else {
					flag=0;
				}
				return flag;
			}	
			});
		    while(executableTaskIDS.size()>0) {
			int A =0;
			int currentTaskID = executableTaskIDS.get(A);
			executableTaskIDS.remove(A);
			taskList.add(currentTaskID);
			taskslist.get(currentTaskID -1).pretasknum = -1;   //当前任务已经被使用，做上标记以防止下次被选用
			//处理后续任务
			for (int k = 0; k < tasks.size(); k++) {
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
				}
			}
		}
	
		
	}

	private void scheduleTaskByMaxSuccessorsRule(List<Integer> executableTaskIDS, List<Integer> taskList) {
		List<Task> tasks=this.getProject().getTasks();
		//按降序排列
		Collections.sort(executableTaskIDS, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				int flag=1;
				if(tasks.get(o1-1).getsuccessortaskIDS().size()<tasks.get(o2-1).getsuccessortaskIDS().size()) {
					flag=1;
				}else if(tasks.get(o1-1).getsuccessortaskIDS().size()>tasks.get(o2-1).getsuccessortaskIDS().size()){
					flag=-1;
				}else {
					flag=0;
				}
				return flag;
			}
			
		});
		//
		while(executableTaskIDS.size()>0) {
			int A =0;
			int currentTaskID = executableTaskIDS.get(A);
			executableTaskIDS.remove(A);
			taskList.add(currentTaskID);
			taskslist.get(currentTaskID -1).pretasknum = -1;   //当前任务已经被使用，做上标记以防止下次被选用
			//处理后续任务
			for (int k = 0; k < tasks.size(); k++) {
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
				}
			}
		}
		
	}

	private void scheduleTaskByRandomRule(List<Integer> executableTaskIDS, List<Integer> taskList) {
		List<Task> tasks = project.getTasks();
		double rand1 = Math.random();
		while(executableTaskIDS.size()>0) {
			int A = (int) ( rand1 * executableTaskIDS.size());
			int currentTaskID = executableTaskIDS.get(A);//优先任务集合中随机选择一个作为当前执行任务
			executableTaskIDS.remove(A);
			taskList.add(currentTaskID);
			taskslist.get(currentTaskID -1).pretasknum = -1;   //当前任务已经被使用，做上标记以防止下次被选用
			
			//处理后续任务
			for (int k = 0; k < tasks.size(); k++) {
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
					//？？？应该将该任务从前序任务集中删除
				}
			}
		}
	}
	private void learnObjCompute(boolean initial) {
		List<Integer> resourceList = new ArrayList<Integer>();
		List<Integer> taskList = this.chromosome.get(0);
		List<Double> _list2 = new ArrayList<>();
		int[] endtime_res = new int[project.getM()];
		int workload[]= new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//用于记录每个资源释放时间
			endtime_res[j] = 0;
			workload[j]=0;
		}
		
		for(int i = 0; i <taskList.size(); i++){
			ITask curTask = taskslist.get(taskList.get(i) -1);
			//资源的选择 引入四种规则  随机  最便宜的资源 最早空闲资源   最小负载资源  最先可以升级的
			//int resourceid=selectResourceC(curTask,endtime_res,workload);
			double rand2 = Math.random();
			List<Integer> list = curTask.getresourceIDs();
			int B = (int) (rand2 * list.size());
			_list2.add(rand2);
			int resourceid = list.get(B);
			resourceList.add(resourceid);
			
			singleCompute(resourceid,this.chromosome.get(0).get(i),endtime_res,workload);//动态计算
		}
		this.chromosome.add(resourceList);
		
	}
	/*
	 * 进行任务序列资源的分配及目标值的计算
	 * 当个体设置了其属性后 其进行对应属性的搜索 通过个体的mark属性判断
	 * 分别有三种情形 mark为1位均衡搜索 mark为2为成本搜索 mark为3为工期搜索
	 */
	public void learnObjCompute() {
		List<Integer> resourceList = new ArrayList<Integer>();
		List<Integer> taskList = this.chromosome.get(0);
		int[] endtime_res = new int[project.getM()];
		int workload[]= new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//用于记录每个资源释放时间
			endtime_res[j] = 0;
			workload[j]=0;
		}
	   
	   int select=0;
	   if(this.getMark()!=0) {
		   select=this.getMark();
	   }else {
		   double rand=Math.random();
		   if(rand<NSGAV_II.pr) {
		    	select=1;//成本&&工期
			}else {
				double rand1=Math.random();
				if(rand1<0.5) {//0.5
					select=2;//成本
				}else {
					select=3;//工期
				}
			}
	   }
	   switch(select) {
	   case 1:{
		   for(int i = 0; i <taskList.size(); i++){
				ITask curTask = taskslist.get(taskList.get(i) -1);
				int resourceid=0;
		        resourceid=selectResourceC(curTask,endtime_res,workload,0.5);//成本&&工期
		        resourceList.add(resourceid);
				singleCompute(resourceid,this.chromosome.get(0).get(i),endtime_res,workload);//动态计算
		   }
	   }
	   case 2: {
		   for(int i = 0; i <taskList.size(); i++){
				ITask curTask = taskslist.get(taskList.get(i) -1);
				int resourceid=0;
				resourceid=selectResourceB(curTask,endtime_res,workload,1);//成本
		        resourceList.add(resourceid);
				singleCompute(resourceid,this.chromosome.get(0).get(i),endtime_res,workload);//动态计算
		   }
	   }
	   case 3:{
		   for(int i = 0; i <taskList.size(); i++){
				ITask curTask = taskslist.get(taskList.get(i) -1);
				int resourceid=0;
				resourceid=selectResourceB(curTask,endtime_res,workload,0);//工期
		        resourceList.add(resourceid);
				singleCompute(resourceid,this.chromosome.get(0).get(i),endtime_res,workload);//动态计算
		   }
	   }
	   }
		this.chromosome.add(resourceList);
	}
	/*
	 * @param curTask 当前需要分配资源的任务
	 * @param endtime_res 每个资源的释放时间
	 * @param searchP 用于判断极端搜索和平衡搜索
	 * 选择资源
	 */
	private int selectResourceB(ITask curTask, int[] endtime_res, int[] workload,double searchP) {
		//两个规则 成本最低  工期最短
		List<IResource> ress=this.resourceslist;
		List<Integer> list = curTask.getresourceIDs();
		double rand=Math.random();
		int resourceid=list.get(0); 
		List<Integer> list1=new ArrayList<>(list);
		
		if(rand<searchP&&list1.size()>1) {
			//-------->成本最低
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer arg0, Integer arg1) {
					double cost1=ress.get(arg0-1).getSalary()*curTask.getStandardDuration()/Math.floor(ress.get(arg0-1).getSkillsInfo().get(curTask.getSkillType()));
					double cost2=ress.get(arg1-1).getSalary()*curTask.getStandardDuration()/Math.floor(ress.get(arg1-1).getSkillsInfo().get(curTask.getSkillType()));
					if(cost1>cost2) {
						return 1;
					}else if(cost1<cost2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}else if(rand<1&&list1.size()>1) {
			//-------->最早完成
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					double finish1=endtime_res[o1-1]+curTask.getStandardDuration()/Math.floor(ress.get(o1-1).getSkillsInfo().get(curTask.getSkillType()));
					double finish2=endtime_res[o2-1]+curTask.getStandardDuration()/Math.floor(ress.get(o2-1).getSkillsInfo().get(curTask.getSkillType()));
					if(finish1>finish2) {
						return 1;
					}else if(finish1<finish2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}
		return resourceid;
	}
	/*
	 * @param curTask 当前需要分配资源的任务
	 * @param endtime_res 每个资源的释放时间
	 * 三种规则 最早完成  成本最低   可以升级
	 * 平衡搜索的资源选择
	 */
	private int selectResourceC(ITask curTask, int[] endtime_res, int[] workload,double searchP) {
		//两个规则 成本最低  工期最短
		List<IResource> ress=this.resourceslist;
		List<Integer> list = curTask.getresourceIDs();
		double rand=Math.random();
		int resourceid=list.get(0); 
		List<Integer> list1=new ArrayList<>(list);
		
		if(rand<0.5&&list1.size()>1) {
			//-------->成本最低
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer arg0, Integer arg1) {
					double cost1=ress.get(arg0-1).getSalary()*curTask.getStandardDuration()/Math.floor(ress.get(arg0-1).getSkillsInfo().get(curTask.getSkillType()));
					double cost2=ress.get(arg1-1).getSalary()*curTask.getStandardDuration()/Math.floor(ress.get(arg1-1).getSkillsInfo().get(curTask.getSkillType()));
					if(cost1>cost2) {
						return 1;
					}else if(cost1<cost2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}else if(rand<1&&list1.size()>1) {
			//-------->最早完成
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					double finish1=endtime_res[o1-1]+curTask.getStandardDuration()/Math.floor(ress.get(o1-1).getSkillsInfo().get(curTask.getSkillType()));
					double finish2=endtime_res[o2-1]+curTask.getStandardDuration()/Math.floor(ress.get(o2-1).getSkillsInfo().get(curTask.getSkillType()));
					if(finish1>finish2) {
						return 1;
					}else if(finish1<finish2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}else if(list1.size()>1){
			//可以升级的 -- 随机
			String qtype = curTask.getSkillType();
			List<Integer> suit=new ArrayList<>();
			for(int i=0;i<list1.size();i++) {
				IResource iRes1=ress.get(list1.get(i)-1);
				double last=iRes1.getSkillsInfo().get(qtype);
				double future=computeUpdate(iRes1,curTask);
				if(Math.floor(future)-Math.floor(last)>=1) {
					suit.add(list1.get(i));
				}
			}
			if(suit!=null&&suit.size()>0) {
				int suitIndex=(int)(Math.random()*suit.size());
				resourceid=suit.get(suitIndex);
			}else {
				int listIndex=(int)(Math.random()*list1.size());
				resourceid=list1.get(listIndex);
			}
		}
		
		return resourceid;
	}
	/*
	 * 选择资源
	 */
	private int selectResource(ITask curTask, int[] endtime_res, int[] workload) {
		List<IResource> ress=this.resourceslist;
		List<Integer> list = curTask.getresourceIDs();
		double rand=Math.random();
		int resourceid=list.get(0);
		if(rand<0.3) {
			//随机
			double rand1=Math.random();
			int B = (int) (rand1 * list.size());
			resourceid = list.get(B);
		}else if(rand<0.5) {
			//-------->成本最低
			List<Integer> list1=new ArrayList<>(list);
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer arg0, Integer arg1) {
					double cost1=ress.get(arg0-1).getSalary()*curTask.getStandardDuration()/ress.get(arg0-1).getSkillsInfo().get(curTask.getSkillType());
					double cost2=ress.get(arg1-1).getSalary()*curTask.getStandardDuration()/ress.get(arg1-1).getSkillsInfo().get(curTask.getSkillType());
					if(cost1>cost2) {
						return 1;
					}else if(cost1<cost2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}else if(rand<0.7) {
			//最早空闲资源-------->最早完成
			List<Integer> list1=new ArrayList<>(list);
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					double finish1=endtime_res[o1-1]+curTask.getStandardDuration()/ress.get(o1-1).getSkillsInfo().get(curTask.getSkillType());
					double finish2=endtime_res[o2-1]+curTask.getStandardDuration()/ress.get(o2-1).getSkillsInfo().get(curTask.getSkillType());
					if(finish1>finish2) {
						return 1;
					}else if(finish1<finish2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid = list1.get(0);
		}else if(rand<0.9) {
			//最小负载资源
			List<Integer> list1=new ArrayList<>(list);
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					if(workload[o1-1]>workload[o2-1]) {
						return 1;
					}else if(workload[o1-1]<workload[o2-1]) {
						return -1;
					}else {
						return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}else {
			//最早可以升级的
			List<Integer> list1=new ArrayList<>(list);
			String qtype = curTask.getSkillType();
			//升序排列
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					IResource iRes1=ress.get(o1-1);
					IResource iRes2=ress.get(o2-1);
					double qinit1 = iRes1.getSkillsInfo().get(qtype);
					double qinit2 = iRes2.getSkillsInfo().get(qtype);
					if((Math.ceil(qinit1)-qinit1)>(Math.ceil(qinit2)-qinit2)) {
						return 1;
					}else if((Math.ceil(qinit1)-qinit1)<(Math.ceil(qinit2)-qinit2)) {
						return -1;
					}else {
						return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}
		return resourceid;
	}
    /*
     * @param rid 资源ID
     * @param tid 任务ID
     * @param endtime_res 资源的释放时间
     * 当给一项任务分配了资源后进行相关的计算和更新
     */
	public void singleCompute(int rid, int tid, int[] endtime_res, int[] workload){
		ITask task = taskslist.get(tid-1);
		IResource resource = resourceslist.get(rid-1);
		if(!canInsert(resource, task,workload)){//不存在紧前调度
			//阶段性计算：更新资源可用时间，更新任务开始结束时间
			phaseCompute(rid, tid, endtime_res,workload);
			//更新技能水平：当前技能表，技能执行时间表	
			updateSkill(resource, task);
			//计算目标值： 工期 成本 
			this.obj = aimCompute(task, resource,endtime_res);
		}else{
			//仅更新目标值和任务开始结束时间，不更新技能is wrong   需要更新该资源分配的任务的完成时间和资源技能
			//updateResourceAnd
			this.obj = aimCompute(task, resource,endtime_res);//左移调度下移动插入点的技能必须高于任务的所需技能  采用资源更新
		}
	}
	//每次遍历 花费时间  直接将改变endTime的资源同maxTime对比
	public double[] aimCompute(ITask curTask, IResource curResource, int[] endtime_res){
		for(int i=0;i<endtime_res.length;i++) {
			if(this.maxtime<endtime_res[i]){
				this.maxtime =endtime_res[i];
			}
		}
		int realDuration = curTask.getFinishTime() - curTask.getStartTime();
		double salary = curResource.getSalary();
		this.cost +=Math.ceil (realDuration*salary);
		return new double[]{this.maxtime, this.cost};
	}
	
	public void phaseCompute(int rid, int tid, int[] endtime_res, int[] workload){
		int endtime = 0;
		ITask curtask = taskslist.get(tid-1);
		String qtype = curtask.getSkill().split(":")[0].trim();
		IResource resource = resourceslist.get(rid-1);
		double qinit = resource.getSkillsInfo().get(qtype);
		qinit=Math.floor(qinit);//1.3 add
		//得到所有前置任务,循环每一前置任务，取最晚结束时间
		List<Integer> pretaskids = curtask.getPredecessorIDs();
		for (int j = 0; j < pretaskids.size();j++){
			if (endtime < taskslist.get(pretaskids.get(j)-1).getFinishTime()){
				endtime = taskslist.get(pretaskids.get(j)-1).getFinishTime();
			}
		}
		//当前任务所对应的资源最晚时间
		if (endtime < endtime_res[rid-1]){
			endtime = endtime_res[rid-1];
		}
		//设置当前任务的开始时间及完成时间
		taskslist.get(tid-1).setstarttime(endtime,qinit);//间隔1可以取消
		//更新当前任务资源的最后完工时间
		endtime_res[rid-1] = taskslist.get(tid-1).finishTime;
		workload[rid-1]+=(int)taskslist.get(tid-1).getStandardDuration()/qinit;
	}
	
	public boolean updateSkill(IResource resource, ITask task){
		String qtype = task.getSkill().split(":")[0].trim();
		double qinit = resource.getSkillsInfo().get(qtype);
		
		double qfinal=computeUpdate(resource,task);
		//更新当前技能水平表
		resource.putSkillsInfo(qtype, qfinal);
		//更新技能执行时间表
		int[] time={task.getStartTime(),task.getFinishTime()};
		resource.putSkillTimetable(qtype,time, qfinal);		
		if(Math.floor(qfinal)-Math.floor(qinit)>=1){//技能跃迁，重新分配任务可用资源
			/*this.jumpTimes++;*/
			for(int i = 0; i < taskslist.size(); i++){
				ITask tmptask = taskslist.get(i);
				if(tmptask.getSkillType().equals(qtype) 
						&& (double)tmptask.getSkillLevel()<qfinal
						&& !tmptask.getresourceIDs().contains(resource.getResourceID())){
							tmptask.addResourceIDs(resource.getResourceID());
							/*this.influenceTasks++;*/
				}
			}
		}
		return false;
	}
	/*
	 * @param task 当前任务
	 * @param resource 将要分配的资源
	 * 计算分配资源后资源的技能水平更新
	 * @return 返回更新的技能水平值
	 */
	private double computeUpdate(IResource resource, ITask task) {
		String qtype = task.getSkill().split(":")[0].trim();
		double qinit = resource.getSkillsInfo().get(qtype);
		double m = resource.getLearnbility();
		int dstan = task.getStandardDuration();
		double M = 0.25;
		double sstart = Math.pow(((1-M)*qinit)/(1-M*qinit), 1/m);//每次计算 耗费时间
		double sdura = dstan/(qinit*8);
		double tmp = Math.pow((sstart+sdura), m);
		double qfinal = tmp/(M*tmp+1-M);
		return qfinal;
	}

	//紧前调度  是否可以左移调度 ？ 但是未将时间表排序判断
	public boolean canInsert(IResource resource, ITask task, int[] workload){
		
		String skillType = task.getSkill().split(":")[0].trim();
		int stanDuration = task.getStandardDuration();
		HashMap<String,HashMap<int[],Double>> timetable = resource.getSkillTimetable();
		double skillLevel = Double.valueOf(task.getSkill().split(":")[1]);
		//得到资源执行时间表
		HashMap<int[],Double> skilltime = timetable.get(skillType);//int[] 为什么迭代没问题
		LinkedList<int[]> skillTime=resource.getSkillTime().get(skillType);
		if(skillTime!=null&&skillTime.size()>=2) {
			for(int j=0;j<skillTime.size()-1;j++) {
				int[] last=skillTime.get(j);
				int[] time=skillTime.get(j+1);
				double rightskill = skilltime.get(last);
				rightskill=Math.floor(rightskill);//1.3 add按照技能整数计算
				//是否满足技能水平和时长条件
				if(rightskill >= skillLevel && stanDuration/rightskill <= time[0]-last[1]){
					List<Integer> preids = task.getPredecessorIDs();
					//是否满足紧前关系
					for(int i = 0; i < preids.size();i++){
						if(taskslist.get(preids.get(i)-1).getFinishTime() >= last[1]){
							return false;
						};
					}
					task.setstarttime(last[1], rightskill);
					workload[resource.getResourceID()-1]+=stanDuration/rightskill;
					//更新资源技能及资源安排的任务的执行时间 好像不可行  影响是全局的
					return true;
				}
			}
		}
		/*
		@SuppressWarnings("rawtypes")
		Iterator timeline = skilltime.keySet().iterator();
		int[] lasttime = (int[])timeline.next();
        
		//遍历时间表  +需要排序 随意迭代的比较不适合
		while(timeline.hasNext()){
			int[] time = (int[])timeline.next();
			double rightskill = skilltime.get(lasttime);
			rightskill=Math.floor(rightskill);//1.3 add按照技能整数计算
			//是否满足技能水平和时长条件
			if(rightskill >= skillLevel && stanDuration/rightskill <= time[0]-lasttime[1]){
				List<Integer> preids = task.getPredecessorIDs();
				//是否满足紧前关系
				for(int i = 0; i < preids.size();i++){
					if(taskslist.get(preids.get(i)-1).getFinishTime() >= lasttime[1]){
						return false;
					};
				}
				task.setstarttime(lasttime[1], rightskill);
				workload[resource.getResourceID()-1]+=stanDuration/rightskill;
				//更新资源技能及资源安排的任务的执行时间 好像不可行  影响是全局的
				return true;
			}
			lasttime = time;
		}*/
		
		return false;
	}

	public Case getProject() {
		return project;
	}

	public void setProject(Case project) {
		this.project = project;
	}


	/**
	 * 二元锦标赛选择方法，参数为两个个体对象
	 * 
	 * @param indiv1
	 *            个体对象
	 * @param indiv2
	 *            另一个个体对象
	 * @return 返回较好的个体
	 */
	public Individual binaryTournament(Individual indiv1, Individual indiv2) {
		Individual individual = indiv1;

		// int flag;
		// flag = Tools.Dominated(indiv1, indiv2);
		int rank1 = indiv1.getNon_dominatedRank();
		int rank2 = indiv2.getNon_dominatedRank();
		if (rank1 < rank2) {
			individual = indiv1;
		}
		if (rank1 > rank2) {
			individual = indiv2;
		}
		if (rank1 == rank2) {
			if (indiv1.getHyperVolume() < indiv2.getHyperVolume()) {
				individual = indiv2;
			}
			if (indiv1.getHyperVolume()> indiv2.getHyperVolume()) {
				individual = indiv1;
			} else {
				if (Math.random() >= 0.5) {
					individual = indiv1;
				} else {
					individual = indiv2;
				}
			}
		}
		return individual;
	}

	public List<IResource> getResourceslist() {
		return resourceslist;
	}

	public void setResourceslist(List<IResource> resourceslist) {
		this.resourceslist = resourceslist;
	}

	public int getMaxtime() {
		return maxtime;
	}

	public void setMaxtime(int maxtime) {
		this.maxtime = maxtime;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public boolean isTeacher() {
		return isTeacher;
	}

	public void setChromosome(List<List<Integer>> chromosome) {
		this.chromosome = chromosome;
	}
	
    public int[] getHeuristics() {
		return heuristics;
	}

	public void setHeuristics(int[] heuristics) {
		this.heuristics = heuristics;
	}
    
	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	//使用低水平启发式算法初始化
	public void initialHeuristic() {
		int[] heuristics=new int[New1.heuristics];
		for(int i=0;i<New1.heuristics;i++) {
			heuristics[i]=(new Random()).nextInt(4)+1;
		}
		this.setHeuristics(heuristics);
	    //使用低水平启发式算法初始化
		for(int i=0;i<heuristics.length;i++) {
			switch(heuristics[i]) {
			  case 1:this.swapNeighbor();break;
			  case 2:this.insertForward();break;
			  case 3:this.insertBackward();break;
			  case 4:this.swapRandom();break;
			}
		}
	}

	private void swapNeighbor() {
		List<List<Integer>> offspringchromosome=new ArrayList<>();
		List<Integer> tasklist1=new ArrayList<>();
		//任务序列复制s
		for(int i=0;i<this.getChromosome().get(0).size();i++) {
			tasklist1.add(this.getChromosome().get(0).get(i));
		}
		//先生成s1
		tasklist1=swapTaskNeighbor(tasklist1);
		//运用基于邻task的模拟退火
		
	}

	private List<Integer> swapTaskNeighbor(List<Integer> tasklist1) {
		while(true) {
			int index_t_2 = (int) (Math.random() * tasklist1.size());
			if (index_t_2 != (tasklist1.size() - 1)) {
				
				int taskID1 = tasklist1.get(index_t_2);
				int taskID2 = tasklist1.get(index_t_2 + 1);
	
				Task task1 = project.getTasks().get(taskID1 - 1);
				Task task2 = project.getTasks().get(taskID2 - 1);
	
				if (!project.isPredecessor(task1, task2)) {
					// 交换两个位置上的任务编号以及资源编号
					tasklist1.set(index_t_2, taskID2);
					tasklist1.set(index_t_2 + 1, taskID1);
					break;
				} 
			}
		}
		return tasklist1;
	}

	private void insertForward() {
		
		
	}

	private void insertBackward() {
		
		
	}

	private void swapRandom() {
		
		
	}

	public double getHyperVolume() {
		return hyperVolume;
	}

	public void setHyperVolume(double hyperVolume) {
		this.hyperVolume = hyperVolume;
	}

	//个体变邻搜索
	public Individual variableNeighborDecent() {
		Individual compare=this;
		int k=1;//第一个邻结构的序号
		while(k<=5) {
			Individual son=compare.variableNeighborWithFirst(k);
			if(Tools.Dominated(son,compare,this.project)==1) {
				compare=son;
			}else {
				k++;
			}
		}
		if(Tools.Dominated(this,compare,this.project)==1) {
			compare=this;
		}
		return compare;
	}
	//个体变邻搜索 将互不支配的解存储
	public List<Individual> variableNeighborDecentVP() {
		List<Individual> savelist=new ArrayList<>();
		Individual compare=this;
		int k=1;//第一个邻结构的序号
		while(k<=5) {
			Individual son=compare.variableNeighborWithFirst(k);
			int flag=Tools.Dominated(son,compare,this.project);
			if(flag==1) {
				compare=son;
			}else {
				if(flag==0) {
					savelist.add(new Individual(son));
				}
				k++;
			}
		}
		if(Tools.Dominated(this,compare,this.project)==1) {
			compare=this;
		}
		savelist.add(compare);
		return savelist;
	}
    //第一个提高 变邻搜索
	private Individual variableNeighborWithFirst(int k) {
		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();
		List<Integer> _tasks = new ArrayList<>();
		int taskLength = this.chromosome.get(0).size();
		for (int i =0; i<taskLength; i++){
			_tasks.add(this.chromosome.get(0).get(i));			
		}
		switch(k) {
		   case 1:  variableNeighbor_NeighborOne(taskLength,_tasks); break;
		   case 2:  variableNeighbor_NeighborProb(taskLength,_tasks); break;
		   case 3:  variableNeighbor_ForwardInsert(taskLength,_tasks); break;
		   case 4:  variableNeighbor_BackWordInsert(taskLength,_tasks); break;
		   case 5:  variableNeighbor_SwapRandom(taskLength,_tasks); break;
		}
		son_chromosome.add(_tasks);
		Individual son = new Individual(son_chromosome,project,this.mark);
		return son;
	}
	//随机交换
	private void variableNeighbor_SwapRandom(int taskLength, List<Integer> _tasks) {
		
		while(true) {
			int geneIndex1=(int)(Math.random()*(taskLength-1));
			int geneIndex2=(int)(Math.random()*(taskLength-1));
			while(geneIndex1==geneIndex2) {
				geneIndex2=(int)(Math.random()*(taskLength-1));
			} 
			if(geneIndex1>geneIndex2) {
				int a=geneIndex1;
				geneIndex1=geneIndex2;
				geneIndex2=a;
			}
			int taskGene1=_tasks.get(geneIndex1);//	ID
			Task t1 = project.getTasks().get(taskGene1 - 1);
			List<Integer> sucsuc=t1.getSucSucIDS();
			boolean can1=true;
			for(int m=geneIndex1+1;m<=geneIndex2;m++) {
				int taskId=_tasks.get(m);
				if(sucsuc.contains(taskId)) {
					can1=false;
					break;
				}
			}
			if(can1) {
				boolean can2=true;
				int taskGene2=_tasks.get(geneIndex2);
				Task t2 = project.getTasks().get(taskGene2 - 1);
				List<Integer> prepre=t2.getPrePreIDs();
				for(int m=geneIndex1;m<geneIndex2;m++) {
					int taskId=_tasks.get(m);
					if(prepre.contains(taskId)) {
						can2=false;
						break;
					}
				}
				if(can2) {
					_tasks.set(geneIndex1, taskGene2);
					_tasks.set(geneIndex2, taskGene1);
					break;
				}
			}
					
		}
		
	}

	//随机向后插入
	private void variableNeighbor_BackWordInsert(int taskLength, List<Integer> _tasks) {
		while(true) {
			int geneIndex1=(int)(Math.random()*(taskLength-1));
			int geneIndex2=(int)(Math.random()*(taskLength-1));
			while(geneIndex1==geneIndex2) {
				geneIndex2=(int)(Math.random()*(taskLength-1));
			} 
			if(geneIndex1>geneIndex2) {
				int a=geneIndex1;
				geneIndex1=geneIndex2;
				geneIndex2=a;
			}
			int taskGene1=_tasks.get(geneIndex1);//	ID
			Task t1 = project.getTasks().get(taskGene1 - 1);
			List<Integer> sucsuc=t1.getSucSucIDS();
			boolean can=true;
			for(int m=geneIndex1+1;m<=geneIndex2;m++) {
				int taskId=_tasks.get(m);
				if(sucsuc.contains(taskId)) {
					can=false;
					break;
				}
			}
			if(can) {
				_tasks.add(geneIndex2, taskGene1);
				_tasks.remove(geneIndex1);
				break;
			}
    	}
		
	}

	// 随机向前插入
    private void variableNeighbor_ForwardInsert(int taskLength, List<Integer> _tasks) {
    	while(true) {
			int geneIndex1=(int)(Math.random()*(taskLength-1));
			int geneIndex2=(int)(Math.random()*(taskLength-1));
			while(geneIndex1==geneIndex2) {
				geneIndex2=(int)(Math.random()*(taskLength-1));
			} 
			if(geneIndex1<geneIndex2) {
				int a=geneIndex1;
				geneIndex1=geneIndex2;
				geneIndex2=a;
			}
			int taskGene1=_tasks.get(geneIndex1);//	ID
			Task t1 = project.getTasks().get(taskGene1 - 1);
			List<Integer> prepre=t1.getPrePreIDs();
			boolean can=true;
			for(int m=geneIndex2;m<geneIndex1;m++) {
				int taskId=_tasks.get(m);
				if(prepre.contains(taskId)) {
					can=false;
					break;
				}
			}
			if(can) {
				_tasks.remove(geneIndex1);
				_tasks.add(geneIndex2, taskGene1);
				break;
			}
    	}
		
	}

	//相邻交换概率决定
	private void variableNeighbor_NeighborProb(int taskLength, List<Integer> _tasks) {
		for (int geneIndex = 0; geneIndex < taskLength - 1; geneIndex++) {
			if (0.1 > Math.random()) {
				int taskGene1=_tasks.get(geneIndex);
				int taskGene2=_tasks.get(geneIndex+1);
				Task t1 = project.getTasks().get(taskGene1 - 1);
				Task t2 = project.getTasks().get(taskGene2 - 1);
				if (!project.isPredecessor(t1, t2)) {//t1不是t2的紧前任务
					// 交换两个位置上的任务编号以及资源编号
					_tasks.set(geneIndex, taskGene2);
					_tasks.set(geneIndex+1, taskGene1);
				}
			}
		}
	}
    //相邻交换一次
	private void variableNeighbor_NeighborOne(int taskLength, List<Integer> _tasks) {
		while(true) {
			int geneIndex=(int)(Math.random()*(taskLength-1));
			int taskGene1=_tasks.get(geneIndex);
			int taskGene2=_tasks.get(geneIndex+1);
			Task t1 = project.getTasks().get(taskGene1 - 1);
			Task t2 = project.getTasks().get(taskGene2 - 1);
			if (!project.isPredecessor(t1, t2)) {//t1不是t2的紧前任务
				// 交换两个位置上的任务编号以及资源编号
				_tasks.set(geneIndex, taskGene2);
				_tasks.set(geneIndex+1, taskGene1);
				break;
			}
		}
	}

}
