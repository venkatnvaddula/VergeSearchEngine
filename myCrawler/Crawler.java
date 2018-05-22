package myCrawler;

import java.util.regex.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.HashSet;

public class Crawler {
	
	public static LinkedList<String> frontier;
	public static HashSet<String> visited;
	
	public static void main(String[] args) throws MalformedURLException, IOException
	{
		String start = "https://www.theverge.com/";
		
		frontier = new LinkedList<String>();
		visited = new HashSet<String>();
				
		frontier.add(start);
		long st = System.currentTimeMillis();
		while(visited.size()<5 && !frontier.isEmpty())
		{
			String url = frontier.pollFirst();
			try
			{
				HashSet<String> extractedUrl = parsePage(url);
				visited.add(url);
				for(String link:extractedUrl)
				{
					if(!visited.contains(link))
					{
						frontier.addLast(link);;
					}
				}
			}
			catch(IOException ioe)
			{
				System.out.println("Invalid url: "+url+" is invalid");
			}
		}
		//System.out.println("TotalTime:"+ (System.currentTimeMillis()-st));
		System.out.println("Visited pages count: "+visited.size());
		
	}
	
	public static HashSet<String> parsePage(String url) throws MalformedURLException, IOException
	{
		HashSet<String> res = new HashSet<String>();
		Pattern patternTag = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
        Pattern patternLink = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
        
        Matcher matcherTag,matcherLink;
        //System.out.println("url: "+url);
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		String line;
		
		//StringBuffer temp = new StringBuffer(url);
		BufferedWriter writer = 
	              new BufferedWriter(new FileWriter("E:\\IR_project\\WebPages_4\\Doc_"+visited.size()+".html"));
		//System.out.println("line: "+line);
		while((line = br.readLine())!=null)
		{
			writer.write(line+"\n");
			matcherTag = patternTag.matcher(line);
			while(matcherTag.find())
			{
				String href = matcherTag.group(1);
				matcherLink = patternLink.matcher(href);
				while(matcherLink.find())
				{
					String link = matcherLink.group(1);
	                link = link.replaceAll("'", "");
	                link = link.replaceAll("\"", "");
	                if((link.contains("www.theverge.com/2017")||link.contains("www.theverge.com/2018")||link.contains("www.theverge.com/archives")) && !link.contains("mailto") && !link.contains(".php"))
	                res.add(link);
	                //System.out.println(link);
				}
			}
		}
		br.close();
		writer.close();
		return res;
	}
}

