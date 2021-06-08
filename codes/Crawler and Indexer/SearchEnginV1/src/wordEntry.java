
import java.math.BigInteger;
import java.util.Vector;

public class wordEntry {
	Vector<Integer>ids=new Vector<Integer>();
	Vector<String>urls = new Vector<String>();
	Vector<Integer>freq=new Vector<Integer>();
	
	public wordEntry(Integer i,String url,int frequency) {
		// TODO Auto-generated constructor stub
		ids.addElement(i);
		urls.addElement(url);
		freq.addElement(frequency);
		
	}

}
