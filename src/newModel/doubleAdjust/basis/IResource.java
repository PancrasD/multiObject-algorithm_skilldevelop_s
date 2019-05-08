package newModel.doubleAdjust.basis;

import java.util.HashMap;
import java.util.LinkedList;

public class IResource {
		//资源ID
		private int resourceID;
		//员工每小时的薪水
		private double salary;
		//员工掌握的技能
		private String skils;
		//资源学习能力
		private double learnbility;
		private HashMap<String,Double> skillsInfo = new HashMap<String,Double>();
		private HashMap<String,HashMap<int[],Double>> skillTimetable = new HashMap<String,HashMap<int[],Double>>();
		private HashMap<String,LinkedList<int[]>> skillTime=new HashMap<>();
		//技能进化时间表 key:技能类型  value:从当前技能水平到下一技能水平需要的时间数组
		private HashMap<String,Double[]> nextLevelTime=new HashMap<>();
		//key:技能类型  value:在当前技能水平累计时间
		private HashMap<String,Double> sustainTime=new HashMap<>();
		@SuppressWarnings("null")
		public IResource(Resource resource){
			this.resourceID=resource.getResourceID();
			this.skils=resource.getSkils();
			this.salary = resource.getSalary();
			this.learnbility = resource.getLearnbility();
			this.nextLevelTime=resource.getNextLevelTime();
			for(String skill:resource.getSkils().trim().split(" ")){
				String[] skillInfo = skill.trim().split(":");
				this.skillsInfo.put(skillInfo[0], Double.valueOf(skillInfo[1]));
				this.sustainTime.put(skillInfo[0],  0.0);
				/*HashMap<int[], Double> table = new HashMap<int[], Double>();
				int[] zero=new int[]{0,0};
				table.put(zero,Double.valueOf(skillInfo[1])+1);
				this.skillTimetable.put(skillInfo[0], table);
				LinkedList<int[]> list=new LinkedList<>();
				list.add(zero);
				skillTime.put(skillInfo[0], list);*/
			}
		}
		//
		public HashMap<String, Double> getSkillsInfo(){
			return skillsInfo;
		}
		
		public void putSkillsInfo(String type, Double level) {
			skillsInfo.put(type, level);
		}
		
		public HashMap<String,HashMap<int[],Double>> getSkillTimetable(){
			return skillTimetable;
		}
		
		public void putSkillTimetable(String type, int[] time, Double level){
			skillTimetable.get(type).put(time, level);
			if(this.getSkillTime().get(type)==null) {
				LinkedList<int[]> list=new LinkedList<>();
				list.add(time);
				this.skillTime.put(type, list);
			}else {
			this.getSkillTime().get(type).add(time);
			}
		}
		
		public int getResourceID() {
			return resourceID;
		}
		public void setResourceID(int resourceID) {
			this.resourceID = resourceID;
		}
		public double getSalary() {
			return salary;
		}
		public void setSalary(double salary) {
			this.salary = salary;
		}
		public String getSkils() {
			return skils;
		}
		public void setSkils(String skils) {
			this.skils = skils;
		}
		
		public void setLearnbility(double learn){
			this.learnbility = learn;
		}
		
		public double getLearnbility(){
			return learnbility;
		}
		public HashMap<String, LinkedList<int[]>> getSkillTime() {
			return skillTime;
		}
		public void setSkillTime(HashMap<String, LinkedList<int[]>> skillTime) {
			this.skillTime = skillTime;
		}
		public HashMap<String, Double[]> getNextLevelTime() {
			return nextLevelTime;
		}
		public void setNextLevelTime(HashMap<String, Double[]> nextLevelTime) {
			this.nextLevelTime = nextLevelTime;
		}
		public HashMap<String, Double> getSustainTime() {
			return sustainTime;
		}
		public void setSustainTime(HashMap<String, Double> sustainTime) {
			this.sustainTime = sustainTime;
		}
		public void setSkillsInfo(HashMap<String, Double> skillsInfo) {
			this.skillsInfo = skillsInfo;
		}
		public void putSustainTime(String qtype,double time) {
			this.sustainTime.put(qtype, time);
		}
}
