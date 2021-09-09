package export_failed_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExportFailedTestMain {

	static String defects4jPath="/home/cxy/GrowingBugRepository/framework/bin";
	static String path="/home/cxy/GrowingBugRepository/framework/bug-mining/bug_mining_projects_info.txt";
	
	public static void main(String[] args) throws Exception {
    	 String checkoutPath="/home/cxy/Downloads/paper/checkout-4j";
    	 String outputPath="/home/cxy/Downloads/paper/failed_tests";
         ArrayList<ArrayList<String> > list = getProjectList(path);
        //for(int i=0;i<list.get(0).size();i++){
        for(int i=0;i<=0;i++){
            String projectName=list.get(0).get(i);
            String subProjectName=list.get(1).get(i);
            if(i<=(0-1))continue;
            checkoutProject(path,projectName,subProjectName,checkoutPath,outputPath);
        }
    }
    public static void checkoutProject(String path,String projectName,String subProjectName,String checkoutPath,String outputPath){
        try{
            File directory = new File(checkoutPath+"/"+projectName);
            directory.mkdir();
        }catch (Exception e) {
            //e.printStackTrace();
        }
        try{
            File directory = new File(outputPath+"/"+projectName);
            directory.mkdir();
        }catch (Exception e) {
            //e.printStackTrace();
        }
        for(int i=1;i<=202;++i){
            Process p0=null;
            Runtime r0=Runtime.getRuntime();
            try {
                p0 = r0.exec("ls "+outputPath+"/"+projectName+"/"+projectName+"_"+i+"/"+subProjectName+"/all_tests ");
                int status = p0.waitFor();
                if(status != 0){
                    continue ;
                }
                p0.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            Runtime r1=Runtime.getRuntime();
            Process p1=null;
            try {
                String cmd2 ="cd "+checkoutPath+"/"+projectName;
                String cmd4="cd "+projectName+"_"+i+"/"+subProjectName;
                String cmd5="rf -f ../temp.txt && touch ../temp.txt";
                String cmd6="defects4j test >> ../temp.txt";
                String cmd8="cd .. ";
                //System.out.println(cmd3);
                int status;
                String[] cmd = new String[]{"sh","-c",cmd2+" ; " +cmd4+" ; " +cmd5+" ; "+cmd6+" ; "+cmd8};
                p1 = r1.exec(cmd);
                status = p1.waitFor(); 
                p1.destroy();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ArrayList<String> res=readTempFile(checkoutPath+"/"+projectName);
            if(res==null || res.size()==0) 
            	continue;
            try {
				writeFile(res,outputPath+"/"+projectName+"/"+i+".txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
    }

    public static ArrayList<ArrayList<String>> getProjectList(String path) throws Exception {
        ArrayList<String> list = readFile(path);
        ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
        ArrayList<String> ansPro= new ArrayList<String>();
        ArrayList<String> ansSubPro= new ArrayList<String>();
        for(String s:list){
            String[] info=s.split("\t");
            ansPro.add(new String(info[0]));
            if(info.length==7){
                ansSubPro.add(info[6]);
            }
            else{
                ansSubPro.add(".");
            }

        }
        ans.add(ansPro);
        ans.add(ansSubPro);
        return ans;
    }

    public static ArrayList<String> readFile(String fileName){
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while((tempString = reader.readLine()) != null)
            {
                if(!tempString.equals("")){
                    result.add(tempString);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }
    
    public static void writeFile(ArrayList<String> res,String fileName) throws IOException{
    	FileWriter writer = new FileWriter(fileName);
    	for(String s:res) { 
			writer.write(s); 
    	}
    	writer.close();
    }
    
    public static ArrayList<String> readTempFile(String fileName){
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader;
        int i=0;
        try{
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while((tempString = reader.readLine()) != null)
            {
            	if(i==0) {
            		i++;
            		continue;
            	}
                if(!tempString.equals("")){
                	String s=tempString.substring(4);
                    result.add(s);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

}
