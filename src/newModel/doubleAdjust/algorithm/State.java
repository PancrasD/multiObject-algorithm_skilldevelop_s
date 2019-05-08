package newModel.doubleAdjust.algorithm;

import java.util.Arrays;
import java.util.Map;

import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;

public class State {
	
	//计算临界值
	double[] bound=new double[2];
	private String casefile;
	public State(String casefile) {
		this.casefile=casefile;
		initBound();
	}
	/*
	 * 计算学习的临界值
	 */
	private void initBound() {
		//计算指标
		Map<String,double[]> upbound=Tools.readBound();
		String fileName=casefile.substring(casefile.lastIndexOf("/")+1, casefile.length())+".txt";
		bound=upbound.get(fileName);
	}
	/*
	 * MID不反转 则设置为初始计算值
	 */
	public void boundSet(double[] measures1_init, double[] measures2_init) {
		this.bound[1]=measures1_init[1]<measures2_init[1]?measures1_init[1]:measures2_init[1];
		
	}
	public void setBound(double[] measures1_init) {
		this.bound[1]=measures1_init[1];
		
	}
	/*
	 * 计算state的索引
	 */
	public  int getState(double hv,double mid) {
		int s=getMIDIndex(mid)*5+getHVIndex(hv);
		return s;
	}
	public int getHVIndex( double hv) {
		hv=hv/bound[0];
		int index=hv<=0.7?0:hv<=0.8?1:hv<=0.9?2:hv<=1?3:4;
		return index;
	}
	public int getMIDIndex( double mid) {
		mid=mid/bound[1];
		int index=mid<=0.6?0:mid<=0.7?1:mid<=0.8?2:mid<=0.9?3:4;
		return index;
	}
	/*
	 * 判断是否进入定义的状态空间内
	 */
	public boolean judgeIn(double[] measures) {
		return !(measures[0]/bound[0]<0.6);//超体积大于限定值0.6才允许进入
	}
	/*
	 * 获取奖赏 以HV值为计算
	 */
	public double getReward(double[] measures, double[] measures_new) {
		return (measures_new[0]-measures[0])/measures[0];
	}
	/*
	 * 获取状态
	 */
	public int getAction(int state, Case project) {
		double[] prob=project.getProbility()[state];
		double theta=0.1;
		int action=-1;
		String type="roulte";
		if(type.equals("roulte")) {
			action=getActionByroulte(prob);
		}else {
			action=Math.random()<theta?getActionByRandom(prob):getActionByMax(prob);
		}
		return action;
	}
	/*
	 * 均匀随机选取
	 */
	private int getActionByRandom(double[] prob) {
		
		return (int) (Math.random()*prob.length);
	}
	/*
	 * 最大概率 选取
	 */
	private int getActionByMax(double[] prob) {
		int index=-1;
		double max=-1;
		for(int i=0;i<prob.length;i++) {
			if(max<prob[i]) {
				max=prob[i];
				index=i;
			}
		}
		return index;
	}
	/*
	 * 轮盘赌获取动作
	 */
	private int getActionByroulte(double[] prob) {
		double rouletteWheelPosition = Math.random();
		// 选择资源
		double spinWheel = 0;  
		int index = -1;
		for(int i=0;i<prob.length;i++) {
			spinWheel+=prob[i];
			if (spinWheel >= rouletteWheelPosition) {
				index=i;
				break;
			}
		}
		if(index==-1) {
			System.out.println();
		}
		return index;
	}
	/*
	 * 根据奖赏值更新前一状态下前一动作对应的Q值
	 * @param reward 奖赏值
	 * @param project 案例
	 * @param state 前一状态
	 * @param action 前一动作
	 * @param newState 新状态
	 */
	public  void updateQ(double reward, Case project, int state, int action, int newState) {
		double[][] Q=project.getQ_value();
		double lr=0.1;
		double dr=0.5;
		double Q_max=getMaxInState(Q[newState]);
		double q_origin = 0;
		try {
			q_origin = Q[state][action];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double q_new=q_origin*(1-lr)+lr*(reward+dr*Q_max);
		if(q_new<0) {
			Q[state][action]=0;
		}
		Q[state][action]=q_new;
		//Q[state][action]=Q[state][action]+reward;
		
	}
	/*
	 * 获取最大值
	 */
	private double getMaxInState(double[] q_value) {
		double max=-10000000;
		for(int i=0;i<q_value.length;i++) {
			max=max<q_value[i]?q_value[i]:max;
		}
		return max;
	}
	/*
	 * 更新更新了Q值的状态行的选择概率
	 * @param project 项目案例
	 * @param state 之前的状态
	 */
	public  void updateProb(Case project, int state) {
		 double[][] Q=project.getQ_value();
		 double[][] prob=project.getProbility();
		 double[] ps=prob[state];
		 double[] QS=Q[state];
		 double Q_max=getMaxInState(QS);
		 Q_max=Q_max==0?1:Q_max;
		 double[] temp=new double[QS.length];
		 double sum=0;
		 double factor=0.5;//
		 for(int i=0;i<QS.length;i++) {
			 temp[i]=Math.exp(factor*(QS[i]/Q_max));
			 sum+=temp[i];
		 }
		 double[]old=Arrays.copyOf(ps, ps.length);
		 for(int i=0;i<ps.length;i++) {
			 ps[i]=temp[i]/sum;
		 }
		 double[] newP=prob[state];
		 if(Double.isNaN(newP[0])) {
			 System.out.println("NAN NAN");
		 }
		 double d=old[0]+newP[0];
	}
	
}
