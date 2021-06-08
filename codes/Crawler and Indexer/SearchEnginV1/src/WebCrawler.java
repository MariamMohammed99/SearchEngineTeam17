import java.io.*;
import java.lang.System.Logger.Level;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.*;
import java.util.logging.Logger;


public class WebCrawler extends Thread implements Runnable{
	
    private Variables va=null;
    private static int check = 0;
	public WebCrawler(Variables v){
		this.va = v;
	}
	
	@Override
	public void run() 
	{

		try
		{
			
			webCrawl();
		} 
		catch (IOException ex)
		{
            Logger.getLogger(WebCrawler.class.getName()).log(null);
        }

	}
	
	//public void webCrawl() throws IOException,FileNotFoundException{
	public void webCrawl() throws IOException,FileNotFoundException,MalformedURLException,HttpStatusException,UnsupportedMimeTypeException {
		while(!(va.maxPagesReached()) && !(va.unvisitedLinks.isEmpty())) { //!(va.unvisitedLinks.size()<2)
			String url,absUrl;
			
				//things i wanna synchronize fel threading (setofLinks,robotLinks,visited..)
				synchronized(va){
					
					String getUrl=va.getFirstNonVisited();
					
					if(!(getUrl.equals(" ")) && getUrl!=null) {
						
						String compContent = va.getCompactContent(getUrl);
						
						if(va.contentExists(compContent)) {
							va.setAsVisited(getUrl);
							url=" ";
							System.out.println("this link's content was added before: "+getUrl);
						}
						
						else
							url=getUrl;
							
						va.outputSavedLinks("src/Saved_Links.txt");
							
						if(!url.equals(" ")) {
							//not all urls got visited
						
							va.setAsVisited(url);
							va.outputVisitedLinks("src/Visited_Links.txt");
							
							//get url content & add it to content set
							if(!compContent.isEmpty())
								va.addContent(compContent);
							//System.out.println("Size of contents set: "+va.setOfContent.size());
							
						}
					
					}
					else
						url=" ";

				}
					
			//check for restricted urls and add them to setOfRobotLinks
			if(!url.equals(" ")) {
				RobotHandler(url); 
				try {
					Document doc = Jsoup.connect(url).ignoreContentType(true).get(); //Fetching the URL
					if (doc !=null)
					{
					System.out.print(Thread.currentThread().getName()+": ");
					System.out.println(url);//Display Crawler Information To The User
		
					Elements hyperLinks = doc.select("a[href]"); //Parsing the HTML to get the hyper links
					check = 0;
					int k = -1;
					int c = -1;
					int index=0;
					String [] hyper = new String[hyperLinks.size()];
					String [] contentArray = new String[hyperLinks.size()];
					for (Element page : hyperLinks) //getting the hyper links 
					{
						absUrl=page.attr("abs:href").split("#")[0];
							
						if(absUrl!=null && !(absUrl.equals("") || absUrl.equals(" "))) {
									
							if(!va.rurlExists(absUrl))
							{//only crawl if link isn't restricted							
								if(!va.urlExists(absUrl)) 
								{//and it wasn't added before
									String compContent = va.getCompactContent(absUrl);
									if(!va.contentExists(compContent)) {
										if(!(va.maxPagesReached()))//((va.setOfLinks.size())+index != va.MAX_PAGES)
										{//not exceeded max_pages
											
											k++;
											hyper[k]=absUrl;
											if(!compContent.isEmpty())
											{
												c++;
												contentArray[c]=compContent;
												//va.addContent(compContent); 
											}
										
										}
										else
										{
											check=1;
										}
									}
									else {
										System.out.println("this link's content was added before: "+absUrl);
									}
									
								}
							}
						}

					}
					
					if (check!=1)
					{
						synchronized(va)
						{
						va.savedLinks.remove(url);
						va.outputSavedLinks("src/Saved_Links.txt");
						}
					}
					
					synchronized(va)
					{
//						if (check!=1)
//						{
//							va.savedLinks.remove(url);
//							va.outputSavedLinks("src/Saved_Links.txt");
//						}
						
						va.addInUnvisited(hyper);
						va.addInSetOfLinks(hyper);
						va.addInContent(contentArray);
						va.outputLinks("src/SeedsOutput.txt");
						va.outputUnvisitedLinks("src/Unvisited_Links.txt");
						va.outputContents("src/Content_Links.txt");
						
						
					}
					}
				} 
				catch (IOException e) 
				{
					System.err.println("For '" + url + "': " + e.getMessage());
					e.printStackTrace();
					System.out.println("");
				}
			}
		}

	}
	
	public void RobotHandler(String url) throws FileNotFoundException
	{//REFERENCE PDF: https://media.techtarget.com/searchDomino/downloads/29713C06.pdf
		
		String inputString,robotURL,splitArr[];
		InputStream input;
		
		try {
			
			URL urlObj=new URL(url+"/robots.txt"); //creates url object from the string
			input=urlObj.openStream(); //openConnection() + getInputStream() => open url
			
			//reading from robots.txt file
			InputStreamReader inputReader =new InputStreamReader(input);
			BufferedReader br=new BufferedReader(inputReader);
			inputString=br.readLine(); 
			
			if(inputString == null) //EOF => return
				return;
			
			//System.out.println("found robot.txt for url: "+url);////////////////////////////////hnrg3ha
			if(inputString.equals("User-agent: *")) //first line of robot.txt
			{//System.out.println("User-agent: found");////////////////////////////////hnrg3ha
				/*The use of User-agents in the robots.txt file allows Web sites to set rules on a UseragentbyUser-agent basis.
				However, typically Web sites want to disallow all robots (or User-agents) access to certain areas,
				so they use a value of asterisk (*) for the User-agent.
				This specifies that all User-agents are disallowed for the rules that follow it.*/
				
				//keep checking until EOF
				while(inputString!=null)
				{
					if(inputString.contains("Disallow:")) //search for Disallow keyword
					{
						/*The lines following the User-agent line are called disallow statements.
						 * The disallow statements define the Web site paths that crawlers are not allowed to access.
						 * For example, the first disallow statement in the sample file tells crawlers not to crawl any links that begin with /cgi-bin/*/
						
						splitArr=inputString.split(" "); //split after "Disallow:" keyword
						//splitArr=inputString.split("/", 2); //remove extra slash at the beginning
						robotURL=url+splitArr[1];        //disallowed url will be the current url + the path defined after "Disallow:"
						
						//WRITE URL FE FILE, CHECK ON IT WHEN CRAWLING??
						//add it to robot links hashset if not already in it
						if(!va.rurlExists(robotURL)) //add it if it wasn't added before
							va.addRUrl(robotURL);
						//setOfLinks.remove(robotURL);
						//System.out.println("found restricted url: "+robotURL); ////////////////////////////////hnrg3ha
					}
					inputString=br.readLine();
				}
			}	
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//System.out.println("No robot file found for this url."); ////////////////////////////////hnrg3ha
		} catch (IOException IO) { //sent is Restricted aslan
			// TODO Auto-generated catch block
			//IO.printStackTrace();
			//WRITE URL FE FILE, CHECK ON IT WHEN CRAWLING??
			//add it to robot links hashset if not already in it
			if(!va.rurlExists(url+"/robots.txt")) //add it if it wasn't added before
				va.addRUrl(url+"/robots.txt");
			//setOfLinks.remove(url+"/robots.txt");
			//System.out.println("The URL itself is restricted...Adding it to robots hashset"); ////////////////////////////////hnrg3ha
		} 	
	}
}