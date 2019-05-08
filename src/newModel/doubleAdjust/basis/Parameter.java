package newModel.doubleAdjust.basis;

public class Parameter {
    private int populationSize;//种群数量
    private double crossoverRate;//交叉率
    private int tour;//锦标赛池子大小
    private double rMutationRate;//资源变异率
    private int s;//单次气味搜索生成果蝇数量
 	// 知识库更新的概率
 	public  double alpha ;
 	// 提供经验的果蝇数量
 	public  int NE ;
 	public String type;//编码类型
 	public int RunTime;
	public Parameter(int populationSize, double crossoverRate, int tour, double rMutationRate,int RunTime) {
    	this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.tour=tour;
    	this.rMutationRate=rMutationRate;
    	this.RunTime=RunTime;
	}
	public Parameter(int populationSize, double crossoverRate, int tour, double rMutationRate,int s
			,double alpha,int NE,String type,int RunTime) {
    	this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.tour=tour;
    	this.rMutationRate=rMutationRate;
    	this.alpha=alpha;
    	this.NE=NE;
    	this.s=s;
    	this.type=type;
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
	

}
