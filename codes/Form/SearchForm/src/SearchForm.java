import java.awt.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.swing.text.Document;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.*;

import com.mongodb.BasicDBList;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/SearchForm") 
public class SearchForm extends HttpServlet{
	public LinkedHashSet<String> setOfLinks = new LinkedHashSet<String>();
	public String[] Links;
	public String userInput="";
	public int pageid=1;
	
//	public SearchForm(String[] queryLinks) {
//		this.Links=queryLinks;
//	}
//	   
	public String[] ReadFile(String nameOfFile){
        try {
            File f = new File(nameOfFile);
            Scanner sc = new Scanner(f);
            while (sc.hasNext()){
            	String test=sc.next();
            	setOfLinks.add(test);
            	//System.out.println("added link: "+test);
            }
            sc.close();
		} catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the seeds list.");
            e.printStackTrace();
        }
        return setOfLinks.toArray(new String[setOfLinks.size()]);
    }
	
    public String Snip(String userInput,String url) {
    	String snippet="";
    	org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "error in jsoup";
		}
    	org.jsoup.select.Elements body = doc.select("body");
    	
    	String description = doc.select("meta[name=description]").get(0).attr("content");
    	
    	snippet=description;
    	return snippet;
    	
    }

    public String asSnippet(String userInput, String url) throws IOException{
    	if(userInput==null)
    		return "";
        String Snippet="";

        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();

        org.jsoup.select.Elements MyElements=doc.select("p");
        

        //String text = doc.body().text(); // "An example link"
        org.jsoup.nodes.Element MyElement=null;

        for(int i=1;i<MyElements.size();i++)
        {
            if(MyElements.get(i).getElementsContainingOwnText(userInput).first()!=null)
            {
                MyElement=MyElements.get(i).getElementsContainingOwnText(userInput).first();
                break;
            }  
        }
        if(MyElement==null||MyElement.text().isEmpty())
        {
            MyElement=doc.getElementsContainingText(userInput).first();
        }
        //still MyElement fady?
        if(MyElement==null||MyElement.text().isEmpty())
        {
            MyElement=doc.getElementsByTag("body").first();
        }

        Snippet=MyElement.text();
        if(Snippet.length()>200){
                          
            int End;

            int MyIndex = Snippet.indexOf(userInput);
            if(MyIndex==-1)
            {
                Snippet=Snippet.substring(0, 200)+"...";
                return Snippet;
            }
            
            if(MyIndex<100)
            {
                Snippet=Snippet.substring(0, 200)+"...";
            }
            else
            {
                int start=MyIndex-99;
                while(Snippet.charAt(start-1)!=' ')
                {
                    if(start==0)
                    {
                        break;
                    }
                    start--; 
                }
                if(start+200<Snippet.length())
                {
                    End=start+200;
                    Snippet=Snippet.substring(start, End)+"...";
                }
                else
                {
                    End=Snippet.length();
                    Snippet=Snippet.substring(start, End);
                }
            }
        }
        return Snippet;
    }
    
    public void processReq(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String textInput=request.getParameter("UserInput");
    	if(textInput!=null && textInput!=userInput)
    		pageid=1;
    	if(textInput!=null)
    		userInput=textInput;

        //1-CALL processRequest ==> SHOULD OUTPUT EL FILE FEL WEB-INF 
    	Database db= new Database();
    	QueryProcessing p = new QueryProcessing(db);
    	
    	String path = this.getServletContext().getRealPath("WEB-INF/stopwords.txt");
    	String search_word = p.preprocess_search_word(userInput,path);
    	BasicDBList urls_list = p.search_query(search_word);
    	
    	
    	
        //==> Get el file name
    	//String path = this.getServletContext().getRealPath("WEB-INF/"+userInput+".txt");
    	//==> howa da elly hab3ato badal comput.txt
        //2-read the file of urls and store them in an array or hashset
        //String Links[]=ReadFile(this.getServletContext().getRealPath("/WEB-INF/comput.txt"));
    	 //String Links[]=urls_list.to
//        String Links[]=new String[13];
//        Links[0]="https://cloud.google.com/support-hub/";
//        Links[1]="https://cloud.google.com/newsletter/?hl=ar";
//        Links[2]="https://cloud.google.com/community/";
//        Links[3]="https://cloud.google.com/community/";
//        Links[4]="https://cloud.google.com/community/";
//        Links[5]="https://cloud.google.com/community/";
//        Links[6]="https://cloud.google.com/community/";
//        Links[7]="https://cloud.google.com/community/";
//        Links[8]="https://cloud.google.com/community/";
//        Links[9]="https://cloud.google.com/community/";
//        Links[10]="https://cloud.google.com/community/";
//        Links[11]="https://cloud.google.com/community/";
//        Links[12]="https://cloud.google.com/community/";
        
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Search Results For "+userInput+"</title>");            
            out.println("</head>");
            out.println("<body style=\"background-color: rgb(204, 204, 177); text-align: center;>\"");
            out.println("<h1 style=\"color: rgb(134, 120, 101);\"> Showing Results For: "+userInput+ "</h1></br>");
            out.println("<form action=\"SearchEngine\" method=\"POST\">");
            out.println("<h4 style=\"color: rgb(134, 120, 101);\">Start Searching:</h4>");    
            out.println("<input type=\"text\" name=\"UserInput\"/> <br/> <br/>");
            out.println("<input style=\"color: rgb(134, 120, 101);\" type=\"submit\" value=\"Search\"/> <br/> <br/>");
            
            //links be 3adad el pages
            int pageCount=(urls_list.size())/10;
            int page=1;
        	while(pageCount>=0) {
        		out.print("<a style=\"color: rgb(82, 78, 58);\" name=\"pageid\" href=\""+page+"\">"+page+"</a> ");
        		pageCount--;
        		page++;
        	}
            
            out.println("</form>");
        

            
            //3-check if array not empty
            if(urls_list!=null)
            	pageCount=(urls_list.size())/10;
            
            if((urls_list.size())!=0 && urls_list!=null)
            {
            	
            	//int pageid=Integer.parseInt(request.getParameter("page"));
            	//int pageid=Integer.parseInt(request.getServletPath());
            	
            	//distribute results on pages
            	
            	int start=(pageid-1)*10;
            	for(int i=start;i<start+10;i++)
            	{
            		if(i>(urls_list.size()))
        				break;
            		
            		out.print("<h6 style=\"color: rgb(134, 120, 101);\"> "+"---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- "+(i+1)+" </h6>");
	                out.print("<h2 style=\"color: rgb(134, 120, 101);\">");
	                
	                out.print("<a href="+(urls_list.get(i)).toString()+" title="+(urls_list.get(i)).toString()+" </a>"+(urls_list.get(i)).toString()+"</a>");
	                out.println("</h2>");
	                
	                String description=asSnippet(userInput, (urls_list.get(i)).toString());
	                if(!description.equals(""))
	                		out.println("<h3 style=\"color: rgb(134, 120, 101);\"> "+description+"</h3></br>");
            		
            	}
            }
            else{
                out.println("</br><h1> No Results </h1></br>");
            }

            out.println("</body>");
            out.println("</html>");
        }

    }
    
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException,NumberFormatException  {
    	String path=request.getServletPath().split("/")[1];
    	pageid=Integer.parseInt(path);
    	
    	processReq(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException,NumberFormatException  {
    	processReq(request,response);
    }
        
}