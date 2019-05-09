package newModel.doubleAdjust;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.IResource;
import newModel.doubleAdjust.basis.ITask;
import newModel.doubleAdjust.basis.Resource;
import newModel.doubleAdjust.basis.Task;


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
	// 个体在种群中的非支配等级
	private int non_dominatedRank;
	// 个体的在非支配层中的拥挤度
	private double crowDistance;
	// 个体的超体积
	private double hyperVolume;
	//案例对象
	private Case project;
	private boolean isTeacher = false;
    //hash
	private int hashCode;
	/*
	 * @param  _chromosome 染色体序列
	 * @param project 案例
	 * 根据已有的染色体创建个体 计算函数目标值  进行的是极端搜索
	 */
	public Individual(List<List<Integer>> _chromosome,Case project) {
		// 创建个体的染色体
		this.project = project;
		settaskslist(project);	
		setResourcesList(project);
		this.chromosome = _chromosome;
		//计算个体的目标函数值，输出计算了起停时间的任务对象list
		objComputeReselectExtreme();	
		Tools.setHashCode(this);
	}
		

    /*
     * @param project 案例
     * @param 
     * 初始化建立 资源选择随机
     */
	public Individual(Case project, boolean initial) {
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		//随机产生DNA及任务序列
		deciphering(project);
		//随机产生资源序列，计算目标函数值
		learnObjCompute(initial);
		Tools.setHashCode(this);
	}
	/*
	 * 单编码的初始化
	 */
	public Individual(Case project, boolean initial,boolean single) {
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		//随机产生DNA及任务序列
		deciphering(project,single);
		//随机产生资源序列，计算目标函数值
		learnObjCompute(initial);
		Tools.setHashCode(this,single);
	}
	/*
	 * 单编码的求解
	 */
	public Individual(List<List<Integer>> _chromosome, Case project,boolean single) {
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		this.chromosome = _chromosome;
		objComputeAdjustRandom();
		Tools.setHashCode(this,single);
	}
	/*
	 * 单资源链表编码下的任务链表
	 */
	private void deciphering(Case project, boolean single) {
		List<Integer> taskList = new ArrayList<Integer>();
		List<Task> tasks=this.project.getTasks();
		for(int i=0;i<tasks.size();i++) {
			if(tasks.get(i).getsuccessortaskIDS().size()>0) {
				taskList.add(i+1);
			}
		}
		for(int i=0;i<tasks.size();i++) {
			if(tasks.get(i).getsuccessortaskIDS().size()==0) {
				taskList.add(i+1);
			}
		}
		this.chromosome.add(taskList);
	}


	/*
	 * @param indiv 个体实例
	 * 复制个体
	 */
	public Individual(Individual indiv) {
		this.project = indiv.project;
		settaskslist(project);
		setResourcesList(project);
		List<List<Integer>> chro=new ArrayList<>();
		for(int i=0;i<indiv.chromosome.size();i++) {
			List<Integer> list=indiv.chromosome.get(i);
			List<Integer> list1=new ArrayList<>();
			for(int j=0;list!=null&&j<list.size();j++) {
				list1.add(list.get(j));
			}
			chro.add(list1);
		}
		this.chromosome = chro;
		this.hashCode=indiv.hashCode;
		this.obj=new double[] {indiv.obj[0],indiv.obj[1]};
	}


	/*
	 * @param _chromosome 染色体序列
	 * @param project 案例
	 * @param mark 
	 * 利用已有的染色体序列进行个体创建及目标函数求解
	 * 需要对染色体资源分配序列进行修正或者重新选择  GAVN  e=1 双链表  e=3 极端搜索
	 */
	public Individual(List<List<Integer>> _chromosome, Case project, int mark) {
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		this.chromosome = _chromosome;
		switch(mark) {
			case 1:objComputeAdjustRandom();break;//双链表中的资源序列修正 随机修正
			case 2:objComputeAdjustRandom();break;//双链表中的资源序列修正  遗传算法
			case 3:objComputeAdjustBasedKnowledge();break;//知识修正objComputeReselectExtreme
		}
		Tools.setHashCode(this);
	}
	/*
     * 修正型计算目标值
     */
	private void objComputeAdjustRandom() {
		
		List<Integer> taskList = this.chromosome.get(0);
		List<Integer> resList = this.chromosome.get(1);
		int[] endtime_res = new int[project.getM()];
		int workload[]= new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//用于记录每个资源释放时间
			endtime_res[j] = 0;
			workload[j]=0;
		}
		for(int i = 0; i <taskList.size(); i++){
			int tid=taskList.get(i);
			ITask curTask = taskslist.get(tid-1);
			int resourceid=resList.get(i);
			IResource res=resourceslist.get(resourceid-1);
			boolean is = isSuit(curTask,res);
			
			if(!is) {
				double rand4=Math.random();
				List<Integer> list = curTask.getresourceIDs();
				int B = (int) (rand4 * list.size());
				resourceid = list.get(B);
				resList.set(i, resourceid);//更新资源 更新染色体
				res=resourceslist.get(resourceid-1);
			}
			this.singleCompute(curTask,res,endtime_res,workload);//动态计算
		}
		this.obj=new double[]{this.maxtime, this.cost};
	}
	/*
     * 修正型计算目标值
     */
	private void objComputeAdjustBasedKnowledge() {
		
		List<Integer> taskList = this.chromosome.get(0);
		List<Integer> resList = this.chromosome.get(1);
		int[] endtime_res = new int[project.getM()];
		int workload[]= new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//用于记录每个资源释放时间
			endtime_res[j] = 0;
			workload[j]=0;
		}
		for(int i = 0; i <taskList.size(); i++){
			int tid=taskList.get(i);
			ITask curTask = taskslist.get(tid-1);
			int resourceid=resList.get(i);
			IResource res=resourceslist.get(resourceid-1);
			boolean is= isSuit(curTask,res);
			if(!is) {
				// 任务t的可执行资源集
				Map<Integer,Double> capapleResource = project.getTasks().get(tid-1).getCapaleResource();
				// 利用轮盘赌法为任务t重新分配资源
				resourceid= Tools.selectResource(capapleResource);
				/*
				double rand4=Math.random();
				List<Integer> list = curTask.getresourceIDs();
				int B = (int) (rand4 * list.size());
				resourceid = list.get(B);
				resList.set(i, resourceid);//更新资源 更新染色体
				res=resourceslist.get(resourceid-1);*/
			}
			this.singleCompute(curTask,res,endtime_res,workload);//动态计算
		}
		this.obj=new double[]{this.maxtime, this.cost};
	}
    /*
     * @param curTask 当前任务
     * @param res 当前资源
     * 资源是具有对应要求的技能类型 判断技能水平是否满足要求
     * @return boolean  满足任务技能水平要求  true  否则false
     */
	private boolean isSuit(ITask curTask, IResource res) {
		String skillType=curTask.getSkillType();
		double requiredLevel=curTask.getSkillLevel();
		double resLevel=res.getSkillsInfo().get(skillType);
		if(resLevel>=requiredLevel) {
			return true;
		}
		return false;
	}

	private void settaskslist(Case project){
		List<Task> tasks=project.getTasks();
		for (int i = 0; i < tasks.size();i++){
			ITask itask = new ITask(tasks.get(i));
			taskslist.add(itask);
		}
	}
	
	private void setResourcesList(Case project) {
		List<Resource> ress=project.getResources();
		for(int i = 0; i<ress.size(); i++){
			IResource iresource = new IResource(ress.get(i));
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
		Individual son = new Individual(son_chromosome,project,1);
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
	/*
	 * @param husband 交叉的个体
	 * @param crosspoint 单点交叉点
	 * @param mark 搜索标志
	 * 交配生子
	 * @return 
	 */
	public Individual Mating(Individual husband, int crosspoint, int mark) {

		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();
		List<Integer> _tasks = new ArrayList<>();
		List<Integer> _resources = new ArrayList<>();
		List<Integer> taskChrom=this.chromosome.get(0);
		List<Integer> resChrom=this.chromosome.get(1);
		boolean [] taskIndex=new boolean[taskChrom.size()];
		for (int i = 0; i < crosspoint;i++){
			int taskID=taskChrom.get(i);
			_tasks.add(taskID);
			if(mark!=3) {//极端搜索不需要
			_resources.add(resChrom.get(i));
			}
		    taskIndex[taskID-1]=true;
		}
		List<Integer> husbandTaskChrom=husband.chromosome.get(0);
		List<Integer> husbandResChrom=husband.chromosome.get(1);
		for (int i = 0; i<husbandTaskChrom.size();i++){
			int taskID=husbandTaskChrom.get(i);
			if (!taskIndex[taskID-1]){
					_tasks.add(taskID);
					if(mark!=3) {
					_resources.add(husbandResChrom.get(i));
					}
				}
		}
		if(this.chromosome.get(0).size()!=husbandTaskChrom.size()) {
			System.out.println("异常");
		}
		son_chromosome.add(_tasks);
		if(mark!=3) {
			son_chromosome.add(_resources);
		}
		Individual son=null;
		switch(mark) {
		case 1:{//变邻搜索
			double prob=Math.random();
			if(prob<project.getParameter().getResSpp()) {
				 son=doubleSearch(_tasks,_resources,son_chromosome);//修正选择加极端重新选择 保留较好的个体
			}else {
				 son = new Individual(son_chromosome, project,mark);	
			 }
			break;
		    }
		case 2:son = new Individual(son_chromosome, project,mark);break;//遗传搜索
		case 3:son = new Individual(son_chromosome, project,mark);break;//极端搜索
		}
		return son;
	}
	
	/*
	 * @param tMutationRate 任务变异概率
	 * @param rMutationRate 资源序列变异概率
	 * 遗传算法二代个体变异
	 * @return son 变异生成的个体
	 */
	public Individual mutationPopulation(double tMutationRate,double rMutationRate) {
		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();
		List<Integer> _tasks = new ArrayList<>();
		List<Integer> _resources = new ArrayList<>();
		List<Integer> tasksChrom =this.chromosome.get(0);
		List<Integer> ressChrom = this.chromosome.get(1);
		int chromosomeLength = tasksChrom.size();
		for (int i =0; i<chromosomeLength; i++){
			_tasks.add(tasksChrom.get(i));
			_resources.add(ressChrom.get(i));
		}
		//相邻交换1  根据变异概率
		for (int geneIndex = 0; geneIndex < chromosomeLength - 1; geneIndex++) {
			if (tMutationRate > Math.random()) {
				int taskGene1=_tasks.get(geneIndex);
				int resourceGene1=_resources.get(geneIndex);
				int taskGene2=_tasks.get(geneIndex+1);
				int resourceGene2=_resources.get(geneIndex+1);
				
				Task t1 = project.getTasks().get(taskGene1 - 1);
				Task t2 = project.getTasks().get(taskGene2 - 1);
				if (!project.isPredecessor(t1, t2)) {//t1不是t2的紧前任务
					// 交换两个位置上的任务编号以及资源编号
					_tasks.set(geneIndex, taskGene2);
					_tasks.set(geneIndex+1, taskGene1);
					_resources.set(geneIndex, resourceGene2);
					_resources.set(geneIndex+1, resourceGene1);
				}
			}
			/*if (rMutationRate > Math.random()) {
				// 每个任务可供选择的资源集合
				// 任务ID
				int gene=_tasks.get(geneIndex);
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// 在可行资源中，随机选择一种
				double  randnum = Math.random();
				int r = (int) (randnum * capapleResource.size());
				_resources.set(geneIndex, capapleResource.get(r));
			}*/
		}
		for (int geneIndex = 0; geneIndex < chromosomeLength - 1; geneIndex++) {
			if (rMutationRate > Math.random()) {
				// 每个任务可供选择的资源集合
				// 任务ID
				int gene=_tasks.get(geneIndex);
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// 在可行资源中，随机选择一种
				double  randnum = Math.random();
				int r = (int) (randnum * capapleResource.size());
				_resources.set(geneIndex, capapleResource.get(r));
			}
		}
		son_chromosome.add(_tasks);
		son_chromosome.add(_resources);
		Individual son = new Individual(son_chromosome,project,1);//1 是mark
		return son;
	}
	   /*
     * @param rMutationRate 资源序列变异率
     * @param k 第几种邻结构任务序列变异
     */
	public Individual mutationPopulationV( double rMutationRate,int neighborType) {
		List<List<Integer>> son_chromosome=new ArrayList<>();
		List<Integer> tasks=this.getChromosome().get(0);
		List<Integer>  ress=this.getChromosome().get(1);
		int taskLength=tasks.size();
		List<Integer> _tasks=new ArrayList<>();
		List<Integer> _ress=new ArrayList<>();
		for(int i=0;i<taskLength;i++) {
			int taskid=tasks.get(i);
			_tasks.add(taskid);
			int resid=ress.get(i);
			/*if(Math.random()<this.getProject().getNSGAV_II().rMutationRate) {
				// 每个任务可供选择的资源集合
				// 任务ID
				int gene=taskid;
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// 在可行资源中，随机选择一种
				double  randnum = Math.random();
				int r = (int) (randnum * capapleResource.size());
				resid=capapleResource.get(r);
			}*/
			_ress.add(resid);
		}
		switch(neighborType){
		case 0:variableNeighbor_NeighborOne(taskLength,_tasks,_ress);break;
		case 1:variableNeighbor_NeighborProb(taskLength,_tasks,_ress);break;
		case 2:variableNeighbor_ForwardInsert(taskLength, _tasks,_ress);break;
		case 3:variableNeighbor_BackWordInsert(taskLength, _tasks,_ress);break;
		case 4:variableNeighbor_adjustSequence(taskLength, _tasks,_ress);break;
		}
		for (int geneIndex = 0; geneIndex < taskLength - 1; geneIndex++) {
			if (rMutationRate > Math.random()) {
				// 每个任务可供选择的资源集合
				// 任务ID
				int gene=_tasks.get(geneIndex);
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// 在可行资源中，随机选择一种
				double  randnum = Math.random();
				int r = (int) (randnum * capapleResource.size());
				_ress.set(geneIndex, capapleResource.get(r));
			}
		}
		son_chromosome.add(_tasks);
		son_chromosome.add(_ress);
		double prob=Math.random();
		if(prob<project.getParameter().getResSpp()) {
			Individual son=doubleSearch(_tasks,_ress,son_chromosome);//
			return son;
		}
		Individual son = new Individual(son_chromosome,project,NSGAV_II.MARK);//1.0 随机修正
		return son;
	}

    /*
      * 两轮搜索 返其大者
     */
	private Individual doubleSearch(List<Integer> _tasks, List<Integer> _ress, List<List<Integer>> son_chromosome) {
		List<List<Integer>> copy_chromosome=new ArrayList<>();
		List<Integer> copy_tasks=new ArrayList<>();
		List<Integer> copy_ress=new ArrayList<>();
		for(int i=0;i<_tasks.size();i++) {
			copy_tasks.add(_tasks.get(i));
			copy_ress.add(_ress.get(i));
		}
		copy_chromosome.add(copy_tasks);
		copy_chromosome.add(copy_ress);
		Individual son1=new Individual(copy_chromosome,project,3);//一个是补全
		Individual son2 = new Individual(son_chromosome,project,1);
		int flag=Tools.Dominated(son1, son2, project);
		if(flag==1) {
			return son1;
		}else if(flag==2){
			return son2;
		}else if(son1.obj[0]*son1.obj[1]>son2.obj[0]*son2.obj[1]) {
			return son2;
		}else return son1;//改变
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
		// 可执行任务集合
		List<Integer> executableTaskIDS = new ArrayList<Integer>();	
		List<Task> tasks = project.getTasks();
		//double rand3=Math.random();
		for (int i = 0; i < project.getN(); i++) {  
			executableTaskIDS.clear();
			for (int k = 0; k < tasks.size(); k++) {
				if (taskslist.get(k).pretasknum == 0){//找到没有紧前任务的任务集合作为优先执行任务集合
					executableTaskIDS.add(tasks.get(k).getTaskID());
				}
			}
			if (executableTaskIDS.size() == 0){
				break;
			}
			//1随机  2最大紧后集   34先选择资源然后选择任务3最大执行时间 4 最大紧后集执行时间和
			/*double rand3=Math.random();
			if(rand3<0.7) {
			   scheduleTaskByRandomRule(executableTaskIDS,taskList);
			}else if(rand3<0.8) {
				scheduleTaskByMaxSuccessorsRule(executableTaskIDS,taskList);
			}else if(rand3>0.9) {
				scheduleTaskByMaxProcessTimeRule(executableTaskIDS,taskList);
			}else {
				scheduleTaskByMaxSumSuccessorsProcessTimeRule(executableTaskIDS,taskList);
			}*/
			double rand3=Math.random();
			int A = (int) ( rand3 * executableTaskIDS.size());
			int currentTaskID = executableTaskIDS.get(A);//优先任务集合中随机选择一个作为当前执行任务
			taskList.add(currentTaskID);
            taskslist.get(currentTaskID -1).pretasknum = -1;   //当前任务已经被使用，做上标记以防止下次被选用
			//处理后续任务
            List<Integer> suc=this.project.getTasks().get(currentTaskID-1).getsuccessortaskIDS();
            for(int k=0;suc!=null&&k<suc.size();k++) {
            	taskslist.get(suc.get(k)-1).pretasknum--;	
            }
			/*for (int k = 0; k < tasks.size(); k++) {//可以优化  遍历紧后集
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
					//？？？应该将该任务从前序任务集中删除
				}
			}*/
		}
		this.chromosome.add(taskList);
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
				List<Integer> suc=this.project.getTasks().get(currentTaskID-1).getsuccessortaskIDS();
	            for(int k=0;suc!=null&&k<suc.size();k++) {
	            	taskslist.get(suc.get(k)-1).pretasknum--;	
	            }
				/*for (int k = 0; k < tasks.size(); k++) {
					//把所有以任务j为前置任务的前置任务数减1；
					if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
						taskslist.get(k).pretasknum--;	
					}
				}*/
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
			List<Integer> suc=this.project.getTasks().get(currentTaskID-1).getsuccessortaskIDS();
            for(int k=0;suc!=null&&k<suc.size();k++) {
            	taskslist.get(suc.get(k)-1).pretasknum--;	
            }
			/*for (int k = 0; k < tasks.size(); k++) {
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
				}
			}*/
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
			List<Integer> suc=this.project.getTasks().get(currentTaskID-1).getsuccessortaskIDS();
            for(int k=0;suc!=null&&k<suc.size();k++) {
            	taskslist.get(suc.get(k)-1).pretasknum--;	
            }
			/*for (int k = 0; k < tasks.size(); k++) {
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
				}
			}*/
		}
		
	}

	private void scheduleTaskByRandomRule(List<Integer> executableTaskIDS, List<Integer> taskList) {
		double rand1 = Math.random();
		while(executableTaskIDS.size()>0) {
			int A = (int) ( rand1 * executableTaskIDS.size());
			int currentTaskID = executableTaskIDS.get(A);//优先任务集合中随机选择一个作为当前执行任务
			executableTaskIDS.remove(A);
			taskList.add(currentTaskID);
			taskslist.get(currentTaskID -1).pretasknum = -1;   //当前任务已经被使用，做上标记以防止下次被选用
			
			//处理后续任务
			List<Integer> suc=this.project.getTasks().get(currentTaskID-1).getsuccessortaskIDS();
            for(int k=0;suc!=null&&k<suc.size();k++) {
            	taskslist.get(suc.get(k)-1).pretasknum--;	
            }
			/*for (int k = 0; k < tasks.size(); k++) {
				//把所有以任务j为前置任务的前置任务数减1；
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
					//？？？应该将该任务从前序任务集中删除
				}
			}*/
		}
	}
	private void learnObjCompute(boolean initial) {
		List<Integer> resourceList = new ArrayList<Integer>();
		List<Integer> taskList = this.chromosome.get(0);
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
			int resourceid = list.get(B);
			resourceList.add(resourceid);
			IResource res=resourceslist.get(resourceid-1);
			this.singleCompute(curTask,res,endtime_res,workload);//动态计算
		}
		this.obj=new double[]{this.maxtime, this.cost};
		this.chromosome.add(resourceList);
		
	}
	/*
	 * 极端搜索 计算目标函数值
	 * 包括资源序列的重新选择设置
	 */
	public void objComputeReselectExtreme() {
		List<Integer> resourceList = new ArrayList<Integer>();
		List<Integer> taskList = this.chromosome.get(0);
		int[] endtime_res = new int[project.getM()];
		int workload[]= new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//用于记录每个资源释放时间
			endtime_res[j] = 0;
			workload[j]=0;
		}
	   double rand=Math.random();
	    int select=0;
	    if(rand<0) {//平衡加入随机project.getNSGAV_II().pr
	         select=0;//成本&&工期
		}else {
			double rand1=Math.random();
			if(rand1<1) {//0.5
				select=3;//工期
			}else {
				select=2;//成本
			}
		}
		for(int i = 0; i <taskList.size(); i++){
			int tid=taskList.get(i);
			ITask curTask = taskslist.get(tid-1);
			int resourceid=0;
			switch(select) {//可以提到前面进行优化
			case 0:{
				List<Integer> list = curTask.getresourceIDs();
				double rand2=Math.random();
				int B = (int) (rand2 * list.size());
				resourceid = list.get(B);
			}
			case 1:resourceid=selectResourceB(curTask,endtime_res,workload,0.5);break;//成本&&工期
			case 2:resourceid=selectResourceB(curTask,endtime_res,workload,1);break;//成本
			case 3:resourceid=selectResourceB(curTask,endtime_res,workload,0);break;//工期
			
			}
			resourceList.add(resourceid);
			IResource res=resourceslist.get(resourceid-1);
			this.singleCompute(curTask,res,endtime_res,workload);//动态计算
		}
		this.obj=new double[]{this.maxtime, this.cost};
		this.chromosome.add(1, resourceList);
	}
	/*
	 * 加权成本和工期
	 */
	private int selectResourceW(ITask curTask, int[] endtime_res, int[] workload,double pp) {
		List<IResource> ress=this.resourceslist;
		List<Integer> list = curTask.getresourceIDs();
		int resourceid=list.get(0);
		List<Integer> list1=new ArrayList<>(list);
		double Obj[]=this.getProject().getTempObj();
		if(Obj==null) {
			Obj=new double[2];
			Obj[0]=this.getProject().getBorderDuration();
			Obj[1]=this.getProject().getBorderCost();
		}
		final double[] tempObj=Obj;
		Collections.sort(list1, new Comparator<Integer>() {

			@Override
			public int compare(Integer arg0, Integer arg1) {
				double cost1=ress.get(arg0-1).getSalary()*curTask.getStandardDuration()/Math.floor(ress.get(arg0-1).getSkillsInfo().get(curTask.getSkillType()));
				double cost2=ress.get(arg1-1).getSalary()*curTask.getStandardDuration()/Math.floor(ress.get(arg1-1).getSkillsInfo().get(curTask.getSkillType()));
				double finish1=endtime_res[arg0-1]+curTask.getStandardDuration()/Math.floor(ress.get(arg0-1).getSkillsInfo().get(curTask.getSkillType()));
				double finish2=endtime_res[arg1-1]+curTask.getStandardDuration()/Math.floor(ress.get(arg1-1).getSkillsInfo().get(curTask.getSkillType()));
				double weight1=(tempObj[1]-cost1)/tempObj[1]+(tempObj[0]-finish1)/tempObj[0];
				double weight2=(tempObj[1]-cost2)/tempObj[1]+(tempObj[0]-finish2)/tempObj[0];
				if(weight1>weight2) {
					return 1;
				}else if(weight1<weight2) {
					return -1;
				}
				return 0;
			}
		});
		resourceid=list1.get(0);
		return resourceid;
	}
	
	//选择资源
	private int selectResourceB(ITask curTask, int[] endtime_res, int[] workload,double pp) {
		//两个规则 成本最低  工期最短
		List<IResource> ress=this.resourceslist;
		List<Integer> list = curTask.getresourceIDs();
		double rand=Math.random();
		int resourceid=list.get(0);
		List<Integer> list1=new ArrayList<>(list);
		String qtype=curTask.getSkillType();
		int  requireLevel=(int) curTask.getSkillLevel();
		HashMap<String,Double[]> testQuesttionMap=project.getTestQuesttionMap();
		Double[] time=testQuesttionMap.get(qtype);
		if(rand<pp&&list1.size()>1) {
			//-------->成本最低
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer arg0, Integer arg1) {
					IResource r1=ress.get(arg0-1);
					IResource r2=ress.get(arg1-1);
					double level1=r1.getSkillsInfo().get(qtype);
					double level2=r2.getSkillsInfo().get(qtype);
					double cost1=r1.getSalary()*time[(int) level1];//标准计算r1.getSalary()*curTask.getDuaration()/time[requireLevel]*time[(int) level1];
					double cost2=r2.getSalary()*time[(int) level2];
					if(cost1>cost2) {
						return 1;
					}else if(cost1<cost2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
		}else if(rand<1&&list1.size()>1) {
			//-------->最早完成
			//升序排序
			Collections.sort(list1, new Comparator<Integer>() {
				@Override
				public int compare(Integer arg0, Integer arg1) {
					IResource r1=ress.get(arg0-1);
					IResource r2=ress.get(arg1-1);
					double level1=r1.getSkillsInfo().get(qtype);
					double level2=r2.getSkillsInfo().get(qtype);
					double finish1=endtime_res[arg0-1]+curTask.getDuaration()/time[requireLevel]*time[(int) level1];
					double finish2=endtime_res[arg1-1]+curTask.getDuaration()/time[requireLevel]*time[(int) level2];
					if(finish1>finish2) {
						return 1;
					}else if(finish1<finish2) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
		}
		/*else if(list1.size()>1){
			//最早可以升级的
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
		}*/
		resourceid=list1.get(0);
		return resourceid;
	}
	//选择资源
	private int selectResource(ITask curTask, int[] endtime_res, int[] workload) {
		List<IResource> ress=this.resourceslist;
		List<Integer> list = curTask.getresourceIDs();
		double rand=Math.random();
		int resourceid=list.get(0);
		String qtype=curTask.getSkillType();
		int  requireLevel=(int) curTask.getSkillLevel();
		HashMap<String,Double[]> testQuesttionMap=project.getTestQuesttionMap();
		Double[] time=testQuesttionMap.get(qtype);
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
					IResource r1=ress.get(arg0-1);
					IResource r2=ress.get(arg1-1);
					double level1=r1.getSkillsInfo().get(qtype);
					double level2=r2.getSkillsInfo().get(qtype);
					double cost1=r1.getSalary()*time[(int) level1];//标准计算r1.getSalary()*curTask.getDuaration()/time[requireLevel]*time[(int) level1];
					double cost2=r2.getSalary()*time[(int) level2];
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
				public int compare(Integer arg0, Integer arg1) {
					IResource r1=ress.get(arg0-1);
					IResource r2=ress.get(arg1-1);
					double level1=r1.getSkillsInfo().get(qtype);
					double level2=r2.getSkillsInfo().get(qtype);
					double finish1=endtime_res[arg0-1]+curTask.getDuaration()/time[requireLevel]*time[(int) level1];
					double finish2=endtime_res[arg1-1]+curTask.getDuaration()/time[requireLevel]*time[(int) level2];
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

	public void singleCompute(ITask curTask, IResource res, int[] endtime_res, int[] workload){
		
		ITask task = curTask;
		IResource resource = res;
	 /*  if(!canInsert(resource, task,workload)){//不存在紧前调度
			//阶段性计算：更新资源可用时间，更新任务开始结束时间
			phaseCompute(curTask, res, endtime_res,workload);
			//更新技能水平：当前技能表，技能执行时间表	
			updateSkill(resource, task);
			//计算目标值： 工期 成本 
			aimCompute(task, resource,endtime_res);
		}else{
			//仅更新目标值和任务开始结束时间，不更新技能is wrong   需要更新该资源分配的任务的完成时间和资源技能
			//updateResourceAnd
			aimCompute(task, resource,endtime_res);//左移调度下移动插入点的技能必须高于任务的所需技能  采用资源更新
		} */
		phaseCompute(curTask, res, endtime_res,workload);
		//更新技能水平：当前技能表，技能执行时间
		updateSkill(resource, task);
		//计算目标值： 工期 成本 
		aimCompute(task, resource,endtime_res);
	}
	//每次遍历 花费时间  直接将改变endTime的资源同maxTime对比
	public void aimCompute(ITask curTask, IResource curResource, int[] endtime_res){
		/*for(int i=0;i<endtime_res.length;i++) {
			if(this.maxtime<endtime_res[i]){
				this.maxtime =endtime_res[i];
			}
		}*///减少循环 只更新改变的
		int rid=curResource.getResourceID();
		if(this.maxtime<endtime_res[rid-1]){
			this.maxtime =endtime_res[rid-1];
		}
		int realDuration = curTask.getFinishTime() - curTask.getStartTime();
		double salary = curResource.getSalary();
		this.cost +=Math.ceil (realDuration*salary);
	}
	
	public void phaseCompute(ITask curTask, IResource res, int[] endtime_res, int[] workload){
		
		int endtime = 0;
		ITask curtask = curTask;
		int rid=res.getResourceID();
		int tid=curTask.getTaskid();
		String qtype = curtask.getSkillType();
		IResource resource = resourceslist.get(rid-1);
		double qinitLevel = resource.getSkillsInfo().get(qtype);
		/*qinit=Math.floor(qinit);//1.3 add*/
		//得到所有前置任务,循环每一前置任务，取最晚结束时间
		List<Integer> pretaskids = curtask.getPredecessorIDs();
		for (int j = 0; j < pretaskids.size();j++){
			int id=pretaskids.get(j);
			int finish=taskslist.get(id-1).getFinishTime();
			if (endtime < finish){
				endtime = finish;
			}
		}
		//当前任务所对应的资源最晚时间
		if (endtime < endtime_res[rid-1]){
			endtime = endtime_res[rid-1];
		}
		//设置当前任务的开始时间及完成时间
		//计算结束时间  需要查询标准考题
		ITask t=taskslist.get(tid-1);
		Double[] examTime=this.project.getTestQuesttionMap().get(qtype);
		//查询项目需要的技能水平
		double requireLevel=t.getSkillLevel();
		double excuTime=t.getDuaration()/examTime[((int) requireLevel)]*examTime[((int) qinitLevel)];
		t.setStartAndFinish(endtime, endtime+excuTime);
		/*taskslist.get(tid-1).setstarttime(endtime,qinit);*/
		//更新当前任务资源的最后完工时间
		endtime_res[rid-1] = t.finishTime;
		/*workload[rid-1]+=(int)taskslist.get(tid-1).getStandardDuration()/qinit;*/
		
	}
	
	public boolean updateSkill(IResource resource, ITask task){
		String qtype = task.getSkillType();
		double qinit = resource.getSkillsInfo().get(qtype);
		//更新当前技能水平表
		double qfinal=qinit;
		if(qinit>=Case.getMaxLevel()) {//不再升级
			
		}else {
			int[] time={task.getStartTime(),task.getFinishTime()};
			double excute=time[1]-time[0];
			double oldSustain=resource.getSustainTime().get(qtype);
			double newSustain=excute+oldSustain;
			double nextLevelTime = 0;
			nextLevelTime = resource.getNextLevelTime().get(qtype)[((int) qinit)];
			
			if(newSustain>=nextLevelTime) {//升级 持有经验时间归0
				qfinal=qinit+1;
				resource.putSkillsInfo(qtype, qfinal);
				resource.putSustainTime(qtype, 0);
			}else {
				resource.putSustainTime(qtype, newSustain);
			}
		}
		if(Math.floor(qfinal)-Math.floor(qinit)>=1){//技能跃迁，重新分配任务可用资源   
			for(int i = 0; i < taskslist.size(); i++){//可以优化  使用关联技能的任务集循环
				ITask tmptask = taskslist.get(i);
				if(tmptask.getSkillType().equals(qtype) 
						&& tmptask.getSkillLevel()<=qfinal
						&& !tmptask.getresourceIDs().contains(resource.getResourceID())){
							tmptask.addResourceIDs(resource.getResourceID());
				}
			}
		}
		return false;
	}
	
	//紧前调度  是否可以左移调度 ？ 但是未将时间表排序判断   
	public boolean canInsert(IResource resource, ITask task, int[] workload){
		@SuppressWarnings("rawtypes")
		String skillType = task.getSkillType();
		double stanDuration = task.getStandardDuration();
		HashMap<String,HashMap<int[],Double>> timetable = resource.getSkillTimetable();
		double skillLevel = task.getSkillLevel();
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
				if(rightskill >= skillLevel && stanDuration/rightskill <= (time[0]-last[1])){
					List<Integer> preids = task.getPredecessorIDs();
					//是否满足紧前关系
					for(int i = 0; i < preids.size();i++){
						if(taskslist.get(preids.get(i)-1).getFinishTime() >= last[1]){
							return false;
						};
					}
					task.setstarttime(last[1], rightskill);
					/*workload[resource.getResourceID()-1]+=stanDuration/rightskill;*/
					//更新资源技能及资源安排的任务的执行时间 好像不可行  影响是全局的
					return true;
				}
			}
		}
		
		/*@SuppressWarnings("rawtypes")
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
	public Individual binaryTournamentForHv(Individual indiv1, Individual indiv2) {
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
			if (indiv1.getCrowDistance() < indiv2.getCrowDistance()) {
				individual = indiv2;
			}
			if (indiv1.getCrowDistance()> indiv2.getCrowDistance()) {
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
    
	/*
	 * @param  mark 搜索标志
	 * @param 
	 * 个体变邻搜索
	 */
	public Individual variableNeighborDecent(int mark) {
		Individual compare=this;
		//随机序列 1-5
		List<Integer> sequence=new ArrayList<>();
		int n=5;
		for(int i=0;i<n;i++) {
			sequence.add(i+1);
		}
		Collections.shuffle(sequence);
		int k=1;//第一个邻结构的序号
		while(k<=5) {
			Individual son=compare.variableNeighborWithFirst(sequence.get(k-1),mark);//使用随机序列顺序搜索sequence.get(k-1)
			int flag=Tools.Dominated(son,compare,this.project);
			if(flag==1||flag==0) {
				if(flag==1) {
					compare=son;
				}
			}else {
				k++;
			}
		}
		/*if(Tools.Dominated(this,compare,this.project)==1) {
			compare=this;
		}*/
		return compare;
	}
	/*
	 * @param mark 搜索标志
	 * 个体变邻搜索 将互不支配的解存储
	 */
	public List<Individual> variableNeighborDecentVP(int mark) {
		List<Individual> savelist=new ArrayList<>();
		Individual compare=this;
		int neighborType=1;//第一个邻结构的序号
		while(neighborType<=5) {
			Individual son=compare.variableNeighborWithFirst(neighborType,mark);
			int flag=Tools.Dominated(son,compare,this.project);
			if(flag==1) {
				compare=son;
			}else {
				if(flag==0) {
					savelist.add(new Individual(son));
				}
				neighborType++;
			}
		}
		if(Tools.Dominated(this,compare,this.project)==1) {
			compare=this;
		}
		savelist.add(compare);
		return savelist;
	}
    /*
     * @param neighborType 邻解结构
     * @param mark 变邻搜索类型  1-双链表修正 3极端重新选择
     * 第一个提高 变邻搜索  
     */
	private Individual variableNeighborWithFirst(int neighborType, int mark) {
		double rMutationRate=project.getNSGAV_II().rMutationRate;
		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();
		List<Integer> _tasks = new ArrayList<>();
		List<Integer> _ress = new ArrayList<>();
		List<Integer> taskChrom=this.chromosome.get(0);
		List<Integer> resChrom=this.chromosome.get(1);
		int taskLength = taskChrom.size();
		for (int i =0; i<taskLength; i++){
			_tasks.add(taskChrom.get(i));	
		    _ress.add(resChrom.get(i));
		}
		switch(neighborType) {
		   case 0:  variableNeighbor_NeighborOne(taskLength,_tasks,_ress); break;
		   case 1:  variableNeighbor_NeighborProb(taskLength,_tasks,_ress); break;
		   case 2:  variableNeighbor_ForwardInsert(taskLength,_tasks,_ress); break;
		   case 3:  variableNeighbor_BackWordInsert(taskLength,_tasks,_ress); break;
		   /*case 6:  variableNeighbor_SwapRandom(taskLength,_tasks,_ress); break;*/
		   case 4:  this.variableNeighbor_adjustSequence(taskLength,_tasks,_ress);break;
		}
		for (int geneIndex = 0; geneIndex < taskLength - 1; geneIndex++) {
			if (rMutationRate > Math.random()) {
				// 每个任务可供选择的资源集合
				// 任务ID
				int gene=_tasks.get(geneIndex);
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// 在可行资源中，随机选择一种
				double  randnum = Math.random();
				int r = (int) (randnum * capapleResource.size());
				_ress.set(geneIndex, capapleResource.get(r));
			  }
		}
		son_chromosome.add(_tasks);
		son_chromosome.add(_ress);
		Individual son =null;
		switch(mark) {
		case 1:{//变邻搜索
			double prob=Math.random();
			if(prob<project.getNSGAV_II().resSp) {
				 son=doubleSearch(_tasks,_ress,son_chromosome);//修正选择加极端重新选择 保留较好的个体
			}else {
				 son = new Individual(son_chromosome, project,mark);	
			 }
			break;
		    }
		case 3:son = new Individual(son_chromosome, project,mark);break;//极端搜索
		}
		return son;
	}
	/*
	 * 后插 保证原来的相对顺序不变 实际上是后移
	 */
	private void variableNeighbor_BackWordInsert(int taskLength, List<Integer> _tasks, List<Integer> _ress) {
		int k=0;
		while(true) {
			int geneIndex1=(int)(Math.random()*(taskLength));
			int geneIndex2=(int)(Math.random()*(taskLength));
			while(geneIndex1==geneIndex2) {
				geneIndex2=(int)(Math.random()*(taskLength));
			} 
			if(geneIndex1>geneIndex2) {//geneIndex1在前
				int a=geneIndex1;
				geneIndex1=geneIndex2;
				geneIndex2=a;
			}
			int taskGene1=_tasks.get(geneIndex1);//	ID
			int resGene1=_ress.get(geneIndex1);
			int insertPos = geneIndex1;
			for(int i=geneIndex1+1;i<=geneIndex2;i++) {
				int taskId=_tasks.get(i);
				List<Integer> preList=taskslist.get(taskId-1).getPredecessorIDs();
				if(preList.contains(taskGene1)) {
					insertPos=i;
					break;
				}
			}
			if(insertPos!=geneIndex1+1) {
				_tasks.add(insertPos, taskGene1);
				_ress.add(insertPos, resGene1);
				_tasks.remove(geneIndex1);
				_ress.remove(geneIndex1);
				break;
			}
			/*k++;*/
		}
		
	}

	/*
	 * 前插 保证原来的相对顺序不变 实际上是前移
	 */
	private void variableNeighbor_ForwardInsert(int taskLength, List<Integer> _tasks, List<Integer> _ress) {
		int k=0;
		while(true) {
			int geneIndex1=(int)(Math.random()*(taskLength));
			int geneIndex2=(int)(Math.random()*(taskLength));
			while(geneIndex1==geneIndex2) {
				geneIndex2=(int)(Math.random()*(taskLength));
			} 
			if(geneIndex1<geneIndex2) {//geneIndex1在后
				int a=geneIndex1;
				geneIndex1=geneIndex2;
				geneIndex2=a;
			}
			int taskGene1=_tasks.get(geneIndex1);//	ID
			int resGene1=_ress.get(geneIndex1);
			List<Integer> preList=taskslist.get(taskGene1-1).getPredecessorIDs();
			int insertPos = geneIndex1;
			for(int i=geneIndex1-1;i>=geneIndex2;i--) {
				int taskId=_tasks.get(i);
				if(preList.contains(taskId)) {
					insertPos=i+1;
					break;
				}
			}
			if(insertPos!=geneIndex1) {
				_tasks.remove(geneIndex1);
				_ress.remove(geneIndex1);
				_tasks.add(insertPos, taskGene1);
				_ress.add(insertPos, resGene1);
				break;
			}
			/*k++;*/
		}
	}

	/*
	 *  
     * @param taskLength 任务序列长度
     * @param _tasks 新的任务序列
     * @param _ress 新的资源序列
     *  子序列调整
	 */
	private void variableNeighbor_adjustSequence(int taskLength, List<Integer> _tasks, List<Integer> _ress) {
		int geneIndex1=(int)(Math.random()*(taskLength-project.getNSGAV_II().len));//索引
		int geneIndex2=geneIndex1+project.getNSGAV_II().len;

		List<ITask>  taskslist=this.getTaskslist();
		List<Integer> childSequence=new ArrayList<>();
		int[] preNum=new int[taskLength];//两种策略 从前往后到geneIndex2  geneIndex1 geneIndex2单独判断
		Map<Integer,List<Integer>> suc=new HashMap<>();
		int[] ress=new int[taskLength];
		for(int i=geneIndex2;i>=geneIndex1;i--) {
			int taskId=_tasks.get(i);
			int resId=_ress.get(i);
			List<Integer> pre=taskslist.get(taskId-1).getPredecessorIDs();
			childSequence.add(taskId);
			ress[taskId-1]=resId;
			for(int j=i-1;j>=geneIndex1;j--) {
				int taskPre=_tasks.get(j);
				if(pre.contains(taskPre)) {
					preNum[taskId-1]++;
					List<Integer> sucList=suc.get(taskPre);
					if(sucList==null) {
						sucList=new ArrayList<>();
						sucList.add(taskId);
						suc.put(taskPre, sucList);
					}else {
						sucList.add(taskId);
					}
				}
			}
		}
		List<Integer> executableTaskIDS = new ArrayList<Integer>();	
		for(int i=0;i<childSequence.size();i++) {
			executableTaskIDS.clear();
			for(int j=0;j<childSequence.size();j++) {
				int taskId=childSequence.get(j);
					if(preNum[taskId-1]==0) {
						executableTaskIDS.add(taskId);
					}
			}
			if (executableTaskIDS.size() == 0){
				break;
			}
			double rand3=Math.random();
			int A = (int) ( rand3 * executableTaskIDS.size());
			int currentTaskID = executableTaskIDS.get(A);//优先任务集合中随机选择一个作为当前执行任务
			_tasks.set(geneIndex1+i, currentTaskID);
			_ress.set(geneIndex1+i, ress[currentTaskID-1]);
			preNum[currentTaskID-1]=-1;
			//更新后继
			List<Integer> sucList=suc.get(currentTaskID);
			for(int m=0;sucList!=null&&m<sucList.size();m++) {
				int taskId=sucList.get(m);
				preNum[taskId-1]--;
			}
		}
		
	}
	  /*
     * @param taskLength 任务序列长度
     * @param _tasks 新的任务序列
     * @param _ress 新的资源序列
        * 含资源序列的相邻交换   概率决定
     */
	private void variableNeighbor_NeighborProb(int taskLength, List<Integer> _tasks, List<Integer> _ress) {
		double neighborP=taskLength<=100?0.05:0.05;//步长0.03:0.015 
		for (int geneIndex = 0; geneIndex < taskLength - 1; geneIndex++) {
			if (neighborP > Math.random()) {
				int taskGene1=_tasks.get(geneIndex);
				int taskGene2=_tasks.get(geneIndex+1);
				int resGene1=_ress.get(geneIndex);
				int resGene2=_ress.get(geneIndex+1);
				Task t1 = project.getTasks().get(taskGene1 - 1);
				Task t2 = project.getTasks().get(taskGene2 - 1);
				if (!project.isPredecessor(t1, t2)) {//t1不是t2的紧前任务
					// 交换两个位置上的任务编号以及资源编号
					_tasks.set(geneIndex, taskGene2);
					_tasks.set(geneIndex+1, taskGene1);
					_ress.set(geneIndex, resGene2);
					_ress.set(geneIndex+1, resGene1);
				}
			}
		}
		
	}
    /*
     * @param taskLength 任务序列长度
     * @param _tasks 新的任务序列
     * @param _ress 新的资源序列
       * 含资源序列的相邻交换   随机一次
     */
	private void variableNeighbor_NeighborOne(int taskLength, List<Integer> _tasks, List<Integer> _ress) {
		int k=0;
		while(k<5) {
			int geneIndex=(int)(Math.random()*(taskLength-1));
			int taskGene1=_tasks.get(geneIndex);
			int taskGene2=_tasks.get(geneIndex+1);
			int resGene1=_ress.get(geneIndex);
			int resGene2=_ress.get(geneIndex+1);
			Task t1 = project.getTasks().get(taskGene1 - 1);
			Task t2 = project.getTasks().get(taskGene2 - 1);
			if (!project.isPredecessor(t1, t2)) {//t1不是t2的紧前任务
				// 交换两个位置上的任务编号以及资源编号
				_tasks.set(geneIndex, taskGene2);
				_tasks.set(geneIndex+1, taskGene1);
				_ress.set(geneIndex, resGene2);
				_ress.set(geneIndex+1, resGene1);
				/*break;*/
			}
			k++;
		}
		
	}

	/*
	 * @param  taskLength 任务序列长度
	 * @param _tasks 复制的任务序列及新的任务序列
	  * 重新调整任务子序列顺序
	 */
	private void variableNeighbor_adjustSequence(int taskLength, List<Integer> _tasks) {
		int geneIndex1=(int)(Math.random()*(taskLength-project.getNSGAV_II().len));//索引
		int geneIndex2=geneIndex1+project.getNSGAV_II().len;

		List<ITask>  taskslist=this.getTaskslist();
		List<Integer> childSequence=new ArrayList<>();
		int[] preNum=new int[taskLength];//两种策略 从前往后到geneIndex2  geneIndex1 geneIndex2单独判断
		Map<Integer,List<Integer>> suc=new HashMap<>();
		for(int i=geneIndex2;i>=geneIndex1;i--) {
			int taskId=_tasks.get(i);
			List<Integer> pre=taskslist.get(taskId-1).getPredecessorIDs();
			childSequence.add(taskId);
			for(int j=i-1;j>=geneIndex1;j--) {
				int taskPre=_tasks.get(j);
				if(pre.contains(taskPre)) {
					preNum[taskId-1]++;
					List<Integer> sucList=suc.get(taskPre);
					if(sucList==null) {
						sucList=new ArrayList<>();
						sucList.add(taskId);
						suc.put(taskPre, sucList);
					}else {
						sucList.add(taskId);
					}
				}
			}
		}
		List<Integer> executableTaskIDS = new ArrayList<Integer>();	
		for(int i=0;i<childSequence.size();i++) {
			executableTaskIDS.clear();
			for(int j=0;j<childSequence.size();j++) {
				int taskId=childSequence.get(j);
					if(preNum[taskId-1]==0) {
						executableTaskIDS.add(taskId);
					}
			}
			if (executableTaskIDS.size() == 0){
				break;
			}
			double rand3=Math.random();
			int A = (int) ( rand3 * executableTaskIDS.size());
			int currentTaskID = executableTaskIDS.get(A);//优先任务集合中随机选择一个作为当前执行任务
			_tasks.set(geneIndex1+i, currentTaskID);
			preNum[currentTaskID-1]=-1;
			//更新后继
			List<Integer> sucList=suc.get(currentTaskID);
			for(int m=0;sucList!=null&&m<sucList.size();m++) {
				int taskId=sucList.get(m);
				preNum[taskId-1]--;
			}
		}
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


	public int getHashCode() {
		return hashCode;
	}


	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
   @Override
	public boolean equals(Object o) {
		if (!(o instanceof Individual)) {
			return false;
		}

		Individual individual = (Individual)o;
		return this.hashCode == individual.getHashCode();
	}
}
