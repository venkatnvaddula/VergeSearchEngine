<%@page import="java.util.LinkedList"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="InvertedIndex.*"%>
<%@page import="java.io.*"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.HashSet"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/style.css" type="text/css">
<title>Output</title>
</head>
<body>
<style>
.submit{
	background-color: #FFA500; 
    border: none;
    color: white;
    padding: 5px 22px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
    -webkit-transition-duration: 0.4s; 
    transition-duration: 0.4s;
}
.submit2:hover {
    box-shadow: 0 12px 16px 0 rgba(0,0,0,0.24),0 17px 50px 0 rgba(0,0,0,0.19);
}
</style>
   
   <% 
   			
   		ArrayList<Map.Entry<Integer, Double>> rankings = (ArrayList<Map.Entry<Integer, Double>>)session.getAttribute("rankings");
		String folderName = "E:\\IR_project\\WebPages_2";
		File folder = new File(folderName);
		File[] filesArray = folder.listFiles();
		ArrayList<File> results = new ArrayList<File>();
		Map<String,String> displayR = new HashMap<String,String>();
		ArrayList<Double> relevanceScore = new ArrayList<Double>();
		
		StringBuffer query = (StringBuffer)session.getAttribute("query");
		HashSet<Integer> RSet = (HashSet<Integer>)session.getAttribute("RSet");
		HashSet<Integer> IRSet = (HashSet<Integer>)session.getAttribute("IRSet");
		Integer start = (Integer)session.getAttribute("start");
		Integer resultSize = (Integer)session.getAttribute("resultSize");
		
		if(rankings!=null)
		for(Map.Entry<Integer,Double> d:rankings)
		{
				results.add(new File(folderName+"\\"+Index.getDocIds().get(d.getKey())));
				relevanceScore.add(d.getValue());
		}
		
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<String> urls = new ArrayList<String>();
		
		for(File file:results)
		{
			LineNumberReader lrt = null;
			String nextLine = null;
			String title[]=null;
			String title1[]=null;	
			String urll = null;
			String desc = null;
			String[] desc1 = null;
			Boolean flag=false;
			//String siteDescription = "";
			String titleValue="";
			//Pattern metaTagDescriptionPattern = Pattern.compile("name=\"description\"(\\s*)content=\"(.*)\"");
			Pattern metaTagTitlePattern = Pattern.compile("property=\"og:title\"(\\s*)content=\"(.*)\"");
			
			lrt = new LineNumberReader(new FileReader(file));
			while ((nextLine = lrt.readLine()) != null) 
			{	
				Matcher matcher2 = metaTagTitlePattern.matcher(nextLine);
				while(matcher2.find()) 
				{
					titleValue = matcher2.group(2);
					titleValue=titleValue.replaceAll("[^a-zA-Z0-9 ]", "");
					
				}
				if(nextLine.contains("canonical"))
				{
					urll = nextLine.substring(28, nextLine.length()-4);
					
				}
				if(nextLine.contains("<p ") && !flag)
				{
					desc=nextLine;
					desc.trim();
					desc=desc.replaceAll("\\<[^>]*>"," ");
					desc=desc.replaceAll("[^a-zA-Z0-9 ]", "");
					flag=true;
					
				}

			}
		
			titles.add(titleValue);
			urls.add(urll);
			
			description.add(desc);
			
		}

   %>
 <br>
	<header>
		<h1><a href="HomePage.html"><span class="blue-text">Maverick Search System</span></a></h1>
	</header>
	<form class="form" name="input" method="GET" action="/DemoIR/HomeServlet">
	<label class="control-label"><strong>Input Query:</strong></label> 
	<input type='text'  name='inputtext' class="mytext" size="35" value="<%=query %>">
	<input type='submit' value='Search' class="submit submit2"><br>
	</form>
 <% if(resultSize==0){%>
 	<br><h1>Sorry... No results found :( </h1> <br>
 <%}else{ %>
  <br><strong>Displaying <%=start+1 %>-<%= start+Math.min(10,resultSize-start)%> of <%=resultSize%> Results </strong><br>
 <form action=HomeServlet method="post">
 <%for (int i=0;i<Math.min(10,resultSize-start);i++){ %>
 <%//System.out.println("i:"+i+" urls: "+urls.size()+" description: "+description.size()); %>
   <br><a href= <%=urls.get(i)%>><%= titles.get(i)%></a> 
  <br><font color="green"> <%=urls.get(i) %> </font>
  <br><font color="black"> <%=description.get(i) %> </font>
  <br><i><font color="red">Relevance Score is </font><%= relevanceScore.get(i) %></i><br>
  
  	<%if(IRSet!=null && IRSet.contains(start+i)){ %>
  	<input type = "radio" name = "<%=start+i%>" value = "R"  > Relevant
  	<input type = "radio" name = "<%=start+i%>" value = "IR" checked = "checked" > Not Relevant<br>
  	<%} else if(RSet!=null && RSet.contains(start+i)) { %>
	<input type = "radio" name = "<%=start+i%>" value = "R"  checked = "checked"> Relevant
  	<input type = "radio" name = "<%=start+i%>" value = "IR"  > Not Relevant<br>  	
  	<% }else {%>
  	<input type = "radio" name = "<%=start+i%>" value = "R"  > Relevant
  	<input type = "radio" name = "<%=start+i%>" value = "IR"  > Not Relevant<br>  	
  	<% }%>
  	
  <%} %>
  
    <%
    if(start==0) {
  	%>
  		<br>
      	
	   	<input type="submit"  name="resubmit" value="ReSubmit" class="submit submit2">
	   	<%
    if(start+10<=resultSize-1) {
  	%>
	   	<input type="submit"  name="next" value="Next" class="submit submit2">  
  	<%
    }} else if(start+10>=resultSize-1) {
  	%>
  		<br>
    	<input type="submit"  name="previous" value="Previous" class="submit submit2">
	   	<input type="submit"  name="resubmit" value="ReSubmit" class="submit submit2">
	   	 
  	<%
    } else {
  	%>
  		<br>
     	<input type="submit"  name="previous" value="Previous" class="submit submit2">
	   	<input type="submit"  name="resubmit" value="ReSubmit" class="submit submit2">
	   	<input type="submit"  name="next" value="Next" class="submit submit2"> 
  	<%
    }}
  	%>

</form> 
</body>
</html>