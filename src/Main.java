import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		City[] cities = readCityFile();
		ACO aco = new ACO(cities);
		aco.execute();
	}
	
	private static City[] readCityFile() throws FileNotFoundException {
		File file = new File("Latitude and Longitude of Taiwan County.csv");
		Scanner sc = new Scanner(file);
		ArrayList<City> cities = new ArrayList<>();
		while(sc.hasNextLine()) {
			String ar[] = sc.nextLine().split(",");
			String name = ar[0];
			double x = Double.valueOf(ar[1]);
			double y = Double.valueOf(ar[2]);
			cities.add(new City(name,x,y));
		}
		sc.close();
		
		City[] responseCities = new City[cities.size()];
		cities.toArray(responseCities);
		
		return responseCities;
	}
}
