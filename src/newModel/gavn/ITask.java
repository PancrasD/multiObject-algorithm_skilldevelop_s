package newModel.gavn;

import java.util.ArrayList;
import java.util.List;

public class ITask {
	public int taskid;
	public int pretasknum;
	public int startTime;
	public int finishTime;
	//任务工期
    private int duaration;
	private double standardDuration;
	//private String skill;
	private String skillType;
	private double skillLevel;
	private List<Integer> predecessorIDs = new  ArrayList<>();
	private List<Integer> resourceIDs ;
	
	public ITask(Task task){
		taskid = task.getTaskID();
		duaration=task.getDuaration();
		pretasknum=task.getpretasknum();
		/*standardDuration = task.getStandardDuration();*/
		skillType = task.getSkillType();
		skillLevel = task.getSkillLevel();
		predecessorIDs = task.getPredecessorIDs();
		resourceIDs = new ArrayList<>(task.getresourceIDs());
	}
	
	public void setstarttime(int starttime, double qlevel){
		startTime = starttime;
		//查询标准考题时间表 进行工作量的衡量
		/*finishTime =(int) Math.ceil(startTime + standardDuration/qlevel);//- 1
*/	}
	public void setStartAndFinish(int start_time,double finish_time) {
		startTime = start_time;
		finishTime=(int) Math.ceil(finish_time);
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

	public int getPretasknum() {
		return pretasknum;
	}

	public void setPretasknum(int pretasknum) {
		this.pretasknum = pretasknum;
	}

	public void setPredecessorIDs(List<Integer> predecessorIDs) {
		this.predecessorIDs = predecessorIDs;
	}

	public int getDuaration() {
		return duaration;
	}

	public void setDuaration(int duaration) {
		this.duaration = duaration;
	}
	

}
