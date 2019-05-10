package newModel.doubleAdjust.basis;

import java.util.HashMap;

public class Parameter {
    private int populationSize;//种群数量
    private double crossoverRate;//交叉率
    private int tour;//锦标赛池子大小
    private double tMutationRate;//任务序列变异率
    private double rMutationRate;//资源变异率
    private int s;//单次气味搜索生成果蝇数量
 	public  double alpha ;// 知识库更新的概率
 	public  int NE ;// 提供经验的果蝇数量
 	public String type;//编码类型
 	public int RunTime;//运行次数
 	private double resSpp;//极端搜索概率
 	private int len;//调整子序列的长度
 	private String mode;//模式 单种群--双种群
 	private HashMap<Integer,Boolean> hashCode=new HashMap<>();
 	//NTGA
	public Parameter(int populationSize, double crossoverRate, int tour, double rMutationRate,int RunTime) {
    	this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.tour=tour;
    	this.rMutationRate=rMutationRate;
    	this.RunTime=RunTime;
	}
	//RL
	public Parameter(int populationSize, double crossoverRate, int tour, double rMutationRate,int s
			,double alpha,int NE,String type,String mode,int RunTime) {
    	this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.tour=tour;
    	this.rMutationRate=rMutationRate;
    	this.alpha=alpha;
    	this.NE=NE;
    	this.s=s;
    	this.mode=mode;
    	this.type=type;
    	this.RunTime=RunTime;
	}
	//GV
	public Parameter(int populationSize, double crossoverRate, double resSpp, double rMutationRate,int len, String type, int RunTime) {
		this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.rMutationRate=rMutationRate;
    	this.type=type;
    	this.resSpp=resSpp;
    	this.len=len;
    	this.RunTime=RunTime;
	}
	//G
	public Parameter(int populationSize, double crossoverRate, double tMutationRate, double rMutationRate,
			String type, int RunTime) {
		this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.rMutationRate=rMutationRate;
    	this.tMutationRate=tMutationRate;
    	this.type=type;
    	this.RunTime=RunTime;
	}
	//SMSEMOA
	public Parameter(int populationSize, double crossoverRate, int tour, double rMutationRate, String type,
			String mode, int RunTime) {
		this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.rMutationRate=rMutationRate;
    	this.tour=tour;
    	this.type=type;
    	this.mode=mode;
    	this.RunTime=RunTime;
	}
	public int getPopulationSize() {
		return populationSize;
	}
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
	public double getCrossoverRate() {
		return crossoverRate;
	}
	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}
	public int getTour() {
		return tour;
	}
	public void setTour(int tour) {
		this.tour = tour;
	}
	public double getrMutationRate() {
		return rMutationRate;
	}
	public void setrMutationRate(double rMutationRate) {
		this.rMutationRate = rMutationRate;
	}
	public int getS() {
		return s;
	}
	public void setS(int s) {
		this.s = s;
	}
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public int getNE() {
		return NE;
	}
	public void setNE(int nE) {
		NE = nE;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getRunTime() {
		return RunTime;
	}
	public void setRunTime(int runTime) {
		RunTime = runTime;
	}
	public double gettMutationRate() {
		return tMutationRate;
	}
	public void settMutationRate(double tMutationRate) {
		this.tMutationRate = tMutationRate;
	}
	public double getResSpp() {
		return resSpp;
	}
	public void setResSpp(double resSpp) {
		this.resSpp = resSpp;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public HashMap<Integer, Boolean> getHashCode() {
		return hashCode;
	}
	public void setHashCode(HashMap<Integer, Boolean> hashCode) {
		this.hashCode = hashCode;
	}
	

}
