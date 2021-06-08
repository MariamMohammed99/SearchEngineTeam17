import java.math.BigInteger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Database {

	
	DBCollection wordsCol;
	
	@SuppressWarnings("resource")
	public Database() {
			
		MongoClient m = new MongoClient("localhost",27017);
		@SuppressWarnings("deprecation")
		DB db=m.getDB("Indexer");
		
		wordsCol=db.getCollection("words");
		
		
	
	}
	public void addNewEntry(String word,int id,String url[],int freq)
	{
		DBObject d =new BasicDBObject("word",word).append("url",url).append("frequency", freq).append("id", id);
		
		wordsCol.insert(d);
	}
	
	
	
	

}
