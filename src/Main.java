import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		City[] cities = readCityFile();
		
		ACO aco = new ACO(cities);
		aco.execute();
		
		String output = "";
		if(aco.bestTourOrder != null) {
			String[] ar = new String[aco.bestTourOrder.length];
			for(int i = 0; i < aco.bestTourOrder.length; i++) {
				int index = aco.bestTourOrder[i];
				ar[i] = cities[index].id;
			}			
			output = String.join(",", ar);			
		}
		
		writeCityFile(output);
	}
	
	private static City[] readCityFile() throws NumberFormatException, FileNotFoundException, IOException {
		ArrayList<City> cities = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("test.csv"))) {
			String line;
		   while ((line = br.readLine()) != null) {
			   if(line.equals("")) continue;
				String ar[] = line.split(",");
				String id = ar[0];
				String name = ar[1];
				double x = Double.valueOf(ar[2]);
				double y = Double.valueOf(ar[3]);
				cities.add(new City(id, name, x, y));
		   }
		}
		
		City[] responseCities = new City[cities.size()];
		cities.toArray(responseCities);
		
		return responseCities;
	}
	
	private static void writeCityFile(String output) throws IOException {
		Writer out = new BufferedWriter(new OutputStreamWriter(
		    new FileOutputStream("output.txt"), "UTF-8"));
		try {
		    out.write(output);
		} finally {
		    out.close();
		}
	}
}
