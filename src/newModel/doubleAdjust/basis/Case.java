package newModel.doubleAdjust.basis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.NSFFA;
import newModel.doubleAdjust.NSGAV_II;
import newModel.doubleAdjust.NSGA_II;
import newModel.doubleAdjust.algorithm.NTGA;


public class Case {
	//设置系数b和初始时间T
	private  static int B=24;//月转化
	private static int T=10;//最低技能对应的时间
	private static int MaxLevel=3;//最大水平
    private  static  String PATH="case_q/q.txt";//技能对应的标准考题下技能时间表
	private int N;// 案例包含的任务数量
	private int M;// 案例包含的资源数量
	private int PR;// 案例包含的紧前关系数量
	private int K;// 案例包含的技能类型数量
    private NSGA_II NSGA_II;
    private  NSGAV_II NSGAV_II;
    private  NSFFA NSFFA;
    private   Parameter parameter;
	private List<Task> tasks = new ArrayList<>();//任务数组
	private List<Resource> resources = new ArrayList<>();//资源数组
	private List<Integer> characteristics = new ArrayList<>();//特征信息数组[任务数量，资源数量，紧前任务数量，技能种类]
	private double borderDuration=0;//最大工期 累计加和
	private double borderCost=0;//最大成本  最大工期*最大薪水
	private double[] tempObj;//传递参数用的
	public int RunTime;//运行次数
	//技能对应的标准任务的时间表  key:技能类型   value:进化时间表
	private HashMap<String,Double[]> TestQuesttionMap=new HashMap<>();
	//临界因子 key:技能类型  value:临界因子
	private HashMap<String,Double> borderValueMap=new HashMap<>();
	List<Individual> pareto=new ArrayList<>();
	private int count;//计数进化
	public static int sub=5;//划分区间数
	// 存储状态-动作对Q值，存在25种状态，每种状态对应5种可选操作
	double[][] q_value = new double[25][3];
	// 存储相应状态下动作的选择概率
	double[][] probility = new double[25][3];
	double[]perfectPoint=new double[2];
	public Case() {
		caseDefinition();
	}
	//浅复制project
	public Case(Case project) {
			this.N=project.N;
			this.M=project.M;
			this.PR=project.PR;
			this.K=project.K;
			this.NSGAV_II=project.NSGAV_II;
			this.NSGA_II=project.NSGA_II;
			this.parameter=project.parameter;
			this.tasks=project.tasks;
			this.characteristics=project.characteristics;
			this.resources=project.resources;
			this.borderCost=project.borderCost;
			this.borderDuration=project.borderDuration;
			this.RunTime=project.RunTime;
			this.TestQuesttionMap=project.TestQuesttionMap;
			this.borderValueMap=project.borderValueMap;
			this.perfectPoint=project.perfectPoint;
			initQ_P();
	}

	public Case(String defFile) {
		readSkillFile();
		readCaseDef(defFile);
		this.N = characteristics.get(0);
		this.M = characteristics.get(1);
		this.PR = characteristics.get(2);
		this.K = characteristics.get(3);
		//设置资源学习能力
		setResourceLearnbility();
		setEvalutionTime();
		//根据技能水平得到每个任务的可用资源集合
		setCapapleResource();
		//得到每个任务的紧后任务集合
		countsuccessor();
		//得到每个任务对应可用资源的使用概率
		setTaskCapapleResource();
		/*setPrePre();
		setSucSuc();*/
		computeBorderValue();
		initQ_P();
		//computePerfectPoint();
	}
	/*
	 * 计算最好点
	 */
	private void computePerfectPoint() {
		List<Task> tasks=this.tasks;
		double duration=0;
		for(int i=0;i<tasks.size();) {
			
		}
		double cost=0;
		
	}
	/*
	 * 初始化Q值和P值
	 */
	private void initQ_P() {
		for(int i=0;i<probility.length;i++) {
			Arrays.fill(probility[i], 1.0/probility[i].length);
		}
		
	}
   
	/*
          * 计算最坏值
     */
	private void computeBorderValue() {
		double MaxDuration=0;
		double MaxCost=0;
		List<Task>tasks=this.getTasks();
		for(int i=0;i<tasks.size();i++) {
			Task task=tasks.get(i);
			MaxDuration+=task.getDuaration();
			List<Integer> canR=(List<Integer>) task.getresourceIDs();
			int maxSaIndex=0;
			double  maxSa=this.getResources().get(canR.get(maxSaIndex)-1).getSalary();
			for(int k=1;k<canR.size();k++) {
				Resource r=this.getResources().get(canR.get(k)-1);
				if(maxSa<r.getSalary()) {
					maxSaIndex=k;
					maxSa=r.getSalary();
				}
			}
			MaxCost+=task.getDuaration()*maxSa;
		}
		this.setBorderCost(MaxCost);
		this.setBorderDuration(MaxDuration);
	}

	//round-robin原则设置学习能力
	public void setResourceLearnbility() {
		double[] learnArr = new double[]{0.515, 0.321, 0.152};
		for(int i=0; i<resources.size();i++){
			resources.get(i).setLearnbility(learnArr[i%3]);
		}
	}
	/*
	 * 设置技能对应的进化需要的时间线
	 */
	private void setEvalutionTime() {
		DecimalFormat df=new DecimalFormat("#0.0");
		for(int i=0; i<resources.size();i++){
			HashMap<String,Double[]> nextLevelTimeMap=new HashMap<>();
			Resource resource=resources.get(i);
			double learn=resource.getLearnbility();
			for(String skill:resource.getSkils().trim().split(" ")){
				String[] skillInfo = skill.trim().split(":");
				Double[] Ts=this.getTestQuesttionMap().get(skillInfo[0]);
				double border=this.getBorderValueMap().get(skillInfo[0]);
				double[] t=gett(Ts,learn,border);
				Double[] nextLevelTime=new Double[t.length-1];
				nextLevelTime[0]=t[1];
				for(int k=2;k<t.length;k++) {
					nextLevelTime[k-1]=Double.valueOf(df.format(t[k]-t[k-1]));
				}
				nextLevelTimeMap.put(skillInfo[0], nextLevelTime);
		  }
		  resource.setNextLevelTime(nextLevelTimeMap);
		}
	
	}
	/*
	 * @param Ts 标准考题下技能各个水平执行需要的时间
	 * @return 从最低技能到达各个技能所需要的时间
	 */
	public  double[] gett(Double[] ts, double learn,double border) {
		double T1=Case.T;;//技能为1初始的时间
		double b=Case.B;//系数
		double m=learn;//学习率
		double M=border;//临界因子
		double t[]=new double[ts.length];
		DecimalFormat df=new DecimalFormat("#0.0");
		for(int i=0;i<t.length;i++) {
			t[i]=b*(Math.pow(Math.E,(1.0/m)*Math.log((1-M)/(ts[i]/T1-M)))-1);
			String ti=df.format(t[i]);
			t[i]=Double.valueOf(ti);
		}
		return t;
	}
    /*
     * 读取技能对应的标准考题时间表文件
     */
	private void readSkillFile() {
		String path=Case.PATH;
		File f=new 	File(path);
		BufferedReader reader=null;
		try {
			 reader=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line=null;
		int k=0;
		HashMap<String,Double[]> examTimeMap=new HashMap<>();
		HashMap<String,Double> borderMap=new HashMap<>();
		try {
			while((line=reader.readLine())!=null) {
				if(line.startsWith("临界因子")) {
					String mes[]=line.split("\\s+");
					borderMap.put("Q"+k, Double.valueOf(mes[1]));
				}
				if(line.startsWith("执行时间")) {
					String mes[]=line.split(":");
					String timeStr[]=mes[1].split("\\s+");
					Double[] time=new Double[timeStr.length];
					for(int i=0;i<timeStr.length;i++) {
						String t=timeStr[i].trim();
						time[i]=Double.valueOf(t);
					}
					examTimeMap.put("Q"+k, time);
					k++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setTestQuesttionMap(examTimeMap);
		this.setBorderValueMap(borderMap);
	}
	// 定义一个读取案例文件的方法，获取案例的相关信息
	//将案例信息保存到变量tasks,resources,characteristics中
	public void readCaseDef(String defFile) {
		try (FileInputStream fis = new FileInputStream(defFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
			String line = null;
			int row = 0;
			int a = 0;
			int b = 0;
			int n = 0;
			int m = 0;
			//按行读取文件
			while ((line = br.readLine()) != null) {
				row++;

				if (row < 3) {//前两行不处理
					continue;
				}
				if (3 <= row && row <= 6) {// 读取案例的一般特征信息：任务数量，资源数量，紧前任务数量，技能种类
					String[] strArr = line.split(":");
					characteristics.add(Integer.valueOf(strArr[1]));
					continue;
				}
				if (line.startsWith("ResourceID     Salary     Skills")) {
					a = row;
					m = characteristics.get(1);
					continue;
				}

				if (a < row && row <= (a + m)) {
					String[] resource = line.trim().split("     ", 3);// 5个空格符为分隔符,并限定数组长度为3
					Resource r = new Resource(Integer.valueOf(resource[0]), Double.valueOf(resource[1]), resource[2]);
					resources.add(r);
					continue;
				}
				if (line.startsWith("TaskID     Duration     Skill     Predecessor IDs")) {
					b = row;
					n = characteristics.get(0);
					continue;
				}
				if (b < row && row <= (b + n)) {
					String[] task = line.trim().split("     ", 4);// 5个空格符为分割符，限定数组长度为4,无紧前任务的，数组长度为3
					if (task.length == 3) {
						Task t = new Task(Integer.valueOf(task[0]), Integer.valueOf(task[1]), task[2]);
						tasks.add(t);
					} else {
						Task t = new Task(Integer.valueOf(task[0]), Integer.valueOf(task[1]), task[2], task[3]);
						tasks.add(t);
					}
					continue;
				}

			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	public void caseDefinition() {

		this.N = 10;
		this.M = 3;
		this.PR = 4;
		this.K = 3;

		String[] resource1 = { "1", "56.0", "Q1:0 Q2:1" }; // 这里暂时模仿读取文件时，形成的字符串数组
		Resource r1 = new Resource(Integer.valueOf(resource1[0]), Double.valueOf(resource1[1]), resource1[2]);
		String[] resource2 = { "2", "53.6", "Q2:2 Q0:1" };
		Resource r2 = new Resource(Integer.valueOf(resource2[0]), Double.valueOf(resource2[1]), resource2[2]);
		String[] resource3 = { "3", "28.9", "Q0:1 Q1:0" };
		Resource r3 = new Resource(Integer.valueOf(resource3[0]), Double.valueOf(resource3[1]), resource3[2]);

		resources.add(r1);
		resources.add(r2);
		resources.add(r3);

		String[] task1 = { "1", "37", "Q2:1" };
		Task t1 = new Task(Integer.valueOf(task1[0]), Integer.valueOf(task1[1]), task1[2]);

		String[] task2 = { "2", "36", "Q2:2" };
		Task t2 = new Task(Integer.valueOf(task2[0]), Integer.valueOf(task2[1]), task2[2]);

		String[] task3 = { "3", "21", "Q0:1" };
		Task t3 = new Task(Integer.valueOf(task3[0]), Integer.valueOf(task3[1]), task3[2]);

		String[] task4 = { "4", "23", "Q1:0" };
		Task t4 = new Task(Integer.valueOf(task4[0]), Integer.valueOf(task4[1]), task4[2]);

		String[] task5 = { "5", "36", "Q0:1" };
		Task t5 = new Task(Integer.valueOf(task5[0]), Integer.valueOf(task5[1]), task5[2]);

		String[] task6 = { "6", "13", "Q2:1" };
		Task t6 = new Task(Integer.valueOf(task6[0]), Integer.valueOf(task6[1]), task6[2]);

		String[] task7 = { "7", "13", "Q1:0", "4 5" };
		Task t7 = new Task(Integer.valueOf(task7[0]), Integer.valueOf(task7[1]), task7[2], task7[3]);

		String[] task8 = { "8", "37", "Q0:1" };
		Task t8 = new Task(Integer.valueOf(task8[0]), Integer.valueOf(task8[1]), task8[2]);

		String[] task9 = { "9", "36", "Q2:1", "7" };
		Task t9 = new Task(Integer.valueOf(task9[0]), Integer.valueOf(task9[1]), task9[2], task9[3]);

		String[] task10 = { "10", "19", "Q1:0", "3" };
		Task t10 = new Task(Integer.valueOf(task10[0]), Integer.valueOf(task10[1]), task10[2], task10[3]);

		tasks.add(t1);
		tasks.add(t2);
		tasks.add(t3);
		tasks.add(t4);
		tasks.add(t5);
		tasks.add(t6);
		tasks.add(t7);
		tasks.add(t8);
		tasks.add(t9);
		tasks.add(t10);
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getM() {
		return M;
	}

	public void setM(int m) {
		M = m;
	}

	public int getPR() {
		return PR;
	}

	public void setPR(int pR) {
		PR = pR;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	public void setCapapleResource() {
		// 每一种资源拥有的技能集合
		List<Map<String, Integer>> staffSkillList = new ArrayList<>();
		for (int i = 0; i < resources.size(); i++) {
			// 资源i拥有的技能-技能水平
			Map<String, Integer> resourceSkill = new HashMap<>();
			String[] skills = resources.get(i).getSkils().trim().split(" ");
			for (String skill : skills) {
				String[] str = skill.split(":");
				resourceSkill.put(str[0], Integer.valueOf(str[1]));
			}
			staffSkillList.add(resourceSkill);
		}
		// 遍历任务集
		for (int i = 0; i < tasks.size(); i++) {
			List<Integer> capapleResourceIDS = new ArrayList<Integer>();
			// 任务i的所需技能类型和水平
			Task task=tasks.get(i);
			String[] skill_level = task.getSkill().trim().split(":");
			String skillType=skill_level[0];
			double skillLevel=Double.valueOf(skill_level[1]);//原始的
			// 遍历每一种资源拥有的技能集合
			for (int j = 0; j < staffSkillList.size(); j++) {
				Map<String, Integer> map = staffSkillList.get(j);
				if (map.keySet().contains(skillType)
						&& map.get(skillType) >=skillLevel) {
					capapleResourceIDS.add(resources.get(j).getResourceID());
				}
			}
			task.setresourceIDs(capapleResourceIDS);
		}
		return;
	}
	
	//？？？未找出全部紧后任务
	public void countsuccessor() {
		//循环每一列
		for (int i = 0; i< tasks.size();i++){
			List<Integer> successorIDS = new ArrayList<>();
			for (int j = i+1; j < tasks.size(); j++) {
				Task task=tasks.get(j);
				int num =  task.getpretasknum();
			    if (num > 0) {
					List<Integer> pre_IDs = task.getPredecessorIDs();
					if(pre_IDs.contains(i+1)) {
						successorIDS.add(task.getTaskID());
					}
				}
			}
			tasks.get(i).setsuccessorTaskIDS(successorIDS);
		}
		return;
	}
	
	/**
	 * 判断任务执行链表中相邻两个任务之间是否存在紧前关系约束。 如果task1是task2的紧前任务，则返回true
	 * 分两种情况：1.task2没有紧前任务,返回false; 2.task2有紧前任务: 紧前任务包含task1; 紧前任务不包含task1
	 * 
	 * @param task1
	 *            任务1
	 * @param task2
	 *            任务2
	 * @return
	 */
	public boolean isPredecessor(Task task1, Task task2) {
		boolean flag = false;

		// task1的ID
		int task1_ID = task1.getTaskID();
		if (task2.getPredecessorIDs().contains(task1_ID)) {
			flag = true;
		}
		return flag;
	}

	//每个可用资源使用的概率：1/可用资源总数
	public void setTaskCapapleResource() {
		for (int i = 0; i < tasks.size(); i++) {
			Map<Integer, Double> r_possibility = new HashMap<>();
			List<Integer> resurceid = tasks.get(i).getresourceIDs();
			for (int j = 0; j < resurceid.size(); j++) {
				r_possibility.put(resurceid.get(j), ((double) 1) / resurceid.size());
			}
			tasks.get(i).setCapaleResource(r_possibility);
		}

	}
	//统计设置任务所有的往前追寻紧前任务集
	private void setPrePre() {
		for(int i = 0; i < tasks.size(); i++) {
			Set<Integer> prepre=new TreeSet<>();
			List<Integer> pre=tasks.get(i).getPredecessorIDs();
			prepre.addAll(new ArrayList<>(pre));
			for(int j=0;pre!=null&&j<pre.size();j++) {
				List<Integer> newpre=tasks.get(pre.get(j)-1).getPrePreIDs();
				prepre.addAll(newpre);
			}
			tasks.get(i).setPrePreIDs(new ArrayList<>(prepre));
		}
		
	}
	//统计并设置任务所有的往后搜索紧后任务集
	private void setSucSuc() {
		for(int i = tasks.size()-1; i >=0; i--) {
			Set<Integer> sucsuc=new TreeSet<>();
			List<Integer> suc=tasks.get(i).getsuccessortaskIDS();
			sucsuc.addAll(suc);
			for(int j=0;suc!=null&&j<suc.size();j++) {
				List<Integer> newsuc=tasks.get(suc.get(j)-1).getSucSucIDS();
				sucsuc.addAll(newsuc);
			}
			tasks.get(i).setSucSucIDS(new ArrayList<>(sucsuc));
		}
		
	}

	public double getBorderDuration() {
		return borderDuration;
	}

	public void setBorderDuration(double borderDuration) {
		this.borderDuration = borderDuration;
	}

	public double getBorderCost() {
		return borderCost;
	}

	public void setBorderCost(double borderCost) {
		this.borderCost = borderCost;
	}

	public double[] getTempObj() {
		return tempObj;
	}

	public void setTempObj(double[] tempObj) {
		this.tempObj = tempObj;
	}

	public NSGA_II getNSGA_II() {
		return NSGA_II;
	}

	public void setNSGA_II(NSGA_II nSGA_II) {
		NSGA_II = nSGA_II;
	}

	public NSGAV_II getNSGAV_II() {
		return NSGAV_II;
	}

	public void setNSGAV_II(NSGAV_II nSGAV_II) {
		NSGAV_II = nSGAV_II;
	}

	public int getRunTime() {
		return RunTime;
	}

	public void setRunTime(int runTime) {
		RunTime = runTime;
	}

	public NSFFA getNSFFA() {
		return NSFFA;
	}

	public void setNSFFA(NSFFA nSFFA) {
		NSFFA = nSFFA;
	}


	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public static int getMaxLevel() {
		return MaxLevel;
	}

	public static void setMaxLevel(int maxLevel) {
		MaxLevel = maxLevel;
	}

	public HashMap<String, Double[]> getTestQuesttionMap() {
		return TestQuesttionMap;
	}

	public void setTestQuesttionMap(HashMap<String, Double[]> testQuesttionMap) {
		TestQuesttionMap = testQuesttionMap;
	}

	public HashMap<String, Double> getBorderValueMap() {
		return borderValueMap;
	}

	public void setBorderValueMap(HashMap<String, Double> borderValueMap) {
		this.borderValueMap = borderValueMap;
	}

	public static String getPATH() {
		return PATH;
	}

	public static void setPATH(String pATH) {
		PATH = pATH;
	}

	public List<Individual> getPareto() {
		return pareto;
	}

	public void setPareto(List<Individual> pareto) {
		this.pareto = pareto;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double[][] getQ_value() {
		return q_value;
	}
	public void setQ_value(double[][] q_value) {
		this.q_value = q_value;
	}
	public double[][] getProbility() {
		return probility;
	}
	public void setProbility(double[][] probility) {
		this.probility = probility;
	}

}
