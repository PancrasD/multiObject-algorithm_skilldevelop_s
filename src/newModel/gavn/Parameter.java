package newModel.gavn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;

import org.junit.Test;

public class Parameter {

	@Test
	public void test() {
		double T1=10;//技能为1初始的时间
		double b=20;//系数
		double m=0.321;//学习率
		double M=0.4;//临界因子
		//10.0 8 7.5 7.0 6.6 0.25  21.9 33.7 40.4 62.1 
		//10.0 8.3 7.4 6.9 6.5       24.4 37.4 46.3 73.6 
		//10.0 7.9 6.9 6.2 5.8 0.34  36.9 71.2 181.4 364.5 
		//10.0 7.8 6.8 6.2 5.7       40.9 81.3 167.4 561.4 
		double[]Ts= {7.3,6.1,5.4 };//各个技能水平执行需要的时间   关键在ts的递减上  ts递减越来越慢{8,7,6.3,5.8,5.5,5.01}
		double t[]=new double[Ts.length];
		for(int i=0;i<t.length;i++) {
			t[i]=b*(Math.pow(Math.E,(1.0/m)*Math.log((1-M)/(Ts[i]/T1-M)) )-1);
		}
		DecimalFormat df=new DecimalFormat("#0.0");
		for(int i=0;i<t.length;i++) {
			System.out.print(df.format(t[i])+" ");
		}
		System.out.println();
		System.out.print(df.format(t[0])+" ");
		for(int i=1;i<t.length;i++) {
			System.out.print(df.format(t[i]-t[i-1])+" ");
		}
	}

	@Test
	public void test1() {//生成Ts序列  考虑5个等级水平  临界因子考虑为0.5
		//标准考题T一等级均为10  临界因子取为M  等级数为k 则k^b<M =>b<lnM/lnk
		double[] level= {1,2,3,4};
		double T1=10;
		double diff[]=new double[5];//代表技能的困难系数 系数越小 则时间减缓越慢  相应的技能水平级之间的时间差距也相对较少
		for(int i=0;i<diff.length;i++) {
			diff[i]=(i)*1.0/10+0.1;//0.2为最低  0.1为差距 0.35为界限  注意此参数与临界因子及考虑的水平级数有关 不能减到临界值之下
		}
		double Qdiff[]=new double[15];
		for(int i=0;i<15;i++) {
			Qdiff[i]=diff[(int) (Math.random()*diff.length)];
		}
		DecimalFormat df=new DecimalFormat("#0.0");
		for(int i=0;i<diff.length;i++) {
			double[] Ts=new double[level.length];
			for(int j=0;j<level.length;j++) {
				Ts[j]=T1*Math.pow(level[j],-diff[i]);//0.3
				System.out.print(df.format(Ts[j])+" " );
			}
		    System.out.println("");
		}
		
	}
	@Test
	public void threeOut() {
		double[] level= {1,2,3,4};
		double T1=10;
		
	}
	public double[] function1(double[] level,double index,double T1) {
		double[] Ts=new double[level.length];
		for(int j=0;j<level.length;j++) {
			Ts[j]=T1*Math.pow(level[j],-0.3);//0.3
		}
		return Ts;
		
	}
	@Test
	public void output() {
		double[] level= {1,2,3,4};//4个水平
		double T1=10;
		double diff[]=new double[8];//代表技能的困难系数 系数越小 则时间减缓越慢  相应的技能水平级之间的时间差距也相对较少
		for(int i=0;i<diff.length;i++) {
			diff[i]=(i)*0.5/10+0.1;//0.2为最低  0.1为差距 0.35为界限  考虑临界因子0.1 0.2 0.3 0.4注意此参数与临界因子及考虑的水平级数有关 不能减到临界值之下
		}
		double[] learn= {0.515,0.418,0.321};
		String path="case_q//t.txt";
		PrintStream ps=getPrintStream(path);
		System.setOut(ps);
		for(int k=0;k<diff.length;k++) {
			System.out.println("第"+k+"种难度");
			DecimalFormat df=new DecimalFormat("#0.0");
			double[] Ts=new double[level.length];
			
			System.out.println("标准考题时间表");
			for(int j=0;j<level.length;j++) {
				Ts[j]=T1*Math.pow(level[j],-diff[k]);
				System.out.print(df.format(Ts[j])+" ");
			}
			System.out.println(" ");
			for(int i=0;i<learn.length;i++) {
				System.out.println("学习率为 "+learn[i]);
				
				System.out.println("技能进化时间线");
				double[] t=gett(Ts, learn[i],0.9-(k+1)*0.05-0.1);
				for(int m=0;m<t.length;m++) {
					System.out.print(df.format(t[m])+" ");
				}
				System.out.println("");
			}
		}
		ps.close();
	}
	public double[] gett(double[] Ts,double learn, double d) {
		double T1=10;//技能为1初始的时间
		double b=24;//系数
		double m=learn;//学习率
		double M=d;//临界因子
		double t[]=new double[Ts.length];
		for(int i=0;i<t.length;i++) {
			t[i]=b*(Math.pow(Math.E,(1.0/m)*Math.log((1-M)/(Ts[i]/T1-M)))-1);
		}
		return t;
	}
	@Test
	public void test2() {//输出最终技能使用的数据
		DecimalFormat df2=new DecimalFormat("#0.00");
		double T1=10;
		double[] level= {1,2,3,4};
		double diff[]=new double[8];//代表技能的困难系数 系数越小 则时间减缓越慢  相应的技能水平级之间的时间差距也相对较少
		double border[]=new double[diff.length];
		for(int i=0;i<diff.length;i++) {
			diff[i]=(i)*0.5/10+0.1;//0.25为最低  0.1为差距 0.35为界限  注意此参数与临界因子及考虑的水平级数有关 不能减到临界值之下
		    border[i]=Double.valueOf(df2.format(0.8-(i+1)*0.05));
		}
		double Qdiff[]=new double[15];
		for(int i=0;i<diff.length;i++) {
			Qdiff[i]=diff[i];
		}
		for(int i=0;i<15-diff.length;i++) {
			Qdiff[i+diff.length]=diff[i];
		}
		DecimalFormat df=new DecimalFormat("#0.0");
		String path="case_q//q.txt";
		PrintStream ps=getPrintStream(path);
		System.setOut(ps);
		System.out.println("/********技能水平-时间表*********/");
		System.out.println("标准考题");
		for(int i=0;i<Qdiff.length;i++) {
			System.out.println("/********技能Q"+(i+1)+"标准考题"+"*********/");
			System.out.println("临界因子"+" "+border[i%border.length]);
			System.out.println("水平 :1 2 3 4 ");
			System.out.print("执行时间  :");
			
			double[] Ts=new double[level.length];
			for(int j=0;j<level.length;j++) {
				Ts[j]=T1*Math.pow(level[j],-Qdiff[i]);//0.3
				System.out.print(df.format(Ts[j])+" ");
			}
//			double[] t=gett(Ts);
//			System.out.println("");
//			System.out.println("/********技能Q"+(i+1)+"升级时间表"+"*********/");
//			System.out.print("达到该等级需要的时间     ");
//			for(int k=0;k<t.length;k++) {
//				System.out.print(df.format(t[k])+" ");
//			}
//			System.out.println("");
//			System.out.print("升级到下一级需要的时间 ");
//			System.out.print(df.format(t[1])+" ");
//			for(int k=2;k<t.length;k++) {
//				System.out.print(df.format(t[k]-t[k-1])+" ");
//			}
		    System.out.println("");
		}
		ps.close();
	}
	/*
	 * 受Ts序列影响  Ts应该前面序列差大 后面序列差小  且逐渐减小 速度递减的递减序列
	 * 研究Ts序列函数  T1*x^(-0.3)  x为技能水平等级
	 * 学习率影响太大  
	 */
	//{10,8,7,6.3,5.8,5.5
	private PrintStream getPrintStream(String path) {
		PrintStream ps=null;
		try {
			File f=new File(path);
			if(f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream fos=new FileOutputStream(f);
			ps = new PrintStream(fos);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ps;
	}
}  //0.515 : 33.926752394597855 64.57616482558407 155.02652336679432 428.6176288976886 1046.8036339971757
   //0.418  : 47.884188558834076 111.19271889760836 322.82088743985656 1101.5291443118633 3332.565585060057 
   //0.321 : 78.20816484224804 249.10280136108423 981.7439625037835 4702.190433547501 20047.986357923153
   //0.152 : 556.184064891272 7723.400770705711 132918.2499737794 3302937.587354199 7.240964839248109E7




