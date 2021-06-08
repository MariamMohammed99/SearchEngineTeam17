import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class Main {
	
	public static Vector<String> readfile(String filename)
	{
		
		Vector<String> urls = new Vector<String>();
		
		try {
		      File myObj = new File(filename);
		      Scanner myReader = new Scanner(myObj);
		      int i=0;
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		      
		        urls.addElement(data);

		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    }
		return urls;
		
	}

	public static void main(String[] args) throws IOException, InterruptedException 
	{
		Variables v = new Variables();
		Scanner scanner = new Scanner(System.in);

		System.out.print("Please, Enter the number of threads: ");

		int numberOfThreads =scanner.nextInt();
		scanner.close();
		
		v.getContent("src/Content_Links.txt");

		v.getSavedLinks("src/Saved_Links.txt");
		
		while (!v.savedLinks.isEmpty()) //interruption occured so add it in the unvisited from the top to crawl first
		{
			v.setAsUnVisited();
		}
		
		v.getUnvisitedLinks("src/Unvisited_Links.txt");
		
		v.getListOfSeeds("src/Seeds.txt");
		
		v.getVisitedLinks("src/Visited_Links.txt");
		
		v.getOutput("src/SeedsOutput.txt");
		
		if(!v.outputSeeds.isEmpty()) //copying the output seeds in the set of links to be appended
		{
			v.setOfLinksToBeAppended = (LinkedHashSet)v.outputSeeds.clone();
		}
		
		while(!v.setOfLinksToBeAppended.isEmpty()) //remove it from the set of links to be appended and add it to the set of links
		{
				String removedURL=v.setOfLinksToBeAppended.stream().findFirst().get();
				
				v.setOfLinksToBeAppended.remove(removedURL);
				if (removedURL!=null)
				{
					v.setOfLinks.add(removedURL);
				}
		}

		Thread[] CThreadArray=new Thread[numberOfThreads];

		for(int i=0;i<numberOfThreads;i++)
		{//Start Crawler Threads And Name Them
			CThreadArray[i] = new WebCrawler(v);
			CThreadArray[i].setName ("Crawler "+ i);
		}
		
		for(int i =0;i<numberOfThreads;i++)
		{//Start Crawler Threads And Name Them
	
			CThreadArray[i].start();
		}
		for(int i =0;i<numberOfThreads;i++)
		{
			CThreadArray[i].join();
		}
	
		System.out.println("FINISHED CRAWLING: max page reached");
	
		//System.out.println("Enter the 1 to Recrawl, 2 to Stop the Crawler \n>>");
		 
		Database db= new Database();
		Vector<String> all_urls = readfile("src/SeedsOutput.txt");
		Thread t1 ;
		List<Thread> threadsList = new LinkedList<>();
		
        for(int i = 0; i < 5000; i=i+500) {
            t1 = new Thread(new Indexer(db,all_urls.subList(i, i+500)));
            threadsList.add(t1);
            t1.start();
        }

        for (Thread thread : threadsList) {
            try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	
}
	
}
