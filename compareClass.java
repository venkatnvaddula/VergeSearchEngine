package InvertedIndex;
import java.util.Comparator;
import java.util.Map;

public class compareClass implements Comparator<Map.Entry<Integer,Double>> {

	public int compare(Map.Entry<Integer,Double> o1, Map.Entry<Integer,Double> o2)
	{
		if(o1.getValue()>o2.getValue())return -1;
		else if(o1.getValue()<o2.getValue())return 1;
		
		return 0;
	}
	
}
