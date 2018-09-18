package com.newAlgorithem.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定义一个任务类，属性包含：
 * 任务ID，任务工期，执行任务所需技能，紧前任务IDs
 * @author 熊凯
 *
 */
public class Task implements Cloneable {
	//任务ID
	private int taskID;
	//任务工期
	private int duaration;
	//执行任务所需技能
	private String skill;
	//紧前任务IDs
	private List<Integer> predecessorIDs = new  ArrayList<>();
	//紧前追溯所有任务IDs
	private List<Integer> prePreIDs = new  ArrayList<>();
	//紧前任务数
	private int pretasknum;
	//标准工期
	private int standardDuration;
	//紧后任务IDS
	private List<Integer> successorTaskIDS = new  ArrayList<>();
	//紧后追溯任务IDs
	private List<Integer> sucSucIDS = new  ArrayList<>();
	//任务可用的资源
	private List<Integer> resourceIDs = new ArrayList<>();
	
	//每个资源使用的可能性
	private Map<Integer,Double> capaleResource = new HashMap<Integer,Double>();
	
	//任务的开始时间
	private int startTime;
	//任务的结束时间
	private int finishTime;
	//技能水平
	private int skillLevel;
	
	
	public Task(){}
	public Task(int taskID,int duaration,String skill){
		this.taskID=taskID;
		this.duaration=duaration;
		this.skill=skill;
		this.pretasknum = 0;
		this.skillLevel = Integer.valueOf(skill.split(":")[1])+1;
		this.standardDuration = duaration*skillLevel;
	}
	public Task(int taskID,int duaration,String skill,String pretaskIDs){
		this.taskID=taskID;
		this.duaration=duaration;
		this.skill=skill;
		this.skillLevel = Integer.valueOf(skill.split(":")[1])+1;
		this.standardDuration = duaration*skillLevel;
		String[] pre_IDs = pretaskIDs.trim().split(" ");
		this.pretasknum =  pre_IDs.length;
		for (int i = 0; i<pretasknum; i++){
			this.predecessorIDs.add(Integer.valueOf(pre_IDs[i]));	
		}
	}
	

	
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	public int getStandardDuration() {
		return standardDuration;
	}
	public void setStandardDuration(int dura) {
		this.standardDuration = dura;
	}
	public int getpretasknum() {
		return pretasknum;
	}
	public void setpretasknum(int num) {
		this.pretasknum = num;
	}
	public int getDuaration() {
		return duaration;
	}
	public void setDuaration(int duaration) {
		this.duaration = duaration;
	}
	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
	}
	public List<Integer> getPredecessorIDs() {
		return predecessorIDs;
	}
	public void setPredecessorIDs(List<Integer> pretaskIDs) {
		this.predecessorIDs = pretaskIDs;
	}
	
	public List<Integer> getresourceIDs( ) {
		return resourceIDs;
	}
	public void setresourceIDs(List<Integer> resIDs) {
		this.resourceIDs = resIDs;
	}
	
	public List<Integer> getsuccessortaskIDS( ) {
		return successorTaskIDS;
	}
	public void setsuccessorTaskIDS(List<Integer> taskids) {
		this.successorTaskIDS = taskids;
	}
	
	
	
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
		this.finishTime = this.startTime + this.duaration - 1;
	}
	public int getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

    public Task clone()throws CloneNotSupportedException {
		//只复制了任务中的值类型变量，如pretasknum、startTime、finishTime
    	//而任务的前继任务数组、后继任务数组、可用资源数组还是引用，在使用时需要注意。
    	Task clone = (Task)super.clone();
   		return clone;     
    }
	public Map<Integer,Double> getCapaleResource() {
		return capaleResource;
	}
	public void setCapaleResource(Map<Integer,Double> capaleResource) {
		this.capaleResource = capaleResource;
	}
	public List<Integer> getPrePreIDs() {
		return prePreIDs;
	}
	public void setPrePreIDs(List<Integer> prePreIDs) {
		this.prePreIDs = prePreIDs;
	}
	public List<Integer> getSucSucIDS() {
		return sucSucIDS;
	}
	public void setSucSucIDS(List<Integer> sucSucIDS) {
		this.sucSucIDS = sucSucIDS;
	}

	
	
}
