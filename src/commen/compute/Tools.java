package commen.compute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tools {

	/*
	 * 读取name 链表用于顺序排列输出
	 */
	public static List<String> readNames() {
		List<String> namesList=new ArrayList<>();
		String names="names";
		File file=new File(names);
		BufferedReader read=null;
		try {
			read=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		    String line=null;
		    while((line=read.readLine())!=null) {
		    	namesList.add(line.trim());
		    }
		    read.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return namesList;
	}
	//约束情况  返回1 obj1约束obj2  返回2 obj2约束obj1  返回0 没有相互之间的关系
	public static int dominate(List<Double> obj1, List<Double> obj2) {
		int flag, n, k;
		flag =  n = k = 0;

		//个体目标函数值
		for (int i = 0; i < obj1.size(); i++) {
			if (obj1.get(i) < obj2.get(i) ) {
				n++;
			} else if (obj1.get(i) > obj2.get(i) ) {
				k++;
			}
		}
		if (k == 0 && n > 0) {
			flag = 1;
		}
		if (n == 0 && k > 0) {
			flag = 2;
		}
		return flag;
    }
	//生成文件夹名字标识 避免重复 
	public static String buildFileName(){
		 //new一个时间对象date
		 Date date = new Date();
		 //格式化
		 SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
		 //格式化时间，并且作为文件名
		 return sdf.format(date);
	
	}

}
