package InvertedIndex;
import java.util.HashMap;
import java.util.ArrayList;;

public class DictonaryEntry {
	//private String term_;
	private int docFreq_;
	private int termFreq_;
	//public LinkedList<PostingListElement> postingList_;
	public HashMap<Integer,ArrayList<Integer>> postingList_;
	
	public DictonaryEntry()
	{
		//term_ = "";
		docFreq_ = 0;
		termFreq_ = 0;
		//postingList_ = new LinkedList<PostingListElement>();
		postingList_ = new HashMap<Integer,ArrayList<Integer>>();
	}
	
	public DictonaryEntry(String term,int docFreq,int termFreq)
	{
		//term_ = term;
		docFreq_ = docFreq;
		termFreq_ = termFreq;
		//postingList_ = new LinkedList<PostingListElement>();
		postingList_ = new HashMap<Integer,ArrayList<Integer>>();
	}
	
	public String toString()
	{
		return docFreq_+","+termFreq_+","+postingList_.toString();
	}
	
	public int getDocFreq()
	{
		return docFreq_;
	}
	public void setDocFreq(int docFreq)
	{
		docFreq_ = docFreq;
	}
	
	public int getTermFreq()
	{
		return termFreq_;
	}
	public void setTermFreq(int termFreq)
	{
		termFreq_ = termFreq;
	}
}
