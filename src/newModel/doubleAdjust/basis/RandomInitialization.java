package newModel.doubleAdjust.basis;

import newModel.doubleAdjust.Population;

public class RandomInitialization implements Initialization {
    
	@Override
	public Population initialize(Case project) {
		Parameter para=project.getParameter();
		String type=para.getType();
		Population population=null;
		switch(type) {
		case "single":population=initialize_single(project);
		case "double":population=initialize_double(project);
		}
		return population;
	}
    /*
     * 单链表 资源链表进化的初始化
     */
	private Population initialize_single(Case project) {
		Parameter para=project.getParameter();
		int populationSize=para.getPopulationSize();
		Population pop=new Population( populationSize,  project,true,true);
		return pop;
	}
    /*
     * 双链表 任务-资源链表进化的初始化
     */
	private Population initialize_double(Case project) {
		Parameter para=project.getParameter();
		int populationSize=para.getPopulationSize();
		Population pop=new Population( populationSize,  project,true);
		return pop;
	}


}
