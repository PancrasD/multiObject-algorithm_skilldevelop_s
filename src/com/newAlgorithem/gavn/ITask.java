package com.newAlgorithem.gavn;

import java.util.ArrayList;
import java.util.List;

public class ITask {
	public int taskid;
	public int pretasknum;
	public int startTime;
	public int finishTime;
	private double standardDuration;
	//private String skill;
	private String skillType;
	private double skillLevel;
	private List<Integer> predecessorIDs = new  ArrayList<>();
	
	private List<Integer> resourceIDs ;
	
	public ITask(Task task){
		taskid = task.getTaskID();
		pretasknum=task.getpretasknum();
		standardDuration = task.getStandardDuration();
		skillType = task.getSkillType();
		skillLevel = task.getSkillLevel();
		predecessorIDs = task.getPredecessorIDs();
		resourceIDs = new ArrayList<>(task.getresourceIDs());//传递有问题
	}
	
	public void setstarttime(int starttime, double qlevel){
		startTime = starttime;
		finishTime =(int) Math.ceil(startTime + standardDuration/qlevel);//- 1
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getFinishTime (){
		return finishTime;
	}
	public double getStandardDuration(){
		return standardDuration;
	}
	
	/*public String getSkill(){
		return skill;
	}*/
	
	public double getSkillLevel(){
		return skillLevel;
	}
	
	public String getSkillType(){
		return skillType;
	}
	
	public List<Integer> getPredecessorIDs(){
		return predecessorIDs;
	} 
	
	public void addResourceIDs(int id){
		resourceIDs.add(id);
	}
	
	public List<Integer> getresourceIDs( ) {
		return resourceIDs;
	}

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	

}
