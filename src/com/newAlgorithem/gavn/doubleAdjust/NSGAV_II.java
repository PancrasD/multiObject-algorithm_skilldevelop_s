package com.newAlgorithem.gavn.doubleAdjust;

public class NSGAV_II {

	// 种群大小
	public   int populationSize;
	// 交叉概率
	public   double crossoverRate ;
	//平衡因子
	public  double pr;//0.6
    /*// 任务序列变异概率
	public static final double tMutationRate = 0.15;
	// 资源序列变异概率
	public static final double rMutationRate = 0.1;*/
	// 迭代次数
	public static double maxGenerations =300;
	//运行次数
	public   int RunTime;
	public  int  neighborN ;
	//局部调整长度
	public  int len;
	//资源搜索概率
	public double resSp;
	// 资源序列变异概率
	public  double rMutationRate ;
	public NSGAV_II(int populationSize,double crossoverRate,double resSp,double rMutationRate,int len,int RunTime) {
		this.populationSize=populationSize;
		this.crossoverRate=crossoverRate;
		this.resSp=resSp;
		this.rMutationRate=rMutationRate;
		this.len=len;
		this.RunTime=RunTime;
	}
}
