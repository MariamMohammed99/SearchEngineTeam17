import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import com.mongodb.*;
import java.lang.Math;

public class Indexer implements Runnable{
	

	public List<String> urls;
	Database db;
	public Indexer(Database db, List<String> list)
	{
	
		this.db=db;
		this.urls = list;
		
	}
	
//	
//	public idf()
//	{
//		
//		BasicDBObject query = new BasicDBObject();
//		query.put("my_urls",url); // (1)
//		DBObject f = db.wordsCol.findOne(query);
//		System.out.print();
//		
//	}
//	
	
	
	public void run()
	{
		
		long count= 0;
		double idf= 0;
		long df= 0;
		for(int i=0;i<urls.size();i++)
		{	

		Map<String,Integer> m=null;
		
		Preprocessing r=new Preprocessing();
		
		
			
			try {
				m=r.languageProcessing(urls.get(i));
				//System.out.println(r.count);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		
		
		 for (Map.Entry<String,Integer> entry : m.entrySet())
		 {
			
			    BasicDBObject query = new BasicDBObject();
				query.put("word", entry.getKey()); // (1)
				DBObject f = db.wordsCol.findOne(query);
				
				
			 if(f == null)
			 {

				Vector<Integer>my_ids=new Vector<Integer>();
				Vector<String>my_urls = new Vector<String>();
//				Vector<Double>my_idf=new Vector<Double>();
				Vector<Double>my_tf=new Vector<Double>();
				 my_urls.add(urls.get(i));
				 my_tf.add((double) ((entry.getValue())/(r.count)));
				 df = 1;
				 idf = Math.log((urls.size())/df);
//				 my_idf.add((double) ((entry.getValue())/m.size()));
				 DBObject d =new BasicDBObject("word",entry.getKey()).append("my_urls",my_urls).append("my_tf", my_tf).append("my_idf",idf).append("my_df", df);
					
				 db.wordsCol.insert(d);
			 }
			 else
			 {
				 //the word already exists
				
					BasicDBObject update_query = new BasicDBObject();
					update_query.put("word", entry.getKey()); 
					DBObject u = db.wordsCol.findOne(update_query); 
					
					BasicDBObject newDocument = new BasicDBObject();
					
					
					BasicDBList my_urls_retreived = (BasicDBList)u.get("my_urls");
					BasicDBList my_tf_retreived = (BasicDBList)u.get("my_tf");
					double my_idf_reterived = (double) u.get("my_idf");
					long my_df_reterived = (long) u.get("my_df");
					my_df_reterived=my_df_reterived+1;
					my_idf_reterived = Math.log((urls.size())/my_df_reterived);
					
					
					my_urls_retreived.add(urls.get(i));
					
					my_tf_retreived.add(entry.getValue());
					
					
					newDocument.put("my_df", my_df_reterived);
					newDocument.put("my_idf", my_idf_reterived);
					newDocument.put("my_urls", my_urls_retreived); 
					newDocument.put("my_tf",my_tf_retreived);
					
					BasicDBObject updateObject = new BasicDBObject();
					updateObject.put("$set", newDocument); 
//					 System.out.println(updateObject);
					db.wordsCol.update(update_query, updateObject);
			 }
			
		 }
		
		}
		
	}
	
	
	
	
	


}
