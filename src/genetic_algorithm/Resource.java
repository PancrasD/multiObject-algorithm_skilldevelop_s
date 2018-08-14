package genetic_algorithm;


/**
 * ����һ����Դ�࣬�������ԣ�
 * ��ԴID��ÿСʱ��нˮ�����յļ���
 * @author �ܿ�
 *
 */
public class Resource {
	//��ԴID
	private int resourceID;
	//Ա��ÿСʱ��нˮ
	private double salary;
	//Ա�����յļ���
	private String skils;
	//��Դѧϰ����
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
