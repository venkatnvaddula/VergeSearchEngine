package PreProcessing;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;

public class DataClean1 {

	private static ArrayList<String> fNames;
	private static ArrayList<HashMap<String, ArrayList<Integer>>> fWordPositions;  //List of maps of all words in file with their positions
	private static HashSet<String> stopWordList;
	
	public static ArrayList<String> getFNames()
	{
		return fNames;
	}
	
	public static HashSet<String> getStopWordList()
	{
		return stopWordList;
	}
	
	public static ArrayList<HashMap<String, ArrayList<Integer>>> getfWordPositions()
	{
		return fWordPositions;
	}
	
	static
	{
		fNames = new ArrayList<String>();
		fWordPositions = new ArrayList<HashMap<String,ArrayList<Integer>>>();
		stopWordList = new HashSet<String>(Arrays.asList("i","a","about","an","are","as","at","be","by","com","de","en","for","from","how","in","is","it","la","of","on","or","that","the","this","to","was","what","when","where","who","will","with","and","www"));
	}
	
	public static void dataClean() throws FileNotFoundException, IOException{
		File folder = new File("E:\\IR_project\\WebPages_2\\");
		File[] listOfFiles = folder.listFiles();
		for(int i=0; i<listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()) {
				fNames.add(listOfFiles[i].getName());
			}
		}
		
		//int l = 0;
		for(int i=0; i<fNames.size(); i++) {
			fWordPositions.add(removeTags(fNames.get(i)));
			//l = l + fStrings.get(fStrings.size()-1).size();
		}
		
	}
	
	public static HashMap<String, ArrayList<Integer>> removeTags(String link) throws FileNotFoundException, IOException{
		BufferedReader  f = new BufferedReader(new FileReader("E:\\IR_project\\WebPages_2\\"+ link));
		String l;
		int position = 0;
		boolean flag = true;
		Pattern st = Pattern.compile("^(<script|<style|<!--).*$",Pattern.CASE_INSENSITIVE);
		Pattern ed = Pattern.compile("^.*(/script>|/style>|-->)$",Pattern.CASE_INSENSITIVE);
		HashMap<String, ArrayList<Integer>> al = new HashMap<String, ArrayList<Integer>>();
		while((l=f.readLine())!=null) {
			String[] ls;
			l = l.replaceAll("<style([\\s\\S]+?)</style>","");
			l = l.replaceAll("<script([\\s\\S]+?)</script>","");
			l = l.replaceAll("<!--([\\s\\S]+?)-->","");
			l = l.trim();
			//cnt++;
			if(l.equals("")) continue;
			Matcher mst = st.matcher(l);
			Matcher med = ed.matcher(l);
			if(mst.matches()) {
				flag = false;
				continue;
				}
			if(med.matches()) {
				flag = true;
				continue;
				}
			if(flag) {
				l = l.replaceAll("\\<[^>]*>"," ");
				ls = l.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
				for(int j=0; j<ls.length; j++) {
					String s = ls[j].trim();
					
					if(!s.equals("") && s.matches("[a-zA-Z.]+")) {
						if(Pattern.matches(".*[a-zA-Z]+.*", s) && !stopWordList.contains(s) && !s.matches("^(http|https|ftp).*$")) {
							s = stemmerMethod(s);
							if(al.containsKey(s)) {
								al.get(s).add(position++);
							}
							else {
								al.put(s,new ArrayList<>(Arrays.asList(position++)));
							}
						}
					}
				}
			}
		}
		f.close();
		return al;
	}
	
	public static ArrayList<String> stemmerMethod(ArrayList<String> al){
		   ArrayList<String> StemmedData = new ArrayList<String>();
		      Stemmer s = new Stemmer();
		     
		     for(String word: al) {
		    	 for(char c: word.toCharArray()) {
		    		 s.add(c);
		    	 }
		    	 s.stem();
		    	 StemmedData.add(s.toString());
		    	 
		     }
		     
		  return StemmedData;
	   }
	
	public static String stemmerMethod(String word){
	      Stemmer s = new Stemmer();
	     
	      for(char c: word.toCharArray()) {
	    	 s.add(c);
	   	 }
	   	 s.stem();
	     
	  return s.toString();
   }
}
