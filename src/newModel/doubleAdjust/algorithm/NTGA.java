package newModel.doubleAdjust.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;
import newModel.doubleAdjust.operator.CrossOver;
import newModel.doubleAdjust.operator.Mutation;
import newModel.doubleAdjust.operator.Select;
import newModel.doubleAdjust.operator.SingleListCrossOver;
import newModel.doubleAdjust.operator.SingleListMutation;
import newModel.doubleAdjust.util.Util;

public class NTGA extends Algorithm{
	
	public NTGA(String _fn, String _fo, List<List<Double>> countResult, Parameter para) {
		super(_fn, _fo, countResult, para);
	}

	public NTGA() {
		// TODO Auto-generated constructor stubs
	}

	@Override
	public void schedule() {
		// 创建案例类对象
		Case project = new Case(casefile);
		project.setParameter(para);
		project.setRunTime(para.getRunTime());
		// 初始化种群
		long startTime = System.currentTimeMillis();
		Population P1 = new Population(project.getParameter().getPopulationSize(),project,true,true);
		Population pp=evolve(P1);
		long endTime = System.currentTimeMillis();
		//从最后得到种群中获取最优解集
		Population pop=new Population(pp.getProject().getPareto().size(),project);
		pop.setPopulation(Tools.getArray(pp.getProject().getPareto()));
		Population solutions = Tools.getbestsolution(pop,1, project);
		solutions=Tools.removeSame(solutions);//去重
	    //输出最优解集
		Tools.outputSolution(solutions,endTime-startTime,datafile,countResult);
	}
    /*
     * 进化种群
     */
	private Population evolve(Population P1) {
		int count=0;
		while (count < 100000 ) {
			P1=getOffSpring_NTGA(P1);
			count+=P1.getPopulationsize()*2;
		}
		return P1;
	}
	public Population getOffSpring_NTGA(Population P) {
		List<Individual> newPopulation = new ArrayList<>();
		Tools.setRankAndCrowD(P, P.getProject());
		Individual firstParent;
		Individual secondParent;
		Individual firstChild;
		Individual secondChild;
		Case project=P.getProject();
		CrossOver crossOver=new SingleListCrossOver();
		Mutation mutation =new SingleListMutation();
		Select select=new Select();
		int populationsize=P.getPopulationsize();
		Parameter para=project.getParameter();
		double crossoverRate=1;
		double mutationRate=0.005;
		int tour=para.getTour();
		while (newPopulation.size() < populationsize) {
			firstParent=select.selectTournament(P.getPopulation(),project.getPareto(),tour);//6 为tournamentSize
			secondParent=select.selectTournament(P.getPopulation(),project.getPareto(),tour);
			List<Individual> childs=crossOver.crossOver(firstParent,secondParent,crossoverRate);//交叉率1
			firstChild=mutation.mutation(childs.get(0),mutationRate);//变异率0.005
			secondChild=mutation.mutation(childs.get(1),mutationRate);
			for (int i = 0; newPopulation.contains(firstChild) && i < 20; i++) {// 20 
				firstChild=mutation.mutation(firstChild, mutationRate);
		        }
	        if (!newPopulation.contains(firstChild)) {
	          newPopulation.add(firstChild);
	        }
	        for (int i = 0; newPopulation.contains(secondChild) && i < 20; i++) {//20
	            secondChild=mutation.mutation(secondChild, mutationRate);
	          }
	        if (!newPopulation.contains(secondChild)) {
	            newPopulation.add(secondChild);
	          }
		}
		Population OffSpring = new Population(populationsize,project,false);
		OffSpring.setPopulation(Tools.getArray(newPopulation));
		Population pop=merged(P,OffSpring);
		Population newPop = pop.slectPopulation(populationsize);
		
		Tools.updatePareto(project,OffSpring);
		
		return newPop;
	}
	/*
	 * 合并两个种群
	 */
	public Population merged(Population p1,Population p2){
		List<Individual> mergedList = new ArrayList<>();
		for (int i = 0; i < p1.size(); i++) {
			if (!mergedList.contains(p1.getPopulation()[i])) {
			  mergedList.add(p1.getPopulation()[i]);				
			}
		}
		for (int i = 0; i < p2.size(); i++) {
			if (!mergedList.contains(p2.getPopulation()[i])) {
				  mergedList.add(p2.getPopulation()[i]);				
				}			
		}
		Population mergedPopulation = new Population(mergedList.size(),p1.getProject());
		for (int i =0; i <mergedList.size();i++){
			mergedPopulation.setIndividual(i, mergedList.get(i));
		}
		return mergedPopulation;
	}
     
}
