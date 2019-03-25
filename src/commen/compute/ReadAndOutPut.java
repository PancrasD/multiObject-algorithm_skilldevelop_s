package commen.compute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;



public class ReadAndOutPut {
	@Test
	public void test() {
		String mofoa="D:\\download\\timdownload\\resultsOf36\\MOFOA\\experiment";
		String fsfoa="D:\\download\\timdownload\\resultsOf36\\FSFOA\\experiment";
		String sfsfoa="D:\\download\\timdownload\\resultsOf36\\RLFSFOA\\experiment";
		Map<String,List<Double>> mofoaList=read(mofoa);
		Map<String,List<Double>> fsfoaList=read(fsfoa);
		Map<String,List<Double>> sfsfoaList=read(sfsfoa);
		List<Double> aver_mofoa=computeAver(mofoaList);
		List<Double> aver_fsfoa=computeAver(fsfoaList);
		List<Double> aver_sfsfoa=computeAver(sfsfoaList);
		//输出
		System.out.println("平均时间");
		System.out.println("SFSFOA  FSFOA  MOFOA");
		outputAver(aver_sfsfoa,aver_fsfoa,aver_mofoa);
		System.out.println("SFSFOA  VS  FSFOA");
		outputCompare(sfsfoaList,fsfoaList);
		System.out.println("SFSFOA  VS  MOFOA");
		outputCompare(sfsfoaList,mofoaList);
		System.out.println("FSFOA VS MOFOA");
		outputCompare(fsfoaList,mofoaList);
	}
	/*
	  * 输出显著性比对结果
	 */
	private void outputCompare(Map<String, List<Double>> sfsfoaList, Map<String, List<Double>> mofoaList) {
		
		for(int k=0;k<30;k++) {
			System.out.print((k+1)+" ");
			for(int i=1;i<=36;i++) {
				List<Double> one=sfsfoaList.get(String.valueOf(i));
				System.out.print(one.get(k)+" ");
			}
			System.out.println();
		}
		System.out.println();
		for(int k=0;k<30;k++) {
			System.out.print((k+1)+" ");
			for(int i=1;i<=36;i++) {
				List<Double> one=mofoaList.get(String.valueOf(i));
				System.out.print(one.get(k)+" ");
			}
			System.out.println();
		}
		System.out.println();
		
	}
	/*
	 * 输出平均值
	 * 输出格式 sfsfoa fsfoa mofoa
	 */
	private void outputAver(List<Double> aver_sfsfoa, List<Double> aver_fsfoa, List<Double> aver_mofoa) {
		for(int i=0;i<aver_mofoa.size();i++) {
			System.out.println(aver_mofoa.get(i)+" "+aver_fsfoa.get(i)+" "+aver_sfsfoa.get(i));
		}
		System.out.println();
	}
	/*
	 * 计算每个案例的平均值
	 */
    private List<Double> computeAver(Map<String, List<Double>> mofoaMap) {
    	List<Double> aver=new ArrayList<>();
		for(int i=1;i<=36;i++) {
			List<Double> child=mofoaMap.get(String.valueOf(i));
			double  averValue=0;
			double sum=0;
			for(int j=0;j<child.size();j++) {
				sum+=child.get(j);
			}
			averValue=sum/child.size();
			aver.add(averValue);
		}
		return aver;
	}
	/*
       * 读取数据
     */
	private Map<String, List<Double>> read(String path) {
		Map<String, List<Double>> map=new HashMap<String, List<Double>>();
		File f=new File(path);
		String[]strs=f.list();
		for(int i=0;i<strs.length;i++) {
			List<Double> fileList=new ArrayList<>();
			String dataPath=path+"\\"+strs[i];
			String index=strs[i].replace(".txt", " ").trim();
			File data=new File(dataPath);
			try {
				BufferedReader bf=new BufferedReader(new InputStreamReader(new FileInputStream(data)));
				String line="";
				while((line=bf.readLine())!=null) {
					if(line.startsWith("Running time")) {
						String[] ss=line.split(":");
						String time=ss[1].replace('s', ' ');
						fileList.add(Double.valueOf(time.trim()));
					}
				}
				bf.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			map.put(index, fileList);
		}
		return map;
	}

}
