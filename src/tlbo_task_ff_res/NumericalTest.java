package tlbo_task_ff_res;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class NumericalTest {
	public static void main(String[] args) {
		/*
        File ff = new File("case_def");
        String[] fl = ff.list(); 
        if (fl==null){
        	System.out.print("û����case_defĿ¼���ҵ������ļ�");
        	return;           	
        }
        for(int i = 0; i<fl.length; i++){
       	 String _fn =  "case_def/" + fl[i];
       	 String _fo = "data/NSFFA_"+fl[i]+".txt";
       	 NSFFA_algorithm(_fn,_fo);
       }
       System.out.println(fl.length +"��������Ӭ�㷨�������"); 
       return;
		*/
		
        if (args.length==1){
            File ff = new File("case_def");
            //��ȡ������case_def�ļ�Ŀ¼
            String[] fl = ff.list(); 
            if (fl==null){
            	System.out.print("û����case_defĿ¼���ҵ������ļ�");
            	return;           	
            }
    		if (args[0].trim().toLowerCase().equals("g")){
    			for(int j = 0; j < 10; j++){
    				for(int i = 0; i<fl.length; i++){
                   	 String _fn =  "case_def/" + fl[i];
                   	 String _fo = "data/NSGA/nsga"+j+"/NSGA_"+fl[i]+".txt";
                   	 NSGA_algorithm(_fn,_fo);
                   }
    			}
                System.out.println(fl.length +"�������Ŵ��㷨�������"); 
                return;
                
    		}
    		if (args[0].trim().toLowerCase().equals("f")){
                for(int i = 0; i<fl.length; i++){
                	 String _fn =  "case_def/" + fl[i];
                	 String _fo = "data/NSFFA_"+fl[i]+".txt";
                	 NSFFA_algorithm(_fn,_fo);
                }
                System.out.println(fl.length +"��������Ӭ�㷨�������"); 
                return;
    		}
    		if (args[0].trim().toLowerCase().equals("t")){
    			for(int j = 0; j < 10; j++){
                for(int i = 0; i<fl.length; i++){
                	 String _fn =  "case_def/" + fl[i];
                	 String _fo = "data/TLBO/tlbo"+j+"/TLBO_"+fl[i]+".txt";
                	 TLBO_algorithm(_fn,_fo);
                }
    			}
                System.out.println(fl.length +"��������ѧ�Ż��㷨�������"); 
                return;
    		}
    		if (args[0].trim().toLowerCase().equals("tf")){
    			for(int j = 0; j < 10; j++){
    				long time=System.currentTimeMillis();
    				 String dic = "data/TLBO_F"+time+"/tlbo_f"+j;
    				 File f=new File(dic);
                	 if(f.exists()) {
                		 f.delete();
                	 }
                	 f.mkdirs();
	                for(int i = 0; i<fl.length; i++){
	                	 String _fn =  "case_def/" + fl[i];
	                	 String _fo = "data/TLBO_F"+time+"/tlbo_f"+j+"/TLBO_F"+fl[i]+".txt";
	                	 TLBO_F_algorithm(_fn,_fo);
	                }
	    			}
	                System.out.println(fl.length +"��������ѧ�Ż���Ӭ�㷨�������"); 
	                return;
    		}

        }else{
        	System.out.print("�����������'g'���Ŵ��㷨��'f'����Ӭ�㷨��'C'���������㷨");
        	return;
        }

	}
	
	public static void TLBO_algorithm(String casefile, String datafile) {
		 //��¼��ʼ�����ʱ�䣬����ͳ�Ʊ��㷨����ʱ��
		long startTime = System.currentTimeMillis();
		// �������������
		Case project = new Case(casefile);

		// ��ʼ����Ⱥ
		Population P = new Population(TLBO.populationSize,project,true);
		
		int generationCount = 0;
        //ѭ������ �㷨ָ���Ĵ���
		while (generationCount < TLBO.maxGenerations ) {

			P = P.getOffSpring_TLBO();

			generationCount++;
		}
		//�����õ���Ⱥ�л�ȡ���Ž⼯
		Population solutions = Tools.getbestsolution(P,1, project);		
		
		 File f = new File(datafile);
		 PrintStream ps = null;
		 try {
		   if (f.exists()) f.delete();
		   f.createNewFile();
		   FileOutputStream fos = new FileOutputStream(f);
		   ps = new PrintStream(fos);
		   System.setOut(ps);
		   //������Ž⼯
		   Tools.printsolutions(solutions,startTime);			   
		 } catch (IOException e) {
			e.printStackTrace();
		 }  finally {
	        if(ps != null) 	ps.close();
	     }
	}
	
	/*@param caseFile:������ȡĿ¼
	 * @param dataFile:����������д��Ŀ¼*/
	public static void NSGA_algorithm(String casefile,String datafile){
	       //��¼��ʼ�����ʱ�䣬����ͳ�Ʊ��㷨����ʱ��
			long startTime = System.currentTimeMillis();
			// �������������
			Case project = new Case(casefile);

			// ��ʼ����Ⱥ
			Population P = new Population(NSGA_II.populationSize,project,true);
			
			int generationCount = 0;
	        //ѭ������ �㷨ָ���Ĵ���
			while (generationCount < NSGA_II.maxGenerations ) {

				P = P.getOffSpring_NSGA();

				generationCount++;
			}
			//�����õ���Ⱥ�л�ȡ���Ž⼯
			Population solutions = Tools.getbestsolution(P,1, project);
			
		    //������Ž⼯
			//Tools.printsolutions(solutions,startTime,datafile);		
			
			
			 File f = new File(datafile);
			 PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   System.setOut(ps);
			   //������Ž⼯
			   Tools.printsolutions(solutions,startTime);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }
		    
		          	
	}
	//��ѧ�㷨���ڲ�����������  ʹ�ù�Ӭ�㷨������Դ��������
	public static void TLBO_F_algorithm(String casefile, String datafile) {
		 //��¼��ʼ�����ʱ�䣬����ͳ�Ʊ��㷨����ʱ��
		long startTime = System.currentTimeMillis();
		// �������������
		Case project = new Case(casefile);

		// ��ʼ����Ⱥ
		Population P = new Population(TLBOF.populationSize,project,true);
		
		int generationCount = 0;
		//List<Integer> best=P.getPopulation().
       //ѭ������ �㷨ָ���Ĵ���
		while (generationCount < TLBOF.maxGenerations ) {

			P = P.getOffSpring_TLBO_F();

			generationCount++;
		}
		//��ȡǰ����ľ�Ӣ����Ĳ������� ������Դ���� ��Ⱥ���� ������������Ⱥ�Ķ�����
		Population elite = Tools.getbestsolution(P,2,project);	
		Population expandPop=elite.serchMoreSpaceByRes(TLBOF.s1);
		//�����õ���Ⱥ�л�ȡ���Ž⼯
		Population solutions = Tools.getbestsolution(expandPop,1, project);		
		
		 File f = new File(datafile);
		 PrintStream ps = null;
		 try {
		   if (f.exists()) f.delete();
		   f.createNewFile();
		   FileOutputStream fos = new FileOutputStream(f);
		   ps = new PrintStream(fos);
		   System.setOut(ps);
		   //������Ž⼯
		   Tools.printsolutions(solutions,startTime);			   
		 } catch (IOException e) {
			e.printStackTrace();
		 }  finally {
	        if(ps != null) 	ps.close();
	     }
	}
	public static void NSFFA_algorithm(String casefile,String datafile){
	       //��¼��ʼ�����ʱ�䣬����ͳ�Ʊ��㷨����ʱ��
			long startTime = System.currentTimeMillis();
			// �������������
			Case project = new Case(casefile);

			// ��ʼ����Ⱥ
			Population P = new Population(NSFFA.NS,project,true);
			
			int generationCount = 0;
	        //ѭ������ �㷨ָ���Ĵ���
			while (generationCount < NSFFA.maxGenerations ) {

				P = P.getOffSpring_NSFFA();

				generationCount++;
			}
			//�����õ���Ⱥ�л�ȡ���Ž⼯
			Population solutions = Tools.getbestsolution(P,1, project);
			
	
			 File f = new File(datafile);
			 PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   System.setOut(ps);
			   //������Ž⼯
			   Tools.printsolutions(solutions,startTime);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }
	}
}
