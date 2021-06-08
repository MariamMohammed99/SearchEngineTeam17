

//Java code to illustrate the get() method
import java.util.*;

public class Hash_Map_Demo {
	public static void main(String[] args)
	{

		// Creating an empty HashMap
		HashMap<String, Integer> hash_map = new HashMap<String, Integer>();

		// Mapping string values to int keys
		hash_map.put("Geeks", 10);
		hash_map.put("mo", 15);
		
		hash_map.merge("mo", 1, Integer::sum);

		
		// Displaying the HashMap
		System.out.println("Initial Mappings are: " + hash_map);
		
		// Getting the value of 25
		System.out.println("The Value is: " + hash_map.get("mo"));

	
	}
}

