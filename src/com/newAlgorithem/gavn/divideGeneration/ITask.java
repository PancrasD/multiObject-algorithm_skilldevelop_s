package com.newAlgorithem.gavn.divideGeneration;

import java.util.ArrayList;
import java.util.List;

public class ITask {
	public int taskid;
	public int pretasknum;
	public int startTime;
	public int finishTime;
	private int standardDuration;
	private String skill;
	private String skillType;
	private int skillLevel;
	private List<Integer> predecessorIDs = new  ArrayList<>();
	
	private List<Integer> resourceIDs = new ArrayList<>();
	
	public ITask(Task task){
		taskid = task.getTaskID();
		pretasknum=task.getpretasknum();
		standardDuration = task.getStandardDuration();
		skill = task.getSkill();
		skillType = skill.split(":")[0].trim();
		skillLevel = Integer.valueOf(skill.split(":")[1])+1;
		predecessorIDs = task.getPredecessorIDs();
		resourceIDs = task.getresourceIDs();
	}
	
	public void setstarttime(int starttime, double qlevel){
		startTime = starttime;
		finishTime =(int) Math.ceil(startTime + standardDuration/qlevel - 1);//向前取 向后取
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getFinishTime (){
		return finishTime;
	}
	public int getStandardDuration(){
		return standardDuration;
	}
	
	public String getSkill(){
		return skill;
	}
	
	public int getSkillLevel(){
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
