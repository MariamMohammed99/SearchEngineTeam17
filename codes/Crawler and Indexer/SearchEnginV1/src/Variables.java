import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.lang.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class Variables {

	public LinkedHashSet<String> setOfLinks = new LinkedHashSet<String>();
	public LinkedHashSet<String> outputSeeds = new LinkedHashSet<String>();
	public LinkedHashSet<String> setOfLinksToBeAppended = new LinkedHashSet<String>();
	public LinkedHashSet<String> unvisitedLinks = new LinkedHashSet<String>();
	public LinkedHashSet<String> visitedLinks = new LinkedHashSet<String>();
	public LinkedHashSet<String> savedLinks = new LinkedHashSet<String>();
	public LinkedHashSet<String> setOfContent = new LinkedHashSet<String>();
	public LinkedHashSet<String> setOfRobotLinks = new LinkedHashSet<String>();
	public static final int MAX_PAGES = 5000;


//1-add hyper links to unvisited hashset
public void addInUnvisited(String [] arr)
{
	for (int i=0; i<arr.length; i++)
	{
		if (!visitedLinks.contains(arr[i]) && arr[i]!=null)
		{
			arr[i]=arr[i].replaceAll("'", "");
			if(!arr[i].equals(""))
				unvisitedLinks.add(arr[i]);
		}
	}	
}

//2-add hyper links to set of links hashset
public void addInSetOfLinks(String [] arr)
{
	for (int i=0; i<arr.length; i++)
	{
		if(arr[i]!=null)
		{
			arr[i]=arr[i].replaceAll("'", "");
			if(!arr[i].equals("") && !maxPagesReached())
				setOfLinks.add(arr[i]);
		}
	}	
}
	
//3-add restricted url to setOfRobotLinks hashset
public boolean addRUrl(String RURL)
{
    return (setOfRobotLinks.add(RURL));
}

//4-check if url exists in setOfLinks hashset
public boolean urlExists(String URL)
{
	return(setOfLinks.contains(URL));
}

//5-check if restricted url exists in setOfRobotLinks hashset
public boolean rurlExists(String RURL)
{
	return(setOfRobotLinks.contains(RURL));
}

//6-check if max no of pages is reached
public boolean maxPagesReached()
{
	return(setOfLinks.size()>=MAX_PAGES);
}

//7-set url as visited
public boolean setAsVisited(String URL)
{
	return(visitedLinks.add(URL));
}
	
//8-get first non-visited url and remove it from unvisited and add it to saved
public String getFirstNonVisited(){
	
	if (!unvisitedLinks.isEmpty())
	{
        String returnedURL=unvisitedLinks.stream().findFirst().get();
        boolean checkurl=isUrlValid(returnedURL);
 
        unvisitedLinks.remove(returnedURL);
        
        if(checkurl) {
        	savedLinks.add(returnedURL);
        	return(returnedURL);
        }
        else
        	return " ";
	}
	else
	return " ";	

}

//9-get listofSeeds and add them to setOfLinks and if no interruption (starting the code for the first time then add it to unvisited)
public void getListOfSeeds(String nameOfFile)
{
       try 
       {
           File f = new File(nameOfFile);
           Scanner sc = new Scanner(f);
            while (sc.hasNext())
            {
            	String test=sc.next();
            	setOfLinks.add(test);
            }
            if (unvisitedLinks.isEmpty())
            {
            	  unvisitedLinks = (LinkedHashSet)setOfLinks.clone();
            }
            sc.close();
		}
       catch (FileNotFoundException e)
        {
            System.out.println("An error occurred while reading the seeds list.");
            e.printStackTrace();
        }
}
	
//10-get visited and add them to visited links
public void getVisitedLinks(String nameOfFile)
{
        try 
        {
            File f = new File(nameOfFile);
            Scanner sc = new Scanner(f);
            while (sc.hasNext())
            {
            	String test=sc.next();
            	visitedLinks.add(test);
            }
            sc.close();
		}
        catch (FileNotFoundException e)
        {
            System.out.println("An error occurred while reading the seeds list.");
            e.printStackTrace();
        }
}

//11-get unvisited and add them to unvisited links
public void getUnvisitedLinks(String nameOfFile)
{
        try {
            File f = new File(nameOfFile);
            Scanner sc = new Scanner(f);
            while (sc.hasNext())
            {
            	String test=sc.next();
            	unvisitedLinks.add(test);
            }
            sc.close();
		}
        catch (FileNotFoundException e)
        {
            System.out.println("An error occurred while reading the seeds list.");
            e.printStackTrace();
        }
}
	
//12-get saved and add them to saved links	
public void getSavedLinks(String nameOfFile)
{
        try
        {
            File f = new File(nameOfFile);
            Scanner sc = new Scanner(f);
            while (sc.hasNext())
            {
            	String test=sc.next();
            	savedLinks.add(test);
            }
            sc.close();
		} 
        catch (FileNotFoundException e) 
        {
            System.out.println("An error occurred while reading the seeds list.");
            e.printStackTrace();
        }
}
	
//13-get seed output and add them to output seeds links
public void getOutput(String nameOfFile)
{
        try
        {
            File f = new File(nameOfFile);
            Scanner sc = new Scanner(f);
            while (sc.hasNext())
            {
            	String test=sc.next();
            	outputSeeds.add(test);
            }
            sc.close();
		} 
        catch (FileNotFoundException e) 
        {
            System.out.println("An error occurred while reading the seeds list.");
            e.printStackTrace();
        }
}

	
//14-write links in output file SeedsOut.txt
public void outputLinks(String nameOfFile)
{
		try 
		{
	        File f = new File(nameOfFile);
	        FileWriter fileWriter = new FileWriter(f);
	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
	        
			for(int i = 0; i<setOfLinks.size(); i++)
			{
				String[] setOfLinksArray = new String[setOfLinks.size()+1];
				setOfLinksArray = setOfLinks.toArray(setOfLinksArray);
				String data = setOfLinksArray[i] + System.getProperty("line.separator");
				bufferWriter.write(data);
			}
			bufferWriter.close();
			fileWriter.close(); 
		} 
		catch (IOException e)
		{
				System.out.println("An error occurred while writing the seeds list.");
	            e.printStackTrace();
			}
}
	
//15-write links in visited file Visited_Links.txt
public void outputVisitedLinks(String nameOfFile)
{
		try
		{
	        File f = new File(nameOfFile);
	        FileWriter fileWriter = new FileWriter(f);
	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
	        
			for(int i = 0; i<visitedLinks.size(); i++)
			{
				String[] setOfSavedLinksArray = new String[visitedLinks.size()+1];
				setOfSavedLinksArray = visitedLinks.toArray(setOfSavedLinksArray);
				String data = setOfSavedLinksArray[i] + System.getProperty("line.separator");
				bufferWriter.write(data);
			}
			bufferWriter.close();
			fileWriter.close(); 
		} 
		catch (IOException e) 
		{
				System.out.println("An error occurred while writing the seeds list.");
	            e.printStackTrace();
			}
}
	
//16-write links in saved file Saved_Links.txt	
public void outputSavedLinks(String nameOfFile)
{
		try 
		{
	        File f = new File(nameOfFile);
	        FileWriter fileWriter = new FileWriter(f);
	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
	        
			for(int i = 0; i<savedLinks.size(); i++)
			{
				String[] setOfSavedLinksArray = new String[savedLinks.size()+1];
				setOfSavedLinksArray = savedLinks.toArray(setOfSavedLinksArray);
				String data = setOfSavedLinksArray[i] + System.getProperty("line.separator");
				bufferWriter.write(data);
			}
			bufferWriter.close();
			fileWriter.close(); 
		} 
		catch (IOException e) 
		{
				System.out.println("An error occurred while writing the seeds list.");
	            e.printStackTrace();
		}
}
	
//17-write links in unvisited file Unvisited_Links.txt		
public void outputUnvisitedLinks(String nameOfFile)
{
		try 
		{
	        File f = new File(nameOfFile);
	        FileWriter fileWriter = new FileWriter(f);
	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
	        
			for(int i = 0; i<unvisitedLinks.size(); i++)
			{
				String[] setOfSavedLinksArray = new String[unvisitedLinks.size()+1];
				setOfSavedLinksArray = unvisitedLinks.toArray(setOfSavedLinksArray);
				String data = setOfSavedLinksArray[i] + System.getProperty("line.separator");
				bufferWriter.write(data);
			}
			bufferWriter.close();
			fileWriter.close(); 
		}
		catch (IOException e) 
		{
				System.out.println("An error occurred while writing the seeds list.");
	            e.printStackTrace();
		}
}
	
//18- remove url from saved links and add it to the top of the unvisited links
public void setAsUnVisited()
{
		String removedURL=savedLinks.stream().findFirst().get();
		savedLinks.remove(removedURL);
		if (removedURL!=null)
		{
			String[] Links = new String[unvisitedLinks.size()];
			Links = unvisitedLinks.toArray(Links);
			String [] newUnvisitedLinks = new String[unvisitedLinks.size()+1];
			int j=1; 
			//Adding the url in the first of the unvisited links
			newUnvisitedLinks[0]=removedURL;
			for (int i = 0; i < unvisitedLinks.size(); i++)
			{
				newUnvisitedLinks[j]=Links[i];
				j++;
			}
        LinkedHashSet<String> unvisitedLinks1 = new LinkedHashSet<String>(Arrays.asList(newUnvisitedLinks));
        unvisitedLinks = unvisitedLinks1;
		}
		
}
	
//19-add content in set of contents
	public boolean addContent(String content) {
        return (setOfContent.add(content));
    }
	
//20-check if content exists
public boolean contentExists(String content)
{
		return(setOfContent.contains(content));
}


public boolean isUrlValid(String url) {
    try {
       URL obj = new URL(url);
       obj.toURI();
       return true;
    } catch (MalformedURLException e) {
       return false;
    } catch (URISyntaxException e) {
       return false;
    }
 }

public String getCompactContent(String url) throws IOException
{
	String compContent="";
	 boolean checkurl = isUrlValid(url);
	
	if(checkurl) {
		Document doc = Jsoup.connect(url).ignoreContentType(true).get();
		Elements MyElements=doc.select("p");
		
		for(Element MyElement: MyElements)
	    {
			if(MyElement.text().length()>4)
				compContent+=MyElement.text().substring(0, 4);
	    }
	}
	
	return compContent;
}

public void addInContent(String [] arr)
{
	for (int i=0; i<arr.length; i++)
	{
		if (arr[i]!=null && arr[i]!="" && arr[i]!=" ")
		{
				setOfContent.add(arr[i]);
		}
	}	
}

public void outputContents(String nameOfFile)
{
		try 
		{
	        File f = new File(nameOfFile);
	        FileWriter fileWriter = new FileWriter(f);
	        BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
	        
			for(int i = 0; i<setOfContent.size(); i++)
			{
				String[] setOfSavedLinksArray = new String[setOfContent.size()+1];
				setOfSavedLinksArray = setOfContent.toArray(setOfSavedLinksArray);
				String data = setOfSavedLinksArray[i] + System.getProperty("line.separator");
				bufferWriter.write(data);
			}
			bufferWriter.close();
			fileWriter.close(); 
		}
		catch (IOException e) 
		{
				System.out.println("An error occurred while writing the seeds list.");
	            e.printStackTrace();
		}
}

public void getContent(String nameOfFile)
{
        try
        {
            File f = new File(nameOfFile);
            Scanner sc = new Scanner(f);
            while (sc.hasNext())
            {
            	String test=sc.next();
            	setOfContent.add(test);
            }
            sc.close();
		} 
        catch (FileNotFoundException e) 
        {
            System.out.println("An error occurred while reading the seeds list.");
            e.printStackTrace();
        }
}


}

