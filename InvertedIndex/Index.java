package InvertedIndex;

import PreProcessing.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.io.*;

public class Index {

	private static LinkedHashMap<String,DictonaryEntry> index_;
	private static ArrayList<String> docIds_;
	private static ArrayList<Double>docMagnitude_;
	private static ArrayList<Double> curQuery;
	static
	{
		index_ = new LinkedHashMap<String,DictonaryEntry>();
		docIds_ = new ArrayList<String>();
		docMagnitude_ = new ArrayList<Double>();
		 curQuery = new ArrayList<Double>();
	}
	
	public static ArrayList<String> getDocIds()
	{
		return docIds_;
	}
	
	static
	{
		String indexFilePath = "E:\\Final_submission_IR\\DemoIR\\src\\Index.txt";
		String idfFilePath = "E:\\Final_submission_IR\\DemoIR\\src\\idf.txt";
		String docIdFilePath = "E:\\Final_submission_IR\\DemoIR\\src\\docIds.txt";
		
		File indexFile = new File(indexFilePath);
		File idfFile = new File(idfFilePath);
		File docIdFile = new File(docIdFilePath);
		
		try
		{
			if(indexFile.exists()&&idfFile.exists()&& docIdFile.exists())
			{
				readData(indexFilePath,idfFilePath,docIdFilePath);
			}
			else
			{
				buildIndex(indexFilePath,idfFilePath,docIdFilePath);
			}
		}
		catch(Exception e2)
		{
			System.out.println("Error while building index!");
		}
	}
	
	
	public static ArrayList<Map.Entry<Integer,Double>> computeRelevance(HashSet<Integer>irrlvnt,HashSet<Integer> rlvnt,ArrayList<Map.Entry<Integer,Double>> docs)
	{
		ArrayList<Map.Entry<Integer,Double>> res = new ArrayList<Map.Entry<Integer,Double>>();
		
		if(rlvnt.size()==0 && irrlvnt.size()==0)
		{
			return docs;
		}
		
		double alpha = 1.0;
		double beta = 0.8;
		double gama = 0.1;
		int totalDocs = docIds_.size();
		double queryMagnitude=0.0;
		ArrayList<Double> newQuery = new ArrayList<Double>();
		
		int idx = 0;
		for(Map.Entry<String, DictonaryEntry> e:index_.entrySet())
		{
			double val = 0.0;
			double val1 = 0.0;
			DictonaryEntry tmp = e.getValue();
			double idf = Math.log10((double)totalDocs/tmp.getDocFreq());
			
			for(int i :rlvnt)
			{
				if(tmp.postingList_.containsKey(docs.get(i).getKey()))
					val+= (idf*tmp.postingList_.get(docs.get(i).getKey()).size());
			}
			
			for(int j :irrlvnt)
			{
				if(tmp.postingList_.containsKey(docs.get(j).getKey()))
					val1+= (idf*tmp.postingList_.get(docs.get(j).getKey()).size());
			}
			
			if(irrlvnt.size()!=0)val1*=((double)gama/irrlvnt.size());
			
			if(rlvnt.size()!=0)val *= ((double)beta/rlvnt.size());
			
			val += (alpha* curQuery.get(idx));
			
			val-=val1;
			
			newQuery.add(val);
			queryMagnitude+=val*val;
			idx++;
		}
		
		HashMap<Integer, Double> scores = new HashMap<Integer, Double>();
		
		for(int i = 0;i<totalDocs;i++)
			scores.put(i, 0.0);
		
		//*********
		idx = 0;
		for(Map.Entry<String, DictonaryEntry> e:index_.entrySet())
		{
			if(newQuery.get(idx).equals(0.0))
			{
				System.out.println("Save time!!");
				idx++;
				continue;
			}
			DictonaryEntry obj = e.getValue();
			HashMap<Integer,ArrayList<Integer>> list = obj.postingList_;
			double idf = Math.log10((double)totalDocs/obj.getDocFreq());
			
			for(Map.Entry<Integer,ArrayList<Integer>> d:list.entrySet())
			{
				if(scores.containsKey(d.getKey()))
					scores.put(d.getKey(),(scores.get(d.getKey())+(idf*d.getValue().size()*newQuery.get(idx))));
				else
					scores.put(d.getKey(),(idf*d.getValue().size()*newQuery.get(idx)));
			}
			idx++;
		}
		
		for(int i=0;i<totalDocs;i++)
		{
			scores.put(i,(scores.get(i)/( Math.sqrt(queryMagnitude)*Math.sqrt(docMagnitude_.get(i)) )));
		}
		
		//*********
		Iterator<Map.Entry<Integer,Double>> it = scores.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry<Integer,Double> tmp = it.next();
			if(tmp.getValue()!=0.0 && !tmp.getValue().isNaN())res.add(tmp);
		}
		//System.out.println("res size:"+res.size());
		Collections.sort(res,new compareClass());
		curQuery = newQuery;
		return res;
		
	}
	
	
	private static ArrayList<Map.Entry<Integer,Double>> computeSimilarity(ArrayList<String> query)
	{
		//ArrayList<Integer>res = new ArrayList<Integer>();
		
		HashMap<String,Integer>map = new HashMap<String,Integer>();
		
		for(String s:query)
		{
			if(map.containsKey(s))map.put(s, map.get(s)+1);
			else map.put(s,1);
		}
		
		curQuery.clear();
		
		HashMap<Integer, Double> ranks = new HashMap<Integer, Double>();
		int numDocs = docIds_.size();
		
		for(int i = 0;i<numDocs;i++)
			ranks.put(i, 0.0);
		
		double queryMagnitude=0.0;
		
		for(Map.Entry<String, DictonaryEntry> st:index_.entrySet())
		{
			if(!map.containsKey(st.getKey()))
			{
				curQuery.add(0.0);
				continue;
			}
				
			DictonaryEntry obj = st.getValue();
			HashMap<Integer,ArrayList<Integer>> list = obj.postingList_;
			double idf = Math.log10((double)numDocs/obj.getDocFreq());
			
			//System.out.println("list size:"+list.size());
			
			int qtf = map.get(st.getKey());
			
			for(Map.Entry<Integer,ArrayList<Integer>> d:list.entrySet())
			{
				if(ranks.containsKey(d.getKey()))
					ranks.put(d.getKey(),(ranks.get(d.getKey())+(idf*idf*d.getValue().size()*qtf)));
				else
					ranks.put(d.getKey(),(idf*idf*d.getValue().size()*qtf));
			}
			
			queryMagnitude += idf*idf*qtf*qtf;
			curQuery.add(idf*qtf);
			
		}
		
		ArrayList<Map.Entry<Integer,Double>> res = new ArrayList<Map.Entry<Integer,Double>>();
		
		Iterator<Map.Entry<Integer,Double>> it = ranks.entrySet().iterator();
		
		ArrayList<Double> tpScores = computeSimilarityTp(query);
		
	
		
		for(int i=0;i<tpScores.size();i++)
		{
			ranks.put(i,(ranks.get(i)/( Math.sqrt(queryMagnitude)*Math.sqrt(docMagnitude_.get(i)) ))+(tpScores.get(i)));
		}
		
		while(it.hasNext())
		{
			Map.Entry<Integer,Double> tmp = it.next();
			if(tmp.getValue()!=0.0 && !tmp.getValue().isNaN())res.add(tmp);
		}
		
		System.out.println("res size:"+res.size());
		Collections.sort(res,new compareClass());
		
		return res;
	}
	
	public static ArrayList<Double> computeSimilarityTp(ArrayList<String> query)
	{
		ArrayList<Double> res = new ArrayList<Double>(Collections.nCopies(docIds_.size(),0.0));
		if(query.size()<=1)return res;
		
		for(int i=0;i<query.size()-1;i++)
		{
			String q1 = query.get(i);
			String q2 = query.get(i+1);
			
			DictonaryEntry obj1 = index_.get(q1);
			DictonaryEntry obj2 = index_.get(q2);
			
			if(obj1==null || obj2==null)continue;
			
			//System.out.println("q1: "+q1+" q2: "+q2);
			for(Map.Entry<Integer,ArrayList<Integer>> e:obj1.postingList_.entrySet())
			{
				double score = 0.0;
				if(obj2.postingList_.containsKey(e.getKey()))
				{
				
					ArrayList<Integer> pos1 = obj1.postingList_.get(e.getKey());
					ArrayList<Integer> pos2 = obj2.postingList_.get(e.getKey());
					for(int j=0;j<pos1.size();j++)
					{
						for(int k=0;k<pos2.size();k++)
						{
							if(pos1.get(j)<pos2.get(k))
							{
								//if(e.getKey()==0)System.out.println("Diff: "+(pos2.get(k)-pos1.get(j)));
								score +=(( (double)1/( (pos1.size()*pos2.size())*(pos2.get(k)-pos1.get(j)) ) ));
							}
						}
					}
					
					res.set(e.getKey(),score);	
				}
			}
		}
		
		for(int i = 0;i<docIds_.size();i++)
			System.out.printf("doc:%s score: %f\n",docIds_.get(i),res.get(i));
		
		System.out.println("----------------------------------------------");
		return res;
	}
	
	public static ArrayList<String> splitQuery(StringBuffer query)
	{
		ArrayList<String>res = new ArrayList<String>();
		int i = 0;
		while(i<query.length() && query.charAt(i)==' ')i++;
		if(i==query.length())return DataClean1.stemmerMethod(res);
		if(i!=0)query = new StringBuffer(query.substring(i));;
		while(query.indexOf(" ")!=-1)
		{
			if( !DataClean1.getStopWordList().contains(query.substring(0, query.indexOf(" ")).toLowerCase()) )
				res.add(query.substring(0, query.indexOf(" ")).toLowerCase());
			int idx = query.indexOf(" ");
			while(idx<query.length() && query.charAt(idx)==' ')idx++;
			if(idx>=query.length())return DataClean1.stemmerMethod(res);
			query = new StringBuffer(query.substring(idx));
		}
		if(!DataClean1.getStopWordList().contains(query.substring(0).toLowerCase()))
			res.add(query.substring(0).toLowerCase());
		
		return DataClean1.stemmerMethod(res);
	}
	
	
	
	public static void buildIndex(String indexFilePath,String idfFile,String docIdFile)  throws FileNotFoundException, IOException
	{
		DataClean1.dataClean();
		
		System.out.println("index_ size: "+index_.size());
		
		for(String files:DataClean1.getFNames())
		{
			docIds_.add(files);
			docMagnitude_.add(0.0);
		}
		
		System.out.println("num_files: "+DataClean1.getfWordPositions().size());
		for(int i = 0;i<DataClean1.getfWordPositions().size();i++)
		{
			for(Map.Entry<String, ArrayList<Integer>> e:DataClean1.getfWordPositions().get(i).entrySet())
			{
				DictonaryEntry t;
				if(index_.containsKey(e.getKey()))
				{
					t = index_.get(e.getKey());
				}
				else
				{
					t = new DictonaryEntry();
				}
				
				t.postingList_.put(i,e.getValue());
				t.setDocFreq(t.getDocFreq()+1);
				t.setTermFreq(t.getTermFreq()+e.getValue().size());
				index_.put(e.getKey(),t);
			}
		}
		
		int totalDocs = docIds_.size();
		
		for(Map.Entry<String,DictonaryEntry> e:index_.entrySet())
		{
			DictonaryEntry t = e.getValue();
			double idf = Math.log10((double)totalDocs/t.getDocFreq());
			
			for(Map.Entry<Integer,ArrayList<Integer>> p:t.postingList_.entrySet())
			{
				docMagnitude_.set(p.getKey(), docMagnitude_.get(p.getKey())+(p.getValue().size()*idf*p.getValue().size()*idf));
			}
		}
		BufferedWriter writer; 
		//String indexFilePath,String idfFile,String docIdFile
	    writer = new BufferedWriter(new FileWriter(idfFile));
		
	    for(Double d:docMagnitude_)
	    {
	    	writer.write(d.toString()+"\n");
	    }
	    writer.close();
	    
	    writer = new BufferedWriter(new FileWriter(docIdFile));
		
	    for(String d:docIds_)
	    {
	    	writer.write(d+"\n");
	    }
	    writer.close();
	    
	    writer = new BufferedWriter(new FileWriter(indexFilePath));
		
	    for(Map.Entry<String,DictonaryEntry> e:index_.entrySet())
	    {
	    	writer.write(e+"\n");
	    }
	    
	    writer.close();
	    
	    
		System.out.println("index_ size: "+index_.size());
		//DataClean.dataClean();
		//System.out.println("finalWords_ size: "+DataClean.getFinalWords().size());
		
	}
	
	public static void readData(String indexFilePath,String idfFile,String docIdFile) throws FileNotFoundException, IOException
	{
		BufferedReader idx = new BufferedReader(new InputStreamReader(new FileInputStream(indexFilePath)));
		BufferedReader idf = new BufferedReader(new InputStreamReader(new FileInputStream(idfFile)));
		BufferedReader dcid = new BufferedReader(new InputStreamReader(new FileInputStream(docIdFile)));
		
		String line;
		while((line = idf.readLine())!=null)
		{
			docMagnitude_.add(Double.parseDouble(line));
		}
		
		while((line = dcid.readLine())!=null)
		{
			docIds_.add(line);
		}
		
		while((line = idx.readLine())!=null)
		{
			String[] arr1 = line.split("=",2);
			
			DictonaryEntry temp = new DictonaryEntry();
			
			String[] arr2 = arr1[1].split(",", 3);
			temp.setDocFreq(Integer.parseInt(arr2[0]));
			temp.setTermFreq(Integer.parseInt(arr2[1]));
			arr2[2] = arr2[2].substring(1, arr2[2].length()-1);
			//System.out.println("arr: "+arr2[2]);
			
			//String[] pl = arr2[2].split(", ");//error
			
			Pattern rdidx = Pattern.compile("\\w+={1}\\[{1}[\\w,\\s]+\\]{1}");
			Matcher idxMatch = rdidx.matcher(arr2[2]);
			
			while(idxMatch.find())
			{
				String pl = idxMatch.group();
				//System.out.println("grp: "+pl);
				String[] plo = pl.split("=");
				
				ArrayList<Integer> pos = new ArrayList<Integer>();
				
				plo[1] = plo[1].substring(1, plo[1].length()-1);
				
				//System.out.println("plo: "+plo[1]);
				for(String p:plo[1].split(", "))
				{
					pos.add(Integer.parseInt(p));
				}
				
				temp.postingList_.put(Integer.parseInt(plo[0]), pos);
			}
			index_.put(arr1[0], temp);
		}
		
		idx.close();
		idf.close();
		dcid.close();
	}
	
	
	
	public static ArrayList<Map.Entry<Integer,Double>> processQuery(StringBuffer query) throws FileNotFoundException, IOException
	{
		System.out.println("Started!!");
	
		
		
		
		//StringBuffer query = new StringBuffer("The forces of water decorated the cave");
		
		//String[] s = query.split(" ");
		
		ArrayList<String>procQuery = splitQuery(query);
		
		if(procQuery.size()==0)return new ArrayList<Map.Entry<Integer,Double>>();
		
		for(String t:procQuery)
			System.out.println(t);
		
		ArrayList<Map.Entry<Integer,Double>> rankings = computeSimilarity(procQuery);
		
		for(Map.Entry<Integer,Double> d:rankings)
		{
			if(d.getValue()!=0)System.out.println("Doc: "+docIds_.get(d.getKey())+" similarity: "+d.getValue());
		}
		
		System.out.println("rankings size: "+rankings.size());
		
		System.out.println("end!!");
		
		return rankings;
		
	}
}
