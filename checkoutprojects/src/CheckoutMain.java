import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CheckoutMain {
	static String path="/Users/chixiaye/bysj/code/GrowingBugRepository/framework/bug-mining/bug_mining_projects_info.txt";
	static String defects4jPath="/home/cxy/GrowingBugRepository/framework/bin";
    public static void main(String[] args) throws Exception {
    	 String outputPath="/Users/chixiaye/paper/checkout-4j";
         ArrayList<ArrayList<String> > list = getProjectList(path);
        //for(int i=0;i<list.get(0).size();i++){
        for(int i=0;i<=0;i++){
            String projectName=list.get(0).get(i);
            String subProjectName=list.get(1).get(i);
            if(i<=(0-1))continue;
            checkoutProject(path,projectName,subProjectName,outputPath);
        }
    }
    public static void checkoutProject(String path,String projectName,String subProjectName,String outputPath){
        try{
            File directory = new File(outputPath+"/"+projectName);
            directory.mkdir();
        }catch (Exception e) {
            //e.printStackTrace();
        }
        for(int i=1;i<=30;++i){
            Runtime r1=Runtime.getRuntime();
            Process p1=null;
            try {
                String cmd0 = "source ~/.bash_profile"; //depend on the os
                String cmd1 ="export PATH=$PATH:"+defects4jPath;
                String cmd2 ="cd "+outputPath+"/"+projectName;
                String cmd3="defects4j checkout -p "+projectName+" -v "+i+"b -w "+projectName+"_"+i+" -s "+subProjectName;
                String cmd4="cd "+projectName+"_"+i+"/"+subProjectName;
                String cmd5="defects4j compile  ";
                String cmd6="defects4j test   ";
                String cmd7="ls all_tests ";//"mvn org.mudebug:prapr-plugin:prapr -Drat.skip=true ";
                String cmd8="cd .. ";
                //System.out.println(cmd3);
                int status;
                String[] cmd = new String[]{"sh","-c",cmd0+" ; "+cmd1+" ; "+cmd2+" ; "+cmd3+" ; "+cmd4+" ; "+cmd5+" ; "+cmd6+" ; "+cmd7+" ; "+cmd8};
                p1 = r1.exec(cmd);
                status = p1.waitFor();
                System.out.println(cmd3);

                p1 = r1.exec("ls "+outputPath+"/"+projectName+"/"+projectName+"_"+i+"/"+subProjectName+"/all_tests ");
                status = p1.waitFor();
                if(status != 0){
                    //System.err.println("Failed to call cmd and the return status's is: " + status);
                    System.err.println(projectName+"_"+i+" failed! ");
                    p1 = r1.exec("rm -rf "+outputPath+"/"+projectName+"/"+projectName+"_"+i);
                    status = p1.waitFor();
                }
                else{
                    System.out.println(projectName+"_"+i+" successed! ");
                }
                p1.destroy();
            } catch (Exception e) {
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
}
