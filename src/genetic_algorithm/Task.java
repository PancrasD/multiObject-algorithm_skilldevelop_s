package genetic_algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ����һ�������࣬���԰�����
 * ����ID�������ڣ�ִ���������輼�ܣ���ǰ����IDs
 * @author �ܿ�
 *
 */
public class Task implements Cloneable {
	//����ID
	private int taskID;
	//������
	private int duaration;
	//ִ���������輼��
	private String skill;
	//��ǰ����IDs
	private List<Integer> predecessorIDs = new  ArrayList<>();
	//��ǰ������
	private int pretasknum;
	//��׼����
	private int standardDuration;
	//��������IDS
	private List<Integer> successorTaskIDS = new  ArrayList<>();
	//������õ���Դ
	private List<Integer> resourceIDs = new ArrayList<>();
	
	//ÿ����Դʹ�õĿ�����
	private Map<Integer,Double> capaleResource = new HashMap<Integer,Double>();
	
	//����Ŀ�ʼʱ��
	private int startTime;
	//����Ľ���ʱ��
	private int finishTime;
	//����ˮƽ
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
		//ֻ�����������е�ֵ���ͱ�������pretasknum��startTime��finishTime
    	//�������ǰ���������顢����������顢������Դ���黹�����ã���ʹ��ʱ��Ҫע�⡣
    	Task clone = (Task)super.clone();
   		return clone;     
    }
	public Map<Integer,Double> getCapaleResource() {
		return capaleResource;
	}
	public void setCapaleResource(Map<Integer,Double> capaleResource) {
		this.capaleResource = capaleResource;
	}

	
	
}
