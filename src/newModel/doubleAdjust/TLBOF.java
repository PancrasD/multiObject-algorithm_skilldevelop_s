package newModel.doubleAdjust;

public class TLBOF {

	// 种群大小
		public static final int populationSize = 300;

		// 任务序列变异概率
		public static final double probp = 0.15;//变异
		
		// 老师个体强化次数
		public static final double rein = 50;
		//果蝇后代个数
		public static final int s=3;	
		public static final int s1=7;	
		// 迭代次数
		public static final double maxGenerations = 500;
		
		/*
		 * 操作序列搜索和资源序列搜索  资源序列搜索依赖于果蝇后代个数 在一开始s小 在后面的话局部搜索s大  时间考量
		 * 考虑果蝇后代不是在单一个体后代集中竞争 而是在所有个体中竞争 是否可以 增加多样性解
		 */
}
