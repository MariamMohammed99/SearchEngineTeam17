

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class QueryProcessing {

	Database db;

	public QueryProcessing(Database db)
	{
	
		this.db=db;
		
		
	}
	
	
public String preprocess_search_word(String s,String swPath)
	{ String stemed="";
		
		if(s!="")
		{
		Vector<String> outStopWrods=removeStopwrords_qp(s,swPath);
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
	                     stemed=porterStemmer.toString();
	            }
	        } finally {
	          
	        }
			}
		}
	return stemed;
	}

public static Vector<String> removeStopwrords_qp(String inputpath,String swPath)
	{
		ArrayList stopwords=new ArrayList<>();
		 Vector<String> v=new Vector();
		try {
			FileInputStream f=new FileInputStream(swPath);
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
	
public static void outputFile(String filename, BasicDBList b ) throws Exception {
 
	
	 try {
         FileWriter writer = new FileWriter(filename,false);
       
         for (int i=0; i<b.size() ; i++)
         { 
        	 writer.write((String) b.get(i));
        	 writer.write("\r\n");  
         
         }
         writer.close();
     } catch (IOException e) {
         e.printStackTrace();
     }

 }


public BasicDBList search_query(String stemed)
{
	BasicDBList my_urls_retreived=null;
	
	BasicDBObject query = new BasicDBObject();
	query.put("word",stemed); // (1)
	DBObject f = db.wordsCol.findOne(query);
	if(f!=null)
		my_urls_retreived = (BasicDBList)f.get("my_urls");
		
	return	my_urls_retreived;

}



public static void main(String[] args) {

	Database db= new Database();
	QueryProcessing p = new QueryProcessing(db);
	
	String search_word = p.preprocess_search_word("comput","stopwords.txt");
	BasicDBList urls_list = p.search_query(search_word);
	try {
		p.outputFile(search_word+".txt", urls_list);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}
