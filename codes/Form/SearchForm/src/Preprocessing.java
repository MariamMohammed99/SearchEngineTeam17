


import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Preprocessing {
	
	public long count=0;
	////////////////////remove stopWords from each line/////////////////////
	
	public static Vector<String> removeStopwrords(String inputpath)
	{
		ArrayList stopwords=new ArrayList<>();
		 Vector<String> v=new Vector();
		try {
			FileInputStream f=new FileInputStream("stopwords.txt");
			byte b[]=new byte[f.available()];
			f.read(b);
			f.close();
			String data []=new String(b).trim().split("\\s",0);
			for(int i=0;i<data.length;i++)
			{
				stopwords.add(data[i].trim());
			}
			
//			FileInputStream f2=new FileInputStream(inputpath);
//			byte b2[]=new byte[f2.available()];
//			f2.read(b2);
//			f2.close();
			String data2 []=new String(inputpath).trim().split("\\s",0);
			String newfile="";
			for(int i=0;i<data2.length;i++)
			{
				//String[] s = data2[i].replaceAll("\\W", "").toLowerCase().split("\\s",0);
				String s[]=data2[i].split("\\s",0);   //first line
				for(int j=0;j<s.length;j++)
				{
					String singleword=s[j];
					
					if(!stopwords.contains(singleword.trim().toLowerCase()))  //doesn't contain any stopwords
					{
						String res = "";
					    for (Character c : s[j].toCharArray())
					    {
					        if(Character.isLetterOrDigit(c))
					            res += c;
					     }
				
					    res = res.replaceAll("[0123456789]","");
					    
					    if(res!="")
					    {
					    	v.add(res); //a word	
					    }
					    	
					}	
				}	
			}	
			}
		catch (Exception e){
			System.out.println("error in meta-information stage");
		}
	//	System.out.println(v);
		return v;
	}
	
	
	
	
	
	
	
	public Map<String,Integer> languageProcessing(String url) throws IOException
	{
		
		
		Document doc=Jsoup.connect(url).ignoreContentType(true).get();
		 Map<String,Integer> map=new HashMap<String,Integer>();
		if (doc != null)
		
		{	
			String arr[]= {"div","section","h1","h2","h3","h4","h5","h6","li","ul","ol","p","nav","span","title"};
		
		// Map<String,Integer> map=new HashMap<String,Integer>();
		
		for(int i=0;i<arr.length;i++)
		{
			Elements e=doc.body().select(arr[i]);
			for(Element e1:e)
			{
				//System.out.println("text:"+e1.text());
				String line=e1.text();  //line
				if(line!="")
				{
				Vector<String> outStopWrods=removeStopwrords(line);
				if(!outStopWrods.isEmpty())
				{
			        try {
			            
			                Stemmer porterStemmer = new Stemmer();
			                //String[] words = ss.split("\\s",0);
			                for (String word : outStopWrods) {
			                    word=word.toLowerCase();		                    
			                    porterStemmer.add(word);
			                    //String stem = porterStemmer.stem(word);
			                    porterStemmer.stem();
			                    String stemed=porterStemmer.toString();
			                    map.merge(stemed, 1, Integer::sum);
			                   
			            }
			                count=count+1;
			        } finally {
			          
			        }
				}
				}

			}
		}
		
		 // System.out.println(map);
		}
		return map;
			
	}
	

	

//	public static void main(String[] args) throws IOException {
//		
//		String url[]= {"https://docs.python.org/3/"};
//		//String url[]= {"computing","compute","comupter"};
//		Map<String,Integer> m=null;
//		
//		for(int i=0;i<url.length;i++)
//		{
//			
//			m=languageProcessing(url[i]);
//			System.out.println("--------------------------------------------------");
//			System.out.println(m);
//
//		}
//	
//	}
}
