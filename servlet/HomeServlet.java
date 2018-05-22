package info.java.tips.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import InvertedIndex.Index;
import PreProcessing.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet 
{
	public static ArrayList<Map.Entry<Integer,Double>> rankings;
	public static Integer start, resultSize;public static final Integer pageLimit=10;
	public static  HashSet<Integer> RSet,IRSet;
	
 static
 {
	 start=0;
	 resultSize=0;
	 RSet = new HashSet<Integer>();
	 IRSet = new HashSet<Integer>();
	 
 }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		StringBuffer query = new StringBuffer(request.getParameter("inputtext"));
		rankings= Index.processQuery(query);
		resultSize=rankings.size();
		start = 0;
		RSet.clear();
		IRSet.clear();
		System.out.println("rankings.size: "+rankings.size());
		response.setContentType("text/html");
		//PrintWriter out = response.getWriter();
		request.getSession().setAttribute("query", query);
		request.getSession().setAttribute("RSet", RSet);
		request.getSession().setAttribute("IRSet", IRSet);
		request.getSession().setAttribute("start", start);
		request.getSession().setAttribute("resultSize", resultSize);
		if(rankings.size()!=0)request.getSession().setAttribute("rankings", new ArrayList<Map.Entry<Integer,Double>>(rankings.subList(start, start+Math.min(pageLimit,resultSize-start))));
		
		response.sendRedirect("Success1.jsp");
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		 //String select[] = request.getParameterValues("Url");
		 
		System.out.println("Do post");
		for(int i = 0;i<Math.min(pageLimit,resultSize-start);i++)
	 	{
	 		String value = request.getParameter(Integer.toString(start+i));
	 		System.out.println("For i:"+i+" value:"+value);
	 		if(value!=null && value.equals("IR"))
	 		{
	 			System.out.println("In if");
	 			
	 			if(RSet.contains(start+i))
	 			{
	 				RSet.remove(start+i);
	 			}
	 			
	 			IRSet.add(start+i);
	 		}
	 		else if(value!=null && value.equals("R"))
	 		{
	 			System.out.println("In else");
	 			
	 			if(IRSet.contains(start+i))
	 			{
	 				IRSet.remove(start+i);
	 			}
	 			
	 			RSet.add(start+i);
	 			
	 		}
	 	}
		
		/*
		System.out.println("Rset in servelet");
		for(int i:RSet)
		System.out.println(i);

		System.out.println("------------------------------");
		System.out.println("IRset in servelet");
		for(int i:IRSet)
		System.out.println(i);
		System.out.println("------------------------------");
		*/
		
		if(request.getParameter("previous")!=null)
		 {
			 //System.out.println("In previous");
			 //response.setContentType("text/html");
				//PrintWriter out = response.getWriter();
				start-=pageLimit;
				request.getSession().setAttribute("start", start);
				request.getSession().setAttribute("resultSize", resultSize);
				request.getSession().setAttribute("rankings", new ArrayList<Map.Entry<Integer,Double>>(rankings.subList(start, start+Math.min(pageLimit,resultSize-start))));
				
				response.sendRedirect("Success1.jsp");
				return;
		 }
		 else if(request.getParameter("resubmit")!=null)
		 {
			 //System.out.println("In resubmit");
			 
			 	rankings=Index.computeRelevance(IRSet,RSet, rankings);
			 	IRSet.clear();
			 	RSet.clear();
			 	start = 0;
			 	resultSize=rankings.size();
			 	request.getSession().setAttribute("start", start);
				request.getSession().setAttribute("resultSize", resultSize);
			 	request.getSession().setAttribute("rankings",  new ArrayList<Map.Entry<Integer,Double>>(rankings.subList(start, start+Math.min(pageLimit,resultSize-start))));
				request.getSession().setAttribute("RSet", RSet);
				request.getSession().setAttribute("IRSet",IRSet);
				response.sendRedirect("Success1.jsp");
		 }
		 else
		 {
			 //System.out.println("In next");
			 //response.setContentType("text/html");
				//PrintWriter out = response.getWriter();
			 	
				start+=pageLimit;
				request.getSession().setAttribute("start", start);
				request.getSession().setAttribute("resultSize", resultSize);
				request.getSession().setAttribute("rankings", new ArrayList<Map.Entry<Integer,Double>>(rankings.subList(start, start+Math.min(pageLimit,resultSize-start))));
				
				response.sendRedirect("Success1.jsp");
				return;
		 }
		
		 
		 
		 /*for(int i=0;i<rankings.size();i++)
		 {
			 String radioResult= request.getParameter(Integer.toString(i));
			 if(radioResult.equals("R"))
				 RSet.add(i);
			 else if(radioResult.equals("IR"))
				 IRSet.add(i);
			 else
				 continue;

		 }*/
		 
		 
		 
		
			 
	}
}
