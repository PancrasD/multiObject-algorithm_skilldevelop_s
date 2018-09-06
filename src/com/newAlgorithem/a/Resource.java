package com.newAlgorithem.a;


/**
 * 定义一个资源类，包含属性：
 * 资源ID，每小时的薪水，掌握的技能
 * @author 熊凯
 *
 */
public class Resource {
	//资源ID
	private int resourceID;
	//员工每小时的薪水
	private double salary;
	//员工掌握的技能
	private String skils;
	//资源学习能力
	private double learnbility;
		
	@SuppressWarnings("null")
	public Resource(int resourceID,double salary,String skills){
		this.resourceID=resourceID;
		this.salary=salary;
		this.skils=skills;
		
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
