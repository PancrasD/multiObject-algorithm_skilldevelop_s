package newModel.doubleAdjust;



public class NSFFA {
	// 种群数量，即果蝇群体数量
	public static int NS = 50 ;
	// 每个个体生成子代种群的大小
	public static int S = 3;
	// 知识库更新的概率
	public static double alpha = 0.15;
	// 提供经验的果蝇数量
	public static int NE = 7;
	
	public static int maxGenerations =250;
	public int RunTime;
	public int getRunTime() {
		return RunTime;
	}
	public void setRunTime(int runTime) {
		RunTime = runTime;
	}
	

}
