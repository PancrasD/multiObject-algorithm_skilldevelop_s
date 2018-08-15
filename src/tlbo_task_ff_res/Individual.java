package tlbo_task_ff_res;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Individual {
	// ����Ⱦɫ���ά��
	static final int chromosomeLayer = 2;
	// ÿ�������Ŀ�꺯������
	static final int objNum = 2;
	// ����������
	private List<ITask> taskslist = new ArrayList<ITask>(); 
	//��������Դ
	private List<IResource> resourceslist = new ArrayList<IResource>();
	// Ⱦɫ��
	private List<List<Integer>> chromosome = new ArrayList<List<Integer>>();
	
	// Ⱦɫ�������
	private List<List<Double>> chromosomeDNA = new ArrayList<List<Double>>();	
	// Ŀ�꺯��
	private double[] obj = new double[objNum];
	private int maxtime = 0;
	private double cost = 0.0;
	/*//��¼����ԾǨ����
	private int jumpTimes = 0;
	//��¼��Ӱ����������
	private int influenceTasks = 0;*/

	// ��������Ⱥ�еķ�֧��ȼ�
	private int non_dominatedRank;
	// ������ڷ�֧����е�ӵ����
	private double crowDistance;
	
	private Case project;
	
	private boolean isTeacher = false;

		
	public Individual(List<List<Integer>> _chromosome,List<List<Double>> _chromosomemDNA,Case project) {
		// ���������Ⱦɫ��
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		this.chromosome = _chromosome;
		this.chromosomeDNA = _chromosomemDNA;

		//��������Ŀ�꺯��ֵ�������������ͣʱ����������list
		learnObjCompute();		
	}
	
	public Individual(List<List<Integer>> _chromosome,Case project) {
		// ���������Ⱦɫ��
		this.project = project;
		settaskslist(project);	
		setResourcesList(project);
		this.chromosome = _chromosome;
		//this.chromosomeDNA = _chromosomemDNA;

		//��������Ŀ�꺯��ֵ�������������ͣʱ����������list
		learnObjCompute();		
	}
		
	//
	public Individual(Case project) {
		this.project = project;
		settaskslist(project);
		setResourcesList(project);
		//�������DNA����������
		deciphering( project);
		//���������Դ���У�����Ŀ�꺯��ֵ
		learnObjCompute();
		
		//��������Ŀ�꺯��ֵ�������������ͣʱ����������list
		/*objCompute(project);*/

	}

	private void settaskslist(Case project){
		for (int i = 0; i < project.getTasks().size();i++){
			ITask itask = new ITask(project.getTasks().get(i));
			taskslist.add(itask);
		}
	}
	
	private void setResourcesList(Case project) {
		for(int i = 0; i<project.getResources().size(); i++){
			IResource iresource = new IResource(project.getResources().get(i));
			resourceslist.add(iresource);
		}
	}
	
	//��indiv����ȡnums������
	public void insert(List<Integer> _tasks, List<Double> _taskdna, int nums, Individual indiv){
		int m = 0;
		for(int n = 0; n < nums; n++){
			boolean met = false;
			while(!met){
				Integer task = indiv.chromosome.get(0).get(m);
				if(!_tasks.contains(task)){
					Double taskDNA = indiv.chromosomeDNA.get(0).get(m);
					_tasks.add(task);
					_taskdna.add(taskDNA);
					met = true;
				}
				m++;
			}
		}
	}
	
	//tlbo����
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
		//ʹ�ù�Ӭ�㷨  ��ζ�������Ӿ�����Smell-based search and vision-based search
		Individual[] smells=Smell_basedSearch(TLBOF.s,son_chromosome,son_chromosomeDNA,project);
		Individual son = vision_basedSearch(smells,project);
		return son;
	}
	

	//������ζ����
	public Individual[] Smell_basedSearch(int s, List<List<Integer>> son_chromosome, List<List<Double>> son_chromosomeDNA, Case project) {
		Individual[] smells=new Individual[s];
		for(int i=0;i<s;i++) {
			Individual son = new Individual(son_chromosome, son_chromosomeDNA, project);
			smells[i]=son;
		}
		return smells;
	}
	//�����Ӿ�����
	private Individual vision_basedSearch(Individual[] smells, Case project) {
		Population pop=new Population(smells,project);
		List<List<Integer>> rank=Tools.setRankAndCrowD(pop, project);
		return smells[rank.get(0).get(0)];
	}
	//��������
	public Individual Mating(Individual husband, int crosspoint) {

		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();

		List<List<Double>> son_chromosomeDNA = new ArrayList<List<Double>>();
		
		List<Integer> _tasks = new ArrayList<>();
		
		List<Double> _taskdna = new ArrayList<>();
		/*List<Integer> _resources = new ArrayList<>();
		 * List<Double> _resourcesdna = new ArrayList<>();*/

		for (int i = 0; i < crosspoint;i++){
			_tasks.add(this.chromosome.get(0).get(i));
			/*_resources.add(this.chromosome.get(1).get(i));*/
			_taskdna.add(this.chromosomeDNA.get(0).get(i));
			/*_resourcesdna.add(this.chromosomeDNA.get(1).get(i));*/
		}
				
		for (int i = 0; i<husband.chromosome.get(0).size();i++){
			if (!_tasks.contains(husband.chromosome.get(0).get(i))){
				_tasks.add(husband.chromosome.get(0).get(i));
				_taskdna.add(husband.chromosomeDNA.get(0).get(i));
				/*_resources.add(husband.chromosome.get(1).get(i));
				
				_resourcesdna.add(husband.chromosomeDNA.get(1).get(i));*/

			}
		}
		son_chromosome.add(_tasks);
		son_chromosomeDNA.add(_taskdna);
		/*son_chromosome.add(_resources);
		son_chromosomeDNA.add(_taskdna);*/
		Individual son = new Individual(son_chromosome, son_chromosomeDNA, project);
		/*Individual son = new Individual(son_chromosome,son_chromosomeDNA,project);*/	
		return son;
	}
	
	//�������
	public Individual mutationPopulation(double tMutationRate,double rMutationRate) {
		List<List<Integer>> son_chromosome = new ArrayList<List<Integer>>();

		List<List<Double>> son_chromosomeDNA = new ArrayList<List<Double>>();
		
		List<Integer> _tasks = new ArrayList<>();
		/*List<Integer> _resources = new ArrayList<>();*/
		List<Double> _taskdna = new ArrayList<>();
		/*List<Double> _resourcesdna = new ArrayList<>();*/
		
		int chromosomeLength = this.chromosome.get(0).size();
		for (int i =0; i<chromosomeLength; i++){
			_tasks.add(this.chromosome.get(0).get(i));
			/*_resources.add(this.chromosome.get(1).get(i));*/
			_taskdna.add(this.chromosomeDNA.get(0).get(i));
			/*_resourcesdna.add(this.chromosomeDNA.get(0).get(i));*/			
		}
		
		for (int geneIndex = 0; geneIndex < chromosomeLength - 1; geneIndex++) {
			if (tMutationRate > Math.random()) {
				int taskGene1=_tasks.get(geneIndex);
				/*int resourceGene1=_resources.get(geneIndex);*/
				double randtask = _taskdna.get(geneIndex);
				/*double randres = _resourcesdna.get(geneIndex);*/
				int taskGene2=_tasks.get(geneIndex+1);
				/*int resourceGene2=_resources.get(geneIndex+1);*/
				
				Task t1 = project.getTasks().get(taskGene1 - 1);
				Task t2 = project.getTasks().get(taskGene2 - 1);
				if (!project.isPredecessor(t1, t2)) {//t1����t2�Ľ�ǰ����
					// ��������λ���ϵ��������Լ���Դ���
					_tasks.set(geneIndex, taskGene2);
					_tasks.set(geneIndex+1, taskGene1);
					/*_resources.set(geneIndex, resourceGene2);
					_resources.set(geneIndex+1, resourceGene1);*/
					_taskdna.set(geneIndex, _taskdna.get(geneIndex+1));
					_taskdna.set(geneIndex+1, randtask);
					/*_resourcesdna.set(geneIndex, _resourcesdna.get(geneIndex+1));
					_resourcesdna.set(geneIndex+1, randres);*/
				}
			}
			
			/*if (rMutationRate > Math.random()) {
				// ÿ������ɹ�ѡ�����Դ����
				// ����ID
				int gene=_tasks.get(geneIndex);
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// �ڿ�����Դ�У����ѡ��һ��
				double  randnum = Math.random();
				_resourcesdna.set(geneIndex, randnum);
				int r = (int) (randnum * capapleResource.size());
				_resources.set(geneIndex, capapleResource.get(r));
			}*/
		}
		son_chromosome.add(_tasks);
		/*son_chromosome.add(_resources);*/
		son_chromosomeDNA.add(_taskdna);
		/*son_chromosomeDNA.add(_resourcesdna);*///����������son_chromosomeDNA.add(_resourcesdna)
		Individual son = new Individual(son_chromosome,son_chromosomeDNA,project);	

		return son;
	}

	// ��ȡ�ø����Ⱦɫ��ṹ(taskid)
	public List<List<Integer>> getChromosome() {
		return this.chromosome;
	}
	public List<List<Double>> getchromosomeDNA() {
		return this.chromosomeDNA;
	}
	public void setchromosomeDNA(List<List<Double>> dna) {
		this.chromosomeDNA = dna;
	}
	
	public List<ITask> getTaskslist() {
		return taskslist;
	}

	public void setTaskslist(List<ITask> taskslist) {
		this.taskslist = taskslist;
	}

	
	// ����Ⱦɫ����������ָ��λ�õ�������
	public void setTaskGene(int offset, int gene) {
		this.chromosome.get(0).set(offset, gene);
	}

	// ��ȡȾɫ����������ָ��λ�õ�������
	public int getTaskGene(int offset) {
		return this.chromosome.get(0).get(offset);
	}

	// ����Ⱦɫ����Դ��������ָ��λ�õ���Դ���
	public void setResourceGene(int offset, int gene) {
		this.chromosome.get(1).set(offset, gene);
	}

	// ��ȡȾɫ����Դ��������ָ��λ�õ���Դ���
	public int getResourceGene(int offset) {
		return this.chromosome.get(1).get(offset);
	}

	// ��ȡ�ø����Ŀ�꺯��ֵ
	public double[] getObj() {
		return this.obj;
	}
	
	public void setObj(double[] obj) {
		this.obj = obj;
	}
	// non_dominatedRank��getter����
	public int getNon_dominatedRank() {
		return non_dominatedRank;
	}

	// non_dominatedRank��setter����
	public void setNon_dominatedRank(int non_dominatedRank) {
		this.non_dominatedRank = non_dominatedRank;
	}

	// crowDistance��getter����
	public double getCrowDistance() {
		return crowDistance;
	}

	// crowDistance��setter����
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
	 * �����toString����
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
	 * ÿ��chromosome����֮���Ӧ��Ŀ�꺯��ֵ����һ��һά�����ʾ�����鳤�ȵ���Ŀ�꺯���ĸ���
	 * 
	 * @param chromosome
	 *            ���ܺ�Ľ�
	 * @return �������ͣʱ����������
	 */
	public void objCompute(Case project) {
		List<Task> tasks = project.getTasks();
		List<Resource> resourses = project.getResources();
		List<Integer> pretaskids;
		int maxtime = 0;
		double cost = 0.0;
		
		int[] endtime_res = new int[project.getM()];
		for (int i = 0; i < endtime_res.length ; i++) {
			//���ڼ�¼ÿ����Դ�ͷ�ʱ��
			endtime_res[i] = 0;
		}
		
		for (int i = 0; i < project.getN(); i++){
			int endtime = 0;
			Task curtask = tasks.get(chromosome.get(0).get(i)-1);
			//�õ�����ǰ������,ѭ��ÿһǰ������ȡ�������ʱ��
			pretaskids = curtask.getPredecessorIDs();
			for (int j = 0; j < pretaskids.size();j++){
				if (endtime < tasks.get(pretaskids.get(j)-1).getFinishTime()){
					endtime = tasks.get(pretaskids.get(j)-1).getFinishTime();
				}
			}
			//��ǰ��������Ӧ����Դ����ʱ��
			if (endtime < endtime_res[chromosome.get(1).get(i)-1]){
				endtime = endtime_res[chromosome.get(1).get(i)-1];
			}
			//���õ�ǰ����Ŀ�ʼʱ�估���ʱ��
			taskslist.get(chromosome.get(0).get(i)-1).setstarttime(endtime +1,1);
			//���µ�ǰ������Դ������깤ʱ��
			endtime_res[chromosome.get(1).get(i)-1] = taskslist.get(chromosome.get(0).get(i)-1).finishTime;
            //��ǰ�����������ʱ��
			if (maxtime < endtime_res[chromosome.get(1).get(i)-1]){
            	maxtime = endtime_res[chromosome.get(1).get(i)-1];
            }
	     
			// ����ɱ�			
			int duration = tasks.get(chromosome.get(0).get(i) - 1).getDuaration();
			double salary = resourses.get(chromosome.get(1).get(i) - 1).getSalary();
			cost += duration * salary;			
			
		}
		this.obj[0] =  (double)maxtime;
		this.obj[1] = cost;
	}


	/**
	 * �������ʼ���⣬���ܳ�����������ʾ�������С���Դ���е�Ⱦɫ��ṹ
	 * 
	 * @param _chromosome
	 *            �������ɵĶ�ά����
	 * @return ����������ִ�����к���Դ����������ɵļ���
	 */
	public void deciphering(Case project) {
		
		List<Integer> taskList = new ArrayList<Integer>();
		/*List<Integer> resourceList = new ArrayList<Integer>();*/
		// ��ִ�����񼯺�
		List<Integer> executableTaskIDS = new ArrayList<Integer>();	
		List<Task> tasks = project.getTasks();

		List<Double> _list1 = new ArrayList<>();
		/*List<Double> _list2 = new ArrayList<>();*/
		
		/*int[] endtime_res = new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//���ڼ�¼ÿ����Դ�ͷ�ʱ��
			endtime_res[j] = 0;
		}*/

		// ��taskList����ִ�����к�resourceList��Դ��������
		for (int i = 0; i < project.getN(); i++) {  
			
			executableTaskIDS.clear();
			double rand1 = Math.random();
			_list1.add(rand1);
			
			for (int k = 0; k < tasks.size(); k++) {
				if (taskslist.get(k).pretasknum == 0){//�ҵ�û�н�ǰ��������񼯺���Ϊ����ִ�����񼯺�
					executableTaskIDS.add(tasks.get(k).getTaskID());
				}
			}
			if (executableTaskIDS.size() == 0){
				break;
			}
			int A = (int) ( rand1 * executableTaskIDS.size());
			int currentTaskID = executableTaskIDS.get(A);//�������񼯺������ѡ��һ����Ϊ��ǰִ������
			taskList.add(currentTaskID);
			taskslist.get(currentTaskID -1).pretasknum = -1;   //��ǰ�����Ѿ���ʹ�ã����ϱ���Է�ֹ�´α�ѡ��
			
			//�����������
			for (int k = 0; k < tasks.size(); k++) {
				//������������jΪǰ�������ǰ����������1��
				if (tasks.get(k).getPredecessorIDs().contains(currentTaskID)){
					taskslist.get(k).pretasknum--;	
					//������Ӧ�ý��������ǰ��������ɾ��
				}
			}
			
			// ���Ӧ����Դ��������resourceList
			// ��ִ�и��������Դ����
			/*ITask curTask = taskslist.get(currentTaskID -1);
			List<Integer> list = curTask.getresourceIDs();
			int B = (int) (rand2 * list.size());
			int resourceid = list.get(B);
			resourceList.add( resourceid );*/
			
			//��������Ŀ��ֵ
			/*singleCompute(resourceid,currentTaskID,endtime_res);*/
		}
		this.chromosomeDNA.add(_list1);
		/*this.chromosomeDNA.add(_list2);*/
		this.chromosome.add(taskList);
		/*this.chromosome.add(resourceList);*/

		return ;
	}
	public void learnObjCompute() {
		List<Integer> resourceList = new ArrayList<Integer>();
		List<Integer> taskList = this.chromosome.get(0);
		List<Double> _list2 = new ArrayList<>();
		int[] endtime_res = new int[project.getM()];
		int workload[]= new int[project.getM()];
		for (int j = 0; j < endtime_res.length ; j++) {
			//���ڼ�¼ÿ����Դ�ͷ�ʱ��
			endtime_res[j] = 0;
			workload[j]=0;
		}
		
		for(int i = 0; i <taskList.size(); i++){
			double rand2 = Math.random();
			ITask curTask = taskslist.get(taskList.get(i) -1);
			List<Integer> list = curTask.getresourceIDs();
			//��Դ��ѡ�� �������ֹ���  ���  ����˵���Դ ���������Դ   ��С������Դ  ���ȿ���������
			int resourceid=selectResource(curTask,endtime_res,workload);
			
			/*int B = (int) (rand2 * list.size());
			_list2.add(rand2);
			int resourceid = list.get(B);*/
			
			resourceList.add(resourceid);
			
			singleCompute(resourceid,this.chromosome.get(0).get(i),endtime_res,workload);//��̬����
		}
		this.chromosomeDNA.add(_list2);
		this.chromosome.add(resourceList);
	}
	//ѡ����Դ
	private int selectResource(ITask curTask, int[] endtime_res, int[] workload) {
		List<IResource> ress=this.resourceslist;
		List<Integer> list = curTask.getresourceIDs();
		double rand=Math.random();
		int resourceid=list.get(0);
		if(rand<0.3) {
			//���
			double rand1=Math.random();
			int B = (int) (rand1 * list.size());
			resourceid = list.get(B);
		}else if(rand<0.5) {
			//����˵���Դ
			List<Integer> list1=new ArrayList<>(list);
			//��������
			Collections.sort(list1, new Comparator<Integer>() {

				@Override
				public int compare(Integer arg0, Integer arg1) {
					if(ress.get(arg0-1).getSalary()>ress.get(arg1-1).getSalary()) {
						return 1;
					}else if(ress.get(arg0-1).getSalary()<ress.get(arg1-1).getSalary()) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid=list1.get(0);
		}else if(rand<0.7) {
			//���������Դ
			List<Integer> list1=new ArrayList<>(list);
			//��������
			Collections.sort(list1, new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					if(endtime_res[o1-1]>endtime_res[o2-1]) {
						return 1;
					}else if(endtime_res[o1-1]<endtime_res[o2-1]) {
						return -1;
					}else {
					    return 0;
					}
				}
			});
			resourceid = list1.get(0);
			
		}else if(rand<0.9) {
			//��С������Դ
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
			//�������������
			List<Integer> list1=new ArrayList<>(list);
			String qtype = curTask.getSkill().split(":")[0].trim();
			//��������
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

	public void singleCompute(int rid, int tid, int[] endtime_res, int[] workload){
		ITask task = taskslist.get(tid-1);
		IResource resource = resourceslist.get(rid-1);
		if(!canInsert(resource, task,workload)){//�����ڽ�ǰ����
			//�׶��Լ��㣺������Դ����ʱ�䣬��������ʼ����ʱ��
			phaseCompute(rid, tid, endtime_res,workload);
			//���¼���ˮƽ����ǰ���ܱ�����ִ��ʱ���	
			updateSkill(resource, task);
			//����Ŀ��ֵ�� ���� �ɱ� 
			this.obj = aimCompute(task, resource,endtime_res);
		}else{
			//������Ŀ��ֵ������ʼ����ʱ�䣬�����¼���is wrong   ��Ҫ���¸���Դ�������������ʱ�����Դ����
			//updateResourceAnd
			this.obj = aimCompute(task, resource,endtime_res);//���Ƶ������ƶ������ļ��ܱ��������������輼��  ������Դ����
		}
	}
	
	public double[] aimCompute(ITask curTask, IResource curResource, int[] endtime_res){
		for(int i=0;i<endtime_res.length;i++) {
			if(this.maxtime<endtime_res[i]){
				this.maxtime =endtime_res[i];
			}
		}
		int realDuration = curTask.getFinishTime() - curTask.getStartTime();
		double salary = curResource.getSalary();
		this.cost += realDuration*salary;
		
		return new double[]{this.maxtime, this.cost};
	}
	
	public void phaseCompute(int rid, int tid, int[] endtime_res, int[] workload){
		int endtime = 0;
		ITask curtask = taskslist.get(tid-1);
		String qtype = curtask.getSkill().split(":")[0].trim();
		IResource resource = resourceslist.get(rid-1);
		double qinit = resource.getSkillsInfo().get(qtype); 
		
		//�õ�����ǰ������,ѭ��ÿһǰ������ȡ�������ʱ��
		List<Integer> pretaskids = curtask.getPredecessorIDs();
		for (int j = 0; j < pretaskids.size();j++){
			if (endtime < taskslist.get(pretaskids.get(j)-1).getFinishTime()){
				endtime = taskslist.get(pretaskids.get(j)-1).getFinishTime();
			}
		}
		//��ǰ��������Ӧ����Դ����ʱ��
		if (endtime < endtime_res[rid-1]){
			endtime = endtime_res[rid-1];
		}
		
		//���õ�ǰ����Ŀ�ʼʱ�估���ʱ��
		taskslist.get(tid-1).setstarttime(endtime,qinit);//���1����ȡ��
		//���µ�ǰ������Դ������깤ʱ��
		endtime_res[rid-1] = taskslist.get(tid-1).finishTime;
		workload[rid-1]+=(int)taskslist.get(tid-1).getStandardDuration()/qinit;
	}
	
	public boolean updateSkill(IResource resource, ITask task){
		String qtype = task.getSkill().split(":")[0].trim();
		double qinit = resource.getSkillsInfo().get(qtype);
		double m = resource.getLearnbility();
		int dstan = task.getStandardDuration();
		double M = 0.25;
		
		double sstart = Math.pow(((1-M)*qinit)/(1-M*qinit), 1/m);
		double sdura = dstan/(qinit*8);
		double tmp = Math.pow((sstart+sdura), m);
		double qfinal = tmp/(M*tmp+1-M);
		
		//���µ�ǰ����ˮƽ��
		resource.putSkillsInfo(qtype, qfinal);
		//���¼���ִ��ʱ���
		resource.putSkillTimetable(qtype, new int[]{task.getStartTime(),task.getFinishTime()}, qfinal);		
		
		if(Math.floor(qfinal)-Math.floor(qinit)>=1){//����ԾǨ�����·������������Դ
			/*System.out.println(resource.getSkillTimetable().get(qtype));*/
			Iterator<int[]> timeline = resource.getSkillTimetable().get(qtype).keySet().iterator();
			//����ʱ���
			/*while(timeline.hasNext()){
				int[] now = timeline.next();
				System.out.println((Object)now);
				System.out.println(resource.getSkillTimetable().get(qtype).get(now));
			}*/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
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
	
	//��ǰ����  �Ƿ�������Ƶ��� �� ����δ��ʱ��������ж�
	public boolean canInsert(IResource resource, ITask task, int[] workload){
		@SuppressWarnings("rawtypes")
		String skillType = task.getSkill().split(":")[0].trim();
		int stanDuration = task.getStandardDuration();
		HashMap<String,HashMap<int[],Double>> timetable = resource.getSkillTimetable();
		double skillLevel = Double.valueOf(task.getSkill().split(":")[1]);
		//�õ���Դִ��ʱ���
		HashMap<int[],Double> skilltime = timetable.get(skillType);//int[] Ϊʲô����û����
		@SuppressWarnings("rawtypes")
		Iterator timeline = skilltime.keySet().iterator();
		int[] lasttime = (int[])timeline.next();
        
		//����ʱ���  +��Ҫ���� ��������ıȽϲ��ʺ�
		while(timeline.hasNext()){
			int[] time = (int[])timeline.next();
			double rightskill = skilltime.get(lasttime);
			//�Ƿ����㼼��ˮƽ��ʱ������
			if(rightskill >= skillLevel && stanDuration/rightskill <= time[0]-lasttime[1]){
				List<Integer> preids = task.getPredecessorIDs();
				//�Ƿ������ǰ��ϵ
				for(int i = 0; i < preids.size();i++){
					if(taskslist.get(preids.get(i)-1).getFinishTime() >= lasttime[1]){
						return false;
					};
				}
				task.setstarttime(lasttime[1], rightskill);
				workload[resource.getResourceID()-1]+=stanDuration/rightskill;
				//������Դ���ܼ���Դ���ŵ������ִ��ʱ�� ���񲻿���  Ӱ����ȫ�ֵ�
				return true;
			}
			lasttime = time;
		}
		
		return false;
	}

	public Case getProject() {
		return project;
	}

	public void setProject(Case project) {
		this.project = project;
	}


	/**
	 * ��Ԫ������ѡ�񷽷�������Ϊ�����������
	 * 
	 * @param indiv1
	 *            �������
	 * @param indiv2
	 *            ��һ���������
	 * @return ���ؽϺõĸ���
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
			if (indiv1.getCrowDistance() < indiv2.getCrowDistance()) {
				individual = indiv2;
			}
			if (indiv1.getCrowDistance() > indiv2.getCrowDistance()) {
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

}
