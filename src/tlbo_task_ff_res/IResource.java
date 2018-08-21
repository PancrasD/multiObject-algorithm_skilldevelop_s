package tlbo_task_ff_res;

import java.util.HashMap;

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
		
		@SuppressWarnings("null")
		public IResource(Resource resource){
			this.resourceID=resource.getResourceID();
			this.skils=resource.getSkils();
			this.salary = resource.getSalary();
			this.learnbility = resource.getLearnbility();
			
			for(String skill:resource.getSkils().trim().split(" ")){
				String[] skillInfo = skill.trim().split(":");
				this.skillsInfo.put(skillInfo[0], Double.valueOf(skillInfo[1])+1);
						
				HashMap<int[], Double> table = new HashMap<int[], Double>();
				table.put(new int[]{0,0}, Double.valueOf(skillInfo[1])+1);
				this.skillTimetable.put(skillInfo[0], table);
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
}
