package clients.customer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SearchName extends HashMap<String,String>{
	
	public SearchName(){
		put("0001","40 inch LED HD TV");
		put("0002","DAB Radio");
		put("0003","Toaster");
		put("0004","Watch");
		put("0005","Digital Camera");
		put("0006","MP3 Player");
		put("0007","32Gb USB2 drive");
	}
		

	public String getNumFromName(SearchName stock, String Name) {
		//int highestMatch = 0;
		Name = Name.toLowerCase();
		//String highestMatchId = null;
		for (Map.Entry<String,String> entry : stock.entrySet()) {
			String entryName = entry.getValue().toLowerCase();
			if (Objects.equals(Name, entryName)) {
				return entry.getKey();
			}
			//int currentMatch = 0;
			else if (entryName.contains(Name)){
				return entry.getKey();
			}
			// Found a new solution thanks for ContainsChar https://stackoverflow.com/questions/506105/how-can-i-check-if-a-single-character-appears-in-a-string
			//for (int charCount = 0; charCount < Math.min(entryName.length(), Name.length()); charCount++) {
						
				//char currChar = entryName.charAt(charCount);
				//if (Objects.equals(currChar, Name.charAt(charCount))) {
				//	currentMatch++;
				//}
				//else {
				//	break;
				//}
			//}
			//if (highestMatch < currentMatch){
			//	highestMatchId = entry.getKey();
			//	highestMatch = currentMatch;
			//}
				
		}
		return null;
		
	}

	
}



