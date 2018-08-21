package genetic_algorithm;

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
	//任务可用的资源
	private List<Integer> resourceIDs = new ArrayList<>();
	
	public ITask(Task task){
		taskid = task.getTaskID();
		pretasknum=task.getpretasknum();
		standardDuration = task.getStandardDuration();
		skill = task.getSkill();
		skillType = skill.split(":")[0];
		skillLevel = Integer.valueOf(skill.split(":")[1])+1;
		predecessorIDs = task.getPredecessorIDs();
		resourceIDs = task.getresourceIDs();
	}
	
	public void setstarttime(int starttime, double qlevel){
		startTime = starttime;
		finishTime = (int)(startTime + standardDuration/qlevel - 1);
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
	

}
