package com.newAlgorithem.gavn.doubleAdjust;

public class NSGA_II  {

	// 种群大小
	public  int populationSize ;
	// 交叉概率
	public  double crossoverRate ;
	// 任务序列变异概率
	public  double tMutationRate ;
	// 资源序列变异概率
	public double rMutationRate ;
	// 迭代次数
	public static final double maxGenerations = 300;
	//运行次数
    public int RunTime;
    public NSGA_II(int populationSize,double crossoverRate, double tMutationRate,double rMutationRate,int RunTime){
    	this.populationSize=populationSize;
    	this.crossoverRate=crossoverRate;
    	this.tMutationRate=tMutationRate;
    	this.rMutationRate=rMutationRate;
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
	public double gettMutationRate() {
		return tMutationRate;
	}
	public void settMutationRate(double tMutationRate) {
		this.tMutationRate = tMutationRate;
	}
	public double getrMutationRate() {
		return rMutationRate;
	}
	public void setrMutationRate(double rMutationRate) {
		this.rMutationRate = rMutationRate;
	}
	public int getRunTime() {
		return RunTime;
	}
	public void setRunTime(int runTime) {
		RunTime = runTime;
	}
    
}
